package com.search.manager.cache.utility;

public class CacheConstants {
	public static final String SEARCH_CACHE_KEY = "src";
	public static final String ELEVATED_LIST_CACHE_KEY = "EL";
	public static final String EXCLUDED_LIST_CACHE_KEY = "EXL";
	public static final String KEYWORDS_CACHE_KEY = "KW";
	public static final String RULE_REDIRECT_CACHE_KEY = "RR";
	
	public enum Operation{
		add,
		edit,
		delete
	}
}
