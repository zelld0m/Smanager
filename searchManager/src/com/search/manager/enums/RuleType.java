package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum RuleType {
	KEYWORD("Keyword", "Keyword"),
	TEMPLATE("Template", "Template");

	private final String displayText;
	private final String description;
	private static final Map<String,RuleType> lookup 
	= new HashMap<String,RuleType>();

	RuleType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}

	static {
		for(RuleType s : EnumSet.allOf(RuleType.class))
			lookup.put(s.getDisplayText(), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}

	public static RuleType get(String displayText) { 
		return lookup.get(displayText); 
	}
	
	public static RuleType get(Integer intValue) { 
        return RuleType.values()[intValue-1];
    }

	public static RuleType getDefaultIfBlank(RuleType ruleType) {
		if(ruleType==null)
			return RuleType.KEYWORD;
		return ruleType;
	}

	public static RuleType getDefaultIfBlank(String ruleType) {
		return getDefaultIfBlank(get(ruleType));
	}

	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
}