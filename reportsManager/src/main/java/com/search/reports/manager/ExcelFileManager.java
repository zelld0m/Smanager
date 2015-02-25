package com.search.reports.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.search.reports.implementations.TypeaheadExcelParser;
import com.search.reports.interfaces.ExcelParser;
import com.search.reports.manager.model.ExcelFileUploaded;
import com.search.reports.manager.model.TypeaheadUpdateReport;
import com.search.reports.manager.model.TypeaheadUpdateReportList;

/**
 *
 * @author Jonathan Livelo
 * @since Nov. 15, 2013
 * @version 1.0
 */
@Component
public class ExcelFileManager {

	private ExcelParser excelParser;
	private TypeaheadExcelParser typeaheadExcelParser;
    private static final Logger logger = LoggerFactory.getLogger(ExcelFileManager.class);
    public static final String BASE_PATH = File.separator + "home" + File.separator + "solr" +   File.separator  + "uploads"  + File.separator;
    public static final String QUEUE_FOLDER = "queue"; 
    public static final String PROCESSED_FOLDER = "processed"; 
    public static final String FAILED_FOLDER = "failed"; 
    
    public ExcelFileManager(){    	
    }
    
    public ExcelFileManager(ExcelParser excelParser) {
    	this.excelParser=excelParser;
	}
    
    public List<ExcelFileUploaded> uploadExportFile(InputStream input,String fileName) {
    	Map<String, InputStream> mp = new HashMap<String, InputStream>();
    	mp.put(fileName, input);
        return uploadExportFile(mp);
    }    

    public List<ExcelFileUploaded> uploadExportFile(Map<String,InputStream> mp) {    	
    	List<ExcelFileUploaded> excelFileUploadeds = Lists.newArrayList();
    	Iterator<Entry<String, InputStream>> it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook((InputStream) pairs.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
			excelFileUploadeds.add(excelParser.createExcelFileUploaded(workbook, pairs.getKey().toString()));
            it.remove();
        }    	
        return excelFileUploadeds;
    }
    
    public List<TypeaheadUpdateReportList> uploadTypeaheadUpdate(String storeId, InputStream inputStream, String fileName) throws Exception {
    	List<TypeaheadUpdateReportList> updateList = Lists.newArrayList();
    	
    	typeaheadExcelParser = new TypeaheadExcelParser();
        XSSFWorkbook workbook = null;
            
 		try {
 			workbook = new XSSFWorkbook(inputStream);
 			updateList.add(typeaheadExcelParser.createTypeaheadUpdateExcel(workbook, storeId, (String) fileName));
 			
 			File file = saveUploadedFile(workbook, fileName, BASE_PATH + File.separator + storeId + File.separator + "typeahead");
 			updateExcelStatus(file, "PENDING");
 		} catch (IOException e) {
 			throw e;
 		}
 		
    	return updateList;
    }
    
    public List<TypeaheadUpdateReportList> getTypeaheadReportList(String storeId, String fileName) throws Exception {
    	List<TypeaheadUpdateReportList> updateList = Lists.newArrayList();
    	
    	typeaheadExcelParser = new TypeaheadExcelParser();
        XSSFWorkbook workbook = null;
            
 		try {
 			workbook = getUploadedWorkbook(storeId, "typeahead", fileName);
 			updateList.add(typeaheadExcelParser.createTypeaheadUpdateExcel(workbook, storeId, (String) fileName));
 			 
 		} catch (IOException e) {
 			throw e;
 		}
 		
    	return updateList;
    }
    
    public XSSFWorkbook getUploadedWorkbook(String storeId, String ruleType, String fileName) throws FileNotFoundException, IOException {
    	
    	return  new XSSFWorkbook(new FileInputStream(BASE_PATH + File.separator + storeId + File.separator + ruleType + File.separator + fileName));
    }
    
    public XSSFWorkbook getTypeaheadWorkbook(List<TypeaheadUpdateReport> rowList) {
    	XSSFWorkbook newWorkbook = new XSSFWorkbook();
    	XSSFSheet sheet = newWorkbook.createSheet();
    	
    	XSSFRow header = sheet.createRow(0);
    	header.createCell(0).setCellValue("KEYWORD");
    	header.createCell(1).setCellValue("PRIORITY");
    	header.createCell(2).setCellValue("ENABLED");
    	
    	for(int i=0 ; i < rowList.size(); i++) {
    		TypeaheadUpdateReport report = rowList.get(i);
    		XSSFRow row = sheet.createRow(i + 1);
    		row.createCell(0).setCellValue(report.getKeyword());
    		row.createCell(1).setCellValue(report.getPriority());
    		row.createCell(2).setCellValue(report.getEnabled());
    	}
    	
    	return newWorkbook;
    }
    
    public Boolean deleteUploadedExcel(ExcelFileUploaded excelFile) {
    	String storeId = excelFile.getStoreId();
    	String fileName = excelFile.getFileName();
    	int ruleType = excelFile.getRuleTypeId();
    	
    	switch(ruleType) {
    	case 13: 
    		File file = new File(BASE_PATH + File.separator + storeId + File.separator + "typeahead" + File.separator + fileName);
    		return file.delete();
    	default: break;
    	}
    	
    	return true;
    }
    
    public Boolean processUploadedExcel(ExcelFileUploaded excelFile) throws IOException {
    	String storeId = excelFile.getStoreId();
    	String fileName = excelFile.getFileName();
    	int ruleType = excelFile.getRuleTypeId();
    	switch(ruleType) {
    	case 13:
    		String baseDirectory = BASE_PATH + File.separator + storeId + File.separator + "typeahead";
    		File file = new File(baseDirectory + File.separator + fileName);

    		if(file.exists()) {
    			File queueFolder = new File(baseDirectory + File.separator + QUEUE_FOLDER);
    			queueFolder.mkdirs();
    			File destFile = new File(queueFolder, fileName);
    			FileUtils.copyFile(file, destFile);
    			updateExcelStatus(file, "QUEUED");
    			return true;

    		}

    		return false;

    	default: break;
    	}
    	return true;
    }
    
    private File saveUploadedFile(XSSFWorkbook workbook, String fileName, String subDirectory) throws IOException {
    	File subdirectory = new File(subDirectory);
    	if(!subdirectory.exists()) {
    		subdirectory.mkdirs();
    	}
    	
    	File newFile = new File(subdirectory.getAbsolutePath() + File.separator + fileName);
    	if(newFile.exists()) {
    		throw new IOException("The file '" + fileName + "' already exists.");
    	}
    	OutputStream outputStream = null;
    	try {
			outputStream = new FileOutputStream(newFile);
			workbook.write(outputStream);
			
		} catch (IOException e) {
			throw e;
		} finally {
			if(outputStream != null) {
				outputStream.close();
			}
		}
    	
    	return newFile;
    }
    
    public void updateExcelStatus(File baseFile, String status) throws IOException {
		File statusFile = new File(baseFile.getAbsolutePath() + ".txt");
		FileWriter writer = new FileWriter(statusFile, false);
		writer.write(status);
		writer.close();
	}

}
