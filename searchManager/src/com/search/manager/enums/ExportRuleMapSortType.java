package com.search.manager.enums;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class, javascript="ExportRuleMapSortType")
public enum ExportRuleMapSortType {
	
	EXPORT_DATE_DESC(1, "EXPORT_DATE_DESC"),
	EXPORT_DATE_ASC (2, "EXPORT_DATE_ASC"),
	RULE_NAME_DESC(3, "RULE_NAME_DESC"),
	RULE_NAME_ASC(4, "RULE_NAME_ASC"),
	PUBLISHED_DATE_DESC(5, "PUBLISHED_DATE_DESC"),
	PUBLISHED_DATE_ASC(6, "PUBLISHED_DATE_ASC");

	private final int code;
	private final String value;
   
	private ExportRuleMapSortType(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public static ExportRuleMapSortType find(String name) {
	    for (ExportRuleMapSortType entity : ExportRuleMapSortType.values()) {
	        if (StringUtils.equalsIgnoreCase(name, entity.getValue())) {
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

}