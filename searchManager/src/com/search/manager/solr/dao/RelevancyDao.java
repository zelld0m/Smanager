package com.search.manager.solr.dao;

import java.util.Collection;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface RelevancyDao {

	Collection<Relevancy> getRelevancyRules(Store store) throws DaoException;

	Relevancy getDefaultRelevancyRule(Store store) throws DaoException;

	Relevancy getRelevancyRule(StoreKeyword storeKeyword) throws DaoException;

	Relevancy getRelevancyRuleByName(Store store, String name)
			throws DaoException;

	Relevancy getRelevancyRuleById(Store store, String id) throws DaoException;

	boolean loadRelevancyRules(Store store) throws DaoException;

	boolean loadRelevancyRuleByName(Store store, String name) throws DaoException;

	boolean loadRelevancyRuleById(Store store, String id) throws DaoException;

	boolean resetRelevancyRules(Store store) throws DaoException;

	boolean resetRelevancyRuleByName(Store store, String name)
			throws DaoException;

	boolean resetRelevancyRuleById(Store store, String id) throws DaoException;

	boolean deleteRelevancyRules(Store store) throws DaoException;

	boolean deleteRelevancyRuleByName(Store store, String name)
			throws DaoException;

	boolean deleteRelevancyRuleById(Store store, String id) throws DaoException;

	boolean updateRelevancyRule(Relevancy relevancy) throws DaoException;

}
