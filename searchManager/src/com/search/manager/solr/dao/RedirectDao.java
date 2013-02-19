package com.search.manager.solr.dao;

import java.util.Collection;
import java.util.Map;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface RedirectDao {

	Collection<RedirectRule> getRedirectRules(Store store) throws DaoException;

	RedirectRule getRedirectRule(StoreKeyword storeKeyword) throws DaoException;

	RedirectRule getRedirectRuleByName(Store store, String name)
			throws DaoException;

	RedirectRule getRedirectRuleById(Store store, String id)
			throws DaoException;

	boolean loadRedirectRules(Store store) throws DaoException;

	boolean loadRedirectRuleByName(Store store, String name)
			throws DaoException;

	boolean loadRedirectRuleById(Store store, String id) throws DaoException;

	boolean resetRedirectRules(Store store) throws DaoException;

	boolean resetRedirectRuleByName(Store store, String name)
			throws DaoException;

	boolean resetRedirectRuleById(Store store, String id) throws DaoException;

	Map<String, Boolean> resetRedirectRulesById(Store store,
			Collection<String> ids) throws DaoException;

	boolean deleteRedirectRules(Store store) throws DaoException;

	boolean deleteRedirectRuleByName(Store store, String name)
			throws DaoException;

	boolean deleteRedirectRuleById(Store store, String id) throws DaoException;

	boolean updateRedirectRule(RedirectRule redirectRule) throws DaoException;

	boolean commitRedirectRule() throws DaoException;

}
