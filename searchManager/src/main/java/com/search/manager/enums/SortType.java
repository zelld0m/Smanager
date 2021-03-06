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
public enum SortType {
	ASC_ALPHABETICALLY("A-Z", "Ascending Alphabetically"),
	DESC_ALPHABETICALLY("Z-A", "Descending Alphabetically"),
	ASC_COUNT("Count Asc", "Ascending by Count"),
	DESC_COUNT("Count Desc", "Descending by Count"),
	DEFAULT_ORDER("Default Order", "Default Order");
	
	private final String displayText;
	private final String description;
	
	private static final Map<String,SortType> lookup 
    = new HashMap<String,SortType>();
	
	SortType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}
	
	static {
         for(SortType s : EnumSet.allOf(SortType.class))
              lookup.put(s.getDisplayText(), s);
    }
	
	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}
	
	public List<SortType> getSortOrder(){
		return Arrays.asList(SortType.values());
	}
	
	public static SortType get(String displayText) { 
         return lookup.get(displayText); 
    }
	
	public static SortType get(Integer intValue) { 
        return SortType.values()[intValue-1];
    }
	
	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
}