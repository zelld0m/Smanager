package com.search.manager.cache.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.enums.RuleType;
import com.search.manager.exception.DataException;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

@Deprecated
@Repository("facetSortCacheDao")
public class FacetSortCacheDao extends CacheDao<FacetSort> {

	private static final Logger logger = Logger.getLogger(FacetSortCacheDao.class);

	@Override
	protected String getCacheKeyInitials() throws DataException {
		return CacheConstants.FACET_SORT_KEYWORD_LIST_CACHE_KEY;
	}
	
	@Override
	public boolean forceUpdateCache(Store store) {
		try{
			String storeId = store.getStoreId();
			if (StringUtils.isNotBlank(storeId)) {
				cacheService.put(CacheConstants.getCacheKey(storeId, CacheConstants.FORCE_UPDATE_CACHE_KEY, CacheConstants.FACET_SORT_KEYWORD_LIST_CACHE_KEY), new CacheModel<Object>());
				cacheService.put(CacheConstants.getCacheKey(storeId, CacheConstants.FORCE_UPDATE_CACHE_KEY, CacheConstants.FACET_SORT_TEMPLATE_LIST_CACHE_KEY), new CacheModel<Object>());
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to set force update cache date for  " + store, e);
		}
		return false;
	}
	
	@Override
	public boolean reload(Store store) throws DataException, DaoException {
		String storeId = store.getStoreId();
		try {
			//reload facet sort rule of type: keyword
			List<String> kwList = keywordCacheDao.getAllKeywords(store);
			for(String kw : kwList){
				StoreKeyword storeKeyword = new StoreKeyword(storeId, kw);
				reload(storeKeyword);
			}
			
			//reload facet sort rule of type: template name
			FacetSort facetSort = new FacetSort();
			facetSort.setStore(store);
			facetSort.setRuleType(RuleType.TEMPLATE);
			SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(facetSort);
			RecordSet<FacetSort> facetSortList = daoService.searchFacetSort(criteria, null);
			
			if(facetSortList != null){
				List<FacetSort> tnList = facetSortList.getList();
				
				for(FacetSort tn : tnList){
					reload(store, tn.getName());
				}
			}
			
			return true;
		} catch (DaoException e) {
			logger.error(e,e);			
		} catch (DataException e) {
			logger.error(e,e);
		}
		return false;		
	}
	
	@Override
	public String getCacheKey(StoreKeyword storeKeyword) throws DataException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.FACET_SORT_KEYWORD_LIST_CACHE_KEY, storeKeyword.getKeywordId());
	}
	
	@Override
	protected String getCacheKey(Store store, String name) throws DataException {
		try {
			DAOValidation.checkFacetSortPK(store, name);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(store.getStoreId(), CacheConstants.FACET_SORT_TEMPLATE_LIST_CACHE_KEY, name);
	}
	
	@Override
	public CacheModel<FacetSort> getDatabaseObject(StoreKeyword storeKeyword)throws DaoException {
		FacetSort facetSortFilter = new FacetSort(storeKeyword.getKeywordTerm(), RuleType.KEYWORD, null, storeKeyword.getStore());
		FacetSort facetSort = daoService.getFacetSort(facetSortFilter);
		return new CacheModel<FacetSort>(facetSort);
	}
	
	@Override
	public CacheModel<FacetSort> getDatabaseObject(Store store, String name)throws DaoException {
		FacetSort facetSortFilter = new FacetSort(name, RuleType.TEMPLATE, null,store);
		FacetSort facetSort = daoService.getFacetSort(facetSortFilter);
		return new CacheModel<FacetSort>(facetSort);
	}

	@Override
	public boolean reload(FacetSort facetSort) throws DataException, DaoException {
		try {
			DAOValidation.checkFacetSortPK(facetSort);
		} catch (Exception e) {
			logger.error("Error during loading of facet sort list to cache - Keyword ", e);
			throw new DataException(e);
		}
		return reload(facetSort);
	}

	@Override
	public boolean reload(List<FacetSort> list) throws DataException, DaoException {
		boolean result = true;
		for (FacetSort facetSort: list) {
			try {
				result &= reload(facetSort);
			} catch (Exception e) {
				result = false;
				continue;
			}
		}
		return result;
	}
}
