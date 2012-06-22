package com.search.manager.cache.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import com.search.manager.exception.DataException;

public class CacheConstants {
	public static final String SEARCH_CACHE_KEY = "search";
	public static final String ELEVATED_LIST_CACHE_KEY 			= "EL";
	public static final String EXCLUDED_LIST_CACHE_KEY 			= "EXL";
	public static final String RELEVANCY_LIST_CACHE_KEY 		= "RL";
	public static final String RELEVANCY_DEFAULT_CACHE_KEY 		= "RD";
	public static final String KEYWORDS_CACHE_KEY 				= "KW";
	public static final String RULE_REDIRECT_CACHE_KEY			= "RR";
	public static final String USER_CACHE_KEY 					= "US";
	public static final String FORCE_UPDATE_CACHE_KEY			= "FU";
	
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
		}else if("sbn".equalsIgnoreCase(storeId)) {
			storeId = "sbn";			
		}else {
			throw new DataException("Unrecognized Store: " + storeId);
		}
		StringBuilder key = new StringBuilder(SEARCH_CACHE_KEY)
						.append(".").append(storeId).append("_")
						.append(type).append("_").append(escapeKeyword(kw));
		return key.toString();
	}
	
	public static String getCacheKey(String type, String kw) throws DataException {
		StringBuilder key = new StringBuilder(SEARCH_CACHE_KEY)
						.append(".").append(type).append("_").append(escapeKeyword(kw));
		return key.toString();
	}
	
	private static String escapeKeyword(String keyword) {
		Pattern p = Pattern.compile("(\\w*)(\\W*)(.*)");
		StringBuilder builder = new StringBuilder();
		String str = keyword.replaceAll("\\s", "_");
		while (StringUtils.isNotBlank(str)) {
			Matcher m = p.matcher(str);
			if (m.matches()) {
				builder.append(m.group(1));
				if (StringUtils.isNotBlank(m.group(2))) {
					builder.append(".").append(Hex.encodeHexString(m.group(2).getBytes())).append(".");
				}
				str = m.group(3);
			}
			else {
				builder.append(str);
				break;
			}
		}
		return builder.toString();
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
			System.out.println(keyword + " -> (" + escapeKeyword(keyword) + ")");
		}
		
	}
	
}
