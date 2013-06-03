package com.search.manager.dao;

import java.util.List;

import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface SearchDaoService {

	/* Elevate */
	public List<ElevateResult> getElevateRules(StoreKeyword storeKeyword)
			throws DaoException;

	public List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword)
			throws DaoException;

	/* Exclude */
	public List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword)
			throws DaoException;

	public List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword)
			throws DaoException;

	/* Demote */
	public List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword)
			throws DaoException;

	public List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword)
			throws DaoException;

	/* Facet Sort */
	public FacetSort getFacetSortRule(StoreKeyword storeKeyword)
			throws DaoException;

	public FacetSort getFacetSortRule(Store store, String templateName)
			throws DaoException;

	/* Redirect */
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword)
			throws DaoException;

	/* Relevancy */
	public Relevancy getRelevancyRule(StoreKeyword storeKeyword)
			throws DaoException;

	public Relevancy getRelevancyRule(Store store, String relevancyId)
			throws DaoException;

	/* Spell */
	public SpellRule getSpellRuleForSearchTerm(String store, String searchTerm)
			throws DaoException;

	public Integer getMaxSuggest(String storeId) throws DaoException;

	/* Banner */

	public List<BannerRuleItem> getActiveBannerRuleItems(Store store,
			String keyword) throws DaoException;

}
