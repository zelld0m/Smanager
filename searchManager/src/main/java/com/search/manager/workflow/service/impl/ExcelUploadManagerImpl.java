package com.search.manager.workflow.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.service.TypeaheadValidationService;
import com.search.manager.workflow.service.WorkflowService;
import com.search.reports.enums.ExcelUploadStatus;
import com.search.reports.manager.ExcelFileManager;
import com.search.reports.manager.model.TypeaheadUpdateReport;
import com.search.reports.manager.model.TypeaheadUpdateReportList;
import com.search.ws.ConfigManager;

@Component("excelUploadManager")
public class ExcelUploadManagerImpl {

	@Autowired
	private ExcelFileManager excelFileManager;
	@Autowired
	private ConfigManager configManager;
	@Autowired
	private RuleStatusService ruleStatusService;
	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;
	@Autowired
	private TypeaheadValidationService typeaheadValidationService;
	@Autowired
	private WorkflowService workflowService;

	private RuleEntity[] enabledRules = { RuleEntity.TYPEAHEAD } ;

	public void scanFiles() {
		Set<String> storeList = configManager.getAllStoresDisplayName().keySet();
		initializeFolders(storeList);
		
		for(String store : storeList) {
			for(RuleEntity ruleEntity : enabledRules) {
				String basePath = ExcelFileManager.BASE_PATH + store + File.separator + ruleEntity.getXmlName();
				String queueFolder = basePath + File.separator + ExcelFileManager.QUEUE_FOLDER;

				File queueDirectory = new File(queueFolder);

				if(queueDirectory.exists()) {
					List<File> files = Arrays.asList(queueDirectory.listFiles());

					Collections.sort(files, new Comparator<File>() {
						@Override
						public int compare(File file1, File file2) {
							if(file1.lastModified() > file2.lastModified())
								return -1;
							else if(file1.lastModified() < file2.lastModified())
								return 1;
							return 0;
						}
					});
					
					for(File file : files) {
						try {
							processFile(store, ruleEntity, file, basePath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private void initializeFolders(Set<String> storeList) {
		for(String store : storeList) {
			for(RuleEntity ruleEntity : enabledRules) {
				String basePath = ExcelFileManager.BASE_PATH + store + File.separator + ruleEntity.getXmlName();	
				File queueDirectory = new File(basePath + File.separator + ExcelFileManager.QUEUE_FOLDER);
				File failedDirectory = new File(basePath + File.separator + ExcelFileManager.FAILED_FOLDER);
				
				queueDirectory.mkdirs();
				failedDirectory.mkdirs();
			}
		}
	}

	private void processFile(String storeId, RuleEntity ruleEntity, File file, String basePath) throws Exception {
		switch(ruleEntity) {
		case TYPEAHEAD: 
			processTypeahead(storeId, basePath, file);			
			break;
		default: break;
		}
	}

	private void processTypeahead(String storeId, String basePath, File file) throws Exception{

		File baseFile = new File(basePath + File.separator + file.getName());
		
		excelFileManager.updateExcelStatus(baseFile, ExcelUploadStatus.IN_PROCESS);
		
		List<TypeaheadUpdateReportList> reportList = excelFileManager.getTypeaheadReportList(storeId, file.getName(), null);
		List<TypeaheadUpdateReport> failedList = new ArrayList<TypeaheadUpdateReport>();
		List<TypeaheadUpdateReport> successList = new ArrayList<TypeaheadUpdateReport>();
		
		// Process the excel entries
		for(TypeaheadUpdateReportList report : reportList) {
			for(TypeaheadUpdateReport row : report.getList()) {

				try{
					
					if(!typeaheadValidationService.validateKeyword(storeId, row.getKeyword())) {
						row.setErrorMessage("Invalid keyword.");
						failedList.add(row);
						continue;
					}
					
					TypeaheadRule typeaheadRule = new TypeaheadRule();
					typeaheadRule.setRuleName(row.getKeyword());
					typeaheadRule.setStoreId(storeId);
					
					SearchResult<TypeaheadRule> result = typeaheadRuleService.search(typeaheadRule);

					if(result.getTotalCount() > 0) {
						typeaheadRule = result.getList().get(0);
					} else {
						typeaheadRule = typeaheadRuleService.add(typeaheadRule);
					}
					
					RuleStatus ruleStatus = ruleStatusService.getRuleStatus(storeId, RuleEntity.getValue(RuleEntity.TYPEAHEAD.getCode()), typeaheadRule.getRuleId());
					
					if(ruleStatus.isLocked()) {
						row.setErrorMessage("The rule is locked.");
						failedList.add(row);
						continue;
					}
					
					typeaheadRule.setPriority(row.getPriority());
					typeaheadRuleService.updatePrioritySection(typeaheadRule, typeaheadRule.getCreatedBy(), new DateTime(), false);
					
					typeaheadRule = new TypeaheadRule();
					typeaheadRule.setRuleName(row.getKeyword());
					typeaheadRule.setStoreId(storeId);
					result = typeaheadRuleService.search(typeaheadRule);
					typeaheadRule = result.getList().get(0);
					
					// Will Not update if the name is the same. Null will retain the previous value.
					typeaheadRule.setRuleName(null);
					
					typeaheadRule.setDisabled(!row.getEnabled());
					typeaheadRule.setPriority(typeaheadRule.getSplunkPriority());
					typeaheadRule = typeaheadRuleService.update(typeaheadRule);

					
					String[] ruleRefIdList = {typeaheadRule.getRuleId()};
					String[] ruleStatusIdList = {ruleStatus.getRuleStatusId()};

					workflowService.processRule(storeId, RuleEntity.getValue(RuleEntity.TYPEAHEAD.getCode()), typeaheadRule.getRuleId(), typeaheadRule.getRuleName(), ImportType.AUTO_PUBLISH, ruleRefIdList, ruleStatusIdList);
					
					successList.add(row);
				} catch( PublishLockException e) {
					e.printStackTrace();
					row.setErrorMessage("Publishing for the rule type 'typeahead' is currently locked.");
					failedList.add(row);
					
				} catch( Exception e) {
					e.printStackTrace();
					failedList.add(row);
				}

			}
		}
		
		// Create a new excel file containing the failed entries.
		if(failedList.size() > 0) {
			XSSFWorkbook workbook = excelFileManager.getTypeaheadWorkbook(failedList);
			File newFile = new File(basePath + File.separator + ExcelFileManager.FAILED_FOLDER + File.separator + file.getName());
			
			if(newFile.exists()) {
				newFile.delete();
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
		}
		
		// Remove from queue folder.
		file.delete();
		excelFileManager.updateExcelStatus(baseFile, ExcelUploadStatus.PROCESSED);
	}
}
