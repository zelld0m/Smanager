package com.search.manager.cache.utility;

import com.search.manager.exception.DataException;
import com.search.manager.utility.StringUtil;

public class CacheConstants {
	public static final String SEARCH_CACHE_KEY = "search";
	public static final String ELEVATED_LIST_CACHE_KEY 			= "EL";
	public static final String DEMOTED_LIST_CACHE_KEY 			= "DL";
	public static final String EXCLUDED_LIST_CACHE_KEY 			= "EXL";
	public static final String FACET_SORT_KEYWORD_LIST_CACHE_KEY	= "FSK";
	public static final String FACET_SORT_TEMPLATE_LIST_CACHE_KEY 	= "FST";
	public static final String RELEVANCY_LIST_CACHE_KEY 		= "RL";
	public static final String RELEVANCY_DEFAULT_CACHE_KEY 		= "RD";
	public static final String KEYWORDS_CACHE_KEY 				= "KW";
	public static final String RULE_REDIRECT_CACHE_KEY			= "RR";
	public static final String USER_CACHE_KEY 					= "US";
	public static final String FORCE_UPDATE_CACHE_KEY			= "FU";
	public static final String CATEGORY_CODES					= "CC";
	
	public enum Operation{
		add,
		edit,
		delete
	}
	
	public static String getCacheKey(String storeId, String type, String kw) throws DataException {
		if("macmall".equalsIgnoreCase(storeId)) {
			storeId = "mc";
		}else if("pcmall".equalsIgnoreCase(storeId)) {
			storeId = "pc";			
		}else if("onsale".equalsIgnoreCase(storeId)) {
			storeId = "ol";			
		}else if("pcmallcap".equalsIgnoreCase(storeId)) {
			storeId = "bd";			
		}else if("ecost".equalsIgnoreCase(storeId)) {
			storeId = "ec";			
		}else if("pcmgbd".equalsIgnoreCase(storeId)) {
			storeId = "pg";			
		}else {
			throw new DataException("Unrecognized Store: " + storeId);
		}
		StringBuilder key = new StringBuilder(SEARCH_CACHE_KEY)
						.append(".").append(storeId).append("_")
						.append(type).append("_").append(StringUtil.escapeKeyword(kw));
		return key.toString();
	}
	
	public static String getCacheKey(String type, String kw) throws DataException {
		StringBuilder key = new StringBuilder(SEARCH_CACHE_KEY)
						.append(".").append(type).append("_").append(StringUtil.escapeKeyword(kw));
		return key.toString();
	}
	

	
	
	public static void main(String[] args) {
		String[] data = {
				"Samsung 32\"",
				"Samsung 32\" 22\"",
				"\"Hello\"",
				"\"Hello\" & \"GoodBye\"",
				"",
				"\n",
				" "
		};
		
		for (String keyword: data) {
			System.out.println(keyword + " -> (" + StringUtil.escapeKeyword(keyword) + ")");
		}
		
	}
	
}
