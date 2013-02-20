package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum ReplaceKeywordMessageType {
	STORE_DEFAULT_TEXT("No additional text", "Do not display any additional text in search results"),
	DISPLAY_STANDARD_TEXT("Display standard text", "Display standard text (Showing Result for \"replacement keyword\" / Search instead for: original Keyword) "),
	DISPLAY_CUSTOM_TEXT("Display custom text", "Display custom text");
   
	private final String displayText;
	private final String description;
	
	private static final Map<String,ReplaceKeywordMessageType> lookup = new HashMap<String,ReplaceKeywordMessageType>();

	ReplaceKeywordMessageType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}

	static {
		for(ReplaceKeywordMessageType s : EnumSet.allOf(ReplaceKeywordMessageType.class))
			lookup.put(s.getDisplayText(), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}

	public static ReplaceKeywordMessageType getByDisplayText(String displayText) { 
		return lookup.get(displayText); 
	}
	
	public static ReplaceKeywordMessageType get(Integer intValue) { 
        return ReplaceKeywordMessageType.values()[intValue-1];
    }
	
	public int getIntValue(){
		return (ordinal()+1);
	}
	
	@Override
	public String toString() {
		return String.valueOf(getIntValue());
	}
}