package com.search.manager.cache.utility;

import com.search.manager.exception.DataException;

public class CacheConstants {
	public static final String SEARCH_CACHE_KEY = "src";
	public static final String ELEVATED_LIST_CACHE_KEY = "EL";
	public static final String EXCLUDED_LIST_CACHE_KEY = "EXL";
	public static final String RELEVANCY_LIST_CACHE_KEY = "RL";
	public static final String RELEVANCY_DETAILS_CACHE_KEY = "RDL";
	public static final String RELEVANCY_KEYWORD_COUNT_CACHE_KEY = "RKC";
	public static final String RELEVANCY_KEYWORD_CACHE_KEY = "RK";
	public static final String RELEVANCY_SEARCH_KEYWORD_LIST_CACHE_KEY = "RSKL";
	public static final String KEYWORDS_CACHE_KEY = "KW";
	public static final String RULE_REDIRECT_CACHE_KEY = "RR";
	
	public enum Operation{
		add,
		edit,
		delete
	}
	
	public static String getCacheKey(String storeName, String type,String kw) throws DataException {
		if("macmall".equalsIgnoreCase(storeName)) {
			storeName = "mc";
		}
		else if("pcmall".equalsIgnoreCase(storeName)) {
			storeName = "pc";			
		}
		else if("onsale".equalsIgnoreCase(storeName)) {
			storeName = "ol";			
		}
		else {
			throw new DataException("Unrecognized Store");
		}
		StringBuilder key = new StringBuilder(SEARCH_CACHE_KEY)
						.append(".").append(storeName).append("_")
						.append(type).append("_").append(kw.replace(" ", "_"));
		return key.toString();
	}
	
}
