package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
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
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@RemoteMethod
	public int addRedirectRule(String ruleName, String redirectType, String searchTerm, String condition, String storeId, Integer priority) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleName(ruleName);
			rule.setSearchTerm(searchTerm);
			rule.setCondition(condition);
			rule.setStoreId(storeId);
			rule.setPriority(priority);
			rule.setCreatedBy(UtilityService.getUsername());
			result = daoService.addRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRedirectRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int updateRedirectRule(String ruleId, String ruleName, String searchTerm, String condition, String storeId, Integer priority) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setRuleName(ruleName);
			rule.setSearchTerm(searchTerm);
			rule.setCondition(condition);
			rule.setStoreId(storeId);
			rule.setPriority(priority);
			rule.setModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during updateRedirectRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int removeRedirectRule(String ruleId, String ruleName, String searchTerm) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setSearchTerm(searchTerm);
			rule.setModifiedBy(UtilityService.getUsername());
			result = daoService.removeRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during removeRedirectRule()",e);
		}
		return result;
	}
	
	@RemoteMethod
	public RecordSet<RedirectRule> getRedirectRule(String searchTerm, String ruleId, int page, int itemsPerPage) {
		try {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setSearchTerm(searchTerm);
			redirectRule.setRuleId(ruleId);
			redirectRule.setStoreId(UtilityService.getStoreName());
			
			SearchCriteria<RedirectRule> searchCriteria = new SearchCriteria<RedirectRule>(redirectRule, null, null, page, itemsPerPage);
			return daoService.getRedirectRules(searchCriteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllRedirectRule()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public int addRedirectKeyword(String ruleId, String searchTerm) throws DaoException {
		int result = -1;
		try {
			// add keyword in case it has not been added yet
			daoService.addKeyword(new StoreKeyword(UtilityService.getStoreName(), searchTerm));
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setSearchTerm(searchTerm);
			rule.setModifiedBy(UtilityService.getUsername());
			result = daoService.addRedirectKeyword(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRedirectKeyword()",e);
		}
		return result;
	}

	@RemoteMethod
	public int removeRedirectKeyword(String ruleId, String searchTerm) throws DaoException {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setSearchTerm(searchTerm);
			rule.setModifiedBy(UtilityService.getUsername());
			result = daoService.removeRedirectKeyword(rule);
		} catch (DaoException e) {
			logger.error("Failed during removeRedirectKeyword()",e);
		}
		return result;
	}

	@RemoteMethod
	public int addRedirectCondition(String ruleId, String condition) throws DaoException {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setCondition(condition);
			result = daoService.addRedirectCondition(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRedirectCondition()",e);
		}
		return result;
	}

	@RemoteMethod
	public int removeRedirectCondition(String ruleId, String condition) throws DaoException {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setCondition(condition);
			result = daoService.removeRedirectCondition(rule);
		} catch (DaoException e) {
			logger.error("Failed during removeRedirectCondition()",e);
		}
		return result;

	}

	@RemoteMethod
	public RecordSet<StoreKeyword> getRedirectKeywords(String ruleId, int page,int itemsPerPage) throws DaoException {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setStoreId(UtilityService.getStoreName());
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  page, itemsPerPage);
			return daoService.getRedirectKeywords(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getElevatedProduct()",e);
		}
		return null;
		
	}
	
}