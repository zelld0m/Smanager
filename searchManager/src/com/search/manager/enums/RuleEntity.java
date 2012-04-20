package com.search.manager.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum RuleEntity {
	ELEVATE(1, "Elevate"),
	EXCLUDE(2, "Exclude"),
	KEYWORD(3, "Keyword"),
	STORE_KEYWORD(4, "Store Keyword"),
	CAMPAIGN(5, "Campaign"),
	BANNER(6, "Banner"),
	QUERY_CLEANING(7, "Query Cleaning"),
	RANKING_RULE(8, "Ranking Rule");

	private final int code;
	private final List<String> values;
   
	private RuleEntity(int code, String ...values) {
		this.code = code;
		this.values = Arrays.asList(values);
	}

	public int getCode() {
		return code;
	}

	public List<String> getValues() {
		return values;
	}

	public static RuleEntity find(String name) {
	    for (RuleEntity entity : RuleEntity.values()) {
	        if (containsIgnoreCase(name, entity.getValues())) {
	            return entity;
	        }
	    }
	    return null;
	}
	
	public static int getId(String name) {
	    return find(name).code;
	}
	
	private static boolean containsIgnoreCase(String str, List<String> list){
	    for(String i : list){
	        if(i.equalsIgnoreCase(StringUtils.deleteWhitespace(str)))
	            return true;
	    }
	    return false;
	}
}