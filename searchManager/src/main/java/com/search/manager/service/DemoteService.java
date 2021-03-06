package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.Comment;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.Store;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;

@Service(value = "demoteService")
@RemoteProxy(
        name = "DemoteServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "demoteService"))
public class DemoteService extends RuleService {

    private static final Logger logger =
            LoggerFactory.getLogger(DemoteService.class);
    @Autowired
    private DaoService daoService;
    @Autowired
    private UtilityService utilityService;
    @Autowired
    private JodaDateTimeUtil jodaDateTimeUtil;
    @Autowired
    private DateAndTimeUtils dateAndTimeUtils;
    @Autowired
    @Qualifier("ruleStatusServiceSp")
    private RuleStatusService ruleStatusService;
    
    @Override
    public RuleEntity getRuleEntity() {
        return RuleEntity.DEMOTE;
    }

    @RemoteMethod
    public int updateItem(String keyword, String memberId, int position, String comment, String expiryDate, String condition) {
        String storeId = utilityService.getStoreId();
        int changes = 0;

        DemoteResult demote = new DemoteResult();
        demote.setStoreKeyword(new StoreKeyword(storeId, keyword));
        demote.setMemberId(memberId);
        try {
            demote = daoService.getDemoteItem(demote);
        } catch (DaoException e) {
            demote = null;
        }

        if (demote == null) {
            return changes;
        }

        DemoteProduct demoteProduct = new DemoteProduct(demote);

        if (position != demote.getLocation()) {
            changes += ((update(keyword, memberId, position, null) > 0) ? 1 : 0);
        }

        if (StringUtils.isNotBlank(comment)) {
            changes += ((addRuleComment(keyword, memberId, comment) > 0) ? 1 : 0);
        }

        if (StringUtils.isNotBlank(condition)) {
            changes += ((update(keyword, memberId, position, condition) > 0) ? 1 : 0);
        }

        if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(jodaDateTimeUtil.formatFromStorePattern(storeId, demoteProduct.getExpiryDate(), JodaPatternType.DATE)))) {
            changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0) ? 1 : 0);
        }

        return changes;
    }

    @RemoteMethod
    public int updateFacet(String keyword, String memberId, int position, String comment, String expiryDate, Map<String, List<String>> filter) {
        int changes = 0;

        String storeId = utilityService.getStoreId();

        DemoteResult demote = new DemoteResult();
        demote.setStoreKeyword(new StoreKeyword(storeId, keyword));
        demote.setMemberId(memberId);

        RedirectRuleCondition rrCondition = new RedirectRuleCondition();
        rrCondition.setStoreId(storeId);
        rrCondition.setFilter(filter);
        utilityService.setFacetTemplateValues(rrCondition);

        try {
            demote = daoService.getDemoteItem(demote);
        } catch (DaoException e) {
            demote = null;
        }

        if (demote == null) {
            return changes;
        }

        DemoteProduct demoteProduct = new DemoteProduct(demote);

        if (position != demote.getLocation()) {
            changes += ((update(keyword, memberId, position, null) > 0) ? 1 : 0);
        }

        if (StringUtils.isNotBlank(comment)) {
            try {
                addComment(comment, demote);
                changes++;
            } catch (DaoException e) {
                logger.error("Error adding comment in updateFacet()", e);
            }
        }

        if (!rrCondition.getCondition().equals(demote.getCondition().getCondition())) {
            changes += ((update(keyword, memberId, position, rrCondition.getCondition()) > 0) ? 1 : 0);
        }

        if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), jodaDateTimeUtil.formatFromStorePattern(storeId, demoteProduct.getExpiryDate(), JodaPatternType.DATE))) {
            changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0) ? 1 : 0);
        }

        return changes;
    }

    private int addItem(String keyword, String edp, RedirectRuleCondition condition, int sequence, String expiryDate, String comment, MemberTypeEntity entity) {
        int result = -1;
        try {
            logger.info(String.format("%s %s %s %d, %s %s", keyword, edp, condition != null ? condition.getCondition() : "", sequence, expiryDate, comment));
            String storeId = utilityService.getStoreId();
            String userName = utilityService.getUsername();
            daoService.addKeyword(new StoreKeyword(storeId, keyword)); // TODO: What if keyword is not added?

            DemoteResult e = new DemoteResult(new StoreKeyword(storeId, keyword));
            e.setLocation(sequence);
            e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : jodaDateTimeUtil.toDateTimeFromStorePattern(storeId, expiryDate, JodaPatternType.DATE));
            e.setCreatedBy(userName);
            e.setComment(utilityService.formatComment(comment));
            e.setDemoteEntity(entity);
            switch (entity) {
                case PART_NUMBER:
                    e.setEdp(edp);
                    break;
                case FACET:
                    e.setCondition(condition);
                    break;
            }

            result = daoService.addDemoteResult(e);
            if (result > 0) {
                if (!StringUtils.isBlank(comment)) {
                    addComment(comment, e);
                }
                try {
                    // TODO: add checking if existing rule status?
                    ruleStatusService.add(new RuleStatus(RuleEntity.DEMOTE, DAOUtils.getStoreId(e.getStoreKeyword()),
                            keyword, keyword, userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
                } catch (CoreServiceException ex) {
                    logger.error("Failed to create rule status for demote: " + keyword);
                }
            }
        } catch (DaoException e) {
            logger.error("Failed during demoteItem()", e);
        }
        return result;
    }

    @RemoteMethod
    public int add(String keyword, String memberTypeId, String value, int sequence, String expiryDate, String comment) {
        MemberTypeEntity memberTypeEntity = MemberTypeEntity.valueOf(memberTypeId);
        return addItem(keyword, memberTypeEntity == MemberTypeEntity.PART_NUMBER ? value : null,
                memberTypeEntity == MemberTypeEntity.FACET ? new RedirectRuleCondition(value) : null,
                sequence, expiryDate, comment, memberTypeEntity);
    }

    @RemoteMethod
    public Map<String, List<String>> addItemToRuleUsingPartNumber(String keyword, int sequence, String expiryDate, String comment, String[] partNumbers) {

        logger.info(String.format("%s %s %d", keyword, partNumbers, sequence));
        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();

        ArrayList<String> passedList = new ArrayList<String>();
        ArrayList<String> failedList = new ArrayList<String>();

        resultMap.put("PASSED", passedList);
        resultMap.put("FAILED", failedList);

        String server = utilityService.getServerName();
        String store = utilityService.getStoreId();

        int count = 0;
        comment = comment.replaceAll("%%timestamp%%", dateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
        comment = comment.replaceAll("%%commentor%%", utilityService.getUsername());

        sequence = (sequence == 0) ? 1 : sequence;
        for (String partNumber : partNumbers) {
            count = 0;
            try {
                String edp = daoService.getEdpByPartNumber(server, store, keyword, StringUtils.trim(partNumber));
                if (StringUtils.isNotBlank(edp)) {
                    count = addItem(keyword, edp, null, sequence++, expiryDate, comment, MemberTypeEntity.PART_NUMBER);
                }
            } catch (DaoException de) {
                logger.error("Failed during addItemToRuleUsingPartNumber()", de);
            }
            if (count > 0) {
                passedList.add(StringUtils.trim(partNumber));
            } else {
                failedList.add(StringUtils.trim(partNumber));
            }
        }
        return resultMap;
    }

    @RemoteMethod
    public int addFacetRule(String keyword, int sequence, String expiryDate, String comment, Map<String, List<String>> filter) {
        RedirectRuleCondition rrCondition = new RedirectRuleCondition(filter);
        rrCondition.setStoreId(utilityService.getStoreId());
        utilityService.setFacetTemplateValues(rrCondition);
        return addItem(keyword, null, rrCondition, sequence, expiryDate, comment, MemberTypeEntity.FACET);
    }

    @RemoteMethod
    public int updateExpiryDate(String keyword, String memberId, String expiryDate) {
        try {
            logger.info(String.format("%s %s %s", keyword, memberId, expiryDate));
            String storeId = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(storeId, keyword));
            e.setMemberId(memberId);
            e.setExpiryDate(jodaDateTimeUtil.toDateTimeFromStorePattern(storeId, expiryDate, JodaPatternType.DATE));
            e.setLastModifiedBy(utilityService.getUsername());
            return daoService.updateDemoteResultExpiryDate(e);
        } catch (DaoException e) {
            logger.error("Failed during updateExpiryDate()", e);
        }
        return -1;
    }

    @RemoteMethod
    public int addComment(String keyword, String memberId, String comment) {
        try {
            logger.info(String.format("%s %s %s", keyword, memberId, comment));
            String store = utilityService.getStoreId();

            if (StringUtils.isNotBlank(comment)) {
                comment = comment.replaceAll("%%timestamp%%", dateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
                comment = comment.replaceAll("%%commentor%%", utilityService.getUsername());
            }

            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            e.setMemberId(memberId);
            e.setLastModifiedBy(utilityService.getUsername());
            e.setComment(utilityService.formatComment(comment));
            return daoService.appendDemoteResultComment(e);
        } catch (DaoException e) {
            logger.error("Failed during addComment()", e);
        }
        return -1;
    }

    @RemoteMethod
    public int deleteItemInRule(String keyword, String memberId) {
        try {
            logger.info(String.format("%s %s", keyword, memberId));
            String store = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            e.setMemberId(memberId);
            e.setLastModifiedBy(utilityService.getUsername());
            e = daoService.getDemoteItem(e);
            return daoService.deleteDemoteResult(e);
        } catch (DaoException e) {
            logger.error("Failed during removeDemote()", e);
        }
        return -1;
    }

    @RemoteMethod
    public int update(String keyword, String memberId, int sequence, String condition) {
        try {
            logger.info(String.format("%s %s %d", keyword, memberId, sequence));
            DemoteResult demote = new DemoteResult();
            demote.setStoreKeyword(new StoreKeyword(utilityService.getStoreId(), keyword));
            demote.setMemberId(memberId);
            try {
                demote = daoService.getDemoteItem(demote);
            } catch (DaoException e) {
                demote = null;
            }
            if (demote != null) {
                if (!StringUtils.isBlank(condition)) {
                	RedirectRuleCondition rr = new RedirectRuleCondition(condition);
                	rr.setStoreId(utilityService.getStoreId());
                    demote.setCondition(rr);
                }
                demote.setLocation(sequence);
                demote.setLastModifiedBy(utilityService.getUsername());
                return daoService.updateDemoteResult(demote);
            }
        } catch (DaoException e) {
            logger.error("Failed during updateDemote()", e);
        }
        return -1;
    }

    @RemoteMethod
    public RecordSet<DemoteProduct> getProducts(String filter, String keyword, int page, int itemsPerPage) {

        if (StringUtils.isBlank(filter) || StringUtils.equalsIgnoreCase("all", filter)) {
            return getAllProducts(keyword, page, itemsPerPage);
        }

        if (StringUtils.equalsIgnoreCase("active", filter)) {
            return getActiveProducts(keyword, page, itemsPerPage);
        }

        if (StringUtils.equalsIgnoreCase("expired", filter)) {
            return getExpiredProducts(keyword, page, itemsPerPage);
        }

        return null;
    }

    @RemoteMethod
    public DemoteProduct getProductByEdp(String keyword, String edp) {

        RecordSet<DemoteProduct> products = getAllProducts(keyword, 0, 100);
        DemoteProduct product = null;
        for (DemoteProduct prod : products.getList()) {
            if (prod.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER && prod.getEdp().equals(StringUtils.trim(edp))) {
                product = prod;
                break;
            }
        }
        return product;
    }

    @RemoteMethod
    public RecordSet<DemoteProduct> getAllProducts(String keyword, int page, int itemsPerPage) {
        RecordSet<DemoteProduct> result = null;
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();

            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null, page, itemsPerPage);
            result = daoService.getDemotedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getAllDemotedProducts()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<DemoteProduct> getAllProductsIgnoreKeyword(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();

            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null, page, itemsPerPage);
            return daoService.getDemotedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getAllDemotedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<DemoteProduct> getActiveProducts(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, DateTime.now(), null, page, itemsPerPage);
            return daoService.getDemotedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getActiveProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<DemoteProduct> getExpiredProducts(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, DateTime.now(), page, itemsPerPage);
            return daoService.getDemotedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getExpiredProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<DemoteProduct> getNoExpiryProducts(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null, page, itemsPerPage);
            return daoService.getNoExpiryDemotedProducts(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getNoExpiryProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public Integer getTotalProductInRule(String keyword) {
        try {
            logger.info(String.format("%s", keyword));
            String store = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null, null, null);
            return daoService.getDemoteResultCount(criteria);
        } catch (DaoException e) {
            logger.error("Failed during getTotalProductInRule()", e);
        }
        return Integer.valueOf(0);
    }

    @RemoteMethod
    public DemoteProduct getProduct(String keyword, String memberId) {
        try {
            logger.info(String.format("%s %s", keyword, memberId));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            DemoteResult e = new DemoteResult();
            e.setStoreKeyword(new StoreKeyword(store, keyword));
            e.setMemberId(memberId);
            return daoService.getDemotedProduct(server, e);
        } catch (DaoException e) {
            logger.error("Failed during getDemotedProduct()", e);
        }
        return null;
    }

    @RemoteMethod
    public String getComment(String keyword, String memberId) {
        DemoteProduct demotedProduct = getProduct(keyword, memberId);
        if (demotedProduct == null) {
            return StringUtils.EMPTY;
        }

        return StringUtils.trimToEmpty(demotedProduct.getComment());
    }

    @RemoteMethod
    public int clearRule(String keyword) {
        try {
            logger.info(String.format("%s", keyword));
            return daoService.clearDemoteResult(new StoreKeyword(utilityService.getStoreId(), keyword));
        } catch (DaoException e) {
            logger.error("Failed during clearRule()", e);
        }
        return -1;
    }

    public DaoService getDaoService() {
        return daoService;
    }

    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }

    private int addComment(String comment, DemoteResult e) throws DaoException {
        Comment com = new Comment();
        com.setComment(comment);
        com.setUsername(utilityService.getUsername());
        com.setReferenceId(e.getMemberId());
        com.setRuleTypeId(RuleEntity.DEMOTE.getCode());
        com.setStore(new Store(utilityService.getStoreId()));
        return daoService.addComment(com);
    }

    @RemoteMethod
    public int addRuleComment(String keyword, String memberId, String pComment) {
        int result = -1;
        String store = utilityService.getStoreId();
        try {
            DemoteResult demote = new DemoteResult();
            demote.setStoreKeyword(new StoreKeyword(store, keyword));
            demote.setMemberId(memberId);
            demote = daoService.getDemoteItem(demote);
            if (demote != null) {
                demote.setComment(pComment);
                demote.setLastModifiedBy(utilityService.getUsername());
                daoService.updateDemoteResultComment(demote);
                result = addComment(pComment, demote);
            }
        } catch (DaoException e) {
            logger.error("Failed during addRuleItemComment()", e);
        }
        return result;
    }
}