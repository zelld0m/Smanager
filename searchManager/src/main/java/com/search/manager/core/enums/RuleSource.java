package com.search.manager.core.enums;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(
        converter=EnumConverter.class, 
        type="enum")
public enum RuleSource {

	USER("User"),
	AUTO_IMPORT("Auto Import"),
	EXCEL_UPLOAD("Excel Upload");
	
	private RuleSource(String name) {
		this.name = name;
	}
	
	private String name;
	
	public String getName() {
		return name;
	}
	
	public static RuleSource find(int ordinal) {
		for(int i = 0; i < values().length; i++) {
			if(i == ordinal)
				return values()[i];
		}
		return null;
	}
}