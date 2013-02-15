package com.search.manager.solr.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoService;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrServerFactory;

public class BaseDaoSolr {

	@Autowired
	DaoService daoService;

	@Autowired
	SolrServerFactory solrServers;

	public static final Integer MAX_ROWS = 1000;
	public static final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:sss'Z'";

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

	public String getCurrentDate() {
		Date date = new Date();
		String formatedDate = (new SimpleDateFormat(SOLR_DATE_FORMAT))
				.format(date);

		return formatedDate;
	}

}
