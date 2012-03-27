package com.search.manager.report;

public class ReportConstant {
	public static final String ELEVATED_TITLE = "Elevate Items for %s";
	
	
	public static String getHeaderText(String format, String value){
		return String.format(format, value);
	}
}
