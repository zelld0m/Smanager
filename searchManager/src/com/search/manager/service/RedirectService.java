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

	@RemoteMethod
	public int addRedirectRule(String ruleName) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleName(ruleName);
			rule.setStoreId(UtilityService.getStoreName());
			rule.setCreatedBy(UtilityService.getUsername());
			result = daoService.addRedirectRule(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRedirectRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int updateRedirectRule(String ruleId, String ruleName, String changeKeyword, Integer priority) {
		int result = -1;
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setRuleName(ruleName);
			rule.setPriority(priority);
			rule.setChangeKeyword(changeKeyword);
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
	public int addKeywordToRule(String ruleId, String keyword) throws DaoException {
		int result = -1;
		try {
			// add keyword in case it has not been added yet
			daoService.addKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setSearchTerm(keyword);
			rule.setModifiedBy(UtilityService.getUsername());
			result = daoService.addRedirectKeyword(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRedirectKeyword()",e);
		}
		return result;
	}

	@RemoteMethod
	public int deleteKeywordInRule(String ruleId, String searchTerm) throws DaoException {
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
	public RecordSet<Keyword> getKeywordInRule(String ruleId, String keyword, int page,int itemsPerPage) throws DaoException {
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
			logger.error("Failed during getElevatedProduct()",e);
		}
		return null;
	}

	@RemoteMethod
	public int getRedirectKeywordCount(String ruleId) throws DaoException {
		try {
			RedirectRule rule = new RedirectRule();
			rule.setRuleId(ruleId);
			rule.setStoreId(UtilityService.getStoreName());
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  null, null);
			return daoService.getRedirectKeywords(criteria).getTotalSize();
		} catch (DaoException e) {
			logger.error("Failed during getElevatedProduct()",e);
		}
		return 0;
	}
	
	@RemoteMethod
	public RecordSet<RedirectRule> getRedirectRuleForKeyword(String keyword) throws DaoException {
		try {
			SearchCriteria<StoreKeyword> criteria = new SearchCriteria<StoreKeyword>(new StoreKeyword(UtilityService.getStoreName(), null), null, null,  0, 0);
			return daoService.getRedirectForKeywords(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getElevatedProduct()",e);
		}
		return null;
	}
}