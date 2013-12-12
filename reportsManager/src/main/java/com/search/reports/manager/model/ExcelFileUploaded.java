package com.search.reports.manager.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class ExcelFileUploaded extends LogProcess {
	String excelFileUploadedId;
	String storeId;
	String fileName;
	int ruleTypeId;	
	DateTime addedOnRuleDate;
	String  addedOnRuleBy;	
	
	public ExcelFileUploaded(String excelFileUploadedId,
			String storeId,
			int ruleTypeId,
			String fileName,
			DateTime addedOnRuleDate,
			String  addedOnRuleBy,			
			String createdBy,
			DateTime createdStamp,
			DateTime createdTxStamp,
			String lastUpdatedBy,
			DateTime lastUpdatedStamp,
			DateTime lastUpdatedTxStamp
			) {
		this.excelFileUploadedId = excelFileUploadedId;
		this.storeId = storeId;
		this.ruleTypeId = ruleTypeId;
		this.fileName = fileName;
		this.addedOnRuleDate =  addedOnRuleDate;
		this.addedOnRuleBy =  addedOnRuleBy;		
		this.createdBy = createdBy;
		this.createdStamp = createdStamp;
		this.createdTxStamp = createdTxStamp;
		this.lastUpdatedBy =  lastUpdatedBy;
		this.lastUpdatedStamp = lastUpdatedStamp;
		this.lastUpdatedTxStamp = lastUpdatedTxStamp;

	}
	public ExcelFileUploaded(){}

	
	List<ExcelFileReport> excelFileReports = new ArrayList<ExcelFileReport>();
	public String getExcelFileUploadedId() {
		return excelFileUploadedId;
	}

	public void setExcelFileUploadedId(String excelFileUploadedId) {
		this.excelFileUploadedId = excelFileUploadedId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<ExcelFileReport> getExcelFileReports() {
		return excelFileReports;
	}

	public void setExcelFileReports(List<ExcelFileReport> excelFileReports) {
		this.excelFileReports = excelFileReports;
	}
	public int getRuleTypeId() {
		return ruleTypeId;
	}
	public void setRuleTypeId(int ruleTypeId) {
		this.ruleTypeId = ruleTypeId;
	}
	public DateTime getAddedOnRuleDate() {
		return addedOnRuleDate;
	}
	public void setAddedOnRuleDate(DateTime addedOnRuleDate) {
		this.addedOnRuleDate = addedOnRuleDate;
	}
	public String getAddedOnRuleBy() {
		return addedOnRuleBy;
	}
	public void setAddedOnRuleBy(String addedOnRuleBy) {
		this.addedOnRuleBy = addedOnRuleBy;
	}
}
