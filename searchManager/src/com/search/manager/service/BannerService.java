package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoService;

@RemoteProxy(
		name = "BannerServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "bannerService")
	)
public class BannerService {
	private static final Logger logger = Logger.getLogger(BannerService.class);
	
	@Autowired private DaoService daoService;
	
	@RemoteMethod
	public int addFeature(String storeId, String keyword, String productId) {
		logger.info(String.format("%s %s %s", storeId, keyword, productId));
		return 0;
	}
	
	@RemoteMethod
	public int removeFeature(String storeId, String keyword, String productId) {
		logger.info(String.format("%s %s %s", storeId, keyword, productId));
		return 0;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}