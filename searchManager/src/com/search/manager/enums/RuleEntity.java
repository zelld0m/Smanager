package com.search.manager.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class, javascript="RuleEntity")
public enum RuleEntity {
	ELEVATE(1, "Elevate"),
	EXCLUDE(2, "Exclude"),
	KEYWORD(3, "Keyword"),
	STORE_KEYWORD(4, "Store Keyword"),
	CAMPAIGN(5, "Campaign"),
	BANNER(6, "Banner"),
	QUERY_CLEANING(7, "Query Cleaning"),
	RANKING_RULE(8, "Ranking Rule"),
	RULE_STATUS(9, "Rule Status"),
	DEMOTE(10, "Demote"),
	FACET_SORT(11, "Facet Sort");

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
	
	public static String getValue(Integer code) {
		String value = null;
	    for (RuleEntity entity : RuleEntity.values()) {
	        if (entity.getCode() == code) {
	            value = entity.getValues().get(0);
	            break;
	        }
	    }
		return value;
	}
	
	private static boolean containsIgnoreCase(String str, List<String> list){
	    for(String i : list){
	        if(StringUtils.equalsIgnoreCase(str, i) || StringUtils.equalsIgnoreCase(str, StringUtils.deleteWhitespace(i)))
	            return true;
	    }
	    return false;
	}
}