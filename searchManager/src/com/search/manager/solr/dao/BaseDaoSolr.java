package com.search.manager.solr.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;

import com.mall.solr.LocalSolrServerRunner;
import com.mall.solr.SolrServerFactory;
import com.search.manager.dao.DaoService;

public class BaseDaoSolr {

	@Autowired
	DaoService daoService;

	@Autowired
	SolrServerFactory solrServers;

	protected boolean commit(LocalSolrServerRunner localSolrServer) {
		try {
			UpdateResponse updateResponse = localSolrServer.commit();
			if (updateResponse.getStatus() == 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected Map<String, Boolean> getKeywordStatusMap(List<String> keywords) {
		Map<String, Boolean> keywordStatus = new HashMap<String, Boolean>();

		for (String key : keywords) {
			keywordStatus.put(key, false);
		}

		return keywordStatus;
	}

}
