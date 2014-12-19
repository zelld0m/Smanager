package com.search.manager.core.enums;

public enum Status {

	ENABLED("ENABLED"),
	DISABLED("DISABLED");
	
	private String name;
	
	private Status(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
