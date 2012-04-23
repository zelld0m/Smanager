package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.common.Logger;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;

public class ExcludeCacheDao extends CacheDao<ExcludeResult> {

	private static final Logger logger = Logger.getLogger(ExcludeCacheDao.class);
	
	@Override
	public String getCacheKey(StoreKeyword storeKeyword) throws DataException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.EXCLUDED_LIST_CACHE_KEY, storeKeyword.getKeywordId());
	}

	@Override
	public CacheModel<ExcludeResult> getDatabaseObject(StoreKeyword storeKeyword)throws DaoException {
		ExcludeResult excludeFilter = new ExcludeResult();
		excludeFilter.setStoreKeyword(storeKeyword);
		// load only non-expired items
		SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter, new Date(), null, 0, 0);
		List<ExcludeResult> excludedList = daoService.getExcludeResultList(criteria).getList();
		if (excludedList == null) {
			excludedList = new ArrayList<ExcludeResult>();
		}
		return new CacheModel<ExcludeResult>(excludedList);
	}

	@Override
	public boolean reload(ExcludeResult exclude) throws DataException, DaoException {
		try {
			DAOValidation.checkStoreKeywordPK(exclude.getStoreKeyword());
		} catch (Exception e) {
			logger.error("Error during loading of excluded list to cache - Keyword ", e);
			throw new DataException(e);
		}
		return reload(exclude.getStoreKeyword());
	}

	@Override
	public boolean reload(List<ExcludeResult> list) throws DataException, DaoException {
		boolean result = true;
		for (ExcludeResult exclude: list) {
			try {
				DAOValidation.checkStoreKeywordPK(exclude.getStoreKeyword());
			} catch (Exception e) {
				logger.error("Error during loading of excluded list to cache - Keyword ", e);
				result = false;
				continue;
			}
			result &= reload(exclude.getStoreKeyword());
		}
		return result;
	}

}
