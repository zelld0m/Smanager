package com.search.manager.workflow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.service.DemoteService;
import com.search.manager.service.ElevateService;
import com.search.manager.service.ExcludeService;
import com.search.manager.service.UtilityService;
import com.search.manager.workflow.dao.ExcelFileUploadedDAO;
import com.search.manager.workflow.service.ExcelFileUploadedService;
import com.search.reports.manager.model.ExcelFileReport;
import com.search.reports.manager.model.ExcelFileUploaded;

@Service(value = "excelFileUploadedService")
@RemoteProxy(name = "ExcelFileUploadedServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "excelFileUploadedService"))
public class ExcelFileUploadedServiceImpl implements  ExcelFileUploadedService{
	private static final int ROW_PER_PAGE = 10; 
	private static Map<String,List<ExcelFileUploaded> > mapExcelFileUploadeds = new HashMap<String,List<ExcelFileUploaded>>();  
	private static final Logger logger = LoggerFactory
			.getLogger(ExcelFileUploadedServiceImpl.class);
	@Autowired
	private ExcelFileUploadedDAO dao;
	@Autowired
	private UtilityService utilityService;	
	@Autowired
    private ElevateService elevateService;	
	@Autowired
    private DemoteService demoteService;
	@Autowired
    private ExcludeService excludeService;	
	
	@RemoteMethod
	public Integer addExcelFileUploadeds(String ruleType) throws DaoException {
		String createdBy = utilityService.getUsername();
		String storeId = utilityService.getStoreId();
		int ruleTypeId = RuleEntity.getId(ruleType);
		List<ExcelFileUploaded> excelFileUploadeds = getMapexcelfileuploadeds().get(createdBy);
		for (ExcelFileUploaded excelFileUploaded : excelFileUploadeds) {
			excelFileUploaded.setStoreId(storeId);	
			excelFileUploaded.setRuleTypeId(ruleTypeId);
			excelFileUploaded.setExcelFileUploadedId(DAOUtils
					.generateUniqueId64Char());
			excelFileUploaded.setCreatedBy(createdBy);
			dao.addExcelFileUploaded(excelFileUploaded);
			for (ExcelFileReport excelFileReport : excelFileUploaded
					.getExcelFileReports()) {
				excelFileReport.setExcelFileUploadedId(excelFileUploaded
						.getExcelFileUploadedId());
				excelFileReport.setCreatedBy(createdBy);
				excelFileReport.setRuleTypeId(ruleTypeId);
				excelFileReport.setStoreId(storeId);
				excelFileReport = dao.addExcelFileReport(excelFileReport);
			}
		}
		logger.info("addExcelFileUploadeds successfully done.");
		return 0;
	}


	public RecordSet<ExcelFileUploaded> getExcelFileUploadeds(String storeId,
			int ruleTypeId,int pageNumber) throws DaoException {
		SearchCriteria<ExcelFileUploaded> criteria = new SearchCriteria<ExcelFileUploaded>(new ExcelFileUploaded(),pageNumber,ROW_PER_PAGE);
		criteria.getModel().setStoreId(storeId);
		criteria.getModel().setRuleTypeId(ruleTypeId);		
		RecordSet<ExcelFileUploaded> recordSet = dao.getExcelFileUploadeds(criteria);
		return recordSet;
	}

	public ExcelFileUploaded getExcelFileUploaded(String excelFileUploadedId,String storeId,int ruleTypeId) throws DaoException {
		ExcelFileUploaded excelFileUploaded = new ExcelFileUploaded();
		excelFileUploaded.setStoreId(storeId);
		excelFileUploaded.setExcelFileUploadedId(excelFileUploadedId);
		excelFileUploaded.setRuleTypeId(ruleTypeId);
		excelFileUploaded = dao.getExcelFileUploaded(excelFileUploaded);
		SearchCriteria<ExcelFileReport> criteria = new SearchCriteria<ExcelFileReport>(
				new ExcelFileReport());
		criteria.getModel().setExcelFileUploadedId(excelFileUploadedId);
		criteria.getModel().setStoreId(storeId);
		criteria.getModel().setRuleTypeId(ruleTypeId);
		List<ExcelFileReport> excelFileReports=dao.getExcelFileReports(criteria).getList();
		excelFileUploaded.setExcelFileReports(excelFileReports);
		return excelFileUploaded;
	}	

	@RemoteMethod
	public int deleteExcelFileUploaded(String excelFileUploadedId,
			String storeId, String fileName) throws DaoException {
		ExcelFileUploaded excelFileUploaded = new ExcelFileUploaded();
		excelFileUploaded.setExcelFileUploadedId(excelFileUploadedId);
		excelFileUploaded.setFileName(fileName);
		excelFileUploaded.setStoreId(storeId);
		return dao.deleteExcelFileUploaded(excelFileUploaded);
	}

	@RemoteMethod
	public int updateExcelFileUploaded(String excelFileUploadedId,String storeId,String ruleType,boolean cleanFirst)
			throws DaoException {
			int ruleTypeId= RuleEntity.getId(ruleType);
			String currentKeyword = "";
			ExcelFileUploaded excelFileUploaded= getExcelFileUploaded(excelFileUploadedId,storeId,ruleTypeId);
			boolean isFirstKeyword = true;
			for (ExcelFileReport excelFileReport : excelFileUploaded
					.getExcelFileReports()) {				
				if (!currentKeyword.equalsIgnoreCase(excelFileReport.getKeyword())){
					currentKeyword=excelFileReport.getKeyword();
					isFirstKeyword=true;
				}  
				if (RuleEntity.get(ruleTypeId) == RuleEntity.ELEVATE){
					if (isFirstKeyword){						
						if (cleanFirst){
							elevateService.clearRule(excelFileReport.getKeyword());
						}
						isFirstKeyword = false;
					}
					int sequence =(int) Float.parseFloat(excelFileReport.getRank());
					String expireDate = excelFileReport.getExpiration().toString(utilityService.getStoreDateFormat());
					elevateService.addItemToRuleUsingPartNumber(excelFileReport.getKeyword(), sequence, expireDate, excelFileReport.getName(), excelFileReport.getSku().split(","));
				}
				if (RuleEntity.get(ruleTypeId) == RuleEntity.DEMOTE){
					if (isFirstKeyword){						
						if (cleanFirst){
							demoteService.clearRule(excelFileReport.getKeyword());
						}
						isFirstKeyword = false;
					}
					int sequence =(int) Float.parseFloat(excelFileReport.getRank());
					String expireDate = excelFileReport.getExpiration().toString(utilityService.getStoreDateFormat());
					demoteService.addItemToRuleUsingPartNumber(excelFileReport.getKeyword(), sequence, expireDate, excelFileReport.getName(), excelFileReport.getSku().split(","));
				}
				if (RuleEntity.get(ruleTypeId) == RuleEntity.EXCLUDE){
					if (isFirstKeyword){						
						if (cleanFirst){
							excludeService.clearRule(excelFileReport.getKeyword());
						}
						isFirstKeyword = false;
					}
					String expireDate = excelFileReport.getExpiration().toString(utilityService.getStoreDateFormat());
					excludeService.addItemToRuleUsingPartNumber(excelFileReport.getKeyword(), expireDate, excelFileReport.getName(), new String[]{excelFileReport.getSku()});
				}				
				
			}
			excelFileUploaded.setAddedOnRuleBy(utilityService.getUsername());
			excelFileUploaded.setAddedOnRuleDate(new DateTime());
		 dao.updateExcelFileUploaded(excelFileUploaded);
		 return 0;
	}

	public Map<String,List<ExcelFileUploaded>> getMapexcelfileuploadeds() {
		return mapExcelFileUploadeds;
	}

	public void setMapExcelFileUploadeds(
			Map<String, List<ExcelFileUploaded>> mapExcelFileUploadeds) {
		ExcelFileUploadedServiceImpl.mapExcelFileUploadeds = mapExcelFileUploadeds;
	}
}