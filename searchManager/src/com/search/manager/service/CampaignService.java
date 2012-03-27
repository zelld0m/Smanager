package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.Campaign;
import com.search.manager.model.RecordSet;

@RemoteProxy(
		name = "CampaignServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "campaignService")
	)
public class CampaignService {
	private static final Logger logger = Logger.getLogger(CampaignService.class);
	
	@Autowired private DaoService daoService;
		
	public RecordSet<Campaign> getCampaignList(String store){
		RecordSet<Campaign> list = null;
		
//		try {
//			//list = daoService.getCampaignList(store);
//		} catch (DaoException e) {
//			return null;
//		}
//		
		return list;
	}
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}