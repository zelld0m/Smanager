package com.search.reports.interfaces;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.search.reports.manager.model.ExcelFileUploaded;

public interface ExcelParser {
	ExcelFileUploaded createExcelFileUploaded(XSSFWorkbook workbook,String fileName);	
}
