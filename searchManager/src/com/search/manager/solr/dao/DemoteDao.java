package com.search.manager.solr.dao;

import java.util.Collection;
import java.util.Map;

import com.search.manager.dao.DaoException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface DemoteDao {

	Collection<DemoteResult> getDemoteRules(Store store) throws DaoException;

	Collection<DemoteResult> getDemoteRules(StoreKeyword storeKeyword)
			throws DaoException;

	Collection<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword)
		throws DaoException;
	
	boolean loadDemoteRules(Store store) throws DaoException;

	boolean loadDemoteRules(StoreKeyword storeKeyword) throws DaoException;

	boolean resetDemoteRules(Store store) throws DaoException;

	boolean resetDemoteRules(StoreKeyword storeKeyword) throws DaoException;
	
	Map<String, Boolean> resetDemoteRules(Store store, Collection<String> keywords) throws DaoException;

	boolean deleteDemoteRules(Store store) throws DaoException;

	boolean deleteDemoteRules(StoreKeyword storeKeyword) throws DaoException;

	boolean updateDemoteRule(DemoteResult demoteResult) throws DaoException;

	boolean commitDemoteRule() throws DaoException;
	
}