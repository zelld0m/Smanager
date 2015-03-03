package com.search.manager.web.controller.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.reports.FileUploadForm;
import com.search.manager.report.model.KeywordAttributeReportBean;
import com.search.manager.report.model.KeywordAttributeReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.TypeaheadReportBean;
import com.search.manager.report.model.TypeaheadReportModel;
import com.search.manager.report.model.xml.KeywordAttributeXML;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.TypeaheadRuleXml;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.service.UtilityService;
import com.search.manager.workflow.dao.ExcelFileUploadedDAO;
import com.search.manager.xml.file.RuleXmlReportUtil;
import com.search.reports.manager.ExcelFileManager;
import com.search.reports.manager.model.ExcelFileUploaded;
import com.search.reports.manager.model.TypeaheadUpdateReport;
import com.search.reports.manager.model.TypeaheadUpdateReportList;

@Controller
@RequestMapping("/typeahead")
@Scope(value = "prototype")
public class TypeaheadController {

	private static final Logger logger =
            LoggerFactory.getLogger(TypeaheadController.class);
	
	@Autowired
    private RuleVersionService ruleVersionService;
	@Autowired
    private DownloadService downloadService;
	@Autowired
    private RuleXmlReportUtil ruleXmlReportUtil;
	@Autowired
    private UtilityService utilityService;
	@Autowired
	private ExcelFileManager excelFileManager;
	@Autowired
	private ExcelFileUploadedDAO excelDao;
	
	@RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store) {
        model.addAttribute("store", store);

        return "rules/typeahead";
    }

	@RequestMapping(value = "/{store}/version/xls")
    public void getRuleVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename, @RequestParam("id") String ruleId,
            @RequestParam String keyword, @RequestParam("clientTimezone") Long clientTimezone,
            @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

        DateTime headerDate = new DateTime();
        String subTitle = "Typeahead Rule [" + keyword + "]";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<TypeaheadReportBean> reportModel = new TypeaheadReportModel(reportHeader,
                new ArrayList<TypeaheadReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        List<RuleXml> xmls = ruleVersionService.getRuleVersions("Typeahead", ruleId);

        for (RuleXml xml : xmls) {
            TypeaheadRuleXml ruleXml = (TypeaheadRuleXml) xml;
            List<TypeaheadReportBean> reportBeans = new ArrayList<TypeaheadReportBean>();
            
            reportBeans.add(new TypeaheadReportBean(new TypeaheadRule(ruleXml)));
            SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(utilityService.getStoreId(), xml, RuleEntity.TYPEAHEAD);
            subModels.add(new TypeaheadReportModel(reportHeader, subReportHeader, reportBeans));
            
            List<KeywordAttributeXML> attributes = ruleXml.getKeywordAttributes();
            
            if(attributes != null && attributes.size() > 0) {
            	List<KeywordAttributeReportBean> subReportBeans = new ArrayList<KeywordAttributeReportBean>();
            	for(KeywordAttributeXML attribXML : attributes) {
            		subReportBeans.add(new KeywordAttributeReportBean(attribXML));
            	}
            	
            	subModels.add(new KeywordAttributeReportModel(reportHeader, KeywordAttributeReportBean.class, subReportBeans));
            	
            }
        }

        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
            try {
                downloadService.downloadXLS(response, reportModel, subModels);
            } catch (ClassNotFoundException e) {
                logger.error("Error encountered while trying to download.", e);
                e.printStackTrace();
            }
        }
    }
	
	@ResponseBody
	@RequestMapping(value = "/upload/{storeId}", method = RequestMethod.POST)
	public ServiceResponse<List<TypeaheadUpdateReportList>> upload(
			@ModelAttribute("uploadForm") FileUploadForm uploadForm,
			Model model, @PathVariable String storeId) {
		ServiceResponse<List<TypeaheadUpdateReportList>> response = new ServiceResponse<List<TypeaheadUpdateReportList>>();
		List<MultipartFile> files = uploadForm.getFiles();
		String fileName = null;
		try {
			if (files != null && files.size() > 0) {
				for (MultipartFile multipartFile : files) {
					fileName = multipartFile.getOriginalFilename();
					
					List<TypeaheadUpdateReportList> updateList = excelFileManager.uploadTypeaheadUpdate(storeId, multipartFile.getInputStream(), fileName);
					
					if(updateList != null && updateList.size() > 0) {
						ExcelFileUploaded excel = new ExcelFileUploaded();
						excel.setFileName(fileName);
						excel.setStoreId(storeId);
						excel.setRuleTypeId(RuleEntity.TYPEAHEAD.getCode());
						excel.setCreatedBy(utilityService.getUsername());
						excel.setExcelFileUploadedId(DAOUtils
								.generateUniqueId64Char());	
						try {
							excelDao.addExcelFileUploaded(excel);
						} catch (DaoException e) {
							e.printStackTrace();
						}
						
						updateList.get(0).setExcelId(excel.getExcelFileUploadedId());
						response.success(updateList);
					} 
				}
			}
		} catch (InvalidFormatException e) {
			response.error(e.getMessage());
		} catch (Exception e) {			
			response.error(e.getMessage());
		}
				
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/exceldetail/{storeId}/{fileId}", method = RequestMethod.POST)
	public ServiceResponse<List<TypeaheadUpdateReportList>> getExcelDetails(@PathVariable String storeId, @PathVariable String fileId) {
		ServiceResponse<List<TypeaheadUpdateReportList>> response = new ServiceResponse<List<TypeaheadUpdateReportList>>();
		try {
			ExcelFileUploaded report = new ExcelFileUploaded();
			report.setExcelFileUploadedId(fileId);
			report.setStoreId(storeId);
			report.setRuleTypeId(RuleEntity.TYPEAHEAD.getCode());
			
			report = excelDao.getExcelFileUploaded(report);
			List<TypeaheadUpdateReportList> errorList = null;
			
			try{
				errorList = excelFileManager.getTypeaheadReportList(storeId, report.getFileName(), ExcelFileManager.FAILED_FOLDER);
			} catch(Exception e) {
				logger.info("Error list '"+ report.getFileName( )+"' for "+storeId+" typeahead does not exist.");
			}
			List<TypeaheadUpdateReportList> updateList = excelFileManager.getTypeaheadReportList(storeId, report.getFileName(), null);
			
			addErrorMessages(updateList, errorList);
			
			if(updateList != null && updateList.size() > 0) {
				response.success(updateList);
			} 

		} catch (Exception e) {			
			response.error(e.getMessage());
		}
		return response;
	}
	
	private void addErrorMessages(List<TypeaheadUpdateReportList> updateList, List<TypeaheadUpdateReportList> errorList) {
		Map<String, String> errorMessageMap = new HashMap<String, String>();
		if(errorList != null && errorList.get(0).getList() != null) {
			for(TypeaheadUpdateReport error : errorList.get(0).getList()) {
				errorMessageMap.put(error.getKeyword(), error.getErrorMessage());
			}
		}
		
		if(updateList != null && updateList.get(0).getList() != null) {
			for(TypeaheadUpdateReport reportRow : updateList.get(0).getList()) {
				reportRow.setErrorMessage(errorMessageMap.get(reportRow.getKeyword()));
			}
		}
	}
			
}
