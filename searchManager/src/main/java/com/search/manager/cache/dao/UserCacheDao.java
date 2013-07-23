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

@Deprecated
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
		return CacheConstants.getCacheKey(CacheConstants.USER_CACHE_KEY, username);
	}
	
	public boolean addUser(UserDetailsImpl userDetails) {
		try {
			logger.debug("adding user: " + userDetails.getUsername());
			localCacheService.putLocalCache(getCacheKey(userDetails.getUsername()), userDetails);
logger.debug(getUser(userDetails.getUsername()).getLoggedIntime());							
			return true;
		} catch (DataException e) {
			logger.error(e,e);
		}
		return false;	
	}
	
	public boolean deleteUser(String username) {
		try {
			logger.debug("removing user: " + username);
			localCacheService.resetLocalCache(getCacheKey(username));
			return true;
		} catch (DataException e) {
			logger.error(e,e);
		}
		return false;	
	}
	
	public boolean updateUser(UserDetailsImpl userDetails) {
		try {
			logger.debug("updating user: " + userDetails.getUsername());
			UserDetailsImpl user = getUser(userDetails.getUsername());
			if (user != null) {
logger.debug(user.getLoggedIntime());				
				user.setCurrentPage(userDetails.getCurrentPage());
			}
			localCacheService.putLocalCache(getCacheKey(userDetails.getUsername()), user);
			return true;
		} catch (DataException e) {
			logger.error(e,e);
		}
		return false;
	}
	
	public UserDetailsImpl getUser(String username) {
		try {
			return localCacheService.getLocalCache(getCacheKey(username));
		} catch (DataException e) {
			logger.error(e,e);
		}
		return null;
	}

}
