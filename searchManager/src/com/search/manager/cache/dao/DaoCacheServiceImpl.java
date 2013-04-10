package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.authentication.dao.UserAuthenticationProvider;
import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

@Service(value="daoCacheService")
@RemoteProxy(
		name = "DAOCacheServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "daoCacheService")
)
public class DaoCacheServiceImpl implements DaoCacheService {

	private static Logger logger = Logger.getLogger(DaoCacheServiceImpl.class);
	
	DaoCacheServiceImpl instance;
	
	@Autowired private UserAuthenticationProvider userDetailsService;
	@Autowired private KeywordCacheDao keywordCacheDao;
	@Autowired private ElevateCacheDao elevateCacheDao;
	@Autowired private ExcludeCacheDao excludeCacheDao;
	@Autowired private DemoteCacheDao demoteCacheDao;
	@Autowired private FacetSortCacheDao facetSortCacheDao;
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
	
	public void setDemoteCacheDao(DemoteCacheDao demoteCacheDao) {
		this.demoteCacheDao = demoteCacheDao;
	}
	
	public void setFacetSortCacheDao(FacetSortCacheDao facetSortCacheDao) {
		this.facetSortCacheDao = facetSortCacheDao;
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
	
	public boolean reloadAllKeywords(Store store) throws DaoException, DataException{
		if(resetAllkeywords(store))
			return keywordCacheDao.reloadAllKeywords(store);
		return false;
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
			logger.error(e,e);
		} catch (DataException e) {
			logger.error(e,e);
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
			//if(hasExactMatchKey(storeKeyword)){
				CacheModel<ElevateResult> cache = elevateCacheDao.getCachedObject(storeKeyword);
				if (cache != null && CollectionUtils.isNotEmpty(cache.getList())) {
					return cache.getList();					
				}
			//}
		}catch (Exception e) {
			logger.error(e,e);
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
			//if(hasExactMatchKey(storeKeyword)){ // quick fix temp
				CacheModel<ExcludeResult> cache = excludeCacheDao.getCachedObject(storeKeyword);
				if (cache != null && CollectionUtils.isNotEmpty(cache.getList())) {
					return cache.getList();					
				}
			//}
		}catch (Exception e) {
			logger.error(e,e);
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
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword) {
		CacheModel<RedirectRule> rule = null;
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			rule = redirectCacheDao.getCachedObject(storeKeyword);
			if (rule != null) {
				return rule.getObj();
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve redirect rule", e);
		}
		return null;
	}
	
	@Override
	public boolean resetRedirectRule(StoreKeyword storeKeyword) throws DaoException, DataException {
		return redirectCacheDao.reset(storeKeyword);
	}

	@Override
	public Relevancy getRelevancyRule(StoreKeyword storeKeyword) {
		try {
			CacheModel<Relevancy> cache = relevancyCacheDao.getCachedObject(storeKeyword);
			if (cache == null || cache.getObj() == null) {
				cache = relevancyCacheDao.getDefaultRelevancy(storeKeyword.getStore());
			}
			Relevancy relevancy = null;
			if (cache != null) {
				relevancy = cache.getObj();
			}
			return relevancy;
		} catch (Exception e) {
			logger.error("Failed to retrieve relevancy rule", e);
		}
		return null;
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

	@Override
	public boolean loginUser(UserDetailsImpl userDetails) {
		UserDetailsImpl user = new UserDetailsImpl();
		user.setUsername(userDetails.getUsername());
		user.setFullName(userDetails.getFullName());
		user.setLoggedInTime(DateTime.now());
		if (user != null && StringUtils.isNotEmpty(user.getUsername())) {
			return userCacheDao.addUser(user);
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

	public boolean setForceReload(Store store) {
		boolean result = setForceReloadElevate(store);
		result &= setForceReloadExclude(store);
		result &= setForceReloadRedirect(store);
		result &= setForceReloadRelevancy(store);
		result &= setForceReloadFacetSort(store);
		return result;
	}
	
	public boolean setForceReloadElevate(Store store) {
		boolean result = elevateCacheDao.forceUpdateCache(store);
		if (result) {
			logger.info("Forcing reload of elevate rules for : " + store);
		}
		else {
			logger.error("Failed to force reload of elevate rules for : " + store);
		}
		return result;
	}
	
	public boolean setForceReloadExclude(Store store) {
		boolean result = excludeCacheDao.forceUpdateCache(store);
		if (result) {
			logger.info("Forcing reload of exclude rules for : " + store);
		}
		else {
			logger.error("Failed to force reload of exclude rules for : " + store);
		}
		return result;
	}
	
	public boolean setForceReloadRedirect(Store store) {
		boolean result = redirectCacheDao.forceUpdateCache(store);
		if (result) {
			logger.info("Forcing reload of redirect rules for : " + store);
		}
		else {
			logger.error("Failed to force reload of redirect rules for : " + store);
		}
		return result;
	}
	
	public boolean setForceReloadRelevancy(Store store) {
		boolean result = relevancyCacheDao.forceUpdateCache(store);
		if (result) {
			logger.info("Forcing reload of relevancy rules for : " + store);
		}
		else {
			logger.error("Failed to force reload of relevancy rules for : " + store);
		}
		return result;
	}
	
	public List<UserDetailsImpl> getLoggedInUsers() throws DaoException, DataException {
		List<UserDetailsImpl> users = new ArrayList<UserDetailsImpl>();
		for (String username: userDetailsService.getUserNames()) {
			UserDetailsImpl user = getUser(username);
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}
	
	@RemoteMethod
	public RecordSet<UserDetailsImpl> getAllLoggedInUser() throws DaoException, DataException {
		List<UserDetailsImpl> users = getLoggedInUsers();
		return new RecordSet<UserDetailsImpl>(users, CollectionUtils.size(users));
	}

	@Override
	public boolean loadDemoteRules(Store store) throws DaoException,
			DataException {
		return demoteCacheDao.reload(store);
	}

	@Override
	public List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword) {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			CacheModel<DemoteResult> cache = demoteCacheDao.getCachedObject(storeKeyword);
			if (cache != null && CollectionUtils.isNotEmpty(cache.getList())) {
				return cache.getList();					
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean updateDemoteRules(DemoteResult demoteResult)
			throws DaoException, DataException {
		try {	
			return demoteCacheDao.reload(demoteResult);
		}catch (Exception e) {
			logger.error("Failed to update demoteResult", e);
		}
		return false;
	}

	@Override
	public boolean resetDemoteRule(StoreKeyword storeKeyword)
			throws DaoException, DataException {
		return demoteCacheDao.reset(storeKeyword);
	}

	@Override
	public boolean setForceReloadDemote(Store store) {
		boolean result = demoteCacheDao.forceUpdateCache(store);
		if (result) {
			logger.info("Forcing reload of demote rules for : " + store);
		}
		else {
			logger.error("Failed to force reload of demote rules for : " + store);
		}
		return result;
	}

	@Override
	public boolean loadFacetSortRules(Store store) throws DaoException,
			DataException {
		return facetSortCacheDao.reload(store);
	}

	@Override
	public FacetSort getFacetSortRule(StoreKeyword storeKeyword) {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			CacheModel<FacetSort> cache = facetSortCacheDao.getCachedObject(storeKeyword);
			if (cache != null) {
				return cache.getObj();					
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return null;
	}
	
	@Override
	public FacetSort getFacetSortRule(Store store, String name) {
		try {
			DAOValidation.checkFacetSortPK(store, name);
				CacheModel<FacetSort> cache = facetSortCacheDao.getCachedObject(store, name);
				if (cache != null) {
					return cache.getObj();					
				}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return null;
	}

	@Override
	public boolean updateFacetSortRule(FacetSort facetSort)
			throws DaoException, DataException {
		try {	
			return facetSortCacheDao.reload(facetSort);
		}catch (Exception e) {
			logger.error("Failed to update facetSort", e);
		}
		return false;
	}

	@Override
	public boolean resetFacetSortRule(StoreKeyword storeKeyword)
			throws DaoException, DataException {
		return facetSortCacheDao.reset(storeKeyword);
	}
	
	@Override
	public boolean resetFacetSortRule(Store store, String name)
			throws DaoException, DataException {
		return facetSortCacheDao.reset(store, name);
	}

	@Override
	public boolean setForceReloadFacetSort(Store store) {
		boolean result = facetSortCacheDao.forceUpdateCache(store);
		if (result) {
			logger.info("Forcing reload of facet sort rules for : " + store);
		}
		else {
			logger.error("Failed to force reload of facet sort rules for : " + store);
		}
		return result;
	}

	@Override
	public List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword) throws DaoException {
		// TODO: implement
		return new ArrayList<ElevateResult>();
	}

	@Override
	public List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword) throws DaoException {
		// TODO: implement
		return new ArrayList<ExcludeResult>();
	}

	@Override
	public List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword) throws DaoException {
		// TODO: implement
		return new ArrayList<DemoteResult>();
	}

	@Override
    public Relevancy getRelevancyRule(Store store, String relevancyId) throws DaoException {
          if (StringUtils.containsIgnoreCase(relevancyId, "default")) {
                CacheModel<Relevancy> relevancy = relevancyCacheDao.getDefaultRelevancy(store);
                if (relevancy != null) {
                      return relevancy.getObj();
                }
          }
          return null;
    }

}
