package com.search.manager.cache.dao;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

@Service(value="daoCacheService")
public class DaoCacheServiceImpl implements DaoCacheService {

	private static Logger logger = Logger.getLogger(DaoCacheServiceImpl.class);
	
	DaoCacheServiceImpl instance;
	
	@Autowired private KeywordCacheDao keywordCacheDao;
	@Autowired private ElevateCacheDao elevateCacheDao;
	@Autowired private ExcludeCacheDao excludeCacheDao;
	@Autowired private RedirectCacheDao redirectCacheDao;
	@Autowired private RelevancyCacheDao relevancyCacheDao;
	@Autowired private UserCacheDao userCacheDao;
	
	public void setInstance(DaoCacheServiceImpl instance) {
		this.instance = instance;
	}

	public void setKeywordCacheDao(KeywordCacheDao keywordCacheDao) {
		this.keywordCacheDao = keywordCacheDao;
	}

	public void setElevateCacheDao(ElevateCacheDao elevateCacheDao) {
		this.elevateCacheDao = elevateCacheDao;
	}

	public void setExcludeCacheDao(ExcludeCacheDao excludeCacheDao) {
		this.excludeCacheDao = excludeCacheDao;
	}

	public void setRedirectCacheDao(RedirectCacheDao redirectCacheDao) {
		this.redirectCacheDao = redirectCacheDao;
	}

	public void setRelevancyCacheDao(RelevancyCacheDao relevancyCacheDao) {
		this.relevancyCacheDao = relevancyCacheDao;
	}

	public DaoCacheServiceImpl() {
		instance = this;
	}

	@Override
	public List<String> getAllKeywords(Store store) throws DaoException, DataException{
		return keywordCacheDao.getAllKeywords(store);
	}
	
	@Override
	public boolean resetAllkeywords(Store store) {
		return keywordCacheDao.resetAllKeywords(store.getStoreId());
	}
	
	public boolean resetElevateRule(StoreKeyword storeKeyword){
		return elevateCacheDao.reset(storeKeyword);
	}
	
	@Override
	public boolean hasExactMatchKey(StoreKeyword storeKeyword){
		try {
			List<String> kwList = getAllKeywords(storeKeyword.getStore());
			return kwList.contains(storeKeyword.getKeywordId());
		} catch (DaoException e) {
			logger.error(e);
		} catch (DataException e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean loadElevateRules(Store store) throws DaoException, DataException {
		return elevateCacheDao.reload(store);
	}
	
	@Override
	public boolean loadExcludeRules(Store store) throws DaoException, DataException {
		return excludeCacheDao.reload(store);
	}
	
	@Override
	public List<ElevateResult> getElevateRules(StoreKeyword storeKeyword){
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			if(hasExactMatchKey(storeKeyword)){
				CacheModel<ElevateResult> cache = elevateCacheDao.getCachedObject(storeKeyword);
				if (cache == null || CollectionUtils.isNotEmpty(cache.getList())) {
					return cache.getList();					
				}
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return Collections.emptyList();
	}
	
	@Override
	public boolean updateElevateRule(ElevateResult elevateResult){
		try {	
			return elevateCacheDao.reload(elevateResult);
		}catch (Exception e) {
			logger.error("Failed to update elevateResult", e);
		}
		return false;
	}
	
	@Override
	public List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword){
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			if(hasExactMatchKey(storeKeyword)){
				CacheModel<ExcludeResult> cache = excludeCacheDao.getCachedObject(storeKeyword);
				if (cache == null || CollectionUtils.isNotEmpty(cache.getList())) {
					return cache.getList();					
				}
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return Collections.emptyList();		
	}
	
	@Override
	public boolean updateExcludeRules(ExcludeResult excludeResult){
		try {	
			return excludeCacheDao.reload(excludeResult);
		}catch (Exception e) {
			logger.error("Failed to update excludeResult", e);
		}
		return false;
	}
	
	public boolean resetExcludeRule(StoreKeyword storeKeyword){
		return excludeCacheDao.reset(storeKeyword);
	}
	
	@Override
	public boolean updateRedirectRule(RedirectRule redirectRule) {
		try {
			if (redirectRule == null || StringUtils.isEmpty(redirectRule.getRuleId())) {
				return false;
			}
			return redirectCacheDao.reload(redirectRule);
		} catch (Exception e) {
			logger.error("Failed to load cache for Redirect rule", e);
		}
		return false;
	}
	
	@Override
	public boolean loadRedirectRules(Store store){
		try {
			DAOValidation.checkStoreId(store);
			return redirectCacheDao.reload(store);
		} catch (Exception e) {
			logger.error("Failed to load cache for Redirect", e);
		}
		return false;
	}
	
	@Override
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword) throws DaoException, DataException {
		CacheModel<RedirectRule> rule = null;
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			rule = redirectCacheDao.getCachedObject(storeKeyword);
			return rule.getObj();
		} catch (Exception e) {
			// no storeKeyword provided
			logger.error("No StoreKeyword specified for getRedirectRule()");
		}
		return null;
	}
	
	@Override
	public boolean resetRedirectRule(StoreKeyword storeKeyword) throws DaoException, DataException {
		return redirectCacheDao.reset(storeKeyword);
	}

	@Override
	public Relevancy getRelevancyRule(StoreKeyword storeKeyword) throws DaoException, DataException {
		CacheModel<Relevancy> cache = relevancyCacheDao.getCachedObject(storeKeyword);
		if (cache == null || cache.getObj() == null) {
			cache = relevancyCacheDao.getDefaultRelevancy(storeKeyword.getStore());
		}
		Relevancy relevancy = null;
		if (cache != null) {
			relevancy = cache.getObj();
		}
		return relevancy;
	}

	@Override
	public Relevancy getDefaultRelevancyRule(Store store) throws DaoException, DataException {
		CacheModel<Relevancy> cache = relevancyCacheDao.getDefaultRelevancy(store);
		Relevancy relevancy = null;
		if (cache != null) {
			relevancy = cache.getObj();
		}
		return relevancy;
	}

	@Override
	public boolean loadRelevancyRules(Store store) throws DaoException, DataException {
		return relevancyCacheDao.reload(store);
	}

	@Override
	public boolean resetRelevancyRule(StoreKeyword storeKeyword) throws DaoException, DataException {
		return relevancyCacheDao.reset(storeKeyword);
	}

	@Override
	public boolean updateRelevancyRule(Relevancy relevancy) throws DaoException, DataException {
		return relevancyCacheDao.reload(relevancy);
	}

	private void ObjectByteReader(Object obj){ // Use this method to get Object size
		java.io.ObjectOutputStream outputStream = null;
        try {
            outputStream = new java.io.ObjectOutputStream(new java.io.FileOutputStream("C:\\Search Manager\\Test Ground\\test.txt"));
            outputStream.writeObject(obj);
        } catch (java.io.FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the ObjectOutputStream
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
	}

	@Override
	public boolean loginUser(UserDetailsImpl userDetails) {
		UserDetailsImpl user = new UserDetailsImpl();
		user.setFullName(userDetails.getUsername());
		user.setUsername(userDetails.getFullName());
		if (userDetails != null && StringUtils.isNotEmpty(userDetails.getUsername())) {
			return userCacheDao.addUser(userDetails);
		}
		return false;
	}

	@Override
	public boolean logoutUser(String username) {
		if (StringUtils.isNotEmpty(username)) {
			return userCacheDao.deleteUser(username);			
		}
		return false;
	}

	@Override
	public UserDetailsImpl getUser(String username) throws DaoException, DataException {
		if (StringUtils.isNotEmpty(username)) {
			return userCacheDao.getUser(username);			
		}
		return null;
	}

	@Override
	public boolean setUserCurrentPage(String username, String currentPage) throws DaoException, DataException {
		if (StringUtils.isNotEmpty(username)) {
			UserDetailsImpl user = userCacheDao.getUser(username);	
			if (user != null) {
				user.setCurrentPage(currentPage);
				userCacheDao.updateUser(user);
				return true;
			}
		}
		return false;
	}
	
}
