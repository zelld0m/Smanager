package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;
import com.search.manager.model.Banner;

@Service(value = "bannerService")
@RemoteProxy(
		name = "BannerServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "bannerService")
	)
public class BannerService {
	private static final Logger logger = Logger.getLogger(BannerService.class);
	
	@Autowired private DaoService daoService;
	
	@RemoteMethod
	public Banner addKeywordsToBanner(String ruleId, String[] keywordList){
		return null;
	}
	
	@RemoteMethod
	public Banner addRule(String bannerName, String linkPath, String imagePath, String imageAlt) {
		//create thumbnail
		return null;
	}
	
	@RemoteMethod
	public int deleteRule(String ruleId) {
		int result = -1;
		return result;
	}
	
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