package com.search.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.service.SolrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("deploymentRuleServiceSolrImpl")
public class DeploymentRuleServiceSolrImpl implements DeploymentRuleService {

    private static final Logger logger =
            LoggerFactory.getLogger(DeploymentRuleServiceSolrImpl.class);
    
    @Autowired
    private SolrService solrService;
    @Autowired
    private DaoService daoService;
    @Autowired
    private DaoService daoServiceStg;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private SimpleMailMessage mailDetails;

    public void setSolrService(SolrService solrService) {
        this.solrService = solrService;
    }

    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }

    public void setDaoServiceStg(DaoService daoServiceStg) {
        this.daoServiceStg = daoServiceStg;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMailDetails(SimpleMailMessage mailDetails) {
        this.mailDetails = mailDetails;
    }

    @Override
    public Map<String, Boolean> publishElevateRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);
        List<ElevateResult> elevatedList = null;
        ElevateResult elevateFilter = new ElevateResult();

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String key : keywords) {
                try {
                    StoreKeyword storeKeyword = new StoreKeyword(store, key);
                    daoService.clearElevateResult(storeKeyword); // prod

                    // retrieve staging data then push to prod
                    daoService.addKeyword(storeKeyword);
                    elevateFilter.setStoreKeyword(storeKeyword);
                    SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
                            elevateFilter, null, null, 0, 0);
                    elevatedList = daoServiceStg.getElevateResultList(criteria)
                            .getList();

                    if (elevatedList != null && elevatedList.size() > 0) {
                        for (ElevateResult elevateResult : elevatedList) {
                            daoService.addElevateResult(elevateResult);
                        }
                    }

                    try {
                        solrService.resetElevateRules(storeKeyword);
                    } catch (Exception e) {
                        errorMsg.append(" - " + key + "\n");
                        hasError = true;
                    }

                    keywordStatus.put(key, true);
                } catch (Exception e) {
                    logger.error("Failed to publish elevate rule: " + key, e);
                    keywordStatus.put(key, false);
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following elevate rules: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitElevateRule()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported elevate rules: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> publishExcludeRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);
        List<ExcludeResult> excludedList = null;
        ExcludeResult excludeFilter = new ExcludeResult();

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String key : keywords) {
                try {
                    StoreKeyword storeKeyword = new StoreKeyword(store, key);
                    daoService.clearExcludeResult(storeKeyword); // prod

                    // retrieve staging data then push to prod
                    daoService.addKeyword(storeKeyword);
                    excludeFilter.setStoreKeyword(storeKeyword);
                    SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
                            excludeFilter, null, null, 0, 0);
                    excludedList = daoServiceStg.getExcludeResultList(criteria)
                            .getList();

                    if (excludedList != null && excludedList.size() > 0) {
                        for (ExcludeResult excludeResult : excludedList) {
                            daoService.addExcludeResult(excludeResult);
                        }
                    }

                    try {
                        solrService.resetExcludeRules(storeKeyword);
                    } catch (Exception e) {
                        errorMsg.append(" - " + key + "\n");
                        hasError = true;
                    }

                    keywordStatus.put(key, true);
                } catch (Exception e) {
                    logger.error("Failed to publish exclude rule: " + key, e);
                    keywordStatus.put(key, false);
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following exclude rules: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitExcludeRule()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported exclude rules: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> publishDemoteRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);
        List<DemoteResult> demotedList = null;
        DemoteResult demoteFilter = new DemoteResult();

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String key : keywords) {
                try {
                    StoreKeyword storeKeyword = new StoreKeyword(store, key);
                    daoService.clearDemoteResult(storeKeyword); // prod

                    // retrieve staging data then push to prod
                    daoService.addKeyword(storeKeyword);
                    demoteFilter.setStoreKeyword(storeKeyword);
                    SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
                            demoteFilter, null, null, 0, 0);
                    demotedList = daoServiceStg.getDemoteResultList(criteria)
                            .getList();

                    if (demotedList != null && demotedList.size() > 0) {
                        for (DemoteResult demoteResult : demotedList) {
                            daoService.addDemoteResult(demoteResult);
                        }
                    }

                    try {
                        solrService.resetDemoteRules(storeKeyword);
                    } catch (Exception e) {
                        errorMsg.append(" - " + key + "\n");
                        hasError = true;
                    }

                    keywordStatus.put(key, true);
                } catch (Exception e) {
                    logger.error("Failed to publish demote rule: " + key, e);
                    keywordStatus.put(key, false);
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following demote rules: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitDemoteRule()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported demote rules: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> publishFacetSortRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String key : keywords) {
                FacetSort facetSort = new FacetSort(key, store);
                int result = -1;

                daoService.deleteFacetSort(facetSort); // prod

                // retrieve staging data then push to prod
                FacetSort addFacetSort = daoServiceStg.getFacetSort(facetSort);

                if (addFacetSort != null) {
                    try {
                        result += daoService.addFacetSort(addFacetSort); // prod

                        // add facet groups
                        FacetGroup facetGroup = new FacetGroup(key, "");
                        SearchCriteria<FacetGroup> criteria = new SearchCriteria<FacetGroup>(
                                facetGroup);
                        RecordSet<FacetGroup> addFacetSortGroups = daoServiceStg
                                .searchFacetGroup(criteria, MatchType.MATCH_ID);

                        if (addFacetSortGroups != null) {
                            List<FacetGroup> addFsGs = addFacetSortGroups
                                    .getList();

                            for (FacetGroup fg : addFsGs) {
                                result += daoService.addFacetGroup(fg); // prod
                            }
                        }

                        // add facet group items
                        FacetGroupItem facetGroupItem = new FacetGroupItem(key,
                                "");
                        SearchCriteria<FacetGroupItem> criteria2 = new SearchCriteria<FacetGroupItem>(
                                facetGroupItem);
                        RecordSet<FacetGroupItem> addFsGroupItems = daoServiceStg
                                .searchFacetGroupItem(criteria2,
                                MatchType.MATCH_ID);

                        if (addFsGroupItems != null) {
                            result += daoService
                                    .addFacetGroupItems(addFsGroupItems
                                    .getList()); // prod
                        }

                        try {
                            if (!solrService.resetFacetSortRuleById(new Store(
                                    store), key)) {
                                errorMsg.append(" - " + key + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(key, (result > 0));
                    } catch (DaoException e) {
                        keywordStatus.put(key, false);
                        logger.error("Failed during addRule()", e);

                        try {
                            daoService
                                    .deleteFacetSort(new FacetSort(key, store));
                        } catch (DaoException de) {
                            logger.error(
                                    "Unable to complete process, need to manually delete rule",
                                    de);
                        }
                    }
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following facet sort rules: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitFacetSortRule()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported facet sort rules: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> publishRedirectRulesMap(String store,
            List<String> ruleIds) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(ruleIds);

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String id : ruleIds) {
                try {
                    RedirectRule redirectRule = new RedirectRule();
                    redirectRule.setRuleId(id);
                    redirectRule.setStoreId(store);
                    daoService.deleteRedirectRule(redirectRule); // prod

                    // retrieve staging data then push to prod
                    RedirectRule addRedirectRule = daoServiceStg
                            .getRedirectRule(redirectRule);

                    if (addRedirectRule != null) {
                        List<String> searchTerms = addRedirectRule
                                .getSearchTerms();

                        if (CollectionUtils.isNotEmpty(searchTerms)) {
                            for (String keyword : searchTerms) {
                                daoService.addKeyword(new StoreKeyword(store,
                                        keyword));
                            }
                        }

                        daoService.addRedirectRule(addRedirectRule); // prod

                        // add redirect keyword
                        if (CollectionUtils.isNotEmpty(searchTerms)) {
                            for (String keyword : searchTerms) {
                                RedirectRule rule = new RedirectRule();
                                rule.setRuleId(addRedirectRule.getRuleId());
                                rule.setStoreId(addRedirectRule.getStoreId());
                                rule.setSearchTerm(keyword);
                                rule.setLastModifiedBy("SYSTEM");
                                daoService.addRedirectKeyword(rule);
                            }
                        }

                        // add rule condition
                        RedirectRule rule = new RedirectRule();
                        rule.setRuleId(addRedirectRule.getRuleId());
                        rule.setStoreId(addRedirectRule.getStoreId());
                        RecordSet<RedirectRuleCondition> conditionSet = daoServiceStg
                                .getRedirectConditions(new SearchCriteria<RedirectRule>(
                                rule, null, null, 0, 0));
                        if (conditionSet != null
                                && conditionSet.getTotalSize() > 0) {
                            for (RedirectRuleCondition condition : conditionSet
                                    .getList()) {
                                condition.setLastModifiedBy("SYSTEM");
                                daoService.addRedirectCondition(condition);
                            }
                        }

                        try {
                            if (!solrService.resetRedirectRuleById(new Store(
                                    store), id)) {
                                errorMsg.append(" - " + id + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + id + "\n");
                            hasError = true;
                        }
                    }
                    keywordStatus.put(id, true);
                } catch (Exception e) {
                    keywordStatus.put(id, false);
                    logger.error("Failed to publish redirect rule: " + id, e);
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following redirect rules: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitRedirectRule()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported redirect rules: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> publishRankingRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String key : keywords) {
                try {
                    Relevancy relevancy = new Relevancy();
                    relevancy.setRelevancyId(key);
                    relevancy.setStore(new Store(store));
                    daoService.deleteRelevancy(relevancy); // prod

                    // retrieve staging data then push to prod
                    Relevancy addRelevancy = daoServiceStg
                            .getRelevancyDetails(relevancy);

                    if (addRelevancy != null) {
                        // add relevancy
                        daoService.addRelevancy(addRelevancy);
                        // add relevancy keywords
                        RecordSet<RelevancyKeyword> relevancyKeywords = daoServiceStg
                                .getRelevancyKeywords(addRelevancy);
                        if (relevancyKeywords.getTotalSize() > 0) {
                            for (RelevancyKeyword rk : relevancyKeywords
                                    .getList()) {
                                daoService.addKeyword(new StoreKeyword(store,
                                        rk.getKeyword().getKeywordId()));
                                daoService.addRelevancyKeyword(rk);
                            }
                        }
                        // save relevancy fields
                        RelevancyField rf = new RelevancyField();
                        rf.setRelevancy(addRelevancy);

                        Map<String, String> relevancyFields = addRelevancy
                                .getParameters();
                        if (relevancyFields != null) {
                            for (String field : relevancyFields.keySet()) {
                                String value = relevancyFields.get(field);
                                if (StringUtils.isNotBlank(value)) {
                                    rf.setFieldName(field);
                                    rf.setFieldValue(value);
                                    daoService.addRelevancyField(rf);
                                }
                            }
                        }
                    }

                    try {
                        if (!solrService.resetRelevancyRuleById(
                                new Store(store), key)) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }
                    } catch (Exception e) {
                        errorMsg.append(" - " + key + "\n");
                        hasError = true;
                    }

                    keywordStatus.put(key, true);
                } catch (Exception e) {
                    logger.error("Failed to publish relevancy rule: " + key, e);
                    keywordStatus.put(key, false);
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following relevancy rules: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitRelevancyRule()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported relevancy rules: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> publishDidYouMeanRulesMap(String store,
            List<String> list) {
        // copying of updated spell files will be handled by rsync cron job
        Map<String, Boolean> resultMap = getKeywordStatusMap(list);
        for (String id : resultMap.keySet()) {
            resultMap.put(id, Boolean.TRUE);
        }
        return resultMap;
    }

    @Override
    public Map<String, Boolean> publishBannerRulesMap(String store,
            List<String> ruleIds) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(ruleIds);

        try {
            boolean hasError = false;
            StringBuffer errorMsg = new StringBuffer();

            for (String id : ruleIds) {
                try {
                    BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
                    BannerRule bannerRule = new BannerRule();
                    bannerRule.setStoreId(store);
                    bannerRule.setRuleId(id);
                    bannerRuleItemFilter.setRule(bannerRule);

                    daoService.deleteBannerRuleItem(bannerRuleItemFilter); // prod
                    daoService.deleteBannerRule(bannerRule); // prod

                    // retrieve staging data then push to prod
                    SearchCriteria<BannerRuleItem> criteria = new SearchCriteria<BannerRuleItem>(
                            bannerRuleItemFilter, 0, 0);
                    List<BannerRuleItem> bannerRuleItems = daoServiceStg
                            .searchBannerRuleItem(criteria).getList();

                    if (bannerRuleItems != null && bannerRuleItems.size() > 0) {
                        daoService.addBannerRule(bannerRuleItems.get(0)
                                .getRule());
                        for (BannerRuleItem bannerRuleItem : bannerRuleItems) {
                            daoService.addBannerImagePath(bannerRuleItem
                                    .getImagePath());
                            daoService.addBannerRuleItem(bannerRuleItem);
                        }
                    }

                    try {
                        solrService.resetBannerRuleItemsByRuleId(new Store(
                                store), id);
                    } catch (Exception e) {
                        errorMsg.append(" - " + id + "\n");
                        hasError = true;
                    }

                    keywordStatus.put(id, true);
                } catch (Exception e) {
                    logger.error("Failed to publish banner rule item: " + id, e);
                    keywordStatus.put(id, false);
                }
            }

            if (hasError) {
                sendIndexStatus(
                        "Failed to index the following banner rule items: \n"
                        + errorMsg.toString(), store);
            }

            if (!solrService.commitBannerRuleItem()) {
                StringBuffer msg = new StringBuffer(
                        "Failed to commit the following imported banner rule items: \n");
                for (String key : keywordStatus.keySet()) {
                    boolean status = keywordStatus.get(key);
                    if (status) {
                        msg.append(" - " + key + "\n");
                    }
                }
                sendIndexStatus(msg.toString(), store);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public boolean loadElevateRules(String store) {
        try {
            solrService.loadElevateRules(new Store(store));
        } catch (DaoException e) {
            logger.error("", e);
        }
        return false;
    }

    @Override
    public boolean loadExcludeRules(String store) {
        try {
            solrService.loadExcludeRules(new Store(store));
        } catch (DaoException e) {
            logger.error("", e);
        }
        return false;
    }

    @Override
    public boolean loadDemoteRules(String store) {
        try {
            solrService.loadDemoteRules(new Store(store));
        } catch (DaoException e) {
            logger.error("", e);
        }
        return false;
    }

    @Override
    public boolean loadFacetSortRules(String store) {
        try {
            solrService.loadFacetSortRules(new Store(store));
        } catch (DaoException e) {
            logger.error("", e);
        }
        return false;
    }

    @Override
    public boolean loadRedirectRules(String store) {
        try {
            solrService.loadRedirectRules(new Store(store));
        } catch (DaoException e) {
            logger.error("", e);
        }
        return false;
    }

    @Override
    public boolean loadRankingRules(String store) {
        try {
            solrService.loadRelevancyRules(new Store(store));
        } catch (DaoException e) {
            logger.error("", e);
        }
        return false;
    }

    @Override
    public Map<String, Boolean> unpublishElevateRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            if (CollectionUtils.isNotEmpty(keywords)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String key : keywords) {
                    try {
                        StoreKeyword storeKeyword = new StoreKeyword(store, key);
                        daoService.clearElevateResult(storeKeyword); // prod

                        try {
                            if (!solrService.deleteElevateRules(storeKeyword)) {
                                errorMsg.append(" - " + key + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(key, true);
                    } catch (Exception e) {
                        logger.error(
                                "Failed to unpublish elevate rule: " + key, e);
                        keywordStatus.put(key, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following elevate rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitElevateRule()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish elevate rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> unpublishExcludeRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            if (CollectionUtils.isNotEmpty(keywords)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String key : keywords) {
                    try {
                        StoreKeyword storeKeyword = new StoreKeyword(store, key);
                        daoService.clearExcludeResult(storeKeyword); // prod

                        try {
                            if (!solrService.deleteExcludeRules(storeKeyword)) {
                                errorMsg.append(" - " + key + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(key, true);
                    } catch (Exception e) {
                        logger.error(
                                "Failed to unpublish exclude rule: " + key, e);
                        keywordStatus.put(key, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following exclude rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitExcludeRule()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish exclude rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> unpublishDemoteRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            if (CollectionUtils.isNotEmpty(keywords)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String key : keywords) {
                    try {
                        StoreKeyword storeKeyword = new StoreKeyword(store, key);
                        daoService.clearDemoteResult(storeKeyword); // prod

                        try {
                            if (!solrService.deleteDemoteRules(storeKeyword)) {
                                errorMsg.append(" - " + key + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(key, true);
                    } catch (Exception e) {
                        logger.error("Failed to unpublish demote rule: " + key,
                                e);
                        keywordStatus.put(key, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following demote rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitDemoteRule()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish demote rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> unpublishFacetSortRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            if (CollectionUtils.isNotEmpty(keywords)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String key : keywords) {
                    try {
                        int result = -1;

                        FacetSort facetSort = new FacetSort();
                        facetSort.setRuleId(key);
                        facetSort.setStore(new Store(store));
                        result = daoService.deleteFacetSort(facetSort); // prod

                        try {
                            if (!solrService.deleteFacetSortRuleById(new Store(
                                    store), key)) {
                                errorMsg.append(" - " + key + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(key, (result > 0));
                    } catch (Exception e) {
                        logger.error("Failed to unpublish facet sort rule: "
                                + key, e);
                        keywordStatus.put(key, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following facet sort rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitFacetSortRule()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish facet sort rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> unpublishRedirectRulesMap(String store,
            List<String> ruleIds) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(ruleIds);

        try {
            if (CollectionUtils.isNotEmpty(ruleIds)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String id : ruleIds) {
                    try {
                        RedirectRule delRel = new RedirectRule();
                        delRel.setRuleId(id);
                        delRel.setStoreId(store);
                        // get list of keywords for ranking rule
                        List<StoreKeyword> sks = new ArrayList<StoreKeyword>();
                        RedirectRule rule = new RedirectRule();
                        rule.setRuleId(id);
                        rule.setStoreId(store);
                        SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
                                rule, null, null, 0, 0);

                        for (StoreKeyword keyword : daoService
                                .getRedirectKeywords(criteria,
                                MatchType.MATCH_ID, ExactMatch.SIMILAR)
                                .getList()) {
                            sks.add(keyword);
                        }
                        daoService.deleteRedirectRule(delRel); // prod

                        try {
                            if (!solrService.deleteRedirectRuleById(new Store(
                                    store), id)) {
                                errorMsg.append(" - " + id + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + id + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(id, true);
                    } catch (Exception e) {
                        logger.error(
                                "Failed to unpublish redirect rule: " + id, e);
                        keywordStatus.put(id, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following redirect rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitRedirectRule()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish redirect rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> unpublishRankingRulesMap(String store,
            List<String> keywords) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

        try {
            if (CollectionUtils.isNotEmpty(keywords)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String key : keywords) {
                    try {
                        Relevancy relevancy = new Relevancy();
                        relevancy.setRelevancyId(key);
                        relevancy.setStore(new Store(store));

                        // get list of keywords for ranking rule
                        List<StoreKeyword> storeKeywords = new ArrayList<StoreKeyword>();

                        for (RelevancyKeyword keyword : daoService
                                .getRelevancyKeywords(relevancy).getList()) {
                            storeKeywords.add(new StoreKeyword(store, keyword
                                    .getKeyword().getKeywordId()));
                        }

                        daoService.deleteRelevancy(relevancy); // prod

                        try {
                            if (!solrService.deleteRelevancyRuleById(new Store(
                                    store), key)) {
                                errorMsg.append(" - " + key + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + key + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(key, true);
                    } catch (Exception e) {
                        logger.error("Failed to unpublish relevancy rule: "
                                + key, e);
                        keywordStatus.put(key, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following relevancy rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitRelevancyRule()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish relevancy rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    @Override
    public Map<String, Boolean> unpublishBannerRulesMap(String store,
            List<String> ruleIds) {
        Map<String, Boolean> keywordStatus = getKeywordStatusMap(ruleIds);

        try {
            if (CollectionUtils.isNotEmpty(ruleIds)) {
                boolean hasError = false;
                StringBuffer errorMsg = new StringBuffer();

                for (String id : ruleIds) {
                    try {
                        BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
                        BannerRule bannerRule = new BannerRule();
                        bannerRule.setStoreId(store);
                        bannerRule.setRuleId(id);
                        bannerRuleItemFilter.setRule(bannerRule);

                        daoService.deleteBannerRuleItem(bannerRuleItemFilter); // prod
                        daoService.deleteBannerRule(bannerRule); // prod

                        try {
                            if (!solrService.deleteBannerRuleItemsByRuleId(
                                    new Store(store), id)) {
                                errorMsg.append(" - " + id + "\n");
                                hasError = true;
                            }
                        } catch (Exception e) {
                            errorMsg.append(" - " + id + "\n");
                            hasError = true;
                        }

                        keywordStatus.put(id, true);
                    } catch (Exception e) {
                        logger.error("Failed to unpublish banner rule: " + id,
                                e);
                        keywordStatus.put(id, false);
                    }
                }

                if (hasError) {
                    sendIndexStatus(
                            "Failed to unpublish the following banner rules: \n"
                            + errorMsg.toString(), store);
                }

                if (!solrService.commitBannerRuleItem()) {
                    StringBuffer msg = new StringBuffer(
                            "Failed to commit the following unpublish banner rules: \n");
                    for (String key : keywordStatus.keySet()) {
                        boolean status = keywordStatus.get(key);
                        if (status) {
                            msg.append(" - " + key + "\n");
                        }
                    }
                    sendIndexStatus(msg.toString(), store);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return keywordStatus;
    }

    private Map<String, Boolean> getKeywordStatusMap(List<String> keywords) {
        Map<String, Boolean> keywordStatus = new HashMap<String, Boolean>();

        for (String key : keywords) {
            keywordStatus.put(key, false);
        }

        return keywordStatus;
    }

    private void sendIndexStatus(String msg, String store) {
        logger.error(msg);
        String localhostname = "";

        try {
            localhostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("", e);
        }

        String subject = "[" + localhostname + ":SearchGuiWS] Rule Indexing - "
                + store;

        SimpleMailMessage message = mailDetails;

        message.setSubject(subject);
        message.setText(msg);

        mailSender.send(message);
    }
}