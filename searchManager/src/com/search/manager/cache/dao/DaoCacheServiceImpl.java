package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.CacheService;
import com.search.manager.cache.service.LocalCacheService;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.Constants;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.SearchHelper;

@Service(value="daoCacheService")
public class DaoCacheServiceImpl implements DaoCacheService {

	DaoCacheServiceImpl instance;
	
	@Autowired private DaoService daoService;
	@Autowired private CacheService<CacheModel<?>> cacheService;
	@Autowired private LocalCacheService<CacheModel<?>> localCacheService;
	
	private static Logger logger = Logger.getLogger(DaoCacheServiceImpl.class);
	
	public DaoCacheServiceImpl() {
		instance = this;
	}
	
	public void setCacheService(CacheService<CacheModel<?>> cacheService_) {
		cacheService = cacheService_;
	}
	
	public void setLocalCacheService(LocalCacheService<CacheModel<?>> localCacheService_) {
		localCacheService = localCacheService_;
	}
	
	public void setDaoService(DaoService daoService_) {
		daoService = daoService_;
	}

	@Override
	public List<String> getAllKeywords(String storeName) throws DaoException, DataException{
		
		List<String> kwList = null;
		CacheModel<String> cache = null;
		List<StoreKeyword> keywordList = null;
		try{
			cache =	(CacheModel<String>) localCacheService.getLocalCache(getCacheKey(storeName, CacheConstants.KEYWORDS_CACHE_KEY, ""));
			
			if(cache != null && cache.getList().size() > 0)
				return cache.getList();
		}catch (Exception e) {}

		try {
			keywordList = daoService.getAllKeywords(storeName).getList();	
			if(CollectionUtils.isNotEmpty(keywordList)){		
				kwList = new ArrayList<String>();		
				for(StoreKeyword key : keywordList){
					kwList.add(key.getKeywordId());
				}
				cache = new CacheModel<String>();
				cache.setList(kwList);
				
				try{
					localCacheService.putLocalCache(getCacheKey(storeName, CacheConstants.KEYWORDS_CACHE_KEY, ""), cache);
				}catch (Exception e) {
					logger.error(e);
				}
	
				return kwList;
			}
		} catch (DaoException e) {
			logger.error(e);
		}

		return Collections.EMPTY_LIST;
	}
	
	public boolean resetAllkeywords(String storeName){
		try {
			cacheService.reset(getCacheKey(storeName, CacheConstants.KEYWORDS_CACHE_KEY, ""));
			return true;
		} catch (DataException e) {
			logger.error(e);
		}
		return false;	
	}
	
	public boolean resetElevateResult(String storeName, String kw){
		try {
			if(kw != null && !"".equals(kw)){	
				if(hasExactMatchKey(storeName,kw)){
					cacheService.reset(getCacheKey(storeName, CacheConstants.ELEVATED_LIST_CACHE_KEY, kw));
					return true;
				}
			}
		} catch (DataException e) {
			logger.error(e);
		}
		return false;
	}
	
	public boolean hasExactMatchKey(String storeName, String kw){
		try {
			List<String> kwList = getAllKeywords(storeName);
			return kwList.contains(kw.trim());
		} catch (DaoException e) {} catch (DataException e) {
			logger.error(e);
		}
		return false;
	}
	
	private String getCacheKey(String storeName, String type,String kw){
		
		if("macmall".equalsIgnoreCase(storeName))
			storeName = "mc";
		else if("pcmall".equalsIgnoreCase(storeName))
			storeName = "pc";
		else if("onsale".equalsIgnoreCase(storeName))
			storeName = "ol";

		return CacheConstants.SEARCH_CACHE_KEY+"."+storeName+"_"+type+kw.replace(" ", "_");
	}
	
	@Override
	public boolean loadElevateResultList(String storeName) throws DaoException {
		
		List<ElevateResult> elevatedList = null;
		CacheModel<ElevateResult> cache = null;
		ElevateResult elevateFilter = new ElevateResult();
		
		try {
			List<String> kwList = getAllKeywords(storeName);
			
			for(String kw : kwList){
				StoreKeyword sk = new StoreKeyword(storeName, kw);
				elevateFilter.setStoreKeyword(sk);
				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
				
				elevatedList = daoService.getElevateResultList(criteria).getList();	
				cache = new CacheModel<ElevateResult>();
				if(CollectionUtils.isNotEmpty(elevatedList))
					cache.setList(elevatedList);
				else
					cache.setList(new ArrayList<ElevateResult>());
				
				try{
					cacheService.put(getCacheKey(storeName, CacheConstants.ELEVATED_LIST_CACHE_KEY, kw), cache);
					logger.info("ElevatedList has been loaded to cache - Keyword: "+kw+" <==> Size: "+elevatedList.size());
				}catch (Exception e) {
					logger.error(e);
				}
			}
			
			return true;
		} catch (DaoException e) {} 
		  catch (DataException e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean loadExcludeResultList(String storeName) throws DaoException {
		
		List<ExcludeResult> excludeList = null;
		CacheModel<ExcludeResult> cache = null;
		ExcludeResult excludeFilter = new ExcludeResult();
		
		try {
			List<String> kwList = getAllKeywords(storeName);
			
			for(String kw : kwList){
				StoreKeyword sk = new StoreKeyword(storeName, kw);
				excludeFilter.setStoreKeyword(sk);
				SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);
				
				excludeList = daoService.getExcludeResultList(criteria).getList();
				cache = new CacheModel<ExcludeResult>();
				if(CollectionUtils.isNotEmpty(excludeList))
					cache.setList(excludeList);
				else
					cache.setList(new ArrayList<ExcludeResult>());
				try{
					cacheService.put(getCacheKey(storeName, CacheConstants.EXCLUDED_LIST_CACHE_KEY, kw), cache);
					logger.info("ExcludedList has been loaded to cache - Keyword: "+kw+" <==> Size: "+excludeList.size());
				}catch (Exception e) {
					logger.error(e);
				}
			}
			
			return true;
		} catch (DaoException e) {} 
		  catch (DataException e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public List<ElevateResult> getElevateResultList(SearchCriteria<ElevateResult> criteria, String storeName){
		
		String kw = criteria.getModel().getStoreKeyword().getKeywordId();
		List<ElevateResult> elevatedList = null;
		CacheModel<ElevateResult> cache = null;
		
		try {	
			if(kw != null && !"".equals(kw)){
				
				if(hasExactMatchKey(storeName,kw)){	
					try{
						cache = (CacheModel<ElevateResult>) cacheService.get(getCacheKey(storeName, CacheConstants.ELEVATED_LIST_CACHE_KEY, kw));
					
						if(cache != null)
							elevatedList = cache.getList();
						else{
							elevatedList = daoService.getElevateResultList(criteria).getList();
							
							if(CollectionUtils.isNotEmpty(elevatedList)){
								logger.info("Server is utilizing database connection");
								return elevatedList;
							}
						}
						
						if(CollectionUtils.isNotEmpty(elevatedList)){
							filterElevatedListByCriteria(elevatedList, criteria);
							return elevatedList;
						}
					}catch (Exception e) {
						logger.error(e);
						elevatedList = daoService.getElevateResultList(criteria).getList();
						
						if(CollectionUtils.isNotEmpty(elevatedList)){
							logger.info("Server is utilizing database connection");
							return elevatedList;
						}
					}		
				}		
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public boolean updateElevateResultList(ElevateResult elevateResult){
		
		CacheModel<ElevateResult> cache = null;

		try {	
			String storeName = elevateResult.getStoreKeyword().getStoreId();
			String kw = elevateResult.getStoreKeyword().getKeywordId();
			ElevateResult elevateFilter = new ElevateResult();
			
			if(hasExactMatchKey(storeName,kw)){
				
				StoreKeyword sk = new StoreKeyword(storeName, kw);
				elevateFilter.setStoreKeyword(sk);
				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
				List<ElevateResult> list = daoService.getElevateResultList(criteria).getList();
				
				if(list != null){
					cache = new CacheModel<ElevateResult>();
					cache.setList(list);
					
					try{
						cacheService.put(getCacheKey(storeName, CacheConstants.ELEVATED_LIST_CACHE_KEY, kw), cache);
						return true;
					}catch(Exception e){
						logger.error(e);
					}
				}
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public RecordSet<ElevateProduct> getElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria, String storeName){
		List<ElevateResult> list = getElevateResultList(criteria, storeName);
		LinkedHashMap<String, ElevateProduct> map = new LinkedHashMap<String, ElevateProduct>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (ElevateResult e: list) {
			ElevateProduct ep = new ElevateProduct();
			ep.setEdp(e.getEdp());
			ep.setLocation(e.getLocation());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			map.put(e.getEdp(), ep);
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<ElevateProduct>(new ArrayList<ElevateProduct>(map.values()),list.size());
	}
	
	@Override
	public int getElevateResultCount(SearchCriteria<ElevateResult> criteria, String storeName){
		return getElevateResultList(criteria, storeName).size();
	}
	
	@Override
	public List<ExcludeResult> getExcludeResultList(SearchCriteria<ExcludeResult> criteria, String storeName){
		
		String kw = criteria.getModel().getStoreKeyword().getKeywordId();
		List<ExcludeResult> excludeList = null;
		CacheModel<ExcludeResult> cache = null;
		
		try {	
			if(kw != null && !"".equals(kw)){
				
				if(hasExactMatchKey(storeName,kw)){	
					
					try{
						cache = (CacheModel<ExcludeResult>) cacheService.get(getCacheKey(storeName, CacheConstants.EXCLUDED_LIST_CACHE_KEY, kw));

						if(cache != null)
							excludeList = cache.getList();
						else{
							excludeList = daoService.getExcludeResultList(criteria).getList();
							
							if(CollectionUtils.isNotEmpty(excludeList)){
								logger.info("Server is utilizing database connection");
								return excludeList;
							}
						}
						
						if(CollectionUtils.isNotEmpty(excludeList)){
							filterExcludedListByCriteria(excludeList, criteria);
							return excludeList;
						}
					}catch (Exception e) {
						logger.error(e);
						excludeList = daoService.getExcludeResultList(criteria).getList();
						
						if(CollectionUtils.isNotEmpty(excludeList)){
							logger.info("Server is utilizing database connection");
							return excludeList;
						}
					}
				}
			} 
		}catch (Exception e) {
			logger.error(e);
		}
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public RecordSet<Product> getExcludedProducts(String serverName, SearchCriteria<ExcludeResult> criteria, String storeName){
		
		List<ExcludeResult> list = getExcludeResultList(criteria, storeName);
		LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (ExcludeResult e: list) {
			Product ep = new Product();
			ep.setEdp(e.getEdp());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			map.put(e.getEdp(), ep);
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<Product>(new ArrayList<Product>(map.values()),list.size());
	}
	
	@Override
	public int getExcludeResultCount(SearchCriteria<ExcludeResult> criteria, String storeName){
		return getExcludeResultList(criteria, storeName).size();
	}
	
	@Override
	public boolean updateExcludeResultList(ExcludeResult excludeResult){
		
		CacheModel<ExcludeResult> cache = null;

		try {	
			String storeName = excludeResult.getStoreKeyword().getStoreId();
			String kw = excludeResult.getStoreKeyword().getKeywordId();
			ExcludeResult excludeFilter = new ExcludeResult();
			
			if(hasExactMatchKey(storeName,kw)){
				StoreKeyword sk = new StoreKeyword(storeName, kw);
				excludeFilter.setStoreKeyword(sk);
				SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);

				List<ExcludeResult> list = daoService.getExcludeResultList(criteria).getList();
				
				if(list != null){
					cache = new CacheModel<ExcludeResult>();
					cache.setList(list);
					try{
						cacheService.put(getCacheKey(storeName, CacheConstants.EXCLUDED_LIST_CACHE_KEY, kw), cache);
						return true;
					}catch (Exception e) {
						logger.error(e);
					}
				}	
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean updateRedirectRule(String storeName){
		
		CacheModel<String> cache = null;
		Map<String,String> fqRule = null;

		try {	
			RecordSet<RedirectRule> rRuleSet = daoService.getRedirectRule(null, null, null, null, null);
			
			if(rRuleSet != null && rRuleSet.getTotalSize() > 0){
				fqRule = new HashMap<String, String>();
				List<RedirectRule> ruleList = rRuleSet.getList();
				for (RedirectRule rule : ruleList) {
					String[] searchTerms = rule.getSearchTerm().split(Constants.DBL_ESC_PIPE_DELIM);
					for (String searchTerm : searchTerms) {
						fqRule.put((rule.getStoreId()+searchTerm).toLowerCase(), rule.getCondition());
					}
				}
				
				if(fqRule != null && fqRule.size() > 0){
					cache = new CacheModel<String>();
					cache.setMap(fqRule);
					try{
						cacheService.put(getCacheKey(storeName, CacheConstants.RULE_REDIRECT_CACHE_KEY, ""), cache);
						return true;
					}catch (Exception e) {
						logger.error(e);
						localCacheService.putLocalCache(CacheConstants.RULE_REDIRECT_CACHE_KEY, cache);
						logger.info("Server is utilizing local heap memory");
						return true;
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public String getRedirectRule(String storeName, String keyword){
		
		CacheModel<String> cache = new CacheModel<String>();

		try {	
			if(isRedirectRuleLoaded(cache, storeName)){
				String rule = cache.getMap().get(keyword.toLowerCase());

				StringBuilder fq = new StringBuilder();
				if (rule != null) {
					fq = fq.append(rule.replace(Constants.DBL_PIPE_DELIM, Constants.OR));
				}
				if (fq.length()>0) {
					fq.insert(0,"(").append(")");
				}
				return fq.toString();
			}else{
				updateRedirectRule(storeName);	
				if(isRedirectRuleLoaded(cache, storeName)){
					String rule = cache.getMap().get(keyword.toLowerCase());
					if(rule == null)
						return "";
					
					StringBuilder fq = new StringBuilder();
					if (rule != null) {
						fq = fq.append(rule.replace(Constants.DBL_PIPE_DELIM, Constants.OR));
					}
					if (fq.length()>0) {
						fq.insert(0,"(").append(")");
					}
					return fq.toString();
				}else{
					return "";
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
		return "";
	}
	
	private void filterElevatedListByCriteria(List<ElevateResult> elevatedList, SearchCriteria<ElevateResult> criteria){

		ListIterator<ElevateResult>  list = elevatedList.listIterator();
		int cnt = 0;
		
		while(list.hasNext()) {
			ElevateResult result = list.next(); 
			cnt++;
	
			if(criteria.getStartDate() != null){
				// remove all records below starting date : remove element when val is 1 or greater than 0
				if(DateAndTimeUtils.compare(criteria.getStartDate(), result.getExpiryDate()) > 0){
					list.remove();
				}
				if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}	
			}else if(criteria.getEndDate() != null){
				// remove all records beyond ending date : remove element when val is -1,0 or less than 1
				if(DateAndTimeUtils.compare(criteria.getEndDate(), result.getExpiryDate()) < 1){
					list.remove();
				}
				if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}	
			}
		} 
	}
	
	private void filterExcludedListByCriteria(List<ExcludeResult> excludedList, SearchCriteria<ExcludeResult> criteria){

		ListIterator<ExcludeResult>  list = excludedList.listIterator();
		int cnt = 0;
		
		while(list.hasNext()) {
			ExcludeResult result = list.next(); 
			cnt++;
	
			if(criteria.getStartDate() != null){
				// remove all records below starting date : remove element when val is 1 or greater than 0
				if(DateAndTimeUtils.compare(criteria.getStartDate(), result.getExpiryDate()) > 0){
					list.remove();
				}
				if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}	
			}else if(criteria.getEndDate() != null){
				// remove all records beyond ending date : remove element when val is -1,0 or less than 1
				if(DateAndTimeUtils.compare(criteria.getEndDate(), result.getExpiryDate()) < 1){
					list.remove();
				}
				if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}	
			}
		} 
	}
	
	private void filterRelevancyListByCriteria(List<Relevancy> relevancyList, SearchCriteria<Relevancy> criteria){

		ListIterator<Relevancy>  list = relevancyList.listIterator();
		int cnt = 0;
		
		while(list.hasNext()) {
			Relevancy result = list.next(); 
			cnt++;
	
			if(criteria.getStartDate() != null && criteria.getEndDate() != null){
				if(DateAndTimeUtils.compare(criteria.getStartDate(), result.getStartDate()) > 0 && DateAndTimeUtils.compare(criteria.getEndDate(), result.getEndDate()) < 1){
					list.remove();
				}

				if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}		
			}
		} 
	}
	
	private void filterRelevancyKeywordListByCriteria(List<RelevancyKeyword> relevancyKeywordList, SearchCriteria<RelevancyKeyword> criteria){

		ListIterator<RelevancyKeyword>  list = relevancyKeywordList.listIterator();
		int cnt = 0;
		
		while(list.hasNext()) {
			cnt++;
	
			if(criteria.getStartRow() != null && criteria.getEndRow() != null){
				if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}		
			}
		} 
	}

	private boolean isRedirectRuleLoaded(CacheModel<String> cache, String storeName){
		try{
			CacheModel<String> temp = (CacheModel<String>) cacheService.get(getCacheKey(storeName, CacheConstants.RULE_REDIRECT_CACHE_KEY, ""));
			
			if(temp != null && temp.getMap() != null && temp.getMap().size() > 0){
				cache.setMap(temp.getMap());
				return true;
			}
		}catch(Exception e){
			logger.error(e);
			localCacheService.getLocalCache(CacheConstants.RULE_REDIRECT_CACHE_KEY);
			logger.info("Server is utilizing local heap memory");
		}
		return false;
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
						cache = (CacheModel<Relevancy>) cacheService.get(getCacheKey(model.getStore().getStoreName(), CacheConstants.RELEVANCY_LIST_CACHE_KEY, kw));
					
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
							filterRelevancyListByCriteria(relevancyList, criteria);
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
	public boolean loadRelevancyResultList(String storeName, MatchType relevancyMatchType) throws DaoException {
		
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
				cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_LIST_CACHE_KEY, ""), cache);
				logger.info("Relevancy list has been loaded to cache <==> Size: "+relevancyList.size());
			}catch (Exception e) {
				logger.error(e);
			}	
			return true;
		} catch (DaoException e) {}  
		return false;
	}
	
	@Override
	public boolean loadRelevancyDetails(String storeName, String ruleId) throws DaoException{
		
		CacheModel<Relevancy> cache = null;
		Relevancy relevancy = new Relevancy();
		
		if(StringUtils.isEmpty(ruleId)){
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
						cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_DETAILS_CACHE_KEY, rel.getRelevancyId()), cache);
						logger.info("Relevancy list has been loaded to cache: RelevancyId is "+rel.getRelevancyId());
					}catch (Exception e) {
						logger.error(e);
					}	
				}
				return true;
			}
		}else{
			relevancy.setRelevancyId(ruleId);
			relevancy.setStore(new Store(storeName));
			relevancy = daoService.getRelevancy(relevancy);
			
			try{
				cache = new CacheModel<Relevancy>(); 
				if(relevancy != null && StringUtils.isNotEmpty(relevancy.getRelevancyId())){
					cache.setObj(relevancy);
					cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_DETAILS_CACHE_KEY, relevancy.getRelevancyId()), cache);
					logger.info("Relevancy list has been loaded to cache: RelevancyId is "+relevancy.getRelevancyId());
				}else{
					cacheService.reset(getCacheKey(storeName,CacheConstants.RELEVANCY_DETAILS_CACHE_KEY, ruleId));
				}
			}catch (Exception e) {
				logger.error(e);
			}	
		}
		return false;
	}

	@Override
	public Relevancy getRelevancyDetails(Relevancy relevancy, String storeName) throws DaoException {
		
		CacheModel<Relevancy> cache = null;
		
		try {		
						cache = (CacheModel<Relevancy>) cacheService.get(getCacheKey(storeName, CacheConstants.RELEVANCY_DETAILS_CACHE_KEY, relevancy.getRelevancyId()));

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
		return null;
	}
	
	@Override
	public boolean loadRelevancyKeywordCount(String storeName, String keyword){
		
		CacheModel<Integer> cache = null;
		List<String> kwList = null;
		int count = 0;
		
		try{	
			if(StringUtils.isEmpty(keyword)){
				kwList = getAllKeywords(storeName);
				
				if(CollectionUtils.isNotEmpty(kwList)){
					for(String kw : kwList){
						count = daoService.getRelevancyKeywordCount(new StoreKeyword(new Store(storeName), new Keyword(kw)));
						cache = new CacheModel<Integer>();
						cache.setObj(count);
						cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_KEYWORD_COUNT_CACHE_KEY, kw), cache);
						logger.info("Relevancy Storekeyword count for \""+kw+"\" has been loaded to cache: Count is "+count);	
					}
				}else
					return false;
			}else{
				if(hasExactMatchKey(storeName, keyword)){
					count = daoService.getRelevancyKeywordCount(new StoreKeyword(new Store(storeName), new Keyword(keyword)));
					cache = new CacheModel<Integer>();
					cache.setObj(count);
					cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_KEYWORD_COUNT_CACHE_KEY, keyword), cache);
					logger.info("Relevancy Storekeyword count for \""+keyword+"\" has been loaded to cache: Count is "+count);
				}else
					return false;
			}
			return true;	
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
	

	@Override
	public int getRelevancyKeywordCount(StoreKeyword storeKeyword) throws DaoException {

		if (storeKeyword == null || storeKeyword.getKeywordId() == null) 
			return 0;
		
		CacheModel<Integer> cache = null;
		int count = 0;
		
		try {		
				if(hasExactMatchKey(storeKeyword.getStoreId(), storeKeyword.getKeywordId())){
					cache = (CacheModel<Integer>) cacheService.get(getCacheKey(storeKeyword.getStoreName(), CacheConstants.RELEVANCY_KEYWORD_COUNT_CACHE_KEY, storeKeyword.getKeywordId()));

					if(cache != null){
						count = (int)cache.getObj();
						return count;
					}else{
						count = daoService.getRelevancyKeywordCount(storeKeyword);
						logger.info("Server is utilizing database connection");
						return count;
					}
				}		
		}catch (Exception e) {
			logger.error(e);
			count = daoService.getRelevancyKeywordCount(storeKeyword);
			logger.info("Server is utilizing database connection");
		}
		return count;
	}
	
	@Override
	public boolean loadRelevancyKeywords(String storeName, RelevancyKeyword relevancyKeyword){
		
		Relevancy relevancy = new Relevancy();
		relevancy.setStore(new Store(storeName));
		relevancy.setRelevancyName("");
		SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy, null, null, 0, 0);
		CacheModel<RelevancyKeyword> cache = null;
		RelevancyKeyword keyword = null;

		try {	
			if(relevancyKeyword != null && relevancyKeyword.getRelevancy().getRelevancyId() != null){
				keyword = daoService.getRelevancyKeyword(relevancyKeyword);
				
				if(keyword != null){
					cache = new CacheModel<RelevancyKeyword>();
					cache.setObj(keyword);
					cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_KEYWORD_CACHE_KEY, relevancyKeyword.getRelevancy().getRelevancyId()+"_"+relevancyKeyword.getKeyword().getKeyword()), cache);
					logger.info("Relevancy Storekeyword for \""+relevancyKeyword.getKeyword().getKeyword()+"\" has been loaded to cache");
				}
			}else{
				List<Relevancy> relList = searchRelevancy(criteria, MatchType.LIKE_NAME);
				List<String> kwList = getAllKeywords(storeName);
		
				for(Relevancy rel : relList){
					for(String kw : kwList){
						RelevancyKeyword relKey = new RelevancyKeyword(new Keyword(kw), new Relevancy(rel.getRelevancyId()));
						keyword = daoService.getRelevancyKeyword(relKey);
						
						if(keyword != null){
							cache = new CacheModel<RelevancyKeyword>();
							cache.setObj(keyword);
							cacheService.put(getCacheKey(storeName,CacheConstants.RELEVANCY_KEYWORD_CACHE_KEY, rel.getRelevancyId()+"_"+kw), cache);
							logger.info("Relevancy Storekeyword for \""+kw+"\" has been loaded to cache");
						}
					}	
				}
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;	
	}
	
	@Override
	public RelevancyKeyword getRelevancyKeyword(RelevancyKeyword relevancyKeyword, String storeName) throws DaoException{
		
		CacheModel<RelevancyKeyword> cache = null;
		RelevancyKeyword keyword = null;
		
		try {		
						cache = (CacheModel<RelevancyKeyword>) cacheService.get(getCacheKey(storeName, CacheConstants.RELEVANCY_KEYWORD_CACHE_KEY, relevancyKeyword.getRelevancy().getRelevancyId()+"_"+relevancyKeyword.getKeyword().getKeyword()));

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
		return null;
	}

	@Override
	public RecordSet<RelevancyKeyword> searchRelevancyKeywords(SearchCriteria<RelevancyKeyword> criteria,MatchType relevancyMatchType, ExactMatch keywordExactMatch)throws DaoException {
		
		RelevancyKeyword model = criteria.getModel();
		String kw = (relevancyMatchType == null) ? null : (relevancyMatchType.equals(MatchType.MATCH_ID) ? model.getRelevancy().getRelevancyId() : model.getRelevancy().getRelevancyName());

		List<RelevancyKeyword> relevancyKWList = null;
		CacheModel<RelevancyKeyword> cache = null;
		
		RecordSet<RelevancyKeyword> record = null;
		
		try {	
						cache = (CacheModel<RelevancyKeyword>) cacheService.get(getCacheKey(model.getRelevancy().getStore().getStoreId(), CacheConstants.RELEVANCY_SEARCH_KEYWORD_LIST_CACHE_KEY, kw));
					
						if(cache != null)
							relevancyKWList = cache.getList();
						else{
							record = daoService.searchRelevancyKeywords(criteria, relevancyMatchType, keywordExactMatch);
							
							if(CollectionUtils.isNotEmpty(record.getList())){
								logger.info("Server is utilizing database connection");
								return record;
							}
						}
						
						if(CollectionUtils.isNotEmpty(relevancyKWList)){
							filterRelevancyKeywordListByCriteria(relevancyKWList, criteria);
							return new RecordSet<RelevancyKeyword>(relevancyKWList, relevancyKWList.size());
						}	
		}catch (Exception e) {
			logger.error(e);
			record = daoService.searchRelevancyKeywords(criteria, relevancyMatchType, keywordExactMatch);
			
			if(CollectionUtils.isNotEmpty(record.getList())){
				logger.info("Server is utilizing database connection");
				return record;
			}
		}
		return null;
	}
}