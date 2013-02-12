package com.search.manager.solr.dao;

import java.util.Collection;
import java.util.Map;

import com.search.manager.dao.DaoException;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface ExcludeDao {

	Collection<ExcludeResult> getExcludeRules(Store store) throws DaoException;

	Collection<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword)
			throws DaoException;

	boolean loadExcludeRules(Store store) throws DaoException;

	boolean loadExcludeRules(StoreKeyword storeKeyword) throws DaoException;

	boolean resetExcludeRules(Store store) throws DaoException;

	boolean resetExcludeRules(StoreKeyword storeKeyword) throws DaoException;

	Map<String, Boolean> resetExcludeRules(Store store,
			Collection<String> keywords) throws DaoException;

	boolean deleteExcludeRules(Store store) throws DaoException;

	boolean deleteExcludeRules(StoreKeyword storeKeyword) throws DaoException;

	boolean updateExcludeRule(ExcludeResult excludeResult) throws DaoException;

	boolean commitExcludeRule() throws DaoException;

}
