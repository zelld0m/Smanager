package com.search.manager.cache.utility;

import com.search.manager.exception.DataException;

public class CacheConstants {
	public static final String SEARCH_CACHE_KEY = "src";
	public static final String ELEVATED_LIST_CACHE_KEY = "EL";
	public static final String EXCLUDED_LIST_CACHE_KEY = "EXL";
	public static final String RELEVANCY_LIST_CACHE_KEY = "RL";
	public static final String RELEVANCY_DEFAULT_CACHE_KEY = "RD";
	public static final String KEYWORDS_CACHE_KEY = "KW";
	public static final String RULE_REDIRECT_CACHE_KEY = "RR";
	
	public enum Operation{
		add,
		edit,
		delete
	}
	
	public static String getCacheKey(String storeId, String type, String kw) throws DataException {
		if("macmall".equalsIgnoreCase(storeId)) {
			storeId = "mc";
		}
		else if("pcmall".equalsIgnoreCase(storeId)) {
			storeId = "pc";			
		}
		else if("onsale".equalsIgnoreCase(storeId)) {
			storeId = "ol";			
		}
		else {
			throw new DataException("Unrecognized Store");
		}
		StringBuilder key = new StringBuilder(SEARCH_CACHE_KEY)
						.append(".").append(storeId).append("_")
						.append(type).append("_").append(kw.replace(" ", "_"));
		return key.toString();
	}
	
}
