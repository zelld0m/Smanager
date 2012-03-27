package com.search.manager.report.model;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;

/**
 * Represents the contents of the report header.
 * Note: When setting title and subtitle, the following strings can be defined:
 * 		 %%StoreName%% 		-> replace with Store Name
 * 		 %%User%% 			-> replace with User's Name
 * 		 %%Date%% 			-> replace with Date
 * These strings will be replaced at runtime by another class
 */
public class ReportHeader {
	
	public ReportHeader() {
	}
	
	public ReportHeader(String reportName, String subReportName, String fileName, Date date) {
		this.reportName = reportName;
		this.subReportName = subReportName;
		this.fileName = fileName;
		this.date = date;
	}

	/** Filename sans extension of the report file generated */
	private String reportName;
	/** Filename sans extension of the report file generated */
	private String subReportName;
	/** Filename sans extension of the report file generated */
	private String fileName;
	private Date date;
	
	private String replaceValues(String string) {
		return StringUtils.replace(
				StringUtils.replace(
					StringUtils.replace(
						string, "%%StoreName%%", UtilityService.getStoreName()),
						"%%User%%", UtilityService.getUsername()),
						"%%Date%%", DateAndTimeUtils.formatDateUsingConfig(UtilityService.getStoreName(), date));
	}
	
	public String getReportName() {
		return replaceValues(reportName);
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getSubReportName() {
		return replaceValues(subReportName);
	}
	
	public void setSubReportName(String subReportName) {
		this.subReportName = subReportName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

}
