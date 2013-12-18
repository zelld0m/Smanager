package com.search.manager.workflow.dao;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.reports.manager.model.ExcelFileReport;
import com.search.reports.manager.model.ExcelFileUploaded;

public interface ExcelFileUploadedDAO {

	
	public ExcelFileReport addExcelFileReport(ExcelFileReport excelFileReport)
			throws DaoException;
	
	public int deleteExcelFileUploaded(ExcelFileUploaded excelFileUploaded)
			throws DaoException;
	
	public ExcelFileUploaded addExcelFileUploaded(ExcelFileUploaded excelFileUploaded)
			throws DaoException;
	
	public ExcelFileUploaded updateExcelFileUploaded(ExcelFileUploaded excelFileUploaded)
			throws DaoException;	
	
	public ExcelFileReport getExcelFileReport(
			ExcelFileReport excelFileReport) throws DaoException;
	
	public RecordSet<ExcelFileReport> getExcelFileReports(
			SearchCriteria<ExcelFileReport> criteria) throws DaoException;
	
	public ExcelFileUploaded getExcelFileUploaded(
			ExcelFileUploaded excelFileUploaded) throws DaoException;
	
	public RecordSet<ExcelFileUploaded> getExcelFileUploadeds(
			SearchCriteria<ExcelFileUploaded> criteria) throws DaoException;
	
	
}
