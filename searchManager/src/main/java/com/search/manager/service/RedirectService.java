package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.ReplaceKeywordMessageType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRule.RedirectType;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.StoreKeyword;

@Service(value = "redirectService")
@RemoteProxy(
        name = "RedirectServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "redirectService"))
public class RedirectService extends RuleService {

    private static final Logger logger =
            LoggerFactory.getLogger(RedirectService.class);
    @Autowired
    private DaoService daoService;
    @Autowired
    private UtilityService utilityService;
    @Autowired
    @Qualifier("ruleStatusServiceSp")
    private RuleStatusService ruleStatusService;
    
    @Override
    public RuleEntity getRuleEntity() {
        return RuleEntity.QUERY_CLEANING;
    }

    private String addRuleAndGetId(String ruleName) {
        String ruleId = null;
        try {
            String store = utilityService.getStoreId();
            String userName = utilityService.getUsername();
            RedirectRule rule = new RedirectRule();
            rule.setRuleName(ruleName);
            rule.setRedirectType(RedirectType.FILTER);
            rule.setStoreId(store);
            rule.setCreatedBy(userName);
            if (daoService.addRedirectRule(rule) > 0) {
                ruleId = rule.getRuleId();
            }
            try {
                ruleStatusService.add(new RuleStatus(RuleEntity.QUERY_CLEANING, store, rule.getRuleId(), ruleName,
                        userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
            } catch (CoreServiceException de) {
                logger.error("Failed to create rule status for query cleaning: " + ruleName);
            }
        } catch (DaoException e) {
            logger.error("Failed during addRule()", e);
        }
        return ruleId;
    }

    @RemoteMethod
    public RedirectRule addRuleAndGetModel(String ruleName) {
        return getRule(addRuleAndGetId(ruleName));
    }

    @RemoteMethod
    public int updateRule(String ruleId, String ruleName, String description) {
        int result = -1;
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setRuleName(ruleName);
            rule.setDescription(description);
            rule.setStoreId(utilityService.getStoreId());
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.updateRedirectRule(rule);
        } catch (DaoException e) {
            logger.error("Failed during updateRule()", e);
        }
        return result;
    }

    @RemoteMethod
    public int updateRKMessageType(String ruleId, int type, String customText) {
        int result = -1;
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setReplaceKeywordMessageType(ReplaceKeywordMessageType.get(type));
            if (ReplaceKeywordMessageType.CUSTOM.getIntValue() == type) {
                rule.setReplaceKeywordMessageCustomText(customText);
            }
            rule.setStoreId(utilityService.getStoreId());
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.updateRedirectRule(rule);
        } catch (DaoException e) {
            logger.error("Failed during updateRule()", e);
        }
        return result;
    }

    @RemoteMethod
    public int setRedirectType(String ruleId, String redirectTypeId) {
        int result = -1;
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setRedirectTypeId(redirectTypeId);
            rule.setStoreId(utilityService.getStoreId());
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.updateRedirectRule(rule);
        } catch (DaoException e) {
            logger.error("Failed during setRedirectType()", e);
        }
        return result;
    }

    @RemoteMethod
    public int setIncludeKeyword(String ruleId, Boolean includeKeyword) {
        int result = -1;
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setIncludeKeyword(includeKeyword);
            rule.setStoreId(utilityService.getStoreId());
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.updateRedirectRule(rule);
        } catch (DaoException e) {
            logger.error("Failed during setIncludeKeyword()", e);
        }
        return result;
    }

    @RemoteMethod
    public int setChangeKeyword(String ruleId, String changeKeyword) {
        int result = -1;
        try {
            changeKeyword = StringUtils.trimToEmpty(changeKeyword);
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setStoreId(utilityService.getStoreId());
            rule.setChangeKeyword(changeKeyword);
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.updateRedirectRule(rule);
        } catch (DaoException e) {
            logger.error("Failed during setChangeKeyword()", e);
        }
        return result;
    }

    @RemoteMethod
    public int updateRedirectUrl(String ruleId, String redirectUrl) {
    	int result = -1;
    	
    	try {
    		redirectUrl = StringUtils.trimToEmpty(redirectUrl);
    		RedirectRule rule = new RedirectRule();
    		rule.setRuleId(ruleId);
    		rule.setStoreId(utilityService.getStoreId());
            rule.setRedirectUrl(redirectUrl);
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.updateRedirectRule(rule);
    	} catch (DaoException e) {
    		logger.error("Failed during setRedirectUrl", e);
    	}
    	return result;
    }
    
    @RemoteMethod
    public int deleteRule(String ruleId) {
        int result = -1;
        try {
            String username = utilityService.getUsername();
            String storeId = utilityService.getStoreId();
            RedirectRule rule = new RedirectRule(ruleId);
            result = daoService.deleteRedirectRule(rule);
            if (result > 0) {
                RuleStatus ruleStatus = new RuleStatus();
                ruleStatus.setRuleTypeId(RuleEntity.QUERY_CLEANING.getCode());
                ruleStatus.setRuleRefId(rule.getRuleId());
                ruleStatus.setStoreId(storeId);
                ruleStatusService.updateRuleStatusDeletedInfo(ruleStatus, username);
            }
        } catch (Exception e) {
            logger.error("Failed during deleteRule()", e);
        }
        return result;
    }
    
    @RemoteMethod
    public int deleteRuleByStore(String ruleId, String storeId) {
        int result = -1;
        try {
            String username = utilityService.getUsername();
            RedirectRule rule = new RedirectRule(ruleId);
            result = daoService.deleteRedirectRule(rule);
            if (result > 0) {
                RuleStatus ruleStatus = new RuleStatus();
                ruleStatus.setRuleTypeId(RuleEntity.QUERY_CLEANING.getCode());
                ruleStatus.setRuleRefId(rule.getRuleId());
                ruleStatus.setStoreId(storeId);
                ruleStatusService.updateRuleStatusDeletedInfo(ruleStatus, username);
            }
        } catch (Exception e) {
            logger.error("Failed during deleteRuleByStore()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<RedirectRule> getAllRule(String name, int page, int itemsPerPage) {
        try {
            RedirectRule redirectRule = new RedirectRule();
            redirectRule.setStoreId(utilityService.getStoreId());
            redirectRule.setRuleName(name);
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(redirectRule, null, null, page, itemsPerPage);
            return daoService.searchRedirectRule(criteria, MatchType.LIKE_NAME);
        } catch (DaoException e) {
            logger.error("Failed during searchRedirect()", e);
        }
        return null;
    }

    @RemoteMethod
    public boolean checkForRuleNameDuplicate(String ruleId, String ruleName) throws DaoException {
        RedirectRule redirectRule = new RedirectRule();
        redirectRule.setStoreId(utilityService.getStoreId());
        redirectRule.setRuleName(ruleName);
        SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(redirectRule, null, null, 0, 0);
        RecordSet<RedirectRule> set = daoService.searchRedirectRule(criteria, MatchType.LIKE_NAME);
        if (set.getTotalSize() > 0) {
            for (RedirectRule r : set.getList()) {
                if (StringUtils.equals(StringUtils.trim(ruleName), StringUtils.trim(r.getRuleName()))) {
                    if (StringUtils.isBlank(ruleId) || !StringUtils.equals(ruleId, r.getRuleId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @RemoteMethod
    public List<String> checkForRuleNameDuplicates(String[] ruleIds, String[] ruleNames) throws DaoException {
        List<String> duplicateRuleNames = new ArrayList<String>();
        for (int i = 0; i < ruleIds.length; i++) {
            String ruleName = ruleNames[i];
            if (checkForRuleNameDuplicate(ruleIds[i], ruleName)) {
                duplicateRuleNames.add(ruleName);
            }
        }
        return duplicateRuleNames;
    }

    @RemoteMethod
    public RedirectRule getRule(String ruleId) {
        try {
            RedirectRule redirectRule = new RedirectRule();
            redirectRule.setStoreId(utilityService.getStoreId());
            redirectRule.setRuleId(ruleId);
            return daoService.getRedirectRule(redirectRule);
        } catch (DaoException e) {
            logger.error("Failed during getRule()", e);
        }
        return null;
    }
    
    @RemoteMethod
    public RedirectRule getRuleByStore(String ruleId, String storeId) {
        try {
            RedirectRule redirectRule = new RedirectRule();
            redirectRule.setStoreId(storeId);
            redirectRule.setRuleId(ruleId);
            return daoService.getRedirectRule(redirectRule);
        } catch (DaoException e) {
            logger.error("Failed during getRuleByStore()", e);
        }
        return null;
    }

    @RemoteMethod
    public int addKeywordToRule(String ruleId, String keyword) {
        int result = -1;
        try {
            // add keyword in case it has not been added yet
            daoService.addKeyword(new StoreKeyword(utilityService.getStoreId(), keyword));
            RedirectRule rule = new RedirectRule();
            rule.setStoreId(utilityService.getStoreId());
            rule.setRuleId(ruleId);
            rule.setSearchTerm(keyword);
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.addRedirectKeyword(rule);
        } catch (DaoException e) {
            logger.error("Failed during addKeywordToRule()", e);
        }
        return result;
    }
    
    
    public int addKeywordToRuleByStore(String ruleId, String keyword, String storeId) {
        int result = -1;
        try {
            daoService.addKeyword(new StoreKeyword(storeId, keyword));
            RedirectRule rule = new RedirectRule();
            rule.setStoreId(storeId);
            rule.setRuleId(ruleId);
            rule.setSearchTerm(keyword);
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.addRedirectKeyword(rule);
        } catch (DaoException e) {
            logger.error("Failed during addKeywordToRuleByStore()", e);
        }
        return result;
    }

    @RemoteMethod
    public int deleteKeywordInRule(String ruleId, String searchTerm) {
        int result = -1;
        try {
            RedirectRule rule = new RedirectRule();
            rule.setStoreId(utilityService.getStoreId());
            rule.setRuleId(ruleId);
            rule.setSearchTerm(searchTerm);
            rule.setLastModifiedBy(utilityService.getUsername());
            result = daoService.deleteRedirectKeyword(rule);
        } catch (DaoException e) {
            logger.error("Failed during deleteKeywordInRule()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<RedirectRuleCondition> addCondition(String ruleId, Map<String, String[]> filter) {
        Map<String, List<String>> listFilter = new HashMap<String, List<String>>();

        for (Entry<String, String[]> entry : filter.entrySet()) {
            listFilter.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }

        return addRuleCondition(ruleId, listFilter);
    }

    public RecordSet<RedirectRuleCondition> addRuleCondition(String ruleId, Map<String, List<String>> filter) {
        try {
            String storeId = utilityService.getStoreId();
            RedirectRuleCondition rr = new RedirectRuleCondition();
            rr.setStoreId(storeId);
            rr.setRuleId(ruleId);
            rr.setStoreId(utilityService.getStoreId());
            rr.setFilter(filter);
            utilityService.setFacetTemplateValues(rr);
            if (daoService.addRedirectCondition(rr) > 0) {
                return getConditionInRule(ruleId, 0, 0);
            }

        } catch (DaoException e) {
            logger.error("Failed during addRuleCondition()", e);
        }
        return null;
    }
    
    public RecordSet<RedirectRuleCondition> addRuleConditionByStore(String ruleId, Map<String, List<String>> filter, String storeId) {
        try {
            RedirectRuleCondition rr = new RedirectRuleCondition();
            rr.setStoreId(storeId);
            rr.setRuleId(ruleId);
            rr.setFilter(filter);
            utilityService.setFacetTemplateValues(rr);
            if (daoService.addRedirectCondition(rr) > 0) {
                return getConditionInRuleByStore(ruleId, 0, 0, storeId);
            }
        } catch (DaoException e) {
            logger.error("Failed during addRuleCondition()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<RedirectRuleCondition> updateCondition(String ruleId, int sequenceNumber, Map<String, String[]> filter) {
        Map<String, List<String>> listFilter = new HashMap<String, List<String>>();

        for (Entry<String, String[]> entry : filter.entrySet()) {
            listFilter.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }

        return updateRuleCondition(ruleId, sequenceNumber, listFilter);
    }

    @RemoteMethod
    public RedirectRuleCondition convertMapToRedirectRuleCondition(Map<String, String[]> filter) {
        Map<String, List<String>> listFilter = new HashMap<String, List<String>>();
        for (Entry<String, String[]> entry : filter.entrySet()) {
            listFilter.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        RedirectRuleCondition rr = new RedirectRuleCondition(listFilter);
        rr.setStoreId(utilityService.getStoreId());
        utilityService.setFacetTemplateValues(rr);
        return rr;
    }

    private RecordSet<RedirectRuleCondition> updateRuleCondition(String ruleId, int sequenceNumber, Map<String, List<String>> filter) {
        try {
            RedirectRuleCondition rr = new RedirectRuleCondition(ruleId, sequenceNumber);
            rr.setStoreId(utilityService.getStoreId());
            rr.setFilter(filter);
            utilityService.setFacetTemplateValues(rr);
            int result = daoService.updateRedirectCondition(rr);

            if (result > 0) {
                return getConditionInRule(ruleId, 0, 0);
            }

        } catch (DaoException e) {
            logger.error("Failed during updateRuleCondition()", e);
        }

        return null;
    }

    @RemoteMethod
    public int deleteConditionInRule(String ruleId, int sequenceNumber) {
        int result = -1;
        try {
            RedirectRuleCondition rr = new RedirectRuleCondition(ruleId, sequenceNumber);
            result = daoService.deleteRedirectCondition(rr);
        } catch (DaoException e) {
            logger.error("Failed during deleteConditionInRule()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<Keyword> getAllKeywordInRule(String ruleId, String keyword, int page, int itemsPerPage) {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setStoreId(utilityService.getStoreId());
            rule.setSearchTerm(keyword);
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, page, itemsPerPage);
            RecordSet<StoreKeyword> storeKeyword = daoService.getRedirectKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR);
            List<Keyword> list = new ArrayList<Keyword>();
            if (storeKeyword.getTotalSize() > 0) {
                for (StoreKeyword sk : storeKeyword.getList()) {
                    list.add(sk.getKeyword());
                }
            }
            return new RecordSet<Keyword>(list, storeKeyword.getTotalSize());
        } catch (DaoException e) {
            logger.error("Failed during getAllKeywordInRule()", e);
        }
        return null;
    }
    
    @RemoteMethod
    public RecordSet<Keyword> getAllKeywordInRuleByStore(String ruleId, String keyword, int page, int itemsPerPage, String storeId) {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setStoreId(storeId);
            rule.setSearchTerm(keyword);
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, page, itemsPerPage);
            RecordSet<StoreKeyword> storeKeyword = daoService.getRedirectKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR);
            List<Keyword> list = new ArrayList<Keyword>();
            if (storeKeyword.getTotalSize() > 0) {
                for (StoreKeyword sk : storeKeyword.getList()) {
                    list.add(sk.getKeyword());
                }
            }
            return new RecordSet<Keyword>(list, storeKeyword.getTotalSize());
        } catch (DaoException e) {
            logger.error("Failed during getAllKeywordInRuleByStore()", e);
        }
        return null;
    }

    @RemoteMethod
    public RecordSet<RedirectRuleCondition> getConditionInRule(String ruleId, int page, int itemsPerPage) {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setStoreId(utilityService.getStoreId());
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, page, itemsPerPage);
            return daoService.getRedirectConditions(criteria);
        } catch (DaoException e) {
            logger.error("Failed during getConditionInRule()", e);
        }
        return null;
    }
    
    public RecordSet<RedirectRuleCondition> getConditionInRuleByStore(String ruleId, int page, int itemsPerPage, String storeId) {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setStoreId(storeId);
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, page, itemsPerPage);
            return daoService.getRedirectConditions(criteria);
        } catch (DaoException e) {
            logger.error("Failed during getConditionInRuleByStore()", e);
        }
        return null;
    }

    @RemoteMethod
    public int getTotalKeywordInRule(String ruleId) {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setRuleId(ruleId);
            rule.setStoreId(utilityService.getStoreId());
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, null, null);
            return daoService.getRedirectKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR).getTotalSize();
        } catch (DaoException e) {
            logger.error("Failed during getTotalKeywordInRule()", e);
        }
        return 0;
    }

    @RemoteMethod
    public int getTotalRuleUsedByKeyword(String keyword) {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setStoreId(utilityService.getStoreId());
            rule.setSearchTerm(keyword);
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, null, null);
            RecordSet<RedirectRule> count = daoService.getRedirectRules(criteria);
            if (count != null) {
                return count.getTotalSize();
            }
        } catch (DaoException e) {
            logger.error("Failed during getTotalRuleUsedByKeyword()", e);
        }
        return 0;
    }

    @RemoteMethod
    public RecordSet<RedirectRule> getAllRuleUsedByKeyword(String keyword) throws DaoException {
        try {
            RedirectRule rule = new RedirectRule();
            rule.setStoreId(utilityService.getStoreId());
            rule.setSearchTerm(keyword);
            SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, 0, 0);
            return daoService.getRedirectRules(criteria);
        } catch (DaoException e) {
            logger.error("Failed during getAllRuleUsedByKeyword()", e);
        }
        return null;
    }
    
    @RemoteMethod
    public int copyRedirectRule(String keyword, String storeId, String existingRuleId, int deleteExisting) {
    	int result = 0;
    	String copiedRedirectId = StringUtils.EMPTY;
    	RedirectRule copiedRedirect = null;
        try {
        	RedirectRule redirectToCopy = getRule(existingRuleId);
        	//delete existing data first before copying
        	if (deleteExisting > 0){
                RedirectRule redirectRule = new RedirectRule();
                redirectRule.setStoreId(storeId);
                redirectRule.setRuleName(redirectToCopy.getRuleName());
                SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(redirectRule, null, null, 0, 0);
                RecordSet<RedirectRule> set = daoService.searchRedirectRule(criteria, MatchType.LIKE_NAME);
                if (set.getTotalSize() > 0) {
                    for (RedirectRule r : set.getList()) {
                    	deleteRuleByStore(r.getRuleId(), storeId);
                    	break;
                    }
                }
        	}
        	
        	RedirectRule redirect = getRedirectRule(redirectToCopy, storeId);
            if (daoService.addRedirectRule(redirect) > 0) {
            	copiedRedirectId = redirect.getRuleId();
            }
            
            if (StringUtils.isNotEmpty(copiedRedirectId)){
            	result = 1;
            	copiedRedirect = getRuleByStore(copiedRedirectId, storeId);
                //add conditions
            	RecordSet<RedirectRuleCondition> existingRuleCondSet =  getConditionInRule(existingRuleId, 0, 0);
            	if (existingRuleCondSet.getTotalSize() > 0){
            		List<RedirectRuleCondition> existingRuleCondList = existingRuleCondSet.getList();
            		for (RedirectRuleCondition rrc : existingRuleCondList){
            			RedirectRuleCondition rr = getRedirectRuleCondition(rrc, copiedRedirectId, storeId);
                        daoService.addRedirectCondition(rr);
            		}
            	}
                
                //add keywords
            	RecordSet<Keyword> redirectToCopyKeywords = 
            			getAllKeywordInRule(existingRuleId, redirectToCopy.getSearchTerm(), 0, 0);
            	if (redirectToCopyKeywords.getTotalSize() > 0){
            		List<Keyword> keywordList = redirectToCopyKeywords.getList();
            		for (Keyword kw : keywordList){
            			addKeywordToRuleByStore(copiedRedirectId, kw.getKeyword(), storeId);
            		}
            	}            	
                try {
                    ruleStatusService.add(new RuleStatus(RuleEntity.QUERY_CLEANING, storeId, copiedRedirect.getRuleId(), copiedRedirect.getRuleName(),
                    		utilityService.getUsername(), utilityService.getUsername(), RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
                } catch (CoreServiceException de) {
                    logger.error("Failed to create rule status for redirect rule: " + copiedRedirect.getRuleName());
                }
            }
        } catch (DaoException e) {
            logger.error("Failed during copyRedirectRule()", e);
        }
    	return result;
    }
    
    private RedirectRule getRedirectRule(RedirectRule redirectToCopy, String storeId){
    	RedirectRule rule = new RedirectRule();
    	rule.setRuleName(redirectToCopy.getRuleName());
    	rule.setRedirectType(redirectToCopy.getRedirectType());
    	rule.setRedirectTypeId(redirectToCopy.getRedirectTypeId());
    	rule.setDescription(redirectToCopy.getDescription());
    	rule.setChangeKeyword(redirectToCopy.getChangeKeyword());
    	rule.setSearchTerm(redirectToCopy.getSearchTerm());
    	rule.setRedirectUrl(redirectToCopy.getRedirectUrl());
    	rule.setIncludeKeyword(redirectToCopy.getIncludeKeyword());
    	rule.setReplaceKeywordMessageType(redirectToCopy.getReplaceKeywordMessageType());
    	rule.setReplaceKeywordMessageCustomText(redirectToCopy.getReplaceKeywordMessageCustomText());
    	rule.setPriority(redirectToCopy.getPriority());
    	rule.setStoreId(storeId);
    	rule.setCreatedBy(utilityService.getUsername());        	
    	return rule;
    }
    
    private RedirectRuleCondition getRedirectRuleCondition(RedirectRuleCondition conditionToCopy, 
    		String ruleIdToUse, String storeId){
    	RedirectRuleCondition rrCondition = new RedirectRuleCondition();
    	rrCondition.setStoreId(storeId);
    	rrCondition.setRuleId(ruleIdToUse);
    	rrCondition.setCondition(conditionToCopy.getCondition());
    	rrCondition.setSequenceNumber(conditionToCopy.getSequenceNumber());
    	rrCondition.setFacetPrefix(utilityService.getStoreFacetPrefixByStore(storeId));
    	rrCondition.setFacetTemplate(utilityService.getStoreFacetTemplateByStore(storeId));
    	rrCondition.setFacetTemplateName(utilityService.getStoreFacetTemplateNameByStore(storeId));
    	return rrCondition;
    }
}
