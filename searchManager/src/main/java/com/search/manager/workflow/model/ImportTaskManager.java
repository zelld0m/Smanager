package com.search.manager.workflow.model;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.QUEUED, null, null, null);
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
			RuleEntity ruleEntity = importRuleQueueItem.getRuleEntity();
			String ruleName = importRuleQueueItem.getTargetRuleName();
			String comment = importRuleQueueItem.getComment();
			String importRuleRefId = importRuleQueueItem.getSourceRuleId();
			String storeId = importRuleQueueItem.getTargetStoreId();
			String storeName = configManager.getStoreName(importRuleQueueItem.getTargetStoreId());
			String importTypeSetting = configManager.getProperty("workflow", storeId, "status."+ruleEntity.getXmlName());
						
			String[] importRuleRefIdList = {importRuleRefId};
			String[] importTypeList = {importRuleQueueItem.getImportType().getDisplayText()};
			String[] importAsRefIdList = {importRuleQueueItem.getTargetRuleId()};
			String[] ruleNameList = {ruleName};
			
			RuleStatus ruleStatus = ruleStatusService.getRuleStatus(storeId, importRuleQueueItem.getRuleEntity().getName(), importRuleQueueItem.getSourceRuleId());

			if(!ruleStatus.isLocked()) {
				updateTaskExecution(importRuleQueueItem, TaskStatus.IN_PROCESS, new DateTime(), null, "");
				
				ruleTransferService.importRejectRules(storeId, storeName, importRuleQueueItem.getCreatedBy(), ruleEntity.getName(), importRuleRefIdList, comment, importTypeList, importAsRefIdList, ruleNameList, null, null);
				
				switch(ImportType.getByDisplayText(importTypeSetting)) {
				case FOR_APPROVAL: 
					workflowService.processRuleStatus(storeId, userName, ruleEntity.getName(), importRuleRefId, ruleName, false); break;
				case AUTO_PUBLISH: 
					workflowService.processRuleStatus(storeId, userName, ruleEntity.getName(), importRuleRefId, ruleName, false);
					RuleStatus ruleStatusInfo = deploymentService.getRuleStatus(storeId, ruleEntity.toString(), importRuleRefId);
					String[] ruleStatusIdList = {ruleStatusInfo.getRuleStatusId()};
					deploymentService.approveRule(storeId, ruleEntity.getNthValue(0), importRuleRefIdList, comment, ruleStatusIdList); 
					workflowService.publishRule(storeId, storeName, userName, ruleEntity.name(), importRuleRefIdList, comment, ruleStatusIdList); 
					break;
				default: 
				}
			} else {
				//TODO: update import task to completed and set error message as rule is locked.
			}
			updateTaskExecution(importRuleQueueItem, TaskStatus.COMPLETED, null, new DateTime(), "");
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