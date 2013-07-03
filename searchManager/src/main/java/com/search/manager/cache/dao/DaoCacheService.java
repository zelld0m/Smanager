package com.search.manager.cache.dao;

import java.util.List;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.SearchDaoService;
import com.search.manager.exception.DataException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public interface DaoCacheService extends SearchDaoService {

	/* Keywords */
	public List<String> getAllKeywords(Store store) throws DaoException, DataException;
	public boolean resetAllkeywords(Store store) throws DaoException, DataException;
	public boolean hasExactMatchKey(StoreKeyword storeKeyword);
	public boolean reloadAllKeywords(Store store) throws DaoException, DataException;

	/* Elevate */
	public boolean loadElevateRules(Store store) throws DaoException, DataException;
	public boolean updateElevateRule(ElevateResult elevateResult) throws DaoException, DataException;
	public boolean resetElevateRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Exclude */
	public boolean loadExcludeRules(Store store) throws DaoException, DataException;
	public boolean updateExcludeRules(ExcludeResult excludeResult) throws DaoException, DataException;
	public boolean resetExcludeRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Demote */
	public boolean loadDemoteRules(Store store) throws DaoException, DataException;
	public boolean updateDemoteRules(DemoteResult demoteResult) throws DaoException, DataException;
	public boolean resetDemoteRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Facet Sort */
	public boolean loadFacetSortRules(Store store) throws DaoException, DataException;
	public boolean updateFacetSortRule(FacetSort facetSort) throws DaoException, DataException;
	public boolean resetFacetSortRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	public boolean resetFacetSortRule(Store store, String name) throws DaoException, DataException;
	
	/* Redirect */
	public boolean loadRedirectRules(Store store) throws DaoException, DataException;
	public boolean updateRedirectRule(RedirectRule redirectRule) throws DaoException, DataException;
	public boolean resetRedirectRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Relevancy */
	public boolean loadRelevancyRules(Store store) throws DaoException, DataException;
	public Relevancy getDefaultRelevancyRule(Store store) throws DaoException, DataException;
	public boolean updateRelevancyRule(Relevancy relevancy) throws DaoException, DataException;
	public boolean resetRelevancyRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Users */
	public boolean loginUser(UserDetailsImpl userDetails) throws DaoException, DataException; 
	public boolean logoutUser(String username) throws DaoException, DataException; 
	public UserDetailsImpl getUser(String username) throws DaoException, DataException;
	public boolean setUserCurrentPage(String username, String currentPage) throws DaoException, DataException;
	public List<UserDetailsImpl> getLoggedInUsers() throws DaoException, DataException;
	
	/* Force reload */
	public boolean setForceReload(Store store);
	public boolean setForceReloadElevate(Store store);
	public boolean setForceReloadExclude(Store store);
	public boolean setForceReloadDemote(Store store);
	public boolean setForceReloadFacetSort(Store store);
	public boolean setForceReloadRedirect(Store store);
	public boolean setForceReloadRelevancy(Store store);
	
}
