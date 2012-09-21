package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;

@Service(value = "facetSortService")
@RemoteProxy(
		name = "FacetSortServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "facetSortService")
	)
public class FacetSortService {
	private static final Logger logger = Logger.getLogger(FacetSortService.class);
	
	@Autowired private DaoService daoService;
	
	@RemoteMethod
	public int addRule(String ruleName, String ruleType, String sortType) {
		
		return 0;
	}
	
	@RemoteMethod
	public int addRule(String keyword, String edp, int sequence, String expiryDate, String comment, boolean forceAdd) {
		return 0;
	}
}