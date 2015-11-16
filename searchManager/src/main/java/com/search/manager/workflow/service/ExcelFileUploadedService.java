package com.search.manager.workflow.service;

import java.util.List;
import java.util.Map;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.reports.manager.model.ExcelFileUploaded;

public interface ExcelFileUploadedService {
	public Integer addExcelFileUploadeds(String ruleType) throws DaoException;

	public RecordSet<ExcelFileUploaded> getExcelFileUploadeds(String storeId,
			int ruleTypeId,int pageNumber) throws DaoException;

	public ExcelFileUploaded getExcelFileUploaded(String excelFileUploadedId,String storeId,int ruleTypeId) throws DaoException ;

	public int deleteExcelFileUploaded(String excelFileUploadedId,
			String storeId, String fileName, String ruleType) throws DaoException;

	public String updateExcelFileUploaded(String excelFileUploadedId,String storeId,String ruleType,boolean cleanFirst)
			throws DaoException;
	public Map<String,List<ExcelFileUploaded>> getMapexcelfileuploadeds();
	public void setMapExcelFileUploadeds(
			Map<String, List<ExcelFileUploaded>> mapExcelFileUploadeds);

}