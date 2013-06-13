package com.search.manager.jodatime;

public enum JodaPatternType {
	DATE("Date"),
	DATE_TIME("Date Time");

	private final String displayText;

	JodaPatternType(String displayText){
		this.displayText = displayText;
	}

	public String getDisplayText() {
		return displayText;
	}
}