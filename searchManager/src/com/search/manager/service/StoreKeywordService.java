package com.search.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.annotations.ScriptScope;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.StoreKeyword;

@Service(value = "storeKeywordService")
@RemoteProxy(
		name = "StoreKeywordServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "storeKeywordService"),
	    scope=ScriptScope.SCRIPT
	)
public class StoreKeywordService {
	private static final Logger logger = Logger.getLogger(StoreKeywordService.class);
	
	@Autowired private DaoService daoService;
	
	@RemoteMethod
	public RecordSet<StoreKeyword> getAllByKeyword(String keyword, int page,int itemsPerPage) throws Exception {
		try {
			logger.info(String.format("%d %d %s", page, itemsPerPage, keyword));
			return daoService.getAllKeywordsMatching(UtilityService.getStoreName(), keyword, page, itemsPerPage);
		} catch (DaoException e) {
			logger.error("Failed during getAllByKeyword()",e);
			throw e;
		} catch (Exception e){
			logger.error("Failed during getAllByKeyword()",e);
			throw e;
		}
	}
	
	@RemoteMethod
	public RecordSet<Keyword> getAllKeyword(String keyword, int page,int itemsPerPage) throws Exception {
		try {
			logger.info(String.format("%d %d %s", page, itemsPerPage, keyword));
			RecordSet<StoreKeyword> storeKeyword =  getAllByKeyword(keyword, page, itemsPerPage);
			List<StoreKeyword> storeKeywordList =  new ArrayList<StoreKeyword>();
			List<Keyword> keywordList = new ArrayList<Keyword>();
			
			if (storeKeyword!=null) storeKeywordList = storeKeyword.getList();
			
			for (StoreKeyword sk: storeKeywordList){
				keywordList.add(sk.getKeyword());
			}
			
			return new RecordSet<Keyword>(keywordList, storeKeyword.getTotalSize());
			
		} catch (DaoException e) {
			logger.error("Failed during getAllByKeyword()",e);
		}
		
		return null;
	}
	
	@RemoteMethod
	public RecordSet<StoreKeyword> getAll() {
		try {
			return daoService.getAllKeywords(UtilityService.getStoreName());
		} catch (DaoException e) {
			logger.error("Failed during getStoreKeywords()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public int addKeyword(String keyword) {
		try {
			logger.info(String.format("%s",keyword));
			return daoService.addKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
		} catch (DaoException e) {
			logger.error("Failed during addKeyword()",e);
		}
		return 0;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}
