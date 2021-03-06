package com.search.manager.solr.dao;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.model.Store;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleType;
import com.search.manager.model.FacetSort;

public interface FacetSortDao {

	Collection<FacetSort> getFacetSortRules(Store store) throws DaoException;

	FacetSort getFacetSortRule(Store store, String name, RuleType ruleType)
			throws DaoException;

	FacetSort getFacetSortRuleById(Store store, String id) throws DaoException;

	boolean loadFacetSortRules(Store store) throws DaoException;

	boolean loadFacetSortRuleByName(Store store, String name, RuleType ruleType)
			throws DaoException;

	boolean loadFacetSortRuleById(Store store, String id) throws DaoException;

	boolean resetFacetSortRules(Store store) throws DaoException;

	boolean resetFacetSortRulesByName(Store store, String name,
			RuleType ruleType) throws DaoException;

	boolean resetFacetSortRulesById(Store store, String id) throws DaoException;

	Map<String, Boolean> resetFacetSortRulesById(Store store,
			Collection<String> ids) throws DaoException;

	boolean deleteFacetSortRules(Store store) throws DaoException;

	boolean deleteFacetSortRuleByName(Store store, String name,
			RuleType ruleType) throws DaoException;

	boolean deleteFacetSortRuleById(Store store, String id) throws DaoException;

	boolean updateFacetSortRule(FacetSort facetSort) throws DaoException;

	boolean commitFacetSortRule() throws DaoException;

}
