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
import com.search.manager.workflow.service.WorkflowService;
import com.search.manager.workflow.task.ImportRulesJob;
import com.search.ws.ConfigManager;

import com.search.manager.enums.ImportType;
import com.search.manager.exception.PublishLockException;

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
	private RuleTransferService ruleTransferService;
	@Autowired
	private WorkflowService workflowService;
	
	
	public void importRules() throws DaoException {
		
		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.QUEUED, null, null, null);
		ImportRuleTask importRuleTask = new ImportRuleTask(null, null, null, null, null, null, null, null, null, taskExecutionResult);
		RecordSet<ImportRuleTask> importRecords = importRuleTaskDAO.getImportRuleTask(new SearchCriteria<ImportRuleTask>(importRuleTask, null, null, 0, 0), SortOrder.DESCRIPTION_ASCENDING);
		
		logger.info("queued records: {}", importRecords.getTotalSize());
		
		for(ImportRuleTask importRuleQueueItem : importRecords.getList()) {
			
			importQueueItems(importRuleQueueItem, importRuleQueueItem.getTargetStoreId(), configManager.getStoreName(importRuleQueueItem.getTargetStoreId()), importRuleQueueItem.getCreatedBy(), importRuleQueueItem.getRuleEntity(), importRuleQueueItem.getSourceRuleId(), "Auto Import", importRuleQueueItem.getImportType(), importRuleQueueItem.getTargetRuleId(), importRuleQueueItem.getTargetRuleName());
		}
		
		importRuleTask.getTaskExecutionResult().setTaskStatus(TaskStatus.FAILED);
		importRecords = importRuleTaskDAO.getImportRuleTask(new SearchCriteria<ImportRuleTask>(importRuleTask, null, null, 0, 0), SortOrder.DESCRIPTION_ASCENDING);
		
		logger.info("failed records: {}", importRecords.getTotalSize());
		
		for(ImportRuleTask failedImportRuleQueueItem : importRecords.getList()) {
			
			importQueueItems(failedImportRuleQueueItem, failedImportRuleQueueItem.getTargetStoreId(), configManager.getStoreName(failedImportRuleQueueItem.getTargetStoreId()), failedImportRuleQueueItem.getCreatedBy(), failedImportRuleQueueItem.getRuleEntity(), failedImportRuleQueueItem.getSourceRuleId(), "Auto Import", failedImportRuleQueueItem.getImportType(), failedImportRuleQueueItem.getTargetRuleId(), failedImportRuleQueueItem.getTargetRuleName());
		}
		
	}
	
	private void importQueueItems(ImportRuleTask importRuleQueueItem, String storeId, String storeName, String userName, RuleEntity ruleEntity, String importRuleRefId, String comment, ImportType importType, String importAsRefId, String ruleName) throws DaoException {
    	try {
    		
    		RuleStatus ruleStatusInfo = deploymentService.getRuleStatus(storeId, ruleEntity.toString(), importRuleRefId);
        	String[] importRuleRefIdList = {importRuleRefId};
        	String[] importTypeList = {importType.getDisplayText()};
        	String[] importAsRefIdList = {importAsRefId};
        	String[] ruleNameList = {ruleName};
        	String[] ruleStatusIdList = {ruleStatusInfo.getRuleStatusId()};
        	String importTypeSetting = configManager.getProperty("workflow", storeId, "status."+ruleEntity.getNthValue(1));
    		
			ruleTransferService.importRejectRules(storeId, storeName, importRuleQueueItem.getCreatedBy(), ruleEntity.name(), importRuleRefIdList, comment, importTypeList, importAsRefIdList, ruleNameList, null, null);
			
			switch(ImportType.getByDisplayText(importTypeSetting)) {
				case FOR_APPROVAL: workflowService.processRuleStatus(storeId, userName, ruleEntity.getNthValue(0), importRuleRefId, ruleName, false); break;
				case AUTO_PUBLISH: workflowService.processRuleStatus(storeId, userName, ruleEntity.getNthValue(0), importRuleRefId, ruleName, false);
								   deploymentService.approveRule(storeId, ruleEntity.getNthValue(0), importRuleRefIdList, comment, ruleStatusIdList); 
								   workflowService.publishRule(storeId, storeName, userName, ruleEntity.name(), importRuleRefIdList, comment, ruleStatusIdList); 
									break;
				default: 
			}
			importRuleQueueItem.setLastModifiedDate(new DateTime());
			importRuleQueueItem.getTaskExecutionResult().setTaskStatus(TaskStatus.COMPLETED);
			importRuleTaskDAO.updateImportRuleTask(importRuleQueueItem);
		} catch (Exception e) {
			logger.error("failed executing ImportTaskManager.importQueueItems.", e);
			importRuleQueueItem.getTaskExecutionResult().setTaskStatus(TaskStatus.FAILED);
			importRuleQueueItem.getTaskExecutionResult().setTaskErrorMessage(e.getMessage());
			importRuleQueueItem.setLastModifiedDate(new DateTime());
			importRuleTaskDAO.updateImportRuleTask(importRuleQueueItem);
			e.printStackTrace();
		} 
	}
}