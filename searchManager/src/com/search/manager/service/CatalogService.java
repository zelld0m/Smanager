package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;

@Service(value = "catalogService")
@RemoteProxy(
		name = "CatalogServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "catalogService")
	)
public class CatalogService {
	private static final Logger logger = Logger.getLogger(CatalogService.class);
	
	@Autowired private DaoService daoService;
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}