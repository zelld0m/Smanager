package com.search.manager.cache.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.LocalCacheService;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;

@Repository("userCacheDao")
public class UserCacheDao {
	
	private final static Logger logger = Logger.getLogger(UserCacheDao.class);
	
	@Autowired private LocalCacheService<CacheModel<?>> localCacheService;

	private String getCacheKey(String username) throws DataException {
		try {
			DAOValidation.checkStringNotEmpty(username, "No username provided");
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(CacheConstants.USER_CACHE_KEY, "");
	}
	
	public boolean addUser(UserDetailsImpl userDetails) {
		try {
			localCacheService.putLocalCache(getCacheKey(userDetails.getUsername()), userDetails);
			return true;
		} catch (DataException e) {
			logger.error(e,e);
		}
		return false;	
	}
	
	public boolean deleteUser(String username) {
		try {
			localCacheService.resetLocalCache(CacheConstants.getCacheKey(CacheConstants.KEYWORDS_CACHE_KEY, username));
			return true;
		} catch (DataException e) {
			logger.error(e,e);
		}		
		return false;	
	}
	
	public boolean updateUser(UserDetailsImpl userDetails) {
		try {
			localCacheService.putLocalCache(CacheConstants.getCacheKey(CacheConstants.KEYWORDS_CACHE_KEY, userDetails.getUsername()), userDetails);
			return true;
		} catch (DataException e) {
			logger.error(e,e);
		}		
		return false;
	}
	
	public UserDetailsImpl getUser(String username) {
		try {
			return localCacheService.getLocalCache(CacheConstants.getCacheKey(CacheConstants.KEYWORDS_CACHE_KEY, username));
		} catch (DataException e) {
			logger.error(e,e);
		}		
		return null;
	}

}
