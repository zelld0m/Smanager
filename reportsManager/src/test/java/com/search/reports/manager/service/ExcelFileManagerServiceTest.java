package com.search.reports.manager.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.search.reports.implementations.DefaultExcelParser;
import com.search.reports.manager.model.ExcelFileReport;
import com.search.reports.manager.model.ExcelFileUploaded;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 14, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class ExcelFileManagerServiceTest {

    @Autowired
    private ExcelFileManagerService excelFileManagerService;

    @Test
    public void testExcelFileManager() throws IOException {
    	excelFileManagerService = new ExcelFileManagerService(new DefaultExcelParser("elevate"));
    	File excelFile = FileUtils.getFile("src/test/resources/report1.xlsx");
    	Map<String, FileInputStream> mp = new HashMap<String, FileInputStream>();
    	mp.put(excelFile.getName(), new FileInputStream(excelFile));
    	List<ExcelFileUploaded> excelFileUploadeds=  excelFileManagerService.uploadExcelFile(mp);
    	ExcelFileUploaded excelFileUploaded=excelFileUploadeds.get(0);
    	System.out.println("jojotest:"+excelFileUploaded.getFileName());
    	System.out.println("jojotest:"+excelFileUploaded.getExcelFileReports().size());
    	List<ExcelFileReport> excelFileReports=excelFileUploaded.getExcelFileReports();
    	for (ExcelFileReport excelFileReport:excelFileReports ){
    		System.out.println("getKeyword: " + excelFileReport.getKeyword());
    		System.out.println("getRank: " + excelFileReport.getRank());
    		System.out.println("getSku: " + excelFileReport.getSku());
    		System.out.println("getExpiration: " + excelFileReport.getExpiration());
    	}
    }    
}
