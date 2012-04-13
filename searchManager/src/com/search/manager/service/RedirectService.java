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
import com.search.manager.utility.RedirectUtility;

@Service(value = "redirectService")
@RemoteProxy(
		name = "RedirectServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "redirectService")
	)
public class RedirectService {

	private static final Logger logger = Logger.getLogger(RedirectService.class);
	
	@Autowired private DaoService daoService;
	@Autowired private RedirectUtility redirectUtility;
	
	public void setRedirectUtility(RedirectUtility redirectUtility) {
		this.redirectUtility = redirectUtility;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@RemoteMethod
	public int addRedirectRule(String ruleName, String searchTerm, String condition, String storeId, Integer activeFlag, Integer priority) {
		int result = -1;
		try {

			RedirectRule rule = new RedirectRule();
			rule.setRuleName(ruleName);
			rule.setSearchTerm(searchTerm);
			rule.setCondition(condition);
			rule.setStoreId(storeId);
			rule.setActiveFlag(activeFlag);
			rule.setPriority(priority);
			rule.setCreatedBy(UtilityService.getUsername());
			result = daoService.addRedirectRule(rule);
			redirectUtility.updateRuleMap();
		} catch (DaoException e) {
			logger.error("Failed during addRedirectRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int updateRedirectRule(Integer ruleId, String ruleName, String searchTerm, String condition, String storeId, Integer activeFlag, Integer priority) {
		int result = -1;
		try {
			
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setRuleName(ruleName);
			rule.setSearchTerm(searchTerm);
			rule.setCondition(condition);
			rule.setStoreId(storeId);
			rule.setActiveFlag(activeFlag);
			rule.setPriority(priority);
			rule.setModifiedBy(UtilityService.getUsername());
			result = daoService.updateRedirectRule(rule);
			redirectUtility.updateRuleMap();
		} catch (DaoException e) {
			logger.error("Failed during updateRedirectRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int removeRedirectRule(Integer ruleId, String ruleName) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setRuleName(ruleName);
			rule.setSearchTerm("");
			rule.setCondition("");
			rule.setStoreId(UtilityService.getStoreName());
			result = daoService.removeRedirectRule(rule);
			redirectUtility.updateRuleMap();
		} catch (DaoException e) {
			logger.error("Failed during removeRedirectRule()",e);
		}
		return result;
	}
	
	@RemoteMethod
	public RecordSet<RedirectRule> getRedirectRule(String searchTerm, Integer ruleId, int page, int itemsPerPage) {
		try {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setSearchTerm(searchTerm);
			redirectRule.setRuleId(ruleId);
			redirectRule.setStoreId(UtilityService.getStoreName());
			
			SearchCriteria<RedirectRule> searchCriteria = new SearchCriteria<RedirectRule>(redirectRule, null, null, page, itemsPerPage);
			return daoService.getRedirectRule(searchCriteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllRedirectRule()",e);
		}
		return null;
	}
	
}