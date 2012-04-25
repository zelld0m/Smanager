package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.LocalCacheService;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

@Repository("keywordCacheDao")
public class KeywordCacheDao {
	
	private final static Logger logger = Logger.getLogger(KeywordCacheDao.class);
	
	@Autowired private DaoService daoService;
	@Autowired private LocalCacheService<CacheModel<?>> localCacheService;
	
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public void setLocalCacheService(
			LocalCacheService<CacheModel<?>> localCacheService) {
		this.localCacheService = localCacheService;
	}

	private String getCacheKey(Store store) throws DaoException, DataException {
		try {
			DAOValidation.checkStoreId(store);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(store.getStoreId(), CacheConstants.KEYWORDS_CACHE_KEY, "");
	}
	
	public List<String> getAllKeywords(Store store) throws DaoException, DataException{
		try {
			DAOValidation.checkStoreId(store);
		} catch (Exception e) {
			throw new DataException(e);
		}
		List<String> kwList = new ArrayList<String>();
		CacheModel<String> cache = null;
		List<StoreKeyword> keywordList = null;
		try{
			cache =	localCacheService.getLocalCache(getCacheKey(store));			
			if(cache != null && cache.getList().size() > 0) {
				return cache.getList();				
			}
		} catch (Exception e) {
			logger.error(e);			
		}

		try {
			keywordList = daoService.getAllKeywords(store.getStoreId()).getList();	
			if(CollectionUtils.isNotEmpty(keywordList)) {
				for(StoreKeyword key : keywordList){
					kwList.add(key.getKeywordId());
				}
				cache = new CacheModel<String>();
				cache.setList(kwList);
				
				try{
					localCacheService.putLocalCache(getCacheKey(store), cache);
				}catch (Exception e) {
					logger.error(e);
				}
				return kwList;
			}
		} catch (DaoException e) {
			logger.error(e);
		}
		return kwList;
	}
	
	public boolean resetAllKeywords(String storeId){
		try {
			localCacheService.resetLocalCache(CacheConstants.getCacheKey(storeId, CacheConstants.KEYWORDS_CACHE_KEY, ""));
			return true;
		} catch (DataException e) {
			logger.error(e);
		}
		return false;	
	}
}
