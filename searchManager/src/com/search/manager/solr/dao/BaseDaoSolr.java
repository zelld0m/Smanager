package com.search.manager.solr.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.mall.solr.SolrServerFactory;
import com.search.manager.dao.DaoService;

public class BaseDaoSolr {

	@Autowired
	DaoService daoService;
	
	@Autowired
	SolrServerFactory solrServers;
	
}
