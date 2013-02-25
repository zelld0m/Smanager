package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter=BeanConverter.class, 
					params=@Param(name="exclude", value="declaringClass"))
public enum ReplaceKeywordMessageType {
	DEFAULT_TEXT("Default Text", "Do not display any additional text in search results"),
	STANDARD_TEXT("Standard Text", "Display standard text (Showing Result for \"replacement keyword\" / Search instead for: original Keyword) "),
	CUSTOM_TEXT("Custom Text", "Display custom text");
   
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
		if(intValue != null)
			return ReplaceKeywordMessageType.values()[intValue-1];		
		return null;
    }
	
	public int getIntValue(){
		return (ordinal()+1);
	}
	
	@Override
	public String toString() {
		return String.valueOf(getIntValue());
	}
}