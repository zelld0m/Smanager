package com.search.manager.enums;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(
		converter=EnumConverter.class, 
		type="enum")
public enum RuleStatusSortType {
	ASC_DESCRIPTION("Ascending Description", "Ascending by Description"),
	DESC_DESCRIPTION("Descending", "Descending by Description"),
	ASC_LAST_PUBLISHED_DATE("Count Asc", "Ascending by Count"),
	DESC_LAST_PUBLISHED_DATE("Count Desc", "Descending by Count");
	
	private final String displayText;
	private final String description;
	
	private static final Map<String,RuleStatusSortType> lookup 
    = new HashMap<String,RuleStatusSortType>();
	
	RuleStatusSortType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}
	
	static {
         for(RuleStatusSortType s : EnumSet.allOf(RuleStatusSortType.class))
              lookup.put(s.getDisplayText(), s);
    }
	
	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}
	
	public List<RuleStatusSortType> getSortOrder(){
		return Arrays.asList(RuleStatusSortType.values());
	}
	
	public static RuleStatusSortType get(String displayText) { 
         return lookup.get(displayText); 
    }
	
	public static RuleStatusSortType get(Integer intValue) { 
        return RuleStatusSortType.values()[intValue-1];
    }
	
	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
}