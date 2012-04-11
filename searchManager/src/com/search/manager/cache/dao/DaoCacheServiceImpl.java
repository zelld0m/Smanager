package com.search.manager.cache.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.log4j.Logger;
import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.service.CacheService;
import com.search.manager.cache.service.LocalCacheService;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.DaoException;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.Constants;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.SearchHelper;

public class DaoCacheServiceImpl implements DaoCacheService {

	DaoCacheServiceImpl instance;
	
	private static DaoService daoService;
	private static CacheService<CacheModel<?>> cacheService;
	private static LocalCacheService<CacheModel<?>> localCacheService;
	
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
		
		try {
			List<StoreKeyword> keywordList = daoService.getAllKeywords(storeName).getList();	
			if(keywordList != null  && keywordList.size() > 0){		
				kwList = new ArrayList<String>();		
				for(StoreKeyword key : keywordList){
					kwList.add(key.getKeywordId());
				}
				cache = new CacheModel<String>();
				cache.setList(kwList);
				
				try{
					cacheService.put(getCacheKey(storeName, CacheConstants.KEYWORDS_CACHE_KEY, ""), cache);
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
	
	private boolean hasExactMatchKey(String storeName, String kw){
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

		return CacheConstants.SEARCH_CACHE_KEY+"."+storeName+"_"+type+kw;
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
				if(elevatedList != null  && elevatedList.size() > 0)
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
				if(excludeList != null  && excludeList.size() > 0)
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
						
						if(elevatedList != null && elevatedList.size() > 0){
							filterElevatedListByCriteria(elevatedList, criteria);
							return elevatedList;
						}
					}catch (Exception e) {
						logger.error(e);
						elevatedList = daoService.getElevateResultList(criteria).getList();
						
						if(elevatedList != null && elevatedList.size() > 0){
							logger.info("Server is utilizing local heap memory");
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
		SearchCriteria<ElevateResult> sc = new SearchCriteria<ElevateResult>(criteria.getModel(), criteria.getStartDate(), criteria.getEndDate(), null, null);
		List<ElevateResult> list = getElevateResultList(sc, storeName);
		return list.size();
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
						
						if(excludeList != null && excludeList.size() > 0){
							filterExcludedListByCriteria(excludeList, criteria);
							return excludeList;
						}
					}catch (Exception e) {
						logger.error(e);
						
						excludeList = daoService.getExcludeResultList(criteria).getList();
						
						if(excludeList != null && excludeList.size() > 0){
							logger.info("Server is utilizing local heap memory");
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
		SearchCriteria<ExcludeResult> sc = new SearchCriteria<ExcludeResult>(criteria.getModel(), criteria.getStartDate(), criteria.getEndDate(), null, null);
		List<ExcludeResult> list = getExcludeResultList(sc, storeName);
		return list.size();
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
				}else if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}	
			}else if(criteria.getEndDate() != null){
				// remove all records beyond ending date : remove element when val is -1,0 or less than 1
				if(DateAndTimeUtils.compare(criteria.getEndDate(), result.getExpiryDate()) < 1){
					list.remove();
				}else if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
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
				}else if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
					// remove all records not in the range
					if(criteria.getStartRow() > cnt || criteria.getEndRow() < cnt)
						list.remove();
				}	
			}else if(criteria.getEndDate() != null){
				// remove all records beyond ending date : remove element when val is -1,0 or less than 1
				if(DateAndTimeUtils.compare(criteria.getEndDate(), result.getExpiryDate()) < 1){
					list.remove();
				}else if(criteria.getStartRow() > 0 && criteria.getEndRow() > 0){
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
}
