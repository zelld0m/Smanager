package com.search.manager.solr.dao;

import com.search.manager.dao.DaoException;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface SpellRuleDao {

	SpellRule getSpellRuleForSearchTerm(String storeId, String searchTerm)
			throws DaoException;

	boolean loadSpellRules(Store store) throws DaoException;

	boolean loadSpellRules(StoreKeyword storeKeyword) throws DaoException;

	boolean loadSpellRuleById(Store store, String ruleId) throws DaoException;

	boolean loadSpellRules(Store store, String dirPath, String fileName)
			throws DaoException;

	boolean resetSpellRules(Store store) throws DaoException;

	boolean resetSpellRules(StoreKeyword storeKeyword) throws DaoException;

	boolean resetSpellRuleById(Store store, String ruleId) throws DaoException;

	boolean deleteSpellRules(Store store) throws DaoException;

	boolean deleteSpellRules(StoreKeyword storeKeyword) throws DaoException;

	boolean deleteSpellRuleById(Store store, String ruleId) throws DaoException;

	boolean updateSpellRule(SpellRule spellRule) throws DaoException;

	boolean commitSpellRule() throws DaoException;

}
