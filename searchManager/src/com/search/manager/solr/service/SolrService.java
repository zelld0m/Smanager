package com.search.manager.solr.service;

import java.util.Collection;
import java.util.Map;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.SearchDaoService;
import com.search.manager.enums.RuleType;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface SolrService extends SearchDaoService {

	/* Elevate */
	Collection<ElevateResult> getElevateRules(Store store) throws DaoException;

	boolean loadElevateRules(Store store) throws DaoException;

	boolean loadElevateRules(StoreKeyword storeKeyword) throws DaoException;

	boolean resetElevateRules(Store store) throws DaoException;

	boolean resetElevateRules(StoreKeyword storeKeyword) throws DaoException;

	Map<String, Boolean> resetElevateRules(Store store,
			Collection<String> keywords) throws DaoException;

	boolean deleteElevateRules(Store store) throws DaoException;

	boolean deleteElevateRules(StoreKeyword storeKeyword) throws DaoException;

	boolean updateElevateRule(ElevateResult elevateResult) throws DaoException;

	boolean commitElevateRule() throws DaoException;

	/* Exclude */
	Collection<ExcludeResult> getExcludeRules(Store store) throws DaoException;

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

	/* Demote */
	Collection<DemoteResult> getDemoteRules(Store store) throws DaoException;

	boolean loadDemoteRules(Store store) throws DaoException;

	boolean loadDemoteRules(StoreKeyword storeKeyword) throws DaoException;

	boolean resetDemoteRules(Store store) throws DaoException;

	boolean resetDemoteRules(StoreKeyword storeKeyword) throws DaoException;

	Map<String, Boolean> resetDemoteRules(Store store,
			Collection<String> keywords) throws DaoException;

	boolean deleteDemoteRules(Store store) throws DaoException;

	boolean deleteDemoteRules(StoreKeyword storeKeyword) throws DaoException;

	boolean updateDemoteRule(DemoteResult demoteResult) throws DaoException;

	boolean commitDemoteRule() throws DaoException;

	/* Facet Sort */
	Collection<FacetSort> getFacetSortRules(Store store) throws DaoException;

	FacetSort getFacetSortRule(Store store, String name, RuleType ruleType)
			throws DaoException;

	FacetSort getFacetSortRuleById(Store store, String id) throws DaoException;

	boolean loadFacetSortRules(Store store) throws DaoException;

	boolean loadFacetSortRuleByName(Store store, String name, RuleType ruleType)
			throws DaoException;

	boolean loadFacetSortRuleById(Store store, String id) throws DaoException;

	boolean resetFacetSortRules(Store store) throws DaoException;

	boolean resetFacetSortRuleByName(Store store, String name, RuleType ruleType)
			throws DaoException;

	boolean resetFacetSortRuleById(Store store, String id) throws DaoException;

	Map<String, Boolean> resetFacetSortRuleById(Store store,
			Collection<String> ids) throws DaoException;

	boolean deleteFacetSortRules(Store store) throws DaoException;

	boolean deleteFacetSortRuleByName(Store store, String name,
			RuleType ruleType) throws DaoException;

	boolean deleteFacetSortRuleById(Store store, String id) throws DaoException;

	boolean updateFacetSortRule(FacetSort facetSort) throws DaoException;

	boolean commitFacetSortRule() throws DaoException;

	/* Redirect */
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

	/* Relevancy */
	Collection<Relevancy> getRelevancyRules(Store store) throws DaoException;

	Relevancy getDefaultRelevancyRule(Store store) throws DaoException;

	Relevancy getRelevancyRule(StoreKeyword storeKeyword) throws DaoException;

	Relevancy getRelevancyRuleByName(Store store, String name)
			throws DaoException;

	Relevancy getRelevancyRuleById(Store store, String id) throws DaoException;

	boolean loadRelevancyRules(Store store) throws DaoException;

	boolean loadRelevancyRuleByName(Store store, String name)
			throws DaoException;

	boolean loadRelevancyRuleById(Store store, String id) throws DaoException;

	boolean resetRelevancyRules(Store store) throws DaoException;

	boolean resetRelevancyRuleByName(Store store, String name)
			throws DaoException;

	boolean resetRelevancyRuleById(Store store, String id) throws DaoException;

	Map<String, Boolean> resetRelevancyRulesById(Store store,
			Collection<String> ids) throws DaoException;

	boolean deleteRelevancyRules(Store store) throws DaoException;

	boolean deleteRelevancyRuleByName(Store store, String name)
			throws DaoException;

	boolean deleteRelevancyRuleById(Store store, String id) throws DaoException;

	boolean updateRelevancyRule(Relevancy relevancy) throws DaoException;

	boolean commitRelevancyRule() throws DaoException;

	/* Spell */

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
