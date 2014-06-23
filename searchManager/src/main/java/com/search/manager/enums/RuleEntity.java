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
	ELEVATE(1, "Elevate", "elevate", "Elevate"),
	EXCLUDE(2, "Exclude", "exclude", "Exclude"),
	KEYWORD(3, "Keyword", "keyword", "Keyword"),
	STORE_KEYWORD(4, "Store Keyword", "storeKeyword", "Store Keyword"),
	CAMPAIGN(5, "Campaign", "campaign", "Campaign"),
	BANNER(6, "Banner", "banner", "Banner"),
	QUERY_CLEANING(7, "Query Cleaning", "queryCleaning", "Redirect Rule"),
	RANKING_RULE(8, "Ranking Rule", "rankingRule", "Relevancy Rule"),
	RULE_STATUS(9, "Rule Status", "ruleStatus", "Rule Status"),
	DEMOTE(10, "Demote", "demote", "Demote"),
	FACET_SORT(11, "Facet Sort", "facetSort", "Facet Sort"),
    SPELL(12, "Did You Mean", "didYouMean", "Did You Mean"),
    TYPEAHEAD(13, "Type-ahead", "typeahead", "Typeahead");

	private final int code;
	private final List<String> values;
	
	public static final RuleEntity[] SEARCH_RULES = {
	    ELEVATE,
	    EXCLUDE,
	    DEMOTE,
	    FACET_SORT,
	    QUERY_CLEANING,
	    RANKING_RULE,
	    SPELL,
	    BANNER
	};
	
	public static final RuleEntity[] RULE_TYPES = {
	    ELEVATE,
	    EXCLUDE,
	    DEMOTE,
	    FACET_SORT,
	    QUERY_CLEANING,
	    RANKING_RULE,
	    SPELL,
	    BANNER,
	    TYPEAHEAD
	};
   
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
	
	public static RuleEntity get(Integer intValue) { 
        return RuleEntity.values()[intValue-1];
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
	
	public String getName() {
		return getNthValue(0);
	}
	
	public String getXmlName() {
		return getNthValue(1);
	}
	
	public String getDisplayName() {
		return getNthValue(2);
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