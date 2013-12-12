package com.search.manager.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.reports.FileUploadForm;
import com.search.manager.service.ExcelFileUploadedService;
import com.search.manager.service.UtilityService;
import com.search.reports.implementations.DefaultExcelParser;
import com.search.reports.manager.model.ExcelFileReport;
import com.search.reports.manager.model.ExcelFileUploaded;
import com.search.reports.manager.service.ExcelFileManagerService;

/**
 * 
 * @author Jonathan Livelo
 * @since Nov 14, 2013
 * @version 1.0
 */
@Controller
@RequestMapping("/excelFileUploaded")
public class ExcelFileUploadedController {

	@Autowired
	private ExcelFileUploadedService excelFileUploadedService;
	@Autowired
	private ExcelFileManagerService excelFileManagerService;

	@RequestMapping(value = "/{ruleType}", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String execute(HttpServletRequest request,
			HttpServletResponse response, Model model,
			@PathVariable String ruleType) throws IOException, DaoException {
		String storeId = UtilityService.getStoreId();
		int ruleTypeId = RuleEntity.getId(ruleType);
		RecordSet<ExcelFileUploaded> recordSet = excelFileUploadedService
				.getExcelFileUploadeds(storeId, ruleTypeId,1);
		model.addAttribute("excelFileUploadeds", recordSet.getList());
		model.addAttribute("ruleType", ruleType);
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", 1);
		return "excelFileUploaded/excelFileUploaded";
	}
	
	@RequestMapping(value = "/paging/{ruleType}/{pageNumber}", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String paging(HttpServletRequest request,
			HttpServletResponse response, Model model,
			@PathVariable String ruleType,@PathVariable int pageNumber) throws IOException, DaoException {
		String storeId = UtilityService.getStoreId();
		int ruleTypeId = RuleEntity.getId(ruleType);
		RecordSet<ExcelFileUploaded> recordSet = excelFileUploadedService
				.getExcelFileUploadeds(storeId, ruleTypeId,pageNumber);
		model.addAttribute("excelFileUploadeds", recordSet.getList());
		model.addAttribute("ruleType", ruleType);
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", pageNumber);
		return "excelFileUploaded/excelFileUploaded";
	}	
	@RequestMapping(value = "/details/{ruleType}/{excelFileUploadedId}", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String details(HttpServletRequest request,
			HttpServletResponse response, Model model,
			@PathVariable String ruleType,@PathVariable String excelFileUploadedId) throws IOException, DaoException {
		String storeId = UtilityService.getStoreId();
		int ruleTypeId = RuleEntity.getId(ruleType);
		ExcelFileUploaded excelFileUploaded = excelFileUploadedService.getExcelFileUploaded(excelFileUploadedId,storeId, ruleTypeId);
		Map<String,List<ExcelFileReport>> mpKeyword = new HashMap<String,List<ExcelFileReport>>();
		for(ExcelFileReport excelFileReport:excelFileUploaded.getExcelFileReports()){
			List<ExcelFileReport> excelFileReports = mpKeyword.get(excelFileReport.getKeyword());
			if(excelFileReports == null){
				excelFileReports= new ArrayList<ExcelFileReport>();				
			}
			excelFileReports.add(excelFileReport);
			mpKeyword.put(excelFileReport.getKeyword(),excelFileReports);
		}
		model.addAttribute("excelFileUploaded", excelFileUploaded);
		model.addAttribute("keywords", mpKeyword);
		model.addAttribute("ruleType", ruleType);
		return "excelFileUploaded/excelFileReport";
	}	

	@RequestMapping(value = "/upload/{ruleType}/", method = RequestMethod.POST)
	public ModelAndView upload(
			@ModelAttribute("uploadForm") FileUploadForm uploadForm,
			Model model, @PathVariable String ruleType) {
		Map<String, FileInputStream> mp = new HashMap<String, FileInputStream>();
		List<MultipartFile> files = uploadForm.getFiles();
		try {
			if (files != null && files.size() > 0) {
				for (MultipartFile multipartFile : files) {
					String fileName = multipartFile.getOriginalFilename();
					InputStream input;
					input = multipartFile.getInputStream();
					if (input instanceof FileInputStream) {
						mp.put(fileName, (FileInputStream) input);
					}
				}
			}
			excelFileManagerService = new ExcelFileManagerService(
					new DefaultExcelParser(ruleType));
		} catch (IOException e) {			
			e.printStackTrace();
		}
		List<ExcelFileUploaded> excelFileUploadeds = excelFileManagerService
				.uploadExcelFile(mp);
		String userName = UtilityService.getUsername();
		Map<String,List<ExcelFileUploaded>> parsedData=new HashMap<String,List<ExcelFileUploaded>>();
		parsedData.put(userName, excelFileUploadeds);
		excelFileUploadedService.setMapExcelFileUploadeds(parsedData);
		model.addAttribute("excelFileUploadeds", excelFileUploadeds);
		return new ModelAndView("excelFileUploaded/excelFileUploadedPreview");
	}

}
