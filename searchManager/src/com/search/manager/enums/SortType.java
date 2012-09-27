package com.search.manager.enums;

import java.util.Arrays;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum SortType {
	ASC_ALPHABETICALLY("A-Z", "Ascending Alphabetically"),
	DESC_ALPHABETICALLY("Z-A", "Descending Alphabetically"),
	ASC_COUNT("Count Asc", "Ascending by Count"),
	DESC_COUNT("Count Desc", "Descending by Count");
	
	private final String displayText;
	private final String description;
	
	SortType(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
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
	
	@Override
	public String toString() {
		return String.valueOf(ordinal());
	}
}