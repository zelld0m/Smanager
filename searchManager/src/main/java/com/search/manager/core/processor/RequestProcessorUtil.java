package com.search.manager.core.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.search.manager.dao.SearchDaoService;
import com.search.manager.model.StoreKeyword;
import com.search.ws.ConfigManager;
import com.search.ws.SolrConstants;

@Component
public class RequestProcessorUtil {
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessorUtil.class);

	private static SearchDaoService daoService;
	private static SearchDaoService solrService;
	
	private static final String[] uniqueFields = {
		SolrConstants.SOLR_PARAM_ROWS,
		SolrConstants.SOLR_PARAM_KEYWORD,
		SolrConstants.SOLR_PARAM_WRITER_TYPE,
		SolrConstants.SOLR_PARAM_START
	};

	public static final void initialize(SearchDaoService daoService, SearchDaoService solrService) {
		RequestProcessorUtil.daoService = daoService;
		RequestProcessorUtil.solrService = solrService;
	}

	public static boolean addNameValuePairToMap(Map<String, List<NameValuePair>> map, String paramName, NameValuePair pair) {
		boolean added = false;
		if (ArrayUtils.contains(uniqueFields, paramName) && map.containsKey(paramName)) {
			logger.warn("Request contained multiple declarations for parameter {}. Discarding subsequent declarations.", paramName);
		} else {
			if (!map.containsKey(paramName)) {
				map.put(paramName, new ArrayList<NameValuePair>());
			}
			map.get(paramName).add(pair);
			added = true;
		}
		return added;
	}
	
	public static Map<String, String> getFacetMap(String storeId) {
		ConfigManager configManager = ConfigManager.getInstance();
		Map<String, String> facetMap = new HashMap<String, String>();
		facetMap.put(SolrConstants.SOLR_PARAM_FACET_NAME, configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_NAME));
		facetMap.put(SolrConstants.SOLR_PARAM_FACET_TEMPLATE, configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE));
		facetMap.put(SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME, configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME));
		return facetMap;
	}

	public static Map<String, String> generateActiveRule(String type, String id, String name, boolean active) {
		Map<String, String> activeRule = new HashMap<String, String>();
		activeRule.put(SolrConstants.TAG_RULE_TYPE, type);
		activeRule.put(SolrConstants.TAG_RULE_ID, id);
		activeRule.put(SolrConstants.TAG_RULE_NAME, name);
		activeRule.put(SolrConstants.TAG_RULE_ACTIVE, String.valueOf(active));
		return activeRule;
	}
	
	public static SearchDaoService getDaoService(boolean isGuiRequest){
		return isGuiRequest? daoService: solrService;
	}

	public static StoreKeyword getStoreKeywordOverride(String storeId, String keyword) {
		return new StoreKeyword(storeId, keyword);
	}

	public static SearchDaoService getDaoService() {
		return daoService;
	}

	public static SearchDaoService getSolrService() {
		return solrService;
	}
}