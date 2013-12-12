package com.search.reports.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.search.reports.interfaces.ExcelParser;
import com.search.reports.manager.model.ExcelFileUploaded;

/**
 *
 * @author Jonathan Livelo
 * @since Nov. 15, 2013
 * @version 1.0
 */
@Component
public class ExcelFileManager {

	private ExcelParser excelParser;
    private static final Logger logger = LoggerFactory.getLogger(ExcelFileManager.class);
    
    public ExcelFileManager(){    	
    }
    
    public ExcelFileManager(ExcelParser excelParser) {
    	this.excelParser=excelParser;
	}
    
    public List<ExcelFileUploaded> uploadExportFile(FileInputStream input,String fileName) {
    	Map<String, FileInputStream> mp = new HashMap<String, FileInputStream>();
    	mp.put(fileName, input);
        return uploadExportFile(mp);
    }    

    public List<ExcelFileUploaded> uploadExportFile(Map<String,FileInputStream> mp) {    	
    	List<ExcelFileUploaded> excelFileUploadeds = Lists.newArrayList();
    	Iterator<Entry<String, FileInputStream>> it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook((FileInputStream) pairs.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
			excelFileUploadeds.add(excelParser.createExcelFileUploaded(workbook, pairs.getKey().toString()));
            it.remove();
        }    	
        return excelFileUploadeds;
    }

}
