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
public enum ExportType {

	AUTOMATIC("Automatic", "Automatic"),
	MANUAL("Manual", "Manual");

	private final String displayText;
	private final String description;
	
	private static final Map<String,SortType> lookup 
    = new HashMap<String,SortType>();
	
	ExportType(String displayText, String description){
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
	
	public static ExportType get(Integer intValue) {
        return intValue == null ? null : ExportType.values()[intValue-1];
    }
	
	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
	
}
