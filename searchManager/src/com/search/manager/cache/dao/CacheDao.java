package com.search.manager.cache.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.CacheService;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.exception.DataException;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public abstract class CacheDao<T> {
	
	@Autowired protected DaoService daoService;
	@Autowired protected KeywordCacheDao keywordCacheDao;
	@Autowired protected CacheService<CacheModel<?>> cacheService;
	
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	public void setKeywordCacheDao(KeywordCacheDao keywordCacheDao) {
		this.keywordCacheDao = keywordCacheDao;
	}
	public void setCacheService(CacheService<CacheModel<?>> cacheService) {
		this.cacheService = cacheService;
	}

	private Logger logger = Logger.getLogger(CacheDao.class);
	
	/**
	 * Get the cache key
	 * @param storeKeyword
	 * @return
	 * @throws DataException
	 */
	protected abstract String getCacheKey(StoreKeyword storeKeyword) throws DataException;
	protected abstract String getCacheKeyInitials() throws DataException;
	protected abstract CacheModel<T> getDatabaseObject(StoreKeyword storeKeyword) throws DaoException;
	public abstract boolean reload(T bean) throws DataException, DaoException;
	public abstract boolean reload(List<T> list) throws DataException, DaoException;

	/**
	 * Reload the entire cache data for store
	 * @param store
	 * @return
	 * @throws DataException
	 * @throws DaoException
	 */
	public boolean reload(Store store) throws DataException, DaoException {
		String storeId = store.getStoreId();
		try {
			List<String> kwList = keywordCacheDao.getAllKeywords(store);
			for(String kw : kwList){
				StoreKeyword storeKeyword = new StoreKeyword(storeId, kw);
				reload(storeKeyword);
			}
			return true;
		} catch (DaoException e) {
			logger.error(e,e);			
		} catch (DataException e) {
			logger.error(e,e);
		}
		return false;		
	}
	
	/**
	 * Get cachedObject
	 * @param storeKeyword
	 * @return
	 * @throws DataException
	 */
	public CacheModel<T> getCachedObject(StoreKeyword storeKeyword) throws DataException {
		CacheModel<T> cache = null;
		boolean cacheError = false;
		try {
			String key = getCacheKey(storeKeyword);
			cache = cacheService.get(key);
			if (cache != null) {
				Date date = getforceUpdateCacheDate(storeKeyword.getStore());
				if (date != null && cache.getUploadedDate().before(date)) { // obsolete data, force a reload
					cache = null;
					reset(storeKeyword);
				}
			}
		} catch (Exception e) {
			logger.error("Problem accessing cache.", e);
			cacheError = true;
		}
		
		if (cache == null) {
			try {
				cache = getDatabaseObject(storeKeyword);
				if (cache != null) {
					logger.info("Retrieved rule from database.");					
					if (!cacheError) {
						try {
							pushToCache(storeKeyword, cache);
						} catch (Exception e) {
							logger.error("Cannot cache object", e);						
						}					
					}
				}
			} catch (Exception e) {
				logger.error("Cannot retrieve rule from database.", e);
			}
		}
		return cache;
	}
	
	public CacheModel<T> getCachedObject(String key) throws DataException {
		return cacheService.get(key);
	}
	
	public boolean reset(String key) {
		try {
			cacheService.reset(key);
			return true;
		} catch (Exception e) {
			logger.error("Failed to reset " + key, e);
		}
		return false;
	}
	
	protected boolean cacheObject(String key, CacheModel<T> t) throws DataException {
		try{
			if (t != null) {
				cacheService.put(key, t);
				logger.info("pushed to cache key " + key);
				return true;				
			}
		} catch (Exception e) {
			logger.error("Failed to cache key " + key, e);
			throw new DataException(e);
		}
		logger.info("cannot push null object to cache key " + key);
		return false;
	}
	
	/**
	 * Reload configuration for storeKeyword
	 * @param storeKeyword
	 * @return
	 * @throws DataException
	 * @throws DaoException
	 */
	public boolean reload(StoreKeyword storeKeyword) throws DataException, DaoException {
		return pushToCache(storeKeyword, getDatabaseObject(storeKeyword));
	}
	
	/**
	 * Clear the cache entry for storeKeyword.
	 */
	public boolean reset(StoreKeyword storeKeyword) {
		try {
			String key = getCacheKey(storeKeyword);
			cacheService.reset(key);
			return true;
		} catch (Exception e) {
			logger.error("Failed to reset " + storeKeyword, e);
		}
		return false;
	}

	protected boolean pushToCache(StoreKeyword storeKeyword, CacheModel<T> t) throws DataException {
		try{
			if (t != null) {
				return cacheObject(getCacheKey(storeKeyword), t);
			}
		} catch (Exception e) {
			logger.error("Failed to cache key for storeKeyword " + storeKeyword, e);
			throw new DataException(e);
		}
		return false;
	}
	
	public boolean forceUpdateCache(Store store) {
		try{
			String storeId = store.getStoreId();
			if (StringUtils.isNotBlank(storeId)) {
				cacheService.put(CacheConstants.getCacheKey(storeId, CacheConstants.FORCE_UPDATE_CACHE_KEY, getCacheKeyInitials()), new CacheModel<Object>());
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to set force update cache date for  " + store, e);
		}
		return false;
	}
	
	protected Date getforceUpdateCacheDate(Store store) {
		try{
			String storeId = store.getStoreId();
			if (StringUtils.isNotBlank(storeId)) {
				CacheModel<Object> cache = cacheService.get(CacheConstants.getCacheKey(storeId, CacheConstants.FORCE_UPDATE_CACHE_KEY, getCacheKeyInitials()));
				if (cache != null) {
					return cache.getUploadedDate();
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get force update cache date for  " + store, e);
		}
		return null;
	}
	
	
}
