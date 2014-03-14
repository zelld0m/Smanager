package com.search.manager.job;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import au.com.bytecode.opencsv.CSVReader;

import com.search.manager.core.enums.RuleSource;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.service.DeploymentService;
import com.search.manager.utility.FileUtil;
import com.search.manager.workflow.service.WorkflowService;
import com.search.ws.ConfigManager;

public class TypeaheadSplunkParser {

	private static final Logger logger =
			LoggerFactory.getLogger(TypeaheadSplunkParser.class);

	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;
	@Autowired
	private ConfigManager configManager;
	@Autowired
	private DeploymentService deploymentService;
	@Autowired
	private RuleStatusService ruleStatusService;
	@Autowired
	private WorkflowService workflowService;

	private static String FOLDER_WEEKLY = "weekly";
	private static String FOLDER_DAILY = "daily";

	private static final String[] FOLDER_TYPES = {FOLDER_WEEKLY, FOLDER_DAILY};
	
	private static String FOLDER_HOME = "";

	private int WEEKLY_KEYWORD_COLUMN = 1;
	private int WEEKLY_COUNT_COLUMN = 2;

	private int DAILY_KEYWORD_COLUMN = 1;
	private int DAILY_COUNT_COLUMN = 2;

	public void scanSplunkFolder(String store) {
		for(String folderType : FOLDER_TYPES) {

			String folder = getStoreFolder(store, folderType);
			List<String> fileList = FileUtil.getFileList(folder);

			if(fileList == null)
				return;

			for(String file : fileList) {
				saveTypeheadRules(store, folder + File.separator + file, folderType);
			}
		}
	}

	private void saveTypeheadRules(String storeId, String file, String folderType) {
		List<String[]> csvData;
		try {
			csvData = getCsvData(file);
			if(csvData != null) {
				for(String[] csvRow : csvData) {
					TypeaheadRule typeaheadRule = null;

					if(FOLDER_DAILY.equals(folderType) && thresholdSatisfied(storeId, csvRow, folderType, DAILY_COUNT_COLUMN)) {
						typeaheadRule = saveTypeaheadRule(storeId, csvRow, folderType, DAILY_KEYWORD_COLUMN);
					} else if(FOLDER_WEEKLY.equals(folderType) && thresholdSatisfied(storeId, csvRow, folderType, WEEKLY_COUNT_COLUMN)) {
						typeaheadRule = saveTypeaheadRule(storeId, csvRow, folderType, WEEKLY_KEYWORD_COLUMN);
					}

					if(typeaheadRule != null) {
						processTypeaheadRule(storeId, folderType, typeaheadRule);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Error in TypeaheadSplunkParser.saveTypeaheadRules", e);
		} catch (CoreServiceException e) {
			logger.error("Error inserting TypeaheadSplunkParser.saveTypeaheadRules", e);
		}
	}

	private TypeaheadRule saveTypeaheadRule(String storeId, String[] csvRow, String folderType, int keywordColumn) throws CoreServiceException {
		TypeaheadRule typeaheadRule = new TypeaheadRule();

		typeaheadRule.setRuleName(csvRow[keywordColumn]);
		typeaheadRule.setCreatedDate(new DateTime());
		typeaheadRule.setCreatedBy("system");

		return typeaheadRuleService.add(typeaheadRule);
	}

	private void processTypeaheadRule(String storeId, String folderType, TypeaheadRule typeaheadRule) throws CoreServiceException {
		String defaultStatus = configManager.getProperty("typeahead", storeId, FOLDER_DAILY.equals(folderType) ? "typeahead.dailyDefaultStatus" : "typeahead.weeklyDefaultStatus" );
		
		if(defaultStatus == null)
			defaultStatus = "For Review";
		
		String ruleType = RuleEntity.TYPEAHEAD.getName();
		RuleStatus ruleStatus = ruleStatusService.getRuleStatus(storeId, ruleType, typeaheadRule.getRuleId());
		ImportType importType = ImportType.getByDisplayText(defaultStatus);

		String[] ruleRefIdList = {typeaheadRule.getRuleId()};
		String[] ruleStatusIdList = {ruleStatus.getRuleStatusId()};

		switch(importType) {
		case FOR_APPROVAL: 
			workflowService.processRuleStatus(storeId, "system", RuleSource.AUTO_IMPORT, ruleType, typeaheadRule.getRuleId(), typeaheadRule.getRuleName(), Boolean.FALSE);
			break;
		case AUTO_PUBLISH: 
			workflowService.processRuleStatus(storeId, "system", RuleSource.AUTO_IMPORT, ruleType, typeaheadRule.getRuleId(), typeaheadRule.getRuleName(), Boolean.FALSE);
			deploymentService.approveRule(storeId, RuleEntity.TYPEAHEAD.getName(), ruleRefIdList, "", ruleStatusIdList);
			try {
				workflowService.publishRule(storeId, configManager.getStoreName(storeId), "system", RuleSource.AUTO_IMPORT, ruleType, ruleRefIdList, "", ruleStatusIdList);
			} catch (PublishLockException e) {
				logger.error("Error publishing TypeaheadSplunkParser.processTypeaheadRule", e);
			}
			break; 
		default: 
			break;
		}
	}

	private boolean thresholdSatisfied(String storeId, String[] csvRow, String folderType, int countColumn) {
		int minCount = Integer.parseInt(FOLDER_DAILY.equals(folderType) ? configManager.getProperty("typeahead", storeId, "typeahead.dailySearchCountThreshold"): configManager.getProperty("typeahead", storeId, "typeahead.weeklySearchCountThreshold"));

		int actualCount = Integer.parseInt(csvRow[countColumn]);

		return actualCount >= minCount;
	}

	private List<String[]> getCsvData(String filePath) throws IOException {
		CSVReader reader = null;
		List<String[]> data = null;

		try {
			reader = new CSVReader(new FileReader(filePath), ',', '\"', '\0', 0, true);
			data = reader.readAll();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return data;
	}

	private String getStoreFolder(String store, String folderType) {

		return FOLDER_HOME + File.separator + store + File.separator + folderType;
	}
}
