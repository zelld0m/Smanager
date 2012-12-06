package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum ImportType {
	FOR_REVIEW("For Review", "For Review"),
	FOR_APPROVAL("For Approval", "For Approval"),
	AUTO_PUBLISH("Auto-Publish", "Auto-Publish");

	private final String displayText;
	private final String description;
	
	private static final Map<String,ImportType> lookup = new HashMap<String,ImportType>();

	ImportType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}

	static {
		for(ImportType s : EnumSet.allOf(ImportType.class))
			lookup.put(s.getDisplayText(), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}

	public static ImportType getByDisplayText(String displayText) { 
		return lookup.get(displayText); 
	}
	
	public static ImportType get(Integer intValue) { 
        return ImportType.values()[intValue-1];
    }
	
	public static ImportType[] PUBLISHER_LIST = new ImportType[]{
		ImportType.FOR_REVIEW,
		ImportType.FOR_APPROVAL,
		ImportType.AUTO_PUBLISH
	};
	
	public static ImportType[] NON_PUBLISHER_LIST = new ImportType[]{
		ImportType.FOR_REVIEW,
		ImportType.FOR_APPROVAL
	}; 

	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
}