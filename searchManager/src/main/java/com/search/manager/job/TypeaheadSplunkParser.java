package com.search.manager.job;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.service.DeploymentService;
import com.search.manager.service.TypeaheadValidationService;
import com.search.manager.workflow.service.WorkflowService;
import com.search.ws.ConfigManager;

@Component("typeaheadSplunkParse")
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
	private TypeaheadValidationService typeaheadValidationService;
	@Autowired
	private WorkflowService workflowService;

	private static String FOLDER_WEEKLY = "weekly";
	private static String FOLDER_DAILY = "daily";

	private static final String[] FOLDER_TYPES = {FOLDER_DAILY, FOLDER_WEEKLY};

	private static final String FOLDER_HOME = "/home/solr/utilities/typeahead";

	private final int WEEKLY_KEYWORD_COLUMN = 1;
	private final int WEEKLY_COUNT_COLUMN = 0;

	private final int DAILY_KEYWORD_COLUMN = 0;
	private final int DAILY_COUNT_COLUMN = 1;
	
	private static final String DAILY = "daily";
	private static final String WEEKLY = "weekly";
	
	private static String NEWLINE = System.getProperty("line.separator");

	public void scanSplunkFolder(String store) {
		logger.info("Scanning keywords for store {}.", store);
		for(String folderType : FOLDER_TYPES) {

			String folder = getStoreFolder(store, folderType);
			logger.info("scanning the folder {}", folder);
			File[] fileList = new File(folder).listFiles();
			logger.info("File list: {}", fileList);
			if(fileList == null)
				return;

			for(File file : fileList) {
				saveTypeheadRules(store, file, folderType);
			}
		}
	}

	private void saveTypeheadRules(String storeId, File file, String folderType) {
		List<String[]> csvData;
		Boolean hasError = false;
		try {
			csvData = getCsvData(file.getAbsolutePath());
			
			if(csvData != null) {
				List<Entry<String, String>> failedEntries = new ArrayList<Entry<String, String>>();
				for(String[] csvRow : csvData) {
					TypeaheadRule typeaheadRule = null;
					
					if(FOLDER_DAILY.equals(folderType) && csvRow == csvData.get(0))
						continue;
					
					Boolean isValid = typeaheadValidationService.validateKeyword(storeId, csvRow[DAILY_KEYWORD_COLUMN]);
					
					if(FOLDER_DAILY.equals(folderType) && thresholdSatisfied(storeId, csvRow, folderType, DAILY_COUNT_COLUMN) && isValid) {
						typeaheadRule = saveTypeaheadRule(DAILY, storeId, csvRow, folderType, DAILY_KEYWORD_COLUMN, DAILY_COUNT_COLUMN);
					} else if( FOLDER_DAILY.equals(folderType) && !isValid) {
						failedEntries.add(new AbstractMap.SimpleEntry<String, String>(csvRow[DAILY_KEYWORD_COLUMN], "Invalid Keyword"));
						continue;
					}
					
					isValid = typeaheadValidationService.validateKeyword(storeId, csvRow[WEEKLY_KEYWORD_COLUMN]);
					
					if(FOLDER_WEEKLY.equals(folderType) && thresholdSatisfied(storeId, csvRow, folderType, WEEKLY_COUNT_COLUMN)  && typeaheadValidationService.validateKeyword(storeId, csvRow[WEEKLY_KEYWORD_COLUMN])) {
						typeaheadRule = saveTypeaheadRule(WEEKLY, storeId, csvRow, folderType, WEEKLY_KEYWORD_COLUMN, WEEKLY_COUNT_COLUMN);
					} else if( FOLDER_WEEKLY.equals(folderType) && !isValid) {
						failedEntries.add(new AbstractMap.SimpleEntry<String, String>(csvRow[WEEKLY_KEYWORD_COLUMN], "Invalid Keyword"));
						continue;
					}

					if(typeaheadRule != null) {
						try {
							processTypeaheadRule(storeId, folderType, typeaheadRule);
						} catch (PublishLockException e) {
							logger.error("Failed to publish the rule '"+typeaheadRule.getRuleName()+"'", e);
							failedEntries.add(new AbstractMap.SimpleEntry<String, String>(typeaheadRule.getRuleName(), e.getMessage()));
						}
					}
				}
				
				if(failedEntries.size() > 1) {
					saveFailedEntries(failedEntries, file.getName(), storeId);
				}
			}

		} catch (IOException e) {
			hasError = true;
			logger.error("Error in TypeaheadSplunkParser.saveTypeaheadRules", e);
		} catch (CoreServiceException e) {
			hasError = true;
			logger.error("Error inserting TypeaheadSplunkParser.saveTypeaheadRules", e);
		}

		if(hasError) {
			File renameFile = new File(file + ".error");
			File currentFile = file;
			
			currentFile.renameTo(renameFile);
		} else {
			FileUtils.deleteQuietly(file);
		}
	}
	
	private void saveFailedEntries(List<Entry<String, String>> failedEntries, String fileName, String storeId) {
		File directory = new File(FOLDER_HOME + File.separator + storeId + File.separator + "Failed");
		if(!directory.exists()) {
			directory.mkdirs();
		}
		
		File newFile = new File(directory, fileName);
		
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(newFile.getAbsolutePath());
			
			for(Entry<String, String> entry : failedEntries) {
				fileWriter.append(entry.getKey())
				.append(",")
				.append(entry.getValue())
				.append(NEWLINE);
			}
		} catch (IOException e) {
			logger.error("Failed to write error list.", e);
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				logger.info("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}

	}

	private TypeaheadRule saveTypeaheadRule(String saveType, String storeId, String[] csvRow, String folderType, int keywordColumn, int countColumn) throws CoreServiceException {
		TypeaheadRule typeaheadRule = new TypeaheadRule();

		typeaheadRule.setRuleName(csvRow[keywordColumn]);
		typeaheadRule.setStoreId(storeId);
		typeaheadRule.setPriority(Integer.parseInt(csvRow[countColumn]));
		
		SearchResult<TypeaheadRule> result = typeaheadRuleService.search(typeaheadRule);
		if(result.getTotalCount() < 1) {
			typeaheadRule.setCreatedDate(new DateTime());
			typeaheadRule.setCreatedBy("system");

			return typeaheadRuleService.add(typeaheadRule);
		} else if(WEEKLY.equals(saveType)){
			TypeaheadRule existing = result.getList().get(0);
			
			RuleStatus ruleStatus = ruleStatusService.getRuleStatus(storeId, RuleEntity.TYPEAHEAD.getName(), existing.getRuleId());
			
			if(ruleStatus.isLocked()) {
				return null;
			}
			
			// Will not update if ruleName is the same.
			existing.setRuleName(null);
			existing.setPriority(Integer.parseInt(csvRow[countColumn]));
			existing.setLastModifiedBy("system");
			existing.setLastModifiedDate(new DateTime());
			return typeaheadRuleService.update(existing);
		}
			return null;
	}

	private void processTypeaheadRule(String storeId, String folderType, TypeaheadRule typeaheadRule) throws CoreServiceException, PublishLockException {
		String defaultStatus = configManager.getProperty("typeahead", storeId, FOLDER_DAILY.equals(folderType) ? "typeahead.dailyDefaultStatus" : "typeahead.weeklyDefaultStatus" );

		if(defaultStatus == null)
			defaultStatus = "For Review";

		String ruleType = RuleEntity.TYPEAHEAD.getName();
		RuleStatus ruleStatus = ruleStatusService.getRuleStatus(storeId, ruleType, typeaheadRule.getRuleId());
		
		if(ruleStatus.isLocked()) {
			throw new PublishLockException("The rule is locked.", typeaheadRule.getCreatedBy(), storeId);
		}
		
		ImportType importType = ImportType.getByDisplayText(defaultStatus);

		String[] ruleRefIdList = {typeaheadRule.getRuleId()};
		String[] ruleStatusIdList = {ruleStatus.getRuleStatusId()};

		workflowService.processRule(storeId, ruleType, typeaheadRule.getRuleId(), typeaheadRule.getRuleName(), importType, ruleRefIdList, ruleStatusIdList);
		
		
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
		} catch (Exception e) {
			logger.error("Error in TypeaheadSplunkParser.getCsvData", e);
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
