package com.search.reports.manager.model;

import java.util.List;

import com.search.reports.enums.ExcelUploadStatus;

public class TypeaheadUpdateReportList {

	private ExcelUploadStatus status;
	private String excelId;
	private String storeId;
	private String fileName;
	private List<TypeaheadUpdateReport> list;
	
	public TypeaheadUpdateReportList() {}
	
	public TypeaheadUpdateReportList(String storeId, String fileName, List<TypeaheadUpdateReport> list) {
		this.storeId = storeId;
		this.fileName = fileName;
		this.list = list;
	}
	
	public ExcelUploadStatus getStatus() {
		return status;
	}

	public void setStatus(ExcelUploadStatus status) {
		this.status = status;
	}

	public String getExcelId() {
		return excelId;
	}
	public void setExcelId(String excelId) {
		this.excelId = excelId;
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
	public List<TypeaheadUpdateReport> getList() {
		return list;
	}
	public void setList(List<TypeaheadUpdateReport> list) {
		this.list = list;
	}
		

}
