package com.search.manager.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.ws.SolrConstants;

public class RequestProcessorUtil {
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessorUtil.class);

	protected static final String[] uniqueFields = {
		SolrConstants.SOLR_PARAM_ROWS,
		SolrConstants.SOLR_PARAM_KEYWORD,
		SolrConstants.SOLR_PARAM_WRITER_TYPE,
		SolrConstants.SOLR_PARAM_START
	};

	protected static boolean addNameValuePairToMap(Map<String, List<NameValuePair>> map, String paramName, NameValuePair pair) {
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
}