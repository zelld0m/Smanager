package com.search.reports.manager.service;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.reports.interfaces.ExcelParser;
import com.search.reports.manager.ExcelFileManager;
import com.search.reports.manager.model.ExcelFileUploaded;

/**
 *
 * @author Jonathan Livelo
 * @since Nov 19, 2013
 * @version 1.0
 */
@Service
public class ExcelFileManagerService {
	public ExcelFileManagerService(){
		
	}
    public ExcelFileManagerService(ExcelParser excelParser) {
    	excelFileManager=new ExcelFileManager(excelParser);
	}
    
	@Autowired
    private ExcelFileManager excelFileManager;
    


    public List<ExcelFileUploaded> uploadExcelFile(Map<String,FileInputStream> mp) {
        return excelFileManager.uploadExportFile(mp);
    }
    
    public List<ExcelFileUploaded> uploadExcelFile(FileInputStream input, String fileName) {
        return excelFileManager.uploadExportFile(input, fileName);
    }    

}
