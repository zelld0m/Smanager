package com.search.manager.core.enums;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter = EnumConverter.class, type = "enum")
public enum SectionType {
	
	Category("Category"),
	Brand("Brand"),
	Section("Section");
	
	 private String name;
	
	private SectionType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static SectionType find(int ordinal) {
        for (int i = 0; i < values().length; i++) {
            if (i == ordinal)
                return values()[i];
        }
        return null;
    }
}
