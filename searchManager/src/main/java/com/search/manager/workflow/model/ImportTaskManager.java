package com.search.manager.workflow.model;

import java.text.MessageFormat;

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
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.service.DeploymentService;
import com.search.manager.service.RuleTransferService;
import com.search.manager.workflow.service.WorkflowService;
import com.search.ws.ConfigManager;

@Component(value="importTaskManager")
public class ImportTaskManager {

	private static final Logger logger =
			LoggerFactory.getLogger(ImportTaskManager.class);

	@Autowired
	private ConfigManager configManager;
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


	public void importRules() throws CoreServiceException {

		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.QUEUED, null, 0, null, null, null);
		ImportRuleTask importRuleTask = new ImportRuleTask(null, null, null, null, null, null, null, null, null, taskExecutionResult);
		SearchResult<ImportRuleTask> importRecords = importRuleTaskService.search(importRuleTask, 0, 0);

		logger.info("queued records: {}", importRecords.getTotalSize());

		for(ImportRuleTask importRuleQueueItem : importRecords.getList()) {
			importQueueItems(importRuleQueueItem, importRuleQueueItem.getCreatedBy());
		}

		importRuleTask.getTaskExecutionResult().setTaskStatus(TaskStatus.FAILED);
		importRecords = importRuleTaskService.search(importRuleTask, 0, 0);

		logger.info("failed records: {}", importRecords.getTotalSize());

		for(ImportRuleTask failedImportRuleQueueItem : importRecords.getList()) {
			importQueueItems(failedImportRuleQueueItem, failedImportRuleQueueItem.getCreatedBy());
		}

	}

	private void importQueueItems(ImportRuleTask importRuleQueueItem, String userName) throws CoreServiceException {
		try {

			String targetStoreId = importRuleQueueItem.getTargetStoreId();
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
			String comment = MessageFormat.format("Imported from {0}.", importRuleQueueItem.getSourceStoreId());

			String[] importRuleRefIdList = {importRuleRefId};
			String[] importTypeList = {importRuleQueueItem.getImportType().getDisplayText()};
			String[] importAsRefIdList = {importRuleQueueItem.getTargetRuleId()};
			String[] ruleNameList = {ruleName};

			RuleStatus ruleStatus = ruleStatusService.getRuleStatus(targetStoreId, importRuleQueueItem.getRuleEntity().getName(), importRuleQueueItem.getSourceRuleId());

			TaskExecutionResult taskExecutionResult = importRuleQueueItem.getTaskExecutionResult();

			taskExecutionResult.setRunAttempt(taskExecutionResult.getRunAttempt() + 1);

			if(ruleStatus != null && Boolean.TRUE.equals(ruleStatus.isLocked())) {
				updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), "The rule is locked.");
				return;
			}

			if(taskExecutionResult.getStateCompleted() == null)
				ruleTransferService.processImportRejectRules(targetStoreId, storeName, importRuleQueueItem.getCreatedBy(), RuleSource.AUTO_IMPORT, ruleEntity.getName(), importRuleRefIdList, comment, importTypeList, importAsRefIdList, ruleNameList, null, null);

			taskExecutionResult.setStateCompleted(ImportType.FOR_REVIEW);

			if(StringUtils.isEmpty(importTypeSetting)) {
				importTypeSetting = "For Approval";
			}

			switch(ImportType.getByDisplayText(importTypeSetting)) {
			case FOR_APPROVAL: 
				if(ImportType.FOR_REVIEW.equals(taskExecutionResult.getStateCompleted())) {
					workflowService.processRuleStatus(targetStoreId, userName, RuleSource.AUTO_IMPORT, ruleEntity.getName(), importRuleRefId, ruleName, false); 
					taskExecutionResult.setStateCompleted(ImportType.FOR_APPROVAL);
				}
				break;
			case AUTO_PUBLISH: 

				RuleStatus ruleStatusInfo = ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.toString(), importRuleQueueItem.getTargetRuleId());
				String[] ruleStatusIdList = {ruleStatusInfo.getRuleStatusId()};

				if(ImportType.FOR_REVIEW.equals(taskExecutionResult.getStateCompleted())) {
					workflowService.processRuleStatus(targetStoreId, userName, RuleSource.AUTO_IMPORT, ruleEntity.getName(), importRuleRefId, ruleName, false);
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