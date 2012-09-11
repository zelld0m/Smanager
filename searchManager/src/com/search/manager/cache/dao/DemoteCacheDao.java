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
import com.search.manager.model.DemoteResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;

@Repository("demoteCacheDao")
public class DemoteCacheDao extends CacheDao<DemoteResult> {

	private static final Logger logger = Logger.getLogger(DemoteCacheDao.class);

	@Override
	protected String getCacheKeyInitials() throws DataException {
		return CacheConstants.DEMOTED_LIST_CACHE_KEY;
	}
	
	@Override
	public String getCacheKey(StoreKeyword storeKeyword) throws DataException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.DEMOTED_LIST_CACHE_KEY, storeKeyword.getKeywordId());
	}

	@Override
	public CacheModel<DemoteResult> getDatabaseObject(StoreKeyword storeKeyword)throws DaoException {
		DemoteResult demoteFilter = new DemoteResult();
		demoteFilter.setStoreKeyword(storeKeyword);
		// load only non-expired items
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(demoteFilter, new Date(), null, 0, 0);
		List<DemoteResult> demotedList = daoService.getDemoteResultList(criteria).getList();
		if (demotedList == null) {
			demotedList = new ArrayList<DemoteResult>();
		}
		return new CacheModel<DemoteResult>(demotedList);
	}

	@Override
	public boolean reload(DemoteResult demote) throws DataException, DaoException {
		try {
			DAOValidation.checkStoreKeywordPK(demote.getStoreKeyword());
		} catch (Exception e) {
			logger.error("Error during loading of demoted list to cache - Keyword ", e);
			throw new DataException(e);
		}
		return reload(demote.getStoreKeyword());
	}

	@Override
	public boolean reload(List<DemoteResult> list) throws DataException, DaoException {
		boolean result = true;
		for (DemoteResult demote: list) {
			try {
				result &= reload(demote);
			} catch (Exception e) {
				result = false;
				continue;
			}
		}
		return result;
	}

}
