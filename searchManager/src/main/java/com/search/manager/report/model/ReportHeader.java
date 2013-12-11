package com.search.manager.report.model;

import org.joda.time.DateTime;

/**
 * Represents the contents of the report header. Note: When setting title and
 * subtitle, the following strings can be defined: %%StoreName%% -> replace with
 * Store Name %%User%% -> replace with User's Name %%Date%% -> replace with Date
 * These strings will be replaced at runtime by another class
 */
public class ReportHeader {

	/** Filename sans extension of the report file generated */
	private String reportName;
	/** Filename sans extension of the report file generated */
	private String subReportName;
	/** Filename sans extension of the report file generated */
	private String fileName;
	private DateTime date;

	public ReportHeader() {
	}

	public ReportHeader(String reportName, String subReportName,
			String fileName, DateTime date) {
		this.reportName = reportName;
		this.subReportName = subReportName;
		this.fileName = fileName;
		this.date = date;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getSubReportName() {
		return subReportName;
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

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

}
