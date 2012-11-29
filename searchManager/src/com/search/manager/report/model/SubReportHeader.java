package com.search.manager.report.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the contents of the report header.
 * Note: When setting title and subtitle, the following strings can be defined:
 * 		 %%StoreName%% 		-> replace with Store Name
 * 		 %%User%% 			-> replace with User's Name
 * 		 %%Date%% 			-> replace with Date
 * These strings will be replaced at runtime by another class
 */
public class SubReportHeader extends ReportHeader {
	
	public SubReportHeader() {
	}
	
	public SubReportHeader(Map<String, String> rows) {
		this.rows = rows;
	}

	Map<String, String> rows;

	public Map<String, String> getRows() {
		return rows;
	}

	public void setRows(Map<String, String> rows) {
		this.rows = rows;
	}
	
	public void addRow(String key, String value){
		if(rows==null){
			rows = new HashMap<String, String>();
		}
		rows.put(key, value);
	}
}
