package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.CacheService;
import com.search.manager.cache.service.LocalCacheService;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.service.UtilityService;

@Service(value="daoCacheService")
public class DaoCacheServiceImpl implements DaoCacheService {

	DaoCacheServiceImpl instance;
	
	@Autowired private KeywordCacheDao keywordCacheDao;
	@Autowired private ElevateCacheDao elevateCacheDao;
	@Autowired private ExcludeCacheDao excludeCacheDao;
	@Autowired private RedirectCacheDao redirectCacheDao;
	@Autowired private RelevancyCacheDao relevancyCacheDao;
	
	@Autowired private DaoService daoService;
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

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public void setCacheService(CacheService<CacheModel<?>> cacheService) {
		this.cacheService = cacheService;
	}

	public void setLocalCacheService(
			LocalCacheService<CacheModel<?>> localCacheService) {
		this.localCacheService = localCacheService;
	}

	@Autowired private CacheService<CacheModel<?>> cacheService;
	@Autowired private LocalCacheService<CacheModel<?>> localCacheService;
	
	private static Logger logger = Logger.getLogger(DaoCacheServiceImpl.class);
	
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
	
	public boolean resetElevateResult(StoreKeyword storeKeyword){
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
	public boolean loadExcludeResultList(Store store) throws DaoException, DataException {
		return excludeCacheDao.reload(store);
	}
	
	@Override
	public List<ElevateResult> getElevateResultList(StoreKeyword storeKeyword){
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
	public boolean updateElevateResultList(ElevateResult elevateResult){
		try {	
			return elevateCacheDao.reload(elevateResult);
		}catch (Exception e) {
			logger.error("Failed to update elevateResult", e);
		}
		return false;
	}
	
	@Override
	public List<ExcludeResult> getExcludeResultList(StoreKeyword storeKeyword){
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
	public boolean updateExcludeResultList(ExcludeResult excludeResult){
		try {	
			return excludeCacheDao.reload(excludeResult);
		}catch (Exception e) {
			logger.error("Failed to update excludeResult", e);
		}
		return false;
	}
	
	public boolean resetExcludeResult(StoreKeyword storeKeyword){
		return excludeCacheDao.reset(storeKeyword);
	}
	
	@Override
	public boolean updateRedirectRule(RedirectRule redirectRule) {
		try {
			if (redirectRule == null || StringUtils.isEmpty(redirectRule.getRuleId())) {
				return false;
			}
			CacheModel<RedirectRule> cache = null;
			RedirectRule rule = daoService.getRedirectRule(redirectRule);
			if (rule != null) {
				cache = new CacheModel<RedirectRule>();
				cache.setObj(rule);
				for (String keyword: rule.getSearchTerms()) {
					cacheService.put(CacheConstants.getCacheKey(rule.getStoreId(), CacheConstants.RULE_REDIRECT_CACHE_KEY, keyword), cache);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load cache for Redirect", e);
		}
		return false;
	}
	
	@Override
	public boolean loadRedirectRules(Store store){
		try {
			DAOValidation.checkStoreId(store);
			CacheModel<RedirectRule> cache = null;
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setStoreId(UtilityService.getStoreName());
			RecordSet<RedirectRule> rules = daoService.getRedirectRules(new SearchCriteria<RedirectRule>(redirectRule, null, null, 0, 0));
			for (RedirectRule rule: rules.getList()) {
				cache = new CacheModel<RedirectRule>();
				cache.setObj(rule);
				for (String keyword: rule.getSearchTerms()) {
					cacheService.put(CacheConstants.getCacheKey(store.getStoreId(), CacheConstants.RULE_REDIRECT_CACHE_KEY, keyword), cache);
				}
			}
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
		} catch (Exception e) {
			// no storeKeyword provided
			logger.error("No StoreKeyword specified for getRedirectRule()");
			return null;
		}

		String storeId = storeKeyword.getStoreId();
		String keywordId = storeKeyword.getKeywordId();
		boolean cacheProblem = false;
		
		try {
			try {
				if (hasExactMatchKey(storeKeyword)) {
					// valid keyword. check cache
					rule = cacheService.get(CacheConstants.getCacheKey(storeId, CacheConstants.RULE_REDIRECT_CACHE_KEY, keywordId));
					if (rule != null) {
						return rule.getObj();
					}
				}
			} catch (DataException e) {
				logger.error("Problem with cache server!", e);
				cacheProblem = true;
			}
			// retrive from DAO
			if (rule != null) {
				rule = new CacheModel<RedirectRule>();
				rule.setObj(daoService.getRedirectRule(new RedirectRule(storeId, keywordId)));
				if (!cacheProblem) {
					try {
						cacheService.put(CacheConstants.getCacheKey(storeId, CacheConstants.RULE_REDIRECT_CACHE_KEY, keywordId), rule);					
					} catch (DataException e) {
						logger.error("Problem with cache server!", e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Problem with Search Manager server!", e);			
		}
		return (rule == null) ? null : rule.getObj();
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
	public List<Relevancy> searchRelevancy(SearchCriteria<Relevancy> criteria, MatchType relevancyMatchType) {
		
		Relevancy model = criteria.getModel();
		String kw = (relevancyMatchType == null) ? null : (relevancyMatchType.equals(MatchType.MATCH_ID) ? model.getRelevancyId() : model.getRelevancyName());

		List<Relevancy> relevancyList = null;
		CacheModel<Relevancy> cache = null;
		
		try {	
			try{
				cache = (CacheModel<Relevancy>) cacheService.get(CacheConstants.getCacheKey(model.getStore().getStoreName(), CacheConstants.RELEVANCY_LIST_CACHE_KEY, kw));
			
				if(cache != null)
					relevancyList = cache.getList();
				else{
					relevancyList = daoService.searchRelevancy(criteria, relevancyMatchType).getList();
					
					if(CollectionUtils.isNotEmpty(relevancyList)){
						logger.info("Server is utilizing database connection");
						return relevancyList;
					}
				}
				
				if(CollectionUtils.isNotEmpty(relevancyList)){
					return relevancyList;
				}
			}catch (Exception e) {
				logger.error(e);
				relevancyList = daoService.searchRelevancy(criteria, relevancyMatchType).getList();
				
				if(CollectionUtils.isNotEmpty(relevancyList)){
					logger.info("Server is utilizing database connection");
					return relevancyList;
				}
			}		
		}catch (Exception e) {
			logger.error(e);
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean loadRelevancyResultList(String storeName, MatchType relevancyMatchType) throws DaoException, DataException {
		
		List<Relevancy> relevancyList = null;
		CacheModel<Relevancy> cache = null;
		
		Relevancy relevancy = new Relevancy();
		relevancy.setStore(new Store(storeName));
		relevancy.setRelevancyName("");
		SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy, null, null, 0, 0);
		
		try {
			relevancyList = daoService.searchRelevancy(criteria, relevancyMatchType).getList();
			cache = new CacheModel<Relevancy>();
			if(CollectionUtils.isNotEmpty(relevancyList))
				cache.setList(relevancyList);
			else
				cache.setList(new ArrayList<Relevancy>());
				
			try{
				cacheService.put(CacheConstants.getCacheKey(storeName,CacheConstants.RELEVANCY_LIST_CACHE_KEY, ""), cache);
				logger.info("Relevancy list has been loaded to cache <==> Size: "+relevancyList.size());
			}catch (Exception e) {
				logger.error(e);
			}	
			return true;
		} catch (DaoException e) {}  
		return false;
	}
	
	@Override
	public boolean loadRelevancyDetails(String storeName) throws DaoException{
		
		CacheModel<Relevancy> cache = null;
		Relevancy relevancy = new Relevancy();
		relevancy.setStore(new Store(storeName));
		relevancy.setRelevancyName("");
		SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy, null, null, 0, 0);

		List<Relevancy> list = searchRelevancy(criteria, MatchType.LIKE_NAME);
		
		if(CollectionUtils.isNotEmpty(list)){
			for(Relevancy rel : list){
				relevancy.setRelevancyName(rel.getRelevancyName());
				relevancy.setRelevancyId(rel.getRelevancyId());
				rel = daoService.getRelevancyDetails(relevancy);
				
				cache = new CacheModel<Relevancy>(); 
				if(rel != null)
					cache.setObj(rel);
				
				try{
					cacheService.put(CacheConstants.getCacheKey(storeName,CacheConstants.RELEVANCY_DETAILS_CACHE_KEY, rel.getRelevancyId()), cache);
					logger.info("Relevancy list has been loaded to cache: RelevancyId is "+rel.getRelevancyId());
				}catch (Exception e) {
					logger.error(e);
				}	
			}
			return true;
		}
		return false;
	}

	@Override
	public Relevancy getRelevancyDetails(Relevancy relevancy, String storeName) throws DaoException, DataException {
		
		CacheModel<Relevancy> cache = null;
		
		try {		
					try{
						cache = (CacheModel<Relevancy>) cacheService.get(CacheConstants.getCacheKey(storeName, CacheConstants.RELEVANCY_DETAILS_CACHE_KEY, relevancy.getRelevancyId()));

						if(cache != null){
							relevancy = (Relevancy)cache.getObj();
							return relevancy;
						}else{
							relevancy = daoService.getRelevancyDetails(relevancy);
							
							if(relevancy != null){
								logger.info("Server is utilizing database connection");
								return relevancy;
							}
						}
					}catch (Exception e) {
						logger.error(e);
						relevancy = daoService.getRelevancyDetails(relevancy);
						
						if(relevancy != null){
							logger.info("Server is utilizing database connection");
							return relevancy;
						}
					}
		}catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public int getRelevancyKeywordCount(StoreKeyword storeKeyword) throws DaoException, DataException {

		if (storeKeyword == null || storeKeyword.getKeywordId() == null) 
			return 0;
		
		CacheModel<Integer> cache = null;
		int count = 0;
		
		try {		
					try{
						cache = (CacheModel<Integer>) cacheService.get(CacheConstants.getCacheKey(storeKeyword.getStoreName(), CacheConstants.RELEVANCY_KEYWORD_COUNT_CACHE_KEY, storeKeyword.getKeywordId()));

						if(cache != null){
							count = (int)cache.getObj();
							return count;
						}else{
							count = daoService.getRelevancyKeywordCount(storeKeyword);
							logger.info("Server is utilizing database connection");
							return count;
						}
					}catch (Exception e) {
						logger.error(e);
						count = daoService.getRelevancyKeywordCount(storeKeyword);
						logger.info("Server is utilizing database connection");
						return count;
					}
		}catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}
	
	public RelevancyKeyword getRelevancyKeyword(RelevancyKeyword relevancyKeyword, String storeName) throws DaoException{
		
		CacheModel<RelevancyKeyword> cache = null;
		RelevancyKeyword keyword = null;
		
		try {		
					try{
						cache = (CacheModel<RelevancyKeyword>) cacheService.get(CacheConstants.getCacheKey(storeName, CacheConstants.RELEVANCY_KEYWORD_CACHE_KEY, relevancyKeyword.getRelevancy().getRelevancyId()));

						if(cache != null){
							keyword = (RelevancyKeyword)cache.getObj();
							return keyword;
						}else{
							keyword = daoService.getRelevancyKeyword(relevancyKeyword);
							
							if(keyword != null){
								logger.info("Server is utilizing database connection");
								return keyword;
							}
						}
					}catch (Exception e) {
						logger.error(e);
						keyword = daoService.getRelevancyKeyword(relevancyKeyword);
						
						if(keyword != null){
							logger.info("Server is utilizing database connection");
							return keyword;
						}
					}
		}catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public RecordSet<RelevancyKeyword> searchRelevancyKeywords(SearchCriteria<RelevancyKeyword> criteria,MatchType relevancyMatchType, ExactMatch keywordExactMatch)throws DaoException, DataException {
		
		RelevancyKeyword model = criteria.getModel();
		String kw = (relevancyMatchType == null) ? null : (relevancyMatchType.equals(MatchType.MATCH_ID) ? model.getRelevancy().getRelevancyId() : model.getRelevancy().getRelevancyName());

		List<RelevancyKeyword> relevancyKWList = null;
		CacheModel<RelevancyKeyword> cache = null;
		
//		try {	
//					try{
//						cache = (CacheModel<RelevancyKeyword>) cacheService.get(CacheConstants.getCacheKey(model.getRelevancy().getStore(), CacheConstants.RELEVANCY_SEARCH_KEYWORD_LIST_CACHE_KEY, kw));
//					
//						if(cache != null)
//							relevancyList = cache.getList();
//						else{
//							relevancyList = daoService.searchRelevancy(criteria, relevancyMatchType).getList();
//							
//							if(CollectionUtils.isNotEmpty(relevancyList)){
//								logger.info("Server is utilizing database connection");
//								return relevancyList;
//							}
//						}
//						
//						if(CollectionUtils.isNotEmpty(relevancyList)){
//							filterRelevancyListByCriteria(relevancyList, criteria);
//							return relevancyList;
//						}
//					}catch (Exception e) {
//						logger.error(e);
//						relevancyList = daoService.searchRelevancy(criteria, relevancyMatchType).getList();
//						
//						if(CollectionUtils.isNotEmpty(relevancyList)){
//							logger.info("Server is utilizing database connection");
//							return relevancyList;
//						}
//					}		
//		}catch (Exception e) {
//			logger.error(e);
//		}
//		return Collections.EMPTY_LIST;
		return null;
	}
}
