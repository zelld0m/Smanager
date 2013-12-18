package com.search.manager.core.enums;

import java.util.EnumSet;

import com.search.manager.workflow.model.TaskStatus;

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
}
