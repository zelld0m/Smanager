package com.search.manager.solr.dao;

import java.util.Collection;
import java.util.Map;

import com.search.manager.dao.DaoException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface ElevateDao {

	Collection<ElevateResult> getElevateRules(Store store) throws DaoException;

	Collection<ElevateResult> getElevateRules(StoreKeyword storeKeyword)
			throws DaoException;

	boolean loadElevateRules(Store store) throws DaoException;

	boolean loadElevateRules(StoreKeyword storeKeyword) throws DaoException;

	boolean resetElevateRules(Store store) throws DaoException;

	Map<String, Boolean> resetElevateRules(Store store, Collection<String> keywords)
			throws DaoException;

	boolean resetElevateRules(StoreKeyword storeKeyword) throws DaoException;

	boolean deleteElevateRules(Store store) throws DaoException;

	boolean deleteElevateRules(StoreKeyword storeKeyword) throws DaoException;

	boolean updateElevateRule(ElevateResult elevateResult) throws DaoException;

	boolean commitElevateRule() throws DaoException;

}
