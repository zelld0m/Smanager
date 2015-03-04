package com.search.reports.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.search.reports.manager.model.TypeaheadUpdateReport;
import com.search.reports.manager.model.TypeaheadUpdateReportList;

public class TypeaheadExcelParser {

	public TypeaheadUpdateReportList createTypeaheadUpdateExcel(XSSFWorkbook workbook, String storeId,
			String fileName) throws IOException {
		
		TypeaheadUpdateReportList list = null;
		
		for(int i=0; i < workbook.getNumberOfSheets() && i < 1  ; i++) {
			XSSFSheet sheet= workbook.getSheetAt(i);
			Iterator<Row> rowIterator = sheet.iterator();
			List<TypeaheadUpdateReport> updateReports = new ArrayList<TypeaheadUpdateReport>();
			
			int count = 0;
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(count == 0) {
					count ++;
					continue;
				}
				Iterator<Cell> cellIterator = row.cellIterator(); 
				TypeaheadUpdateReport report = new TypeaheadUpdateReport();
				try{
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						
						switch(cell.getColumnIndex()) {
						case 0:
							
							if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
								report.setKeyword(new String(cell.getStringCellValue()));
							} else {
								double value = cell.getNumericCellValue();
								if(value == (long) value)
									report.setKeyword(String.format("%d", (long) cell.getNumericCellValue()));
								else
									report.setKeyword(String.format("%s", cell.getNumericCellValue()));
							}
							
							break;
						case 1: report.setPriority(new Double (cell.getNumericCellValue()).intValue()); break;
						case 2: report.setEnabled(cell.getBooleanCellValue()); break;
						case 3: if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
									report.setErrorMessage(cell.getStringCellValue());
								}
							break;
							
						}
					}
				} catch(Exception e) {
					throw new IOException("Invalid excel format. Please download the template to verify the correct format.");
				}
				if(StringUtils.isNotEmpty(report.getKeyword()))
					updateReports.add(report);
				
			}
			list = new TypeaheadUpdateReportList(storeId, fileName, updateReports);
		}
		
		return list;
	}

}
