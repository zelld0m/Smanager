package com.search.manager.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(
		converter=EnumConverter.class, 
		type="enum")
public enum RuleEntity {
	ELEVATE(1, "Elevate", "elevate"),
	EXCLUDE(2, "Exclude", "exclude"),
	KEYWORD(3, "Keyword", "keyword"),
	STORE_KEYWORD(4, "Store Keyword", "storeKeyword"),
	CAMPAIGN(5, "Campaign", "campaign"),
	BANNER(6, "Banner", "banner"),
	QUERY_CLEANING(7, "Query Cleaning", "queryCleaning"),
	RANKING_RULE(8, "Ranking Rule", "rankingRule"),
	RULE_STATUS(9, "Rule Status", "ruleStatus"),
	DEMOTE(10, "Demote", "demote"),
	FACET_SORT(11, "Facet Sort", "facetSort"),
    SPELL(12, "Did You Mean", "didYouMean");

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
	        if (containsIgnoreCase(name, entity.getValues()) || StringUtils.equalsIgnoreCase(entity.name(), name)) {
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
	
	public String getNthValue(int valueIndex) {
		return values.get(valueIndex);
	}
	
	private static boolean containsIgnoreCase(String str, List<String> list){
	    for(String i : list){
	        if(StringUtils.equalsIgnoreCase(str, i) || 
	        		StringUtils.equalsIgnoreCase(StringUtils.deleteWhitespace(str), StringUtils.deleteWhitespace(i)) || 
	        		StringUtils.equalsIgnoreCase(StringUtils.deleteWhitespace(i), StringUtils.deleteWhitespace(StringUtils.replaceChars(str, '_', ' '))))
	            return true;
	    }
	    return false;
	}
}
