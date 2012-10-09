package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.FacetSort;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;

@Repository("facetSortCacheDao")
public class FacetSortCacheDao extends CacheDao<FacetSort> {

	private static final Logger logger = Logger.getLogger(FacetSortCacheDao.class);

	@Override
	protected String getCacheKeyInitials() throws DataException {
		return CacheConstants.FACET_SORT_KEYWORD_LIST_CACHE_KEY;
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
	public CacheModel<FacetSort> getDatabaseObject(StoreKeyword storeKeyword)throws DaoException {
		//TODO
		/*FacetSort facetSortFilter = new FacetSort();
		facetSortFilter.setStoreKeyword(storeKeyword);
		// load only non-expired items
		SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(facetSortFilter, new Date(), null, 0, 0);
		List<FacetSort> facetSortList = daoService.getFacetSortList(criteria).getList();
		if (facetSortList == null) {
			facetSortList = new ArrayList<FacetSort>();
		}
		return new CacheModel<FacetSort>(facetSortList);*/
		return null;
	}

	@Override
	public boolean reload(FacetSort facetSort) throws DataException, DaoException {
		/*try {
			DAOValidation.checkStoreKeywordPK(facetSort.getStoreKeyword());
		} catch (Exception e) {
			logger.error("Error during loading of facet sort list to cache - Keyword ", e);
			throw new DataException(e);
		}
		return reload(facetSort.getStoreKeyword());*/
		//TODO
		return true;
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
