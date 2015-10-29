package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
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
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.SearchHelper;

@Service("elevateService")
@RemoteProxy(
        name = "ElevateServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "elevateService"))
public class ElevateService extends RuleService {

    private static final Logger logger =
            LoggerFactory.getLogger(ElevateService.class);
    @Autowired
    private DaoService daoService;
    @Autowired
	private UtilityService utilityService;
    @Autowired
    private JodaDateTimeUtil jodaDateTimeUtil;
    @Autowired
    private DateAndTimeUtils dateAndTimeUtils;
    @Autowired
    private SearchHelper searchHelper;
    @Autowired
    @Qualifier("ruleStatusServiceSp")
    private RuleStatusService ruleStatusService;
    
    @Override
    public RuleEntity getRuleEntity() {
        return RuleEntity.ELEVATE;
    }

    @RemoteMethod
    public int updateElevateItem(String keyword, String memberId, int position, String comment, String expiryDate, String condition) {
        int changes = 0;
        String storeId = utilityService.getStoreId();
        ElevateResult elevate = new ElevateResult();
        elevate.setStoreKeyword(new StoreKeyword(storeId, keyword));
        elevate.setMemberId(memberId);

        try {
            elevate = daoService.getElevateItem(elevate);
        } catch (DaoException e) {
            elevate = null;
        }

        if (elevate == null) {
            return changes;
        }

        ElevateProduct elevateProduct = new ElevateProduct(elevate);

        if (position != elevate.getLocation()) {
            changes += ((updateElevate(keyword, memberId, position, null) > 0) ? 1 : 0);
        }

        if (StringUtils.isNotBlank(comment)) {
            changes += ((addComment(keyword, memberId, comment) > 0) ? 1 : 0);
        }

        if (StringUtils.isNotBlank(condition)) {
            changes += ((updateElevate(keyword, memberId, position, condition) > 0) ? 1 : 0);
        }

        if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(jodaDateTimeUtil.formatFromStorePattern(storeId, elevateProduct.getExpiryDate(), JodaPatternType.DATE)))) {
            changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0) ? 1 : 0);
        }

        return changes;
    }

    @RemoteMethod
    public int updateElevateFacet(String keyword, String memberId, int position, String comment, String expiryDate, Map<String, List<String>> filter) {
        int changes = 0;

        String storeId = utilityService.getStoreId();
        ElevateResult elevate = new ElevateResult();
        elevate.setStoreKeyword(new StoreKeyword(storeId, keyword));
        elevate.setMemberId(memberId);

        RedirectRuleCondition rrCondition = new RedirectRuleCondition();
        rrCondition.setStoreId(storeId);
        rrCondition.setFilter(filter);
        utilityService.setFacetTemplateValues(rrCondition);

        try {
            elevate = daoService.getElevateItem(elevate);
        } catch (DaoException e) {
            elevate = null;
        }

        if (elevate == null) {
            return changes;
        }

        ElevateProduct elevateProduct = new ElevateProduct(elevate);

        if (position != elevate.getLocation()) {
            changes += ((updateElevate(keyword, memberId, position, null) > 0) ? 1 : 0);
        }

        if (StringUtils.isNotBlank(comment)) {
            try {
                addComment(comment, elevate);
                changes++;
            } catch (DaoException e) {
                logger.error("Error adding comment in updateElevateFacet()", e);
            }
        }

        if (!rrCondition.getCondition().equals(elevate.getCondition().getCondition())) {
            changes += ((updateElevate(keyword, memberId, position, rrCondition.getCondition()) > 0) ? 1 : 0);
        }

        if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(jodaDateTimeUtil.formatFromStorePattern(storeId, elevateProduct.getExpiryDate(), JodaPatternType.DATE)))) {
            changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0) ? 1 : 0);
        }

        return changes;
    }

    private int addItem(String keyword, String edp, RedirectRuleCondition condition, int sequence, String expiryDate, String comment, MemberTypeEntity entity, boolean forceAdd) {
        int result = -1;
        try {
            logger.info(String.format("%s %s %s %d %s %s", keyword, edp, condition != null ? condition.getCondition() : "", sequence, expiryDate, comment));
            String storeId = utilityService.getStoreId();
            String userName = utilityService.getUsername();
            daoService.addKeyword(new StoreKeyword(storeId, keyword)); // TODO: What if keyword is not added?

            ElevateResult e = new ElevateResult(new StoreKeyword(storeId, keyword));
            e.setLocation(sequence);
            e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : jodaDateTimeUtil.toDateTimeFromStorePattern(storeId, expiryDate, JodaPatternType.DATE));
            e.setCreatedBy(userName);
            e.setComment(utilityService.formatComment(comment));
            e.setElevateEntity(entity);
            e.setForceAdd(forceAdd);
            switch (entity) {
                case PART_NUMBER:
                    e.setEdp(edp);
                    break;
                case FACET:
                    e.setCondition(condition);
                    break;
            }

            result = daoService.addElevateResult(e);
            if (result > 0) {
                if (!StringUtils.isBlank(comment)) {
                    addComment(comment, e);
                }
                if (e.isForceAdd()) {
                    result = 2;
                }
                try {
                    // TODO: add checking if existing rule status?
                    ruleStatusService.add(new RuleStatus(RuleEntity.ELEVATE, DAOUtils.getStoreId(e.getStoreKeyword()),
                            keyword, keyword, userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
                } catch (CoreServiceException de) {
                    logger.error("Failed to create rule status for elevate: " + keyword);
                }
            }
        } catch (DaoException e) {
            logger.error("Failed during addItem()", e);
        }
        return result;
    }

    @RemoteMethod
    public int addProductItemForceAdd(String keyword, String edp, int sequence, String expiryDate, String comment) {
        return addItem(keyword, edp, null, sequence, expiryDate, comment, MemberTypeEntity.PART_NUMBER, true);
    }

    @RemoteMethod
    public int addProductItem(String keyword, String edp, int sequence, String expiryDate, String comment) {
        return addItem(keyword, edp, null, sequence, expiryDate, comment, MemberTypeEntity.PART_NUMBER, false);
    }

    @RemoteMethod
    public Map<String, List<String>> addItemToRuleUsingPartNumber(String keyword, int sequence, String expiryDate, String comment, String[] partNumbers) {
        logger.info(String.format("%s %s %d", keyword, partNumbers, sequence));
        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
        final String storeName = utilityService.getStoreId();
        final String serverName = utilityService.getServerName();
        ArrayList<String> passedList = new ArrayList<String>();
        ArrayList<String> failedList = new ArrayList<String>();
        resultMap.put("PASSED", passedList);
        resultMap.put("FAILED", failedList);

        String store = utilityService.getStoreId();
        int count = 0;
        comment = comment.replaceAll("%%timestamp%%", dateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
        comment = comment.replaceAll("%%commentor%%", utilityService.getUsername());

        sequence = (sequence == 0) ? 1 : sequence;
        for (String partNumber : partNumbers) {
            count = 0;
            if (searchHelper.verifyProductId(serverName, storeName, partNumber)) {
	        	// OPSTRACK - no more DPNo > EDP conversion - the partNumbers list contains Opstrack_ProductID
	        	count = addItem(keyword, partNumber, null, sequence++, expiryDate, comment, MemberTypeEntity.PART_NUMBER, false);
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
        return addItem(keyword, null, rrCondition, sequence, expiryDate, comment, MemberTypeEntity.FACET, false);
    }

    @RemoteMethod
    public int updateExpiryDate(String keyword, String memberId, String expiryDate) {
        int result = -1;
        try {
            logger.info(String.format("%s %s %s", keyword, memberId, expiryDate));
            String storeId = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(storeId, keyword), memberId);
            e = daoService.getElevateItem(e);
            if (e != null) {
                e.setExpiryDate(jodaDateTimeUtil.toDateTimeFromStorePattern(storeId, expiryDate, JodaPatternType.DATE));
                e.setLastModifiedBy(utilityService.getUsername());
                result = daoService.updateElevateResultExpiryDate(e);
            }
        } catch (DaoException e) {
            logger.error("Failed during updateExpiryDate()", e);
        } catch (Exception e) {
            logger.error("Failed during updateExpiryDate()", e);
        }


        return result;
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

            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword), memberId);
            e.setLastModifiedBy(utilityService.getUsername());
            e.setComment(utilityService.formatComment(comment));
            daoService.appendElevateResultComment(e);
            return addComment(comment, e);
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
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword), memberId);
            e.setLastModifiedBy(utilityService.getUsername());
            e = daoService.getElevateItem(e);
            return daoService.deleteElevateResult(e);
        } catch (DaoException e) {
            logger.error("Failed during removeElevate()", e);
        }
        return -1;
    }

    @RemoteMethod
    public int updateElevate(String keyword, String memberId, int sequence, String condition) {
        try {
            logger.info(String.format("%s %s %d", keyword, memberId, sequence));
            ElevateResult elevate = new ElevateResult(new StoreKeyword(utilityService.getStoreId(), keyword), memberId);

            try {
                elevate = daoService.getElevateItem(elevate);
            } catch (DaoException e) {
                elevate = null;
            }
            if (elevate != null) {
                if (!StringUtils.isBlank(condition)) {
                	RedirectRuleCondition rrc = new RedirectRuleCondition(condition);
                	rrc.setStoreId(utilityService.getStoreId());
                    elevate.setCondition(rrc);
                }
                elevate.setLocation(sequence);
                elevate.setLastModifiedBy(utilityService.getUsername());
                return daoService.updateElevateResult(elevate);
            }
        } catch (DaoException e) {
            logger.error("Failed during updateElevate()", e);
        }
        return -1;
    }

    @RemoteMethod
    public int updateElevateForceAdd(String keyword, String memberId, boolean forceAddFlag) {
        try {
            logger.info(String.format("%s %s %b", keyword, memberId, forceAddFlag));
            ElevateResult elevate = new ElevateResult(new StoreKeyword(utilityService.getStoreId(), keyword), memberId);

            try {
                elevate = daoService.getElevateItem(elevate);
            } catch (DaoException e) {
                elevate = null;
            }
            if (elevate != null) {
                elevate.setForceAdd(forceAddFlag);
                elevate.setLastModifiedBy(utilityService.getUsername());
                return daoService.updateElevateResult(elevate);
            }
        } catch (DaoException e) {
            logger.error("Failed during updateElevate()", e);
        }
        return -1;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getProducts(String filter, String keyword, int page, int itemsPerPage) {

        RecordSet<ElevateProduct> rs = null;
        if (StringUtils.isBlank(filter) || StringUtils.equalsIgnoreCase("all", filter)) {
            rs = getAllElevatedProductsIgnoreKeyword(keyword, page, itemsPerPage);
        } else if (StringUtils.equalsIgnoreCase("active", filter)) {
            rs = getActiveElevatedProductsIgnoreKeyword(keyword, page, itemsPerPage);
        } else if (StringUtils.equalsIgnoreCase("expired", filter)) {
            rs = getExpiredElevatedProductsIgnoreKeyword(keyword, page, itemsPerPage);
        }

        if (rs != null && CollectionUtils.isNotEmpty(rs.getList())) {
            utilityService.setFacetTemplateValues(rs.getList());
        }

        return rs;
    }

    @RemoteMethod
    public ElevateProduct getProductByEdp(String keyword, String edp) {

        RecordSet<ElevateProduct> products = getAllElevatedProducts(keyword, 0, 100);
        ElevateProduct product = null;
        for (ElevateProduct prod : products.getList()) {
            if (prod.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER && prod.getEdp().equals(StringUtils.trim(edp))) {
                product = prod;
                break;
            }
        }
        return product;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getAllElevatedProducts(String keyword, int page, int itemsPerPage) {
        RecordSet<ElevateProduct> result = null;
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();

            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null, page, itemsPerPage);
            result = daoService.getElevatedProducts(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getAllElevatedProducts()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getAllElevatedProductsIgnoreKeyword(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();

            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null, page, itemsPerPage);
            return daoService.getElevatedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getAllElevatedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getActiveElevatedProducts(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, DateTime.now(), null, page, itemsPerPage);
            return daoService.getElevatedProducts(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getActiveElevatedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getActiveElevatedProductsIgnoreKeyword(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, DateTime.now(), null, page, itemsPerPage);
            return daoService.getElevatedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getActiveElevatedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getExpiredElevatedProducts(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, DateTime.now(), page, itemsPerPage);
            return daoService.getElevatedProducts(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getExpiredElevatedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getExpiredElevatedProductsIgnoreKeyword(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, DateTime.now().minusDays(1), page, itemsPerPage);
            return daoService.getElevatedProductsIgnoreKeyword(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getExpiredElevatedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<ElevateProduct> getNoExpiryElevatedProducts(String keyword, int page, int itemsPerPage) {
        try {
            logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null, page, itemsPerPage);
            return daoService.getNoExpiryElevatedProducts(server, criteria);
        } catch (DaoException e) {
            logger.error("Failed during getNoExpiryElevatedProducts()", e);
        }
        return null;
    }

    @RemoteMethod
    public Integer getTotalProductInRule(String keyword) {
        try {
            logger.info(String.format("%s", keyword));
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null, null, null);
            return daoService.getElevateResultCount(criteria);
        } catch (DaoException e) {
            logger.error("Failed during getTotalProductInRule()", e);
        }
        return null;
    }

    @RemoteMethod
    public ElevateProduct getElevatedProduct(String keyword, String memberId) {
        try {
            logger.info(String.format("%s %s", keyword, memberId));
            String server = utilityService.getServerName();
            String store = utilityService.getStoreId();
            ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
            e.setMemberId(memberId);
            return daoService.getElevatedProduct(server, e);
        } catch (DaoException e) {
            logger.error("Failed during getElevatedProduct()", e);
        }
        return null;
    }

    @RemoteMethod
    public String getComment(String keyword, String memberId) {
        ElevateProduct elevatedProduct = getElevatedProduct(keyword, memberId);
        if (elevatedProduct == null) {
            return StringUtils.EMPTY;
        }

        return StringUtils.trimToEmpty(elevatedProduct.getComment());
    }

    @RemoteMethod
    public int clearRule(String keyword) {
        try {
            logger.info(String.format("%s", keyword));
            return daoService.clearElevateResult(new StoreKeyword(utilityService.getStoreId(), keyword));
        } catch (DaoException e) {
            logger.error("Failed during clearRule()", e);
        }
        return -1;
    }

    private int addComment(String comment, ElevateResult e) throws DaoException {
        Comment com = new Comment();
        com.setComment(comment);
        com.setUsername(utilityService.getUsername());
        com.setReferenceId(e.getMemberId());
        com.setRuleTypeId(RuleEntity.ELEVATE.getCode());
        com.setStore(new Store(utilityService.getStoreId()));
        return daoService.addComment(com);
    }

    @RemoteMethod
    public int addRuleComment(String keyword, String memberId, String pComment) {
        int result = -1;
        String store = utilityService.getStoreId();

        try {
            ElevateResult elevate = new ElevateResult();
            elevate.setStoreKeyword(new StoreKeyword(store, keyword));
            elevate.setMemberId(memberId);
            elevate = daoService.getElevateItem(elevate);
            if (elevate != null) {
                elevate.setComment(pComment);
                elevate.setLastModifiedBy(utilityService.getUsername());
                daoService.updateElevateResultComment(elevate);
                result = addComment(pComment, elevate);
            }
        } catch (DaoException e) {
            logger.error("Failed during addRuleItemComment()", e);
        }
        return result;
    }

    @RemoteMethod
    public Map<String, Boolean> isRequireForceAdd(final String keyword, String[] memberIds) {
        ExecutorService execService = Executors.newFixedThreadPool(10);
        final String storeName = utilityService.getStoreId();
        final String serverName = utilityService.getServerName();
        final Map<String, Boolean> map = new HashMap<String, Boolean>();
        int tasks = 0;

        try {
            ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(execService);
            for (int i = 0, size = memberIds.length; i < size; i++) {
                final String memberId = memberIds[i];
                ElevateResult elevate = new ElevateResult(new StoreKeyword(storeName, keyword), memberId);
                elevate = daoService.getElevateItem(elevate);
                if (elevate != null) {
                    String condition = "";
                    if (MemberTypeEntity.FACET.equals(elevate.getEntity())) {
                        RedirectRuleCondition rr = elevate.getCondition();
                        utilityService.setFacetTemplateValues(rr);
                        condition = rr.getConditionForSolr();
                    } else {
                        condition = String.format("SystemProductID:%s", elevate.getEdp());
                    }
                    final String filter = condition;
                    completionService.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            boolean forceAdd = false;
                            try {
                                forceAdd = searchHelper.isForceAddCondition(serverName, storeName, keyword, filter);
                            } catch (Exception e) {
                                logger.error("Failed to get force add status for condition: " + filter, e);
                            }
                            map.put(memberId, forceAdd);
                            return true;
                        }
                    });
                    tasks++;
                }
            }

            while (tasks > 0) {
                try {
                    completionService.take();
                } catch (InterruptedException e) {
                    logger.error("Failed to get if force add required for condition", e);
                }
                tasks--;
            }
        } catch (DaoException e) {
            logger.error("Failed during isRequireForceAdd()", e);
        } finally {
            if (execService != null) {
                execService.shutdown();
            }
        }
        return map;
    }

    @RemoteMethod
    public Map<String, Boolean> isItemInNaturalResult(final String keyword, String[] memberIds, String[] conditions) {

        ExecutorService execService = Executors.newFixedThreadPool(10);
        final String storeName = utilityService.getStoreId();
        final String serverName = utilityService.getServerName();
        final Map<String, Boolean> map = new HashMap<String, Boolean>();
        int tasks = 0;

        try {
            ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(execService);
            for (int i = 0, size = memberIds.length; i < size; i++) {
                final String memberId = memberIds[i];
                final String condition = conditions[i];
                if (StringUtils.isNotBlank(condition)) {
                    completionService.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            boolean forceAdd = false;
                            try {
                                forceAdd = searchHelper.isForceAddCondition(serverName, storeName, keyword, condition);
                            } catch (Exception e) {
                                logger.error("Failed to get force add status for condition: " + condition, e);
                            }
                            map.put(memberId, forceAdd);
                            return true;
                        }
                    });
                    tasks++;
                }
            }

            while (tasks > 0) {
                try {
                    completionService.take();
                } catch (InterruptedException e) {
                    logger.error("Failed to get if force add required for condition", e);
                }
                tasks--;
            }

        } finally {
            if (execService != null) {
                execService.shutdown();
            }
        }
        return map;
    }
}
