package com.search.reports.enums;

public enum ExcelUploadStatus {

	PENDING("PENDING", "Pending"),
	QUEUED("QUEUED", "Queued"),
	IN_PROCESS("IN PROCESS", "In Process"),
	PROCESSED("PROCESSED", "Processed");

	private final String name;
	private final String displayName;

	private ExcelUploadStatus(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static ExcelUploadStatus getByName(String name) {
		for(ExcelUploadStatus value : values()) {
			if(name.equalsIgnoreCase(value.getName()))
				return value;
		}

		return null;
	}
}


