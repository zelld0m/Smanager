package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
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
	protected String getCacheKey(Store store, String name) throws DataException {
		StoreKeyword storeKeyword = new StoreKeyword(store, new Keyword(name));
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
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(demoteFilter, DateTime.now(), null, 0, 0);
		List<DemoteResult> demotedList = daoService.getDemoteResultList(criteria).getList();
		if (demotedList == null) {
			demotedList = new ArrayList<DemoteResult>();
		}
		return new CacheModel<DemoteResult>(demotedList);
	}
	
	@Override
	protected CacheModel<DemoteResult> getDatabaseObject(Store store,
			String name) throws DaoException {
		DemoteResult demoteFilter = new DemoteResult();
		StoreKeyword storeKeyword = new StoreKeyword(store, new Keyword(name));
		demoteFilter.setStoreKeyword(storeKeyword);
		// load only non-expired items
		SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(demoteFilter, DateTime.now(), null, 0, 0);
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