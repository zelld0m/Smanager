package com.search.manager.utility;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SearchLogger {
	private static final Logger logger = Logger.getLogger(SearchLogger.class);
	
	private static boolean goLog;
	private String keyword;
	
	public SearchLogger(boolean isSearchGui, int startRow, int requestedRows, String keyword) {
		this.keyword = keyword;
		validate(isSearchGui, startRow, requestedRows, keyword);
	}
	
	public static void logInfo(boolean isSearchGui, int startRow, int requestedRows, String keyword){
		if(validate(isSearchGui, startRow, requestedRows, keyword))
			info("keyword : "+keyword);
	}
	
	public static void logError(boolean isSearchGui, int startRow, int requestedRows, String keyword){
		if(validate(isSearchGui, startRow, requestedRows, keyword))
			error("keyword : "+keyword);
	}
	
	public static void logDebug(boolean isSearchGui, int startRow, int requestedRows, String keyword){
		if(validate(isSearchGui, startRow, requestedRows, keyword))
			debug("keyword : "+keyword);
	}
	
	public static void info(Object obj){
		logger.info(obj);
	}
	
	public static void error(Object obj){
		logger.error(obj);
	}
	
	public static void debug(Object obj){
		logger.debug(obj);
	}
	
	public void logInfo(){
		if(goLog)
			info("keyword : "+this.keyword);
	}
	
	public void logError(){
		if(goLog)
			error("keyword : "+this.keyword);
	}
	
	public void logDebug(){
		if(goLog)
			debug("keyword : "+this.keyword);
	}

	private static boolean validate(boolean isSearchGui, int startRow, int requestedRows, String keyword){
		if(!isSearchGui && startRow == 0 && requestedRows > 0 && StringUtils.isNotEmpty(keyword))
			goLog = true;
		else
			goLog = false;
		
		return goLog;
	}
}
