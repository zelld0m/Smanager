package com.search.reports.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

}
