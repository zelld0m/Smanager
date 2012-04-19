package com.search.webservice;

import java.util.List;
import org.apache.log4j.Logger;
import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.utility.PropsUtils;
import com.search.webservice.model.ElevatedList;
import com.search.webservice.model.ExcludedList;
import com.search.webservice.model.TransportList;

public class SearchGuiServiceImpl implements SearchGuiService{

	private static Logger logger = Logger.getLogger(SearchGuiServiceImpl.class);
	private static String token;
	private static final String RESOURCE_MAP = "token";
	
	private static DaoCacheService daoCacheService;
	private static DaoService daoService;
	private static DaoService daoServiceStg;
	
	public void setDaoCacheService(DaoCacheService daoCacheService_) {
		daoCacheService = daoCacheService_;
	}

	public void setDaoService(DaoService daoService_) {
		daoService = daoService_;
	}
	
	public void setDaoServiceStg(DaoService daoServiceStg_) {
		daoServiceStg = daoServiceStg_;
	}

	static{
		try {
			token = PropsUtils.getValue(RESOURCE_MAP);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	@Override
	public boolean loadElevateList(String store, String token) {
		try {
			if(isValidToken(token))
				return daoCacheService.loadElevateResultList(store);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean loadExcludeList(String store, String token) {
		try {
			if(isValidToken(token))
				return daoCacheService.loadExcludeResultList(store);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean loadRelevancyDetails(String store, String token) {
		try {
			if(isValidToken(token))
				return daoCacheService.loadRelevancyDetails(store);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean loadRelevancyList(String store, String token) {
		try {
			if(isValidToken(token))
				return daoCacheService.loadRelevancyResultList(store,MatchType.LIKE_NAME);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	private boolean isValidToken(String token_){	
		try{	
			if(token.equals(token_)){
				logger.info("User has valid token ... ");
				return true;
			}else{
				logger.info("User has invalid token ... ");
				return false;
			}
		}catch(Exception e){
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushElevateList(ElevatedList list) {
		try {
			if(isValidToken(list.getToken())){
				if(list != null && list.getMap() != null && list.getMap().size() > 0){
					
					for(String key : list.getMap().keySet()){
						if(daoCacheService.hasExactMatchKey(list.getStore(), key)){
							ElevateResult delEl = new ElevateResult();
							delEl.setStoreKeyword(new StoreKeyword(list.getStore(), key));
							delEl.setEdp(null);
							daoService.deleteElevateResult(delEl);

							if(list.getMap().get(key) != null){
								for(Object e : list.getMap().get(key)){
									daoService.addElevateResult((ElevateResult)e);
								}	
							}
							daoCacheService.updateElevateResultList(delEl);
						}
					}
				}
				return true;
			}	
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushExcludeList(ExcludedList list) {
		try {
			if(isValidToken(list.getToken())){
				if(list != null && list.getMap() != null && list.getMap().size() > 0){
					
					for(String key : list.getMap().keySet()){
						if(daoCacheService.hasExactMatchKey(list.getStore(), key)){
							ExcludeResult delEl = new ExcludeResult();
							delEl.setStoreKeyword(new StoreKeyword(list.getStore(), key));
							delEl.setEdp(null);
							daoService.deleteExcludeResult(delEl);

							if(list.getMap().get(key) != null){
								for(Object e : list.getMap().get(key)){
									daoService.addExcludeResult((ExcludeResult) e);
								}	
							}
							daoCacheService.updateExcludeResultList(delEl);
						}
					}
				}
				return true;
			}	
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushElevateList(TransportList list) {
		
		List<ElevateResult> elevatedList = null;
		ElevateResult elevateFilter = new ElevateResult();
		
		try {
			if(isValidToken(list.getToken())){
				if(list != null && list.getList().size() > 0){
					for(String key : list.getList()){
						if(daoCacheService.hasExactMatchKey(list.getStore(), key)){
							ElevateResult delEl = new ElevateResult();
							delEl.setStoreKeyword(new StoreKeyword(list.getStore(), key));
							daoService.clearElevateResult(new StoreKeyword(list.getStore(), key)); // prod
							
							// retrieve staging data then push to prod
							StoreKeyword sk = new StoreKeyword(list.getStore(), key);
							elevateFilter.setStoreKeyword(sk);
							SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
							
							elevatedList = daoServiceStg.getElevateResultList(criteria).getList();
							
							if(elevatedList != null && elevatedList.size() > 0){
								for(ElevateResult e : elevatedList){
									daoService.addElevateResult((ElevateResult) e);
								}
							}
							
							// update cache data
							daoCacheService.updateElevateResultList(delEl); // prod
						}
					}
				}
				return true;
			}	
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushExcludeList(TransportList list) {
		
		List<ExcludeResult> excludeList = null;
		ExcludeResult excludeFilter = new ExcludeResult();
		
		try {
			if(isValidToken(list.getToken())){
				if(list != null && list.getList().size() > 0){
					
					for(String key : list.getList()){
						if(daoCacheService.hasExactMatchKey(list.getStore(), key)){
							ExcludeResult delEl = new ExcludeResult();
							delEl.setStoreKeyword(new StoreKeyword(list.getStore(), key));
							daoService.clearExcludeResult(new StoreKeyword(list.getStore(), key)); // prod
							
							// retrieve staging data then push to prod
							StoreKeyword sk = new StoreKeyword(list.getStore(), key);
							excludeFilter.setStoreKeyword(sk);
							SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);
							
							excludeList = daoServiceStg.getExcludeResultList(criteria).getList();
							
							if(excludeList != null && excludeList.size() > 0){
								for(ExcludeResult e : excludeList){
									daoService.addExcludeResult((ExcludeResult) e);
								}
							}
							
							daoCacheService.updateExcludeResultList(delEl); // prod
						}
					}
				}
				return true;
			}	
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}
}
