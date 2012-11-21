package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum MemberTypeEntity {
	PART_NUMBER("Part Number", "Part Number"),
	FACET("Facet", "Facet");

	private final String displayText;
	private final String description;
	private static final Map<String,MemberTypeEntity> lookup = new HashMap<String,MemberTypeEntity>();

	MemberTypeEntity(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}

	static {
		for(MemberTypeEntity s : EnumSet.allOf(MemberTypeEntity.class))
			lookup.put(StringUtils.lowerCase(s.getDisplayText()), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}

	public static MemberTypeEntity get(String displayText) { 
		return lookup.get(StringUtils.lowerCase(displayText)); 
	}
	
	public static MemberTypeEntity get(Integer intValue) { 
        return (intValue >= 0 && intValue < lookup.size()) ? MemberTypeEntity.values()[intValue-1] : null;
    }

}
