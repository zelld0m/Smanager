package com.search.manager.workflow.model;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.enums.RuleSource;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImportRuleTask;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TaskExecutionResult;
import com.search.manager.core.model.TaskStatus;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.service.DeploymentService;
import com.search.manager.service.RuleTransferService;
import com.search.manager.workflow.service.AutoImportManager;
import com.search.manager.workflow.service.WorkflowService;
import com.search.ws.ConfigManager;

@Component(value="importTaskManager")
public class ImportTaskManager {

	private static final Logger logger =
			LoggerFactory.getLogger(ImportTaskManager.class);

	@Autowired
	private AutoImportManager autoImportManager;
	@Autowired
	private ConfigManager configManager;
	@Autowired
	private DaoService daoService;
	@Autowired
	private DeploymentService deploymentService;
	@Autowired
	private ImportRuleTaskService importRuleTaskService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	private RuleTransferService ruleTransferService;
	@Autowired
	private WorkflowService workflowService;
	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;
	
	private RuleEntity[] specialRules = {RuleEntity.TYPEAHEAD, RuleEntity.QUERY_CLEANING, RuleEntity.RANKING_RULE, RuleEntity.FACET_SORT};
	private List<RuleEntity> specialRuleList = Arrays.asList(specialRules);
	
	public void importRules() throws CoreServiceException {

		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.FAILED, null, 0, null, null, null);
		ImportRuleTask importRuleTask = new ImportRuleTask(null, null, null, null, null, null, null, null, null, taskExecutionResult);
		SearchResult<ImportRuleTask> importRecords = importRuleTaskService.search(importRuleTask, 0, 0);

		logger.info("failed records: {}", importRecords.getTotalSize());

		for(ImportRuleTask importRuleQueueItem : importRecords.getList()) {
			importQueueItems(importRuleQueueItem, importRuleQueueItem.getCreatedBy());
		}

		importRuleTask.getTaskExecutionResult().setTaskStatus(TaskStatus.QUEUED);
		importRecords = importRuleTaskService.search(importRuleTask, 0, 0);

		logger.info("queued records: {}", importRecords.getTotalSize());

		for(ImportRuleTask failedImportRuleQueueItem : importRecords.getList()) {
			importQueueItems(failedImportRuleQueueItem, failedImportRuleQueueItem.getCreatedBy());
		}

	}

	private void importQueueItems(ImportRuleTask importRuleQueueItem, String userName) throws CoreServiceException {
		try {

			String targetStoreId = importRuleQueueItem.getTargetStoreId();
			String sourceStoreId = importRuleQueueItem.getSourceStoreId();

			int maxAttempts = Integer.parseInt(StringUtils.defaultIfBlank(configManager.getProperty("workflow", targetStoreId, "maxRunAttempts"), "5"));

			if(importRuleQueueItem.getTaskExecutionResult().getRunAttempt() >= maxAttempts) {
				logger.info("Max run attempts has been reached, ignoring rule {}", importRuleQueueItem.getTargetRuleName());
				return;
			}

			DateTime startDate = new DateTime();

			updateTaskExecution(importRuleQueueItem, TaskStatus.IN_PROCESS, startDate, startDate, null);
			RuleEntity ruleEntity = importRuleQueueItem.getRuleEntity();
			String ruleName = importRuleQueueItem.getTargetRuleName();

			String importRuleRefId = importRuleQueueItem.getSourceRuleId();
			String storeName = configManager.getStoreName(targetStoreId);
			String importTypeSetting = importRuleQueueItem.getImportType().getDisplayText();
			String comment = MessageFormat.format("Imported from {0}.", sourceStoreId);

			String[] importRuleRefIdList = {importRuleRefId};
			String[] importTypeList = {importRuleQueueItem.getImportType().getDisplayText()};
			String[] importAsRefIdList = {importRuleQueueItem.getTargetRuleId()};
			String[] ruleNameList = {ruleName};
			String importAsId = importRuleQueueItem.getTargetRuleId();

			RuleStatus ruleStatus = null;
			
			if(specialRuleList.contains(ruleEntity)) {
				ruleStatus = autoImportManager.getTargetRuleStatus(ruleEntity, sourceStoreId, importRuleRefId, ruleName, targetStoreId);

			} else {
				ruleStatus = ruleStatusService.getRuleStatus(targetStoreId, importRuleQueueItem.getRuleEntity().getName(), importRuleQueueItem.getSourceRuleId());
			}

			TaskExecutionResult taskExecutionResult = importRuleQueueItem.getTaskExecutionResult();

			taskExecutionResult.setRunAttempt(taskExecutionResult.getRunAttempt() + 1);

			if(ruleStatus != null && Boolean.TRUE.equals(ruleStatus.isLocked())) {
				updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), "The rule is locked.");
				return;
			} else if(ruleStatus != null && (RuleEntity.QUERY_CLEANING.equals(ruleEntity) || RuleEntity.RANKING_RULE.equals(ruleEntity))) {
				ExportRuleMap existingTargetMap = autoImportManager.getExportRuleMap(ruleEntity, null, null, targetStoreId, ruleStatus.getRuleId());

				importAsRefIdList[0] = ruleStatus.getRuleId();
				
				if(existingTargetMap != null && !importRuleRefId.equals(existingTargetMap.getRuleIdOrigin())) {
					updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), "This rule is mapped to another source rule.");
					return;
				} else if(existingTargetMap != null){
					importAsRefIdList[0] = existingTargetMap.getRuleIdTarget();
				}
				
			}

			if(taskExecutionResult.getStateCompleted() == null)
				ruleTransferService.processImportRejectRules(targetStoreId, storeName, importRuleQueueItem.getCreatedBy(), RuleSource.AUTO_IMPORT, ruleEntity.getName(), importRuleRefIdList, comment, importTypeList, importAsRefIdList, ruleNameList, null, null);

			taskExecutionResult.setStateCompleted(ImportType.FOR_REVIEW);

			if(StringUtils.isEmpty(importTypeSetting)) {
				importTypeSetting = "For Approval";
			}

			if(specialRuleList.contains(ruleEntity)) {
				importAsId = autoImportManager.getImportAsId(ruleEntity, autoImportManager.getTargetRuleStatus(ruleEntity, sourceStoreId, importRuleRefId, ruleName, targetStoreId), sourceStoreId, importRuleRefId, targetStoreId, importAsRefIdList[0]);
			}

			switch(ImportType.getByDisplayText(importTypeSetting)) {
			case FOR_APPROVAL: 
				if(ImportType.FOR_REVIEW.equals(taskExecutionResult.getStateCompleted())) {
					workflowService.processRuleStatus(targetStoreId, userName, RuleSource.AUTO_IMPORT, ruleEntity.getName(), importAsId, ruleName, false); 
					taskExecutionResult.setStateCompleted(ImportType.FOR_APPROVAL);
				}
				break;
			case AUTO_PUBLISH: 

				RuleStatus ruleStatusInfo = ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.toString(), importAsId);
				String[] ruleStatusIdList = {ruleStatusInfo.getRuleStatusId()};

				if(ImportType.FOR_REVIEW.equals(taskExecutionResult.getStateCompleted())) {
					workflowService.processRuleStatus(targetStoreId, userName, RuleSource.AUTO_IMPORT, ruleEntity.getName(), importAsId, ruleName, false);
					taskExecutionResult.setStateCompleted(ImportType.FOR_APPROVAL);
				}

				if(ImportType.FOR_APPROVAL.equals(taskExecutionResult.getStateCompleted())) {
					deploymentService.approveRule(targetStoreId, ruleEntity.getNthValue(0), importAsRefIdList, comment, ruleStatusIdList); 
					workflowService.publishRule(targetStoreId, storeName, userName, RuleSource.AUTO_IMPORT, ruleEntity.name(), importAsRefIdList, comment, ruleStatusIdList);
					taskExecutionResult.setStateCompleted(ImportType.AUTO_PUBLISH);
				}
				break;

			default: 
			}

			updateTaskExecution(importRuleQueueItem, TaskStatus.COMPLETED, startDate, new DateTime(), "");


		} catch (Exception e) {
			logger.error("failed executing ImportTaskManager.importQueueItems.", e);
			updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), e.getMessage());
		} 
	}

	private void updateTaskExecution(ImportRuleTask importRuleTask, TaskStatus taskStatus, DateTime startDate, DateTime endDate, String errorMessage) throws CoreServiceException {

		importRuleTask.getTaskExecutionResult().setTaskErrorMessage(errorMessage);
		importRuleTask.getTaskExecutionResult().setTaskStatus(taskStatus);
		importRuleTask.setLastModifiedBy(importRuleTask.getCreatedBy());
		if(startDate != null) {
			importRuleTask.setLastModifiedDate(startDate);
			importRuleTask.getTaskExecutionResult().setTaskStartDateTime(startDate);
		}

		if(endDate != null) {
			importRuleTask.setLastModifiedDate(endDate);
			importRuleTask.getTaskExecutionResult().setTaskEndDateTime(endDate);
		}

		importRuleTaskService.update(importRuleTask);
	}
}