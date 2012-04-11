package com.search.webservice;

import org.apache.log4j.Logger;
import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.DaoException;
import com.search.manager.utility.PropsUtils;

public class SearchGuiServiceImpl implements SearchGuiService{

	private static Logger logger = Logger.getLogger(SearchGuiServiceImpl.class);
	private static String token;
	private static final String RESOURCE_MAP = "token";
	
	private static DaoCacheService daoCacheService;
	
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

	public void setDaoCacheService(DaoCacheService daoCacheService_) {
		daoCacheService = daoCacheService_;
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
}
