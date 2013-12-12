package com.search.reports.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import com.search.reports.interfaces.ExcelParser;
import com.search.reports.manager.model.ExcelFileReport;
import com.search.reports.manager.model.ExcelFileUploaded;

public class DefaultExcelParser implements ExcelParser {
	public DefaultExcelParser(String sheetName) {
		this.sheetName = sheetName;
	}

	String sheetName;
	
	@Override
	public ExcelFileUploaded createExcelFileUploaded(XSSFWorkbook workbook, String fileName) {
		XSSFSheet sheet= workbook.getSheet(sheetName);
		Iterator<Row> rowIterator = sheet.iterator();
        ExcelFileUploaded excelFileUploaded = new ExcelFileUploaded();
        excelFileUploaded.setFileName(fileName);
        List<ExcelFileReport> excelFileReports=new ArrayList<ExcelFileReport>();        
        boolean isFirstRead = true;
       
        while (rowIterator.hasNext()) {
        	Row row = rowIterator.next();
        	Iterator<Cell> cellIterator = row.cellIterator(); 
        	ExcelFileReport excelFileReport=new ExcelFileReport();
        	int cellCounter = 0;
        	if (!isFirstRead){
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    	switch(cellCounter){
                    	case 0:
                    			excelFileReport.setKeyword(cell.getStringCellValue());
                    			 break;
                    	case 1:
                    			excelFileReport.setRank(String.valueOf(cell.getNumericCellValue()));
                    			 break;
                    	case 2:		
                    			excelFileReport.setSku(String.valueOf(cell.getNumericCellValue()));
                    			 break;
                    	case 3:            
                    			excelFileReport.setName(cell.getStringCellValue());
                    			 break;
                    	case 4:                                			                    		
                				excelFileReport.setExpiration(new DateTime(cell.getDateCellValue()));
                				 break;
                		default:break;                			
                    	}

                    cellCounter++;
                }     
                if (!excelFileReport.getKeyword().equals(""))
                	excelFileReports.add(excelFileReport);
                else
                	break;
        	}else{
        		isFirstRead=false;
        	}

        }
        excelFileUploaded.setExcelFileReports(excelFileReports);
		return excelFileUploaded;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}


}
