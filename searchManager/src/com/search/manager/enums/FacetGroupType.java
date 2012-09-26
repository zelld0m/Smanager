package com.search.manager.enums;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum FacetGroupType {
	CATEGORY("Category"),
	MANUFACTURER("Manufacturer");
	
	private final String displayText;
	
	private FacetGroupType(String displayText) {
		this.displayText = displayText;
	}

	public String getDisplayText() {
		return displayText;
	}
}