package com.search.manager.workflow.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.core.enums.RuleSource;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.RuleStatusDAO.SortOrder;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.service.DeploymentService;
import com.search.manager.service.RuleTransferService;
import com.search.manager.workflow.dao.ImportRuleTaskDAO;
import com.search.manager.workflow.service.RuleStatusService;
import com.search.manager.workflow.service.WorkflowService;
import com.search.ws.ConfigManager;

import com.search.manager.enums.ImportType;

@Repository(value="importTaskManager")
public class ImportTaskManager {

	private static final Logger logger =
			LoggerFactory.getLogger(ImportTaskManager.class);

	@Autowired
	private ConfigManager configManager;
	@Autowired
	private DeploymentService deploymentService;
	@Autowired
	private ImportRuleTaskDAO importRuleTaskDAO;
	@Autowired
	private RuleStatusService ruleStatusService;
	@Autowired
	private RuleTransferService ruleTransferService;
	@Autowired
	private WorkflowService workflowService;


	public void importRules() throws DaoException {

		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.QUEUED, null, 0, null, null, null);
		ImportRuleTask importRuleTask = new ImportRuleTask(null, null, null, null, null, null, null, null, null, taskExecutionResult);
		RecordSet<ImportRuleTask> importRecords = importRuleTaskDAO.getImportRuleTask(new SearchCriteria<ImportRuleTask>(importRuleTask, null, null, 0, 0), SortOrder.DESCRIPTION_ASCENDING);

		logger.info("queued records: {}", importRecords.getTotalSize());

		for(ImportRuleTask importRuleQueueItem : importRecords.getList()) {
			importQueueItems(importRuleQueueItem, importRuleQueueItem.getCreatedBy());
		}

		importRuleTask.getTaskExecutionResult().setTaskStatus(TaskStatus.FAILED);
		importRecords = importRuleTaskDAO.getImportRuleTask(new SearchCriteria<ImportRuleTask>(importRuleTask, null, null, 0, 0), SortOrder.DESCRIPTION_ASCENDING);

		logger.info("failed records: {}", importRecords.getTotalSize());

		for(ImportRuleTask failedImportRuleQueueItem : importRecords.getList()) {
			importQueueItems(failedImportRuleQueueItem, failedImportRuleQueueItem.getCreatedBy());
		}

	}

	private void importQueueItems(ImportRuleTask importRuleQueueItem, String userName) throws DaoException {
		try {

			String targetStoreId = importRuleQueueItem.getTargetStoreId();
			int maxAttempts = Integer.parseInt(StringUtils.defaultIfBlank(configManager.getProperty("workflow", targetStoreId, "maxRunAttempts"), "5"));

			if(importRuleQueueItem.getTaskExecutionResult().getRunAttempt() < maxAttempts) {

				RuleEntity ruleEntity = importRuleQueueItem.getRuleEntity();
				String ruleName = importRuleQueueItem.getTargetRuleName();
				String comment = importRuleQueueItem.getComment();
				String importRuleRefId = importRuleQueueItem.getSourceRuleId();
				String storeName = configManager.getStoreName(importRuleQueueItem.getTargetStoreId());
				String importTypeSetting = configManager.getProperty("workflow", targetStoreId, "status."+ruleEntity.getXmlName());

				String[] importRuleRefIdList = {importRuleRefId};
				String[] importTypeList = {importRuleQueueItem.getImportType().getDisplayText()};
				String[] importAsRefIdList = {importRuleQueueItem.getTargetRuleId()};
				String[] ruleNameList = {ruleName};

				RuleStatus ruleStatus = ruleStatusService.getRuleStatus(targetStoreId, importRuleQueueItem.getRuleEntity().getName(), importRuleQueueItem.getSourceRuleId());

				TaskExecutionResult taskExecutionResult = importRuleQueueItem.getTaskExecutionResult();

				taskExecutionResult.setRunAttempt(taskExecutionResult.getRunAttempt() + 1);

				if(!ruleStatus.isLocked()) {

					
					if(taskExecutionResult.getStateCompleted() == null)
						ruleTransferService.processImportRejectRules(targetStoreId, storeName, importRuleQueueItem.getCreatedBy(), RuleSource.AUTO_IMPORT, ruleEntity.getName(), importRuleRefIdList, comment, importTypeList, importAsRefIdList, ruleNameList, null, null);

					taskExecutionResult.setStateCompleted(ImportType.FOR_REVIEW);

					switch(ImportType.getByDisplayText(importTypeSetting)) {
					case FOR_APPROVAL: 
						if(ImportType.FOR_REVIEW.equals(taskExecutionResult.getStateCompleted())) {
							workflowService.processRuleStatus(targetStoreId, userName, RuleSource.AUTO_IMPORT, ruleEntity.getName(), importRuleRefId, ruleName, false); 
							taskExecutionResult.setStateCompleted(ImportType.FOR_APPROVAL);
						}
						break;
					case AUTO_PUBLISH: 

						RuleStatus ruleStatusInfo = deploymentService.getRuleStatus(targetStoreId, ruleEntity.toString(), importRuleQueueItem.getTargetRuleId());
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

					updateTaskExecution(importRuleQueueItem, TaskStatus.COMPLETED, null, new DateTime(), "");
				} else {
					//TODO: update import task to completed and set error message as rule is locked.
					updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), "The rule is locked.");
				}
			} else {
				logger.info("Max run attempts has been reached, ignoring rule {}", importRuleQueueItem.getTargetRuleName());
			}

		} catch (Exception e) {
			logger.error("failed executing ImportTaskManager.importQueueItems.", e);
			updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), e.getMessage());
		} 
	}

	private void updateTaskExecution(ImportRuleTask importRuleTask, TaskStatus taskStatus, DateTime startDate, DateTime endDate, String errorMessage) throws DaoException {

		importRuleTask.getTaskExecutionResult().setTaskErrorMessage(errorMessage);
		importRuleTask.getTaskExecutionResult().setTaskStatus(taskStatus);

		if(startDate != null) {
			importRuleTask.setLastModifiedDate(startDate);
			importRuleTask.getTaskExecutionResult().setTaskStartDateTime(startDate);

		} else if(endDate != null) {
			importRuleTask.setLastModifiedDate(endDate);
			importRuleTask.getTaskExecutionResult().setTaskEndDateTime(endDate);
		}

		importRuleTaskDAO.updateImportRuleTask(importRuleTask);
	}
}