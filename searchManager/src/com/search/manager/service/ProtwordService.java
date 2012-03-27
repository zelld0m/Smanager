package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoService;

@RemoteProxy(
		name = "ProtwordServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "protwordService")
	)
public class ProtwordService {
	private static final Logger logger = Logger.getLogger(ProtwordService.class);
	
	@Autowired private DaoService daoService;
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}