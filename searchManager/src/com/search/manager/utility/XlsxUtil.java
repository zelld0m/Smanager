package com.search.manager.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
public class XlsxUtil {
	
	/** Retrieve XLSX worksheet by worksheet number given workbook */
	public static XSSFSheet getXlsxWorksheet(XSSFWorkbook workbook, int sheetNum) throws IOException{
		  try{
			  
			  if(workbook == null)
					throw new NullPointerException("Workbook doesn't exist");
			  
			  if(workbook.getSheetAt(sheetNum) == null)
				  throw new NullPointerException("Worksheet number \""+sheetNum+"\" doesn't exist");

			  return workbook.getSheetAt(sheetNum);   
			}catch (NullPointerException e) {
				throw new NullPointerException(e.getMessage());
			}  
	}
	
	/** Retrieve XLSX worksheet by worksheet name given workbook */
	public static XSSFSheet getXlsxWorksheet(XSSFWorkbook workbook, String sheetName) throws IOException{ 
		try{
			if(workbook == null)
				throw new NullPointerException("Workbook doesn't exist");

			if(workbook.getSheet(sheetName) == null)
				throw new NullPointerException("Worksheet \""+sheetName+"\" doesn't exist");
			
			return workbook.getSheet(sheetName);  
		}catch (NullPointerException e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/** Retrieve XLSX  worksheet by worksheet name and file path */
	public static XSSFSheet getXlsxWorksheet(String xlsxFile, String sheetName) throws IOException{ 
		try{
			XSSFWorkbook workbook = getXlsxWorkbook(xlsxFile);
			
			 if(workbook.getSheet(sheetName) == null)
				  throw new NullPointerException();
			
			return workbook.getSheet(sheetName);  
		}catch (NullPointerException e) {
			throw new NullPointerException("Worksheet \""+sheetName+"\" doesn't exist");
		}
	}
	
	/** Retrieve XLSX  worksheet by worksheet number and file path */
	public static XSSFSheet getXlsxWorksheet(String xlsxFile, int sheetNum) throws IOException{
		  try{
			  XSSFWorkbook workbook = getXlsxWorkbook(xlsxFile);
			  
			  if(workbook.getSheetAt(sheetNum) == null)
				  throw new NullPointerException();

			  return workbook.getSheetAt(sheetNum);   
			}catch (NullPointerException e) {
				throw new NullPointerException("Worksheet number \""+sheetNum+"\" doesn't exist");
			}  
	}
	
	/** Retrieve XLSX  workbook by file path */
	public static XSSFWorkbook getXlsxWorkbook(String xlsxFile) throws IOException{
		FileInputStream xlsx = null;
		try {
			xlsx = new FileInputStream(xlsxFile);
			return new XSSFWorkbook(xlsx); 
		}catch (FileNotFoundException e){
			throw new IOException();
		}finally{
			if(xlsx != null)
				xlsx.close();
		}
	}
	
	/** Retrieve XLSX worksheet data by worksheet number given workbook data*/
	public static Vector<String[]> getXlsxData(XSSFWorkbook workbook, int sheetNum) throws IOException{
		
		Vector<String[]> list = new Vector<String[]>();
	    
	    XSSFSheet sheet = getXlsxWorksheet(workbook, sheetNum);
	    Iterator<Row> rows = sheet.rowIterator();
	    int rowcnt = 0;
	    int colcnt = 0;
	    String[] data = null;
	        
        while (rows.hasNext()){
            XSSFRow row = ((XSSFRow) rows.next());
            Iterator<Cell> cells = row.cellIterator();
           
            int cellcnt = 0;      
            if(rowcnt == 0){
            	while(cells.hasNext()){
            		cells.next();
            		colcnt++;
                }
             	rowcnt++;
             	continue;
            }else{
            	data = new String[colcnt];
            	while(cells.hasNext()){
            		 XSSFCell cell = (XSSFCell) cells.next();
            		 
            		 if(colcnt < cell.getColumnIndex() + 1)
            			 break;
            		 
            		 data[cellcnt] = getCellValue(cell);
            		 cellcnt++;	 
                }
            }
            
            if(data != null)
            	list.add(data);
            rowcnt++; 
        }
		return list;   
	}
	
	/** Retrieve XLSX worksheet data by worksheet number and file path */
	public static Vector<String[]> getXlsxData(String xlsxFile, int sheetNum) throws IOException{
		
		Vector<String[]> list = new Vector<String[]>();
	    
	    XSSFSheet sheet = getXlsxWorksheet(xlsxFile, sheetNum);
	    Iterator<Row> rows = sheet.rowIterator();
	    int rowcnt = 0;
	    int colcnt = 0;
	    String[] data = null;
	        
        while (rows.hasNext()){
            XSSFRow row = ((XSSFRow) rows.next());
            Iterator<Cell> cells = row.cellIterator();
           
            int cellcnt = 0;      
            if(rowcnt == 0){
            	while(cells.hasNext()){
            		cells.next();
            		colcnt++;
                }
             	rowcnt++;
             	continue;
            }else{
            	data = new String[colcnt];
            	while(cells.hasNext()){
            		 XSSFCell cell = (XSSFCell) cells.next();
            		 
            		 if(colcnt < cell.getColumnIndex() + 1)
            			 break;
            		 
            		 data[cellcnt] = getCellValue(cell);
            		 cellcnt++;	 
                }
            }
            
            if(data != null)
            	list.add(data);
            rowcnt++; 
        }
		return list;
	}
	
	/** Retrieve XLSX worksheet data by worksheet name given workbook data */
	public static Vector<String[]> getXlsxData(XSSFWorkbook workbook, String sheetName) throws IOException{
		
		Vector<String[]> list = new Vector<String[]>();
	    
	    XSSFSheet sheet = getXlsxWorksheet(workbook, sheetName);
	    Iterator<Row> rows = sheet.rowIterator();
	    int rowcnt = 0;
	    int colcnt = 0;
	    String[] data = null;
	        
        while (rows.hasNext()){
            XSSFRow row = ((XSSFRow) rows.next());
            Iterator<Cell> cells = row.cellIterator();
           
            int cellcnt = 0;      
            if(rowcnt == 0){
            	while(cells.hasNext()){
            		cells.next();
            		colcnt++;
                }
             	rowcnt++;
             	continue;
            }else{
            	data = new String[colcnt];
            	while(cells.hasNext()){
            		 XSSFCell cell = (XSSFCell) cells.next();
            		 
            		 if(colcnt < cell.getColumnIndex() + 1)
            			 break;
            		 
            		 data[cellcnt] = getCellValue(cell);
            		 cellcnt++;	 
                }
            }
            
            if(data != null)
            	list.add(data);
            rowcnt++; 
        }
		return list;
	}
	
	/** Retrieve XLSX worksheet data by worksheet name and file path */
	public static Vector<String[]> getXlsxData(String xlsxFile, String sheetName) throws IOException{
		
		Vector<String[]> list = new Vector<String[]>();
	    
	    XSSFSheet sheet = getXlsxWorksheet(xlsxFile, sheetName);
	    Iterator<Row> rows = sheet.rowIterator();
	    int rowcnt = 0;
	    int colcnt = 0;
	    String[] data = null;
	        
        while (rows.hasNext()){
            XSSFRow row = ((XSSFRow) rows.next());
            Iterator<Cell> cells = row.cellIterator();
           
            int cellcnt = 0;      
            if(rowcnt == 0){
            	while(cells.hasNext()){
            		cells.next();
            		colcnt++;
                }
             	rowcnt++;
             	continue;
            }else{
            	data = new String[colcnt];
            	while(cells.hasNext()){
            		 XSSFCell cell = (XSSFCell) cells.next();
            		 
            		 if(colcnt < cell.getColumnIndex() + 1)
            			 break;
            		 
            		 data[cellcnt] = getCellValue(cell);
            		 cellcnt++;	 
                }
            }
            
            if(data != null)
            	list.add(data);
            rowcnt++; 
        }
		return list;
	}
	
	public static String getCellValue(Cell cell){
		
		try{
			switch (cell.getCellType()) {
	        case Cell.CELL_TYPE_STRING:
	            return cell.getRichStringCellValue().getString();
	        case Cell.CELL_TYPE_NUMERIC:
	            if (DateUtil.isCellDateFormatted(cell))
	                return cell.getDateCellValue().toString();
	            else 
	                return String.valueOf(cell.getNumericCellValue());
	        case Cell.CELL_TYPE_BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case Cell.CELL_TYPE_FORMULA:
	            return String.valueOf(cell.getCellFormula());
	        default:
	            return "";
			}
		}catch(Exception e){}
		return "";
	}
}