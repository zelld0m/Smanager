package com.search.manager.cache.dao;

import java.util.List;

import com.search.manager.dao.DaoException;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;

public interface DaoCacheService {
	/* Keywords */
	public List<String> getAllKeywords(Store store) throws DaoException, DataException;
	public boolean resetAllkeywords(Store store) throws DaoException, DataException;
	public boolean hasExactMatchKey(StoreKeyword storeKeyword);

	/* Elevate */
	public boolean loadElevateRules(Store store) throws DaoException, DataException, DataException;
	public List<ElevateResult> getElevateResultList(StoreKeyword storeKeyword) throws DaoException, DataException;
	public boolean updateElevateResultList(ElevateResult elevateResult) throws DaoException, DataException;
	public boolean resetElevateResult(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Exclude */
	public boolean loadExcludeResultList(Store store) throws DaoException, DataException;
	public List<ExcludeResult> getExcludeResultList(StoreKeyword storeKeyword) throws DaoException, DataException;
	public boolean updateExcludeResultList(ExcludeResult excludeResult) throws DaoException, DataException;
	public boolean resetExcludeResult(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Redirect */
	public boolean loadRedirectRules(Store store) throws DaoException, DataException;
	public boolean updateRedirectRule(RedirectRule redirectRule) throws DaoException, DataException;
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword) throws DaoException, DataException;
	
	/* Relevancy */
	public boolean loadRelevancyResultList(String storeName, MatchType relevancyMatchType) throws DaoException, DataException;
	public boolean loadRelevancyDetails(String storeName) throws DaoException, DataException;
	public Relevancy getRelevancyDetails(Relevancy relevancy, String storeName) throws DaoException, DataException;
	public List<Relevancy> searchRelevancy(SearchCriteria<Relevancy> criteria, MatchType relevancyMatchType) throws DaoException, DataException;
	public int getRelevancyKeywordCount(StoreKeyword storeKeyword) throws DaoException, DataException;
	public RelevancyKeyword getRelevancyKeyword(RelevancyKeyword relevancyKeyword, String storeName) throws DaoException, DataException;
	public RecordSet<RelevancyKeyword> searchRelevancyKeywords(SearchCriteria<RelevancyKeyword> criteria, 
			MatchType relevancyMatchType, ExactMatch keywordExactMatch) throws DaoException, DataException;
	
}
