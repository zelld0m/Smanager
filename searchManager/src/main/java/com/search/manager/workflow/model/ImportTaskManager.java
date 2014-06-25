package com.search.manager.workflow.model;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import com.search.manager.core.model.Store;
import com.search.manager.core.model.TaskExecutionResult;
import com.search.manager.core.model.TaskStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
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

			if(RuleEntity.TYPEAHEAD.equals(ruleEntity) || RuleEntity.QUERY_CLEANING.equals(ruleEntity) || RuleEntity.RANKING_RULE.equals(ruleEntity)) {
				ruleStatus = getTargetRuleStatus(ruleEntity, sourceStoreId, importRuleRefId, ruleName, targetStoreId);

			} else {
				ruleStatus = ruleStatusService.getRuleStatus(targetStoreId, importRuleQueueItem.getRuleEntity().getName(), importRuleQueueItem.getSourceRuleId());
			}

			TaskExecutionResult taskExecutionResult = importRuleQueueItem.getTaskExecutionResult();

			taskExecutionResult.setRunAttempt(taskExecutionResult.getRunAttempt() + 1);

			if(ruleStatus != null && Boolean.TRUE.equals(ruleStatus.isLocked())) {
				updateTaskExecution(importRuleQueueItem, TaskStatus.FAILED, null, new DateTime(), "The rule is locked.");
				return;
			} else if(ruleStatus != null && (RuleEntity.QUERY_CLEANING.equals(ruleEntity) || RuleEntity.RANKING_RULE.equals(ruleEntity))) {
				ExportRuleMap existingTargetMap = getExportRuleMap(ruleEntity, null, null, targetStoreId, ruleStatus.getRuleId());

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

			if(RuleEntity.TYPEAHEAD.equals(ruleEntity) || RuleEntity.QUERY_CLEANING.equals(ruleEntity) || RuleEntity.RANKING_RULE.equals(ruleEntity)) {
				importAsId = getImportAsId(ruleEntity, getTargetRuleStatus(ruleEntity, sourceStoreId, importRuleRefId, ruleName, targetStoreId), sourceStoreId, importRuleRefId, targetStoreId, importAsRefIdList[0]);
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

	private RuleStatus getTargetRuleStatus(RuleEntity ruleEntity, String storeId, String ruleId, String ruleName, String targetStoreId) throws CoreServiceException {
		switch(ruleEntity) {

		case TYPEAHEAD:
			TypeaheadRule typeaheadRule = new TypeaheadRule();

			typeaheadRule.setStoreId(storeId);
			typeaheadRule.setRuleName(ruleName);

			SearchResult<TypeaheadRule> result = typeaheadRuleService.search(typeaheadRule);

			if(result.getTotalSize() > 0) {
				return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), result.getList().get(0).getRuleId());
			}

			break;
		case QUERY_CLEANING:
			ExportRuleMap redirectMap = getExportRuleMap(ruleEntity, storeId, ruleId, targetStoreId, null);

			String redirectRuleId = null;

			if(redirectMap != null && redirectMap.getRuleIdTarget() != null){
				redirectRuleId = redirectMap.getRuleIdTarget();
			} else {
				RedirectRule redirectRule = getRedirectRule(targetStoreId, ruleName);
				if(redirectRule != null) {
					redirectRuleId = redirectRule.getRuleId();
				}
			}

			return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), redirectRuleId);

		case RANKING_RULE:
			ExportRuleMap relevancyMap = getExportRuleMap(ruleEntity, storeId, ruleId, targetStoreId, null);

			String relevancyRuleId = null;

			if(relevancyMap != null && relevancyMap.getRuleIdTarget() != null){
				relevancyRuleId = relevancyMap.getRuleIdTarget();
			} else {
				Relevancy relevancyRule = getRelevancyRule(targetStoreId, ruleName);

				if(relevancyRule != null) {
					relevancyRuleId = relevancyRule.getRuleId();
				}
			}

			return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), relevancyRuleId);

		default:
			break;
		}
		return null;
	}

	private RedirectRule getRedirectRule(String targetStoreId, String ruleName) {
		RedirectRule redirectRule = new RedirectRule();

		redirectRule.setStoreId(targetStoreId);
		redirectRule.setRuleName(ruleName);
				
		SearchCriteria<RedirectRule> redirectCriteria = new SearchCriteria<RedirectRule>(redirectRule);

		try {
			RecordSet<RedirectRule> redirectResult = daoService.searchRedirectRule(redirectCriteria, MatchType.MATCH_NAME);
			
			redirectRule = redirectResult.getTotalSize() > 0 ? redirectResult.getList().get(0) : null;
			return redirectRule;

		} catch (DaoException e2) {
			e2.printStackTrace();
		}

		return null;
	}

	private Relevancy getRelevancyRule(String targetStoreId, String ruleName) {
		Relevancy relevancyRule = new Relevancy();
		Store relevancyStore = new Store();
		relevancyStore.setStoreId(targetStoreId);

		relevancyRule.setStore(relevancyStore);
		relevancyRule.setRuleName(ruleName);
		SearchCriteria<Relevancy> relevancyCriteria = new SearchCriteria<Relevancy>(relevancyRule);

		try {
			RecordSet<Relevancy> relevancyResult = daoService.searchRelevancy(relevancyCriteria, MatchType.MATCH_NAME);
			relevancyRule = relevancyResult.getTotalSize() > 0 ? relevancyResult.getList().get(0) : null;
			return relevancyRule;

		} catch (DaoException e2) {
			e2.printStackTrace();
		}

		return null;
	}

	private String getImportAsId(RuleEntity ruleEntity, RuleStatus ruleStatus, String sourceStoreId, String sourceRuleId, String targetStore, String importAsId) {

		switch(ruleEntity) {
		case TYPEAHEAD:
			importAsId = ruleStatus.getRuleId();
			break;
		case QUERY_CLEANING:
		case RANKING_RULE:
			ExportRuleMap existingMap = getExportRuleMap(ruleEntity, sourceStoreId, sourceRuleId, targetStore, null);

			if(existingMap.getRuleIdTarget() != null) {
				importAsId = existingMap.getRuleIdTarget();
			}

			break;
		default:
			break;
		}

		return importAsId;
	}

	private ExportRuleMap getExportRuleMap(RuleEntity ruleEntity, String sourceStoreId, String sourceRuleId, String targetStore, String importAsId) {
		ExportRuleMap searchExportRuleMap = new ExportRuleMap(sourceStoreId, sourceRuleId, null,
				targetStore, importAsId, null, ruleEntity);
		List<ExportRuleMap> rtList;
		try {
			rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(searchExportRuleMap), null).getList();
			if (CollectionUtils.isNotEmpty(rtList)) {
				return rtList.get(0);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}

		return null;
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