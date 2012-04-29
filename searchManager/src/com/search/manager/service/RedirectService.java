package com.search.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;

@Service(value = "redirectService")
@RemoteProxy(
		name = "RedirectServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "redirectService")
	)
public class RedirectService {

	private static final Logger logger = Logger.getLogger(RedirectService.class);
	
	@Autowired private DaoService daoService;

	public int addRule(String ruleName) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleName(ruleName);
			rule.setStoreId(UtilityService.getStoreName());
			rule.setCreatedBy(UtilityService.getUsername());
			result = daoService.addRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRule()",e);
		}
		return result;
	}
	
	public String addRuleAndGetId(String ruleName) {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleName(ruleName);
			rule.setStoreId(UtilityService.getStoreName());
			rule.setCreatedBy(UtilityService.getUsername());
			return daoService.addRedirectRuleAndGetId(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRuleAndGetId()",e);
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
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during updateRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int deleteRule(RedirectRule rule) {
		int result = -1;
		try {
			result = daoService.deleteRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during deleteRule()",e);
		}
		return result;
	}
	
	@RemoteMethod
	public RecordSet<RedirectRule> getAllRule(String searchTerm, int page, int itemsPerPage) {
		try {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setSearchTerm(searchTerm);
			redirectRule.setStoreId(UtilityService.getStoreName());
			SearchCriteria<RedirectRule> searchCriteria = new SearchCriteria<RedirectRule>(redirectRule, null, null, page, itemsPerPage);
			return daoService.getRedirectRules(searchCriteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllRule()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public RedirectRule getRule(String ruleId) {
		try {
			RedirectRule redirectRule = new RedirectRule();
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
	public int addRuleCondition(String ruleId, String condition) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setCondition(condition);
			result = daoService.addRedirectCondition(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRuleCondition()",e);
		}
		return result;
	}

	@RemoteMethod
	public int deleteConditionInRule(String ruleId, String condition) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setCondition(condition);
			result = daoService.deleteRedirectCondition(rule);
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
			RecordSet<StoreKeyword> storeKeyword = daoService.getRedirectKeywords(criteria);
			List<Keyword> list = new ArrayList<Keyword>();
			if (storeKeyword.getTotalSize() > 0) {
				for (StoreKeyword sk: storeKeyword.getList()) {
					list.add(sk.getKeyword());
				}
			}
			return new RecordSet<Keyword>(list, list.size());
		} catch (DaoException e) {
			logger.error("Failed during getAllKeywordInRule()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<RedirectRuleCondition> getConditionInRule(String ruleId, int page,int itemsPerPage) {
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
			return daoService.getRedirectKeywords(criteria).getTotalSize();
		} catch (DaoException e) {
			logger.error("Failed during getTotalKeywordInRule()", e);
		}
		return 0;
	}
	
	@RemoteMethod
	public RecordSet<RedirectRule> getAllRuleUsedByKeyword(String keyword) throws DaoException {
		try {
			SearchCriteria<StoreKeyword> criteria = new SearchCriteria<StoreKeyword>(new StoreKeyword(UtilityService.getStoreName(), null), null, null,  0, 0);
			return daoService.getRedirectForKeywords(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllRuleUsedByKeyword()",e);
		}
		return null;
	}
}