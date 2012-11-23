package com.search.manager.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public enum RuleStatusEntity {
	// approval status
	PENDING("Pending Approval", "Pending Approval"),
	APPROVED("Changes Approved", "Changes Approved"),
	REJECTED("Changes Rejected", "Changes Rejected"),
	// publish status
	PUBLISHED("Published", "Published"),
	UNPUBLISHED("Unpublished", "Unpublished"),
	// update status
	ADD("Add rule", "Add rule"),
	DELETE("Delete rule", "Delete rule"),
	UPDATE("Update rule", "Update rule"),
	// export/import status
	EXPORTED("Exported", "Exported"),
	IMPORTED("Imported", "Imported");

	
	private final String displayText;
	private final String description;
	
	private static final Map<String,RuleStatusEntity> lookup = new HashMap<String,RuleStatusEntity>();

	RuleStatusEntity(String displayText, String description){
		this.displayText = displayText;
		this.description = description;
	}

	static {
		for(RuleStatusEntity s : EnumSet.allOf(RuleStatusEntity.class))
			lookup.put(s.getDisplayText(), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getDescription() {
		return description;
	}

	public static RuleStatusEntity get(String displayText) { 
		return lookup.get(displayText); 
	}
	
	public static RuleStatusEntity get(Integer intValue) { 
        return RuleStatusEntity.values()[intValue-1];
    }

	public static String getString(RuleStatusEntity ... entities) {
		StringBuilder builder = new StringBuilder();
		for (RuleStatusEntity entity: entities) {
			builder.append(",").append(entity.toString());
		}
		if (builder.length() > 0) {
			builder.delete(0, 1);
		}
		return builder.toString();
	}
	
}
