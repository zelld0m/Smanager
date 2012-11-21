package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRule.RedirectType;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.StoreKeyword;

@Service(value = "redirectService")
@RemoteProxy(
		name = "RedirectServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "redirectService")
)
public class RedirectService extends RuleService{

	private static final Logger logger = Logger.getLogger(RedirectService.class);

	@Autowired private DaoService daoService;

	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.QUERY_CLEANING;
	}

	private String addRuleAndGetId(String ruleName) {
		try {
			String store = UtilityService.getStoreName();
			String userName = UtilityService.getUsername();
			RedirectRule rule = new RedirectRule();
			rule.setRuleName(ruleName);
			rule.setRedirectType(RedirectType.FILTER);
			rule.setStoreId(store);
			rule.setCreatedBy(userName);
			if (daoService.addRedirectRule(rule) > 0) {
				return rule.getRuleId();
			}
			try {
				daoService.addRuleStatus(new RuleStatus(RuleEntity.QUERY_CLEANING, store, rule.getRuleId(), ruleName, 
						userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
			} catch (DaoException de) {
				logger.error("Failed to create rule status for query cleaning: " + ruleName);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRule()",e);
		}
		return null;
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
			rule.setStoreId(UtilityService.getStoreName());
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during updateRule()",e);
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
			rule.setStoreId(UtilityService.getStoreName());
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during setRedirectType()",e);
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
			rule.setStoreId(UtilityService.getStoreName());
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during setIncludeKeyword()",e);
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
			rule.setStoreId(UtilityService.getStoreName());
			rule.setChangeKeyword(changeKeyword);
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during setChangeKeyword()",e);
		}
		return result;
	}

	@RemoteMethod
	public int deleteRule(RedirectRule rule) {
		int result = -1;
		try {
			result = daoService.deleteRedirectRule(rule);
			if (result > 0) {
				RuleStatus ruleStatus = new RuleStatus();
				ruleStatus.setRuleTypeId(RuleEntity.QUERY_CLEANING.getCode());
				ruleStatus.setRuleRefId(rule.getRuleId());
				ruleStatus.setStoreId(rule.getStoreId());
				daoService.processRuleStatus(ruleStatus, true);
			}
		} catch (DaoException e) {
			logger.error("Failed during deleteRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<RedirectRule> getAllRule(String name, int page, int itemsPerPage) {
		try {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setStoreId(UtilityService.getStoreName());
			redirectRule.setRuleName(name);
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(redirectRule, null, null,  page, itemsPerPage);
			return daoService.searchRedirectRule(criteria, MatchType.LIKE_NAME);
		} catch (DaoException e) {
			logger.error("Failed during searchRedirect()",e);
		}
		return null;
	}

	@RemoteMethod
	public boolean checkForRuleNameDuplicate(String ruleId, String ruleName) throws DaoException {
		RedirectRule redirectRule = new RedirectRule();
		redirectRule.setStoreId(UtilityService.getStoreName());
		redirectRule.setRuleName(ruleName);
		SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(redirectRule, null, null,  0, 0);
		RecordSet<RedirectRule> set = daoService.searchRedirectRule(criteria, MatchType.LIKE_NAME);
		if (set.getTotalSize() > 0) {
			for (RedirectRule r: set.getList()) {
				if (StringUtils.equals(StringUtils.trim(ruleName), StringUtils.trim(r.getRuleName()))) {
					if (StringUtils.isBlank(ruleId) || !StringUtils.equals(ruleId, r.getRuleId())){
						return true;						
					}
				}
			}
		}
		return false;
	}
	
	@RemoteMethod
	public RedirectRule getRule(String ruleId) {
		try {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setStoreId(UtilityService.getStoreName());
			redirectRule.setRuleId(ruleId);
			return daoService.getRedirectRule(redirectRule);
		} catch (DaoException e) {
			logger.error("Failed during getRule()",e);
		}
		return null;
	}

	@RemoteMethod
	public int addKeywordToRule(String ruleId, String keyword) {
		int result = -1;
		try {
			// add keyword in case it has not been added yet
			daoService.addKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
			RedirectRule rule = new RedirectRule();
			rule.setStoreId(UtilityService.getStoreName());
			rule.setRuleId(ruleId);
			rule.setSearchTerm(keyword);
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.addRedirectKeyword(rule);
		} catch (DaoException e) {
			logger.error("Failed during addKeywordToRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int deleteKeywordInRule(String ruleId, String searchTerm) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setStoreId(UtilityService.getStoreName());
			rule.setRuleId(ruleId);
			rule.setSearchTerm(searchTerm);
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.deleteRedirectKeyword(rule);
		} catch (DaoException e) {
			logger.error("Failed during deleteKeywordInRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<RedirectRuleCondition> addCondition(String ruleId, Map<String, String[]> filter) {
		Map<String, List<String>> listFilter = new HashMap<String, List<String>>();
		
		for(Entry<String, String[]> entry: filter.entrySet()){
			listFilter.put(entry.getKey(), Arrays.asList(entry.getValue()));
		}
		
		return addRuleCondition(ruleId, listFilter);
	}
	
	public RecordSet<RedirectRuleCondition> addRuleCondition(String ruleId, Map<String, List<String>> filter) {
		int result = -1;
		try {
			RedirectRuleCondition rr = new RedirectRuleCondition();
			rr.setRuleId(ruleId);
			rr.setStoreId(UtilityService.getStoreName());
			rr.setFilter(filter);
			result = daoService.addRedirectCondition(rr);
			
			if (result>0){
				return getConditionInRule(ruleId, 0, 0);
			}
			
		} catch (DaoException e) {
			logger.error("Failed during addRuleCondition()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<RedirectRuleCondition> updateCondition(String ruleId, int sequenceNumber, Map<String, String[]> filter) {
		Map<String, List<String>> listFilter = new HashMap<String, List<String>>();
		
		for(Entry<String, String[]> entry: filter.entrySet()){
			listFilter.put(entry.getKey(), Arrays.asList(entry.getValue()));
		}
		
		return updateRuleCondition(ruleId, sequenceNumber, listFilter);
	}
	
	public RecordSet<RedirectRuleCondition> updateRuleCondition(String ruleId, int sequenceNumber, Map<String, List<String>> filter) {
		try {
			RedirectRuleCondition rr = new RedirectRuleCondition(ruleId, sequenceNumber);
			rr.setStoreId(UtilityService.getStoreName());
			rr.setFilter(filter);
			int result = daoService.updateRedirectCondition(rr);
			
			if (result>0){
				return getConditionInRule(ruleId, 0, 0);
			}
			
		} catch (DaoException e) {
			logger.error("Failed during updateRuleCondition()",e);
		}
		
		return null;
	}

	@RemoteMethod
	public int deleteConditionInRule(String ruleId, int sequenceNumber) {
		int result = -1;
		try {
			RedirectRuleCondition rr = new RedirectRuleCondition(ruleId, sequenceNumber);
			rr.setStoreId(UtilityService.getStoreName());
			result = daoService.deleteRedirectCondition(rr);
		} catch (DaoException e) {
			logger.error("Failed during deleteConditionInRule()",e);
		}
		return result;
	}
	
	@RemoteMethod
	public RecordSet<Keyword> getAllKeywordInRule(String ruleId, String keyword, int page,int itemsPerPage) {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setStoreId(UtilityService.getStoreName());
			rule.setSearchTerm(keyword);
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  page, itemsPerPage);
			RecordSet<StoreKeyword> storeKeyword = daoService.getRedirectKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR);
			List<Keyword> list = new ArrayList<Keyword>();
			if (storeKeyword.getTotalSize() > 0) {
				for (StoreKeyword sk: storeKeyword.getList()) {
					list.add(sk.getKeyword());
				}
			}
			return new RecordSet<Keyword>(list, storeKeyword.getTotalSize());
		} catch (DaoException e) {
			logger.error("Failed during getAllKeywordInRule()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<RedirectRuleCondition> getConditionInRule(String ruleId, int page, int itemsPerPage) {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setStoreId(UtilityService.getStoreName());
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  page, itemsPerPage);
			return daoService.getRedirectConditions(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getConditionInRule()", e);
		}
		return null;
	}

	@RemoteMethod
	public int getTotalKeywordInRule(String ruleId) {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setStoreId(UtilityService.getStoreName());
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  null, null);
			return daoService.getRedirectKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR).getTotalSize();
		} catch (DaoException e) {
			logger.error("Failed during getTotalKeywordInRule()", e);
		}
		return 0;
	}

	@RemoteMethod
	public int getTotalRuleUsedByKeyword(String keyword){
		try {
			RedirectRule rule = new RedirectRule();
			rule.setStoreId(UtilityService.getStoreName());
			rule.setSearchTerm(keyword);
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  null, null);
			RecordSet<RedirectRule> count = daoService.getRedirectRules(criteria);
			if (count != null) {
				return count.getTotalSize();
			}
		} catch (DaoException e) {
			logger.error("Failed during getTotalRuleUsedByKeyword()",e);
		}
		return 0;
	}
	
	@RemoteMethod
	public RecordSet<RedirectRule> getAllRuleUsedByKeyword(String keyword) throws DaoException {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setStoreId(UtilityService.getStoreName());
			rule.setSearchTerm(keyword);
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null, 0, 0);
			return daoService.getRedirectRules(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllRuleUsedByKeyword()",e);
		}
		return null;
	}
}