package com.search.manager.enums;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum RuleType {
	KEYWORD("Keyword", "Keyword"),
	TEMPLATE("Template", "Template");

	private final String displayText;
	private final String description;

	RuleType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return String.valueOf(ordinal());
	}
}