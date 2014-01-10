package com.search.manager.service.rules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.RuleService;
import com.search.manager.service.UtilityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "facetSortService")
@RemoteProxy(
        name = "FacetSortServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "facetSortService"))
public class FacetSortService extends RuleService {

    private static final Logger logger =
            LoggerFactory.getLogger(FacetSortService.class);
    
    @Autowired
    private DaoService daoService;
    @Autowired
    private UtilityService utilityService;
    
    @Override
    public RuleEntity getRuleEntity() {
        return RuleEntity.FACET_SORT;
    }

    @RemoteMethod
    public FacetSort addRule(String ruleName, String ruleType, String sortType) {
        int result = -1;
        String ruleId = "";
        String store = utilityService.getStoreId();
        String username = utilityService.getUsername();

        try {
            FacetSort rule = new FacetSort(ruleName, ruleType, sortType, store);
            rule.setCreatedBy(username);
            ruleId = daoService.addFacetSortAndGetId(rule);

            if (StringUtils.isNotBlank(ruleId)) {
                if (RuleType.KEYWORD.getDisplayText().equalsIgnoreCase(ruleType)) {
                    daoService.addKeyword(new StoreKeyword(store, ruleName));
                }

                result = addAllFacetGroup(ruleId);
            }

            try {
                daoService.addRuleStatus(new RuleStatus(RuleEntity.FACET_SORT, store, ruleId, ruleName,
                        username, username, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
            } catch (DaoException de) {
                logger.error("Failed to create rule status for ranking rule: " + ruleName);
            }

            if (result > 0) {
                return getRuleById(ruleId);
            }
        } catch (DaoException e) {
            logger.error("Failed during addRule()", e);
            try {
                daoService.deleteFacetSort(new FacetSort(ruleId, store));
            } catch (DaoException de) {
                logger.error("Unable to complete process, need to manually delete rule", de);
            }
        }

        return null;
    }

    @RemoteMethod
    public static Map<String, String> getSortOrderList() {
        Map<String, String> sortOrderList = new LinkedHashMap<String, String>();

        for (SortType st : SortType.values()) {
            sortOrderList.put(st.name(), st.getDisplayText());
        }

        return sortOrderList;
    }

    @RemoteMethod
    public int deleteRule(String ruleId) {
        int result = -1;

        try {
            String store = utilityService.getStoreId();
            String username = utilityService.getUsername();
            FacetSort rule = new FacetSort(ruleId, store);
            rule.setLastModifiedBy(username);
            result = daoService.deleteFacetSort(rule);
            if (result > 0) {
                RuleStatus ruleStatus = new RuleStatus();
                ruleStatus.setRuleTypeId(RuleEntity.FACET_SORT.getCode());
                ruleStatus.setRuleRefId(ruleId);
                ruleStatus.setStoreId(store);
                daoService.updateRuleStatusDeletedInfo(ruleStatus, username);
            }
        } catch (DaoException e) {
            logger.error("Failed during deleteRule()", e);
        }

        return result;
    }

    @RemoteMethod
    public int updateRule(String ruleId, String name, String sortType, Map<String, String[]> facetGroupItems, Map<String, String> sortOrders) {
        int result = -1;

        try {
            String store = utilityService.getStoreId();
            String username = utilityService.getUsername();
            FacetSort facetSort = new FacetSort(ruleId, name, "", sortType, store);
            facetSort.setLastModifiedBy(username);
            result = daoService.updateFacetSort(facetSort);

            if (MapUtils.isNotEmpty(facetGroupItems)) {
                FacetGroup facetGroup = new FacetGroup();

                for (Map.Entry<String, String[]> entry : facetGroupItems.entrySet()) {
                    String facetGroupId = entry.getKey();
                    String[] arrFacetGroupItems = entry.getValue();

                    facetGroup.setId(facetGroupId);
                    facetGroup.setSortType(SortType.get(sortOrders.get(facetGroupId)));
                    facetGroup.setLastModifiedBy(username);
                    facetGroup.setStoreId(facetSort.getStoreId());
                    facetGroup.setRuleId(ruleId);
                    result += daoService.updateFacetGroup(facetGroup);

                    FacetGroup updatedFacetGroup = new FacetGroup(ruleId, facetGroupId);
                    SearchCriteria<FacetGroup> criteria = new SearchCriteria<FacetGroup>(updatedFacetGroup);

                    //TODO create daoService getFacetGroup by ruleId and facetGroupId
                    RecordSet<FacetGroup> facets = daoService.searchFacetGroup(criteria, MatchType.MATCH_ID);

                    if (facets != null && facets.getTotalSize() > 0) {
                        updatedFacetGroup = facets.getList().get(0);
                        clearFacetGroupItem(facetGroupId);
                        result += addFacetGroupItems(ruleId, updatedFacetGroup, arrFacetGroupItems);
                    }
                }
            }
        } catch (DaoException e) {
            logger.error("Failed during updateRule()", e);
        }

        return result;
    }

    public int addFacetGroupItems(String ruleId, FacetGroup facetGroup, String[] arrFacetGroupItems) {
        try {
            List<FacetGroupItem> facetGroupItems = new ArrayList<FacetGroupItem>();
            for (int i = 0; i < ArrayUtils.getLength(arrFacetGroupItems); i++) {
                FacetGroupItem facetGroupItem = new FacetGroupItem(facetGroup.getId(), "", arrFacetGroupItems[i], i + 1);
                facetGroupItem.setCreatedBy(utilityService.getUsername());
                facetGroupItem.setFacetGroupId(facetGroup.getId());
                facetGroupItem.setStoreId(utilityService.getStoreId());
                facetGroupItem.setRuleId(ruleId);
                facetGroupItem.setFacetGroup(facetGroup);

                facetGroupItems.add(facetGroupItem);
            }

            return daoService.addFacetGroupItems(facetGroupItems);
        } catch (DaoException e) {
            logger.error("Failed during addFacetGroupItems()", e);
        }
        return -1;
    }

    @RemoteMethod
    public RecordSet<FacetSort> getAllRule(String name, int page, int itemsPerPage) {
        logger.info(String.format("%s %d %d", name, page, itemsPerPage));
        try {
            String store = utilityService.getStoreId();
            FacetSort facetSort = new FacetSort("", name, store);
            SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(facetSort, page, itemsPerPage);
            return daoService.searchFacetSort(criteria, MatchType.LIKE_NAME);
        } catch (DaoException e) {
            logger.error("Failed during getAllRule()", e);
        }
        return null;
    }

    public FacetSort getRule(FacetSort facetSort) {
        try {
            return daoService.getFacetSort(facetSort);
        } catch (DaoException e) {
            logger.error("Failed during getRule()", e);
        }
        return null;
    }

    @RemoteMethod
    public FacetSort getRuleById(String ruleId) {
        String store = utilityService.getStoreId();
        return getRule(new FacetSort(ruleId, store));
    }

    @RemoteMethod
    public FacetSort getRuleByNameAndType(String ruleName, String ruleType) {
        String store = utilityService.getStoreId();
        return getRule(new FacetSort(ruleName, RuleType.get(ruleType), null, new Store(store)));
    }

    @RemoteMethod
    public FacetSort getRuleByName(String storeId, String ruleName) {
        FacetSort facetSort = null;
        StoreKeyword sk = new StoreKeyword(storeId, ruleName);

        try {
            if (StringUtils.isNotEmpty(sk.getKeywordTerm())) {
                facetSort = daoService.getFacetSort(new FacetSort(sk.getKeywordTerm(), RuleType.KEYWORD, null, sk.getStore()));
            }

            if (facetSort == null) {
                facetSort = daoService.getFacetSort(new FacetSort(ruleName, RuleType.TEMPLATE, null, sk.getStore()));
            }
        } catch (DaoException e) {
            logger.error("Failed to fetch rule id for Facet Sort rule : " + ruleName, e);
        }

        return facetSort;
    }

    @RemoteMethod
    public RecordSet<FacetGroup> getAllFacetGroup(String ruleId) {
        try {
            FacetGroup facetGroup = new FacetGroup(ruleId, "");
            SearchCriteria<FacetGroup> criteria = new SearchCriteria<FacetGroup>(facetGroup);
            return daoService.searchFacetGroup(criteria, MatchType.MATCH_ID);
        } catch (DaoException e) {
            logger.error("Failed during getAllFacetGroup()", e);
        }
        return null;
    }

    public int addAllFacetGroup(String ruleId) {
        int facetGroupAdded = 0;

        for (FacetGroupType facetGroupType : FacetGroupType.values()) {
            facetGroupAdded += addFacetGroup(ruleId, facetGroupType.getDisplayText(), facetGroupType.getDisplayText(), null, Integer.parseInt(facetGroupType.toString())) > 0 ? 1 : 0;
        }

        return facetGroupAdded;
    }

    @RemoteMethod
    public int addFacetGroup(String ruleId, String name, String facetGroupType, String sortType, Integer sequence) {
        int result = -1;

        try {
            String username = utilityService.getUsername();
            FacetGroup facetGroup = new FacetGroup(ruleId, name, facetGroupType, sortType, sequence);
            facetGroup.setCreatedBy(username);
            return daoService.addFacetGroup(facetGroup);
        } catch (DaoException e) {
            logger.error("Failed during addFacetGroup()", e);
        }

        return result;
    }

    @RemoteMethod
    public RecordSet<FacetGroupItem> getAllFacetGroupItem(String ruleId, String facetGroupId) {
        try {
            FacetGroupItem facetGroupItem = new FacetGroupItem(ruleId, facetGroupId);
            SearchCriteria<FacetGroupItem> criteria = new SearchCriteria<FacetGroupItem>(facetGroupItem);
            return daoService.searchFacetGroupItem(criteria, MatchType.MATCH_ID);
        } catch (DaoException e) {
            logger.error("Failed during getAllFacetGroup()", e);
        }
        return null;
    }

    public int addFacetGroupItem(FacetGroupItem facetGroupItem) {
        int result = -1;
        try {
            result = daoService.addFacetGroupItem(facetGroupItem);
        } catch (DaoException e) {
            logger.error("Failed during addFacetGroupItem()", e);
        }
        return result;
    }

    @RemoteMethod
    public int addAllFacetGroupItem(String facetGroupId, String facetGroupItems) {
        int result = -1;

        String[] arrFacetGroupItem = StringUtils.split(facetGroupItems, ',');
        int arrFacetGroupItemSize = ArrayUtils.getLength(arrFacetGroupItem);

        if (arrFacetGroupItemSize > 0) {
            clearFacetGroupItem(facetGroupId);
            for (int i = 0; i < arrFacetGroupItemSize; i++) {
                addFacetGroupItem(new FacetGroupItem(facetGroupId, "", arrFacetGroupItem[i], i + 1));
            }
        }
        return result;
    }

    @RemoteMethod
    public int addSingleFacetGroupItem(String facetGroupId, String name, Integer sequence) {
        FacetGroupItem facetGroupItem = new FacetGroupItem(facetGroupId, "", name, sequence);
        facetGroupItem.setCreatedBy(utilityService.getUsername());
        return addFacetGroupItem(facetGroupItem);
    }

    @RemoteMethod
    public int updateFacetGroupItem(String memberId, String name, Integer sequence) {
        int result = -1;

        try {
            String username = utilityService.getUsername();
            FacetGroupItem facetGroupItem = new FacetGroupItem(memberId, name, sequence);
            facetGroupItem.setLastModifiedBy(username);
            return daoService.updateFacetGroupItem(facetGroupItem);
        } catch (DaoException e) {
            logger.error("Failed during deleteFacetGroupItem()", e);
        }

        return result;
    }

    @RemoteMethod
    public int deleteFacetGroupItem(String memberId) {
        int result = -1;

        try {
            String username = utilityService.getUsername();
            FacetGroupItem facetGroupItem = new FacetGroupItem(memberId);
            facetGroupItem.setLastModifiedBy(username);
            return daoService.deleteFacetGroupItem(facetGroupItem);
        } catch (DaoException e) {
            logger.error("Failed during deleteFacetGroupItem()", e);
        }

        return result;
    }

    @RemoteMethod
    public int clearFacetGroupItem(String facetGroupId) {
        int result = -1;

        try {
            String username = utilityService.getUsername();
            FacetGroup facetGroup = new FacetGroup(facetGroupId);
            facetGroup.setLastModifiedBy(username);
            return daoService.clearFacetGroupItem(facetGroup);
        } catch (DaoException e) {
            logger.error("Failed during clearFacetGroupItem()", e);
        }

        return result;
    }
}