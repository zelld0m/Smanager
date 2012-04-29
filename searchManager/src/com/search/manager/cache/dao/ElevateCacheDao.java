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
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;

@Repository("elevateCacheDao")
public class ElevateCacheDao extends CacheDao<ElevateResult> {

	private static final Logger logger = Logger.getLogger(ElevateCacheDao.class);

	@Override
	protected String getCacheKeyInitials() throws DataException {
		return CacheConstants.ELEVATED_LIST_CACHE_KEY;
	}
	
	@Override
	public String getCacheKey(StoreKeyword storeKeyword) throws DataException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.ELEVATED_LIST_CACHE_KEY, storeKeyword.getKeywordId());
	}

	@Override
	public CacheModel<ElevateResult> getDatabaseObject(StoreKeyword storeKeyword)throws DaoException {
		ElevateResult elevateFilter = new ElevateResult();
		elevateFilter.setStoreKeyword(storeKeyword);
		// load only non-expired items
		SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter, new Date(), null, 0, 0);
		List<ElevateResult> elevatedList = daoService.getElevateResultList(criteria).getList();
		if (elevatedList == null) {
			elevatedList = new ArrayList<ElevateResult>();
		}
		return new CacheModel<ElevateResult>(elevatedList);
	}

	@Override
	public boolean reload(ElevateResult elevate) throws DataException, DaoException {
		try {
			DAOValidation.checkStoreKeywordPK(elevate.getStoreKeyword());
		} catch (Exception e) {
			logger.error("Error during loading of elevated list to cache - Keyword ", e);
			throw new DataException(e);
		}
		return reload(elevate.getStoreKeyword());
	}

	@Override
	public boolean reload(List<ElevateResult> list) throws DataException, DaoException {
		boolean result = true;
		for (ElevateResult elevate: list) {
			try {
				result &= reload(elevate);
			} catch (Exception e) {
				result = false;
				continue;
			}
		}
		return result;
	}

}
