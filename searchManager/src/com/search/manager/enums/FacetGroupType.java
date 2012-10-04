package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum FacetGroupType {
	CATEGORY("Category", "Category"),
	MANUFACTURER("Manufacturer", "Manufacturer");

	private final String displayText;
	private final String description;
	private static final Map<String,FacetGroupType> lookup 
	= new HashMap<String,FacetGroupType>();

	private FacetGroupType(String displayText, String description) {
		this.displayText = displayText;
		this.description = description;
	}

	static {
		for(FacetGroupType s : EnumSet.allOf(FacetGroupType.class))
			lookup.put(s.getDisplayText(), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}

	public static FacetGroupType get(String displayText) { 
		return lookup.get(displayText); 
	}

	public static FacetGroupType get(Integer intValue) { 
        return FacetGroupType.values()[intValue-1];
    }
	
	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
}