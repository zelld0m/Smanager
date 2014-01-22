package com.search.manager.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImportRuleTask;
import com.search.manager.core.model.TaskExecutionResult;
import com.search.manager.core.model.TaskStatus;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.StringUtil;
import com.search.manager.web.service.ImportRuleTaskDwrService;
import com.search.manager.xml.file.RuleTransferUtil;

@Component
@RemoteProxy(name = "ImportRuleTaskService", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "importRuleTaskService"))
public class ImportRuleTaskDwrServiceImpl implements ImportRuleTaskDwrService {

	@Autowired
	@Qualifier("importRuleTaskServiceSp")
	private ImportRuleTaskService importRuleTaskService;
	@Autowired
	private RuleTransferUtil ruleTransferUtil;
	@Autowired
	private UtilityService utilityService;

	@Override
	@RemoteMethod
	public ServiceResponse<List<ImportRuleTask>> getTasks(String storeId, String ruleEntityType, String ruleId)
			throws CoreServiceException {
		ImportRuleTask importRuleTask = new ImportRuleTask();
		importRuleTask.setTargetStoreId(storeId);
		importRuleTask.setTargetRuleId(ruleId);
		importRuleTask.setRuleEntity(RuleEntity.find(ruleEntityType));
		SearchResult<ImportRuleTask> recordSet = importRuleTaskService.search(importRuleTask, 0, 0);

		List<ImportRuleTask> importRuleTaskList = new ArrayList<ImportRuleTask>(recordSet.getList());

		CollectionUtils.filter(importRuleTaskList, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				ImportRuleTask task = (ImportRuleTask) object;
				TaskExecutionResult result = task.getTaskExecutionResult();
				TaskStatus taskStatus = result.getTaskStatus();
				return ArrayUtils.contains(TaskStatus.AWAITING_COMPLETION_STATUSES, taskStatus);
			}
		});

		ServiceResponse<List<ImportRuleTask>> serviceResponse = new ServiceResponse<List<ImportRuleTask>>();
		serviceResponse.success(importRuleTaskList);
		return serviceResponse;
	}

	@Override
	@RemoteMethod
	public ServiceResponse<ImportRuleTask> getTask(String storeId, String taskId) {

		ServiceResponse<ImportRuleTask> serviceResponse = new ServiceResponse<ImportRuleTask>();

		ImportRuleTask importRuleTask;
		try {
			importRuleTask = importRuleTaskService.searchById(storeId, taskId);
			serviceResponse.success(importRuleTask);
		} catch (CoreServiceException e) {
			e.printStackTrace();
			serviceResponse.error("Unable to retrieve task. Please contact your system administrator.");
		}

		return serviceResponse;
	}

	@Override
	@RemoteMethod
	public ServiceResponse<Boolean> cancelTask(String storeId, String taskId) throws CoreServiceException {

		return updateQueueTask(storeId, taskId, TaskStatus.CANCELED, null);
	}

	@Override
	@RemoteMethod
	public ServiceResponse<Boolean> resetAttempts(String storeId, String taskId) throws CoreServiceException {

		return updateQueueTask(storeId, taskId, TaskStatus.QUEUED, 0);
	}

	@Override
	@RemoteMethod
	public ServiceResponse<RuleXml> getRule(String storeId, String taskId) throws CoreServiceException {

		ImportRuleTask importRuleTask = importRuleTaskService.searchById(storeId, taskId);
		ServiceResponse<RuleXml> serviceResponse = new ServiceResponse<RuleXml>();
		if(importRuleTask != null) {
			RuleXml ruleXml = ruleTransferUtil.getRuleToImport(importRuleTask.getTargetStoreId(), importRuleTask.getRuleEntity(),  StringUtil.escapeKeyword(importRuleTask.getSourceRuleId()));
			serviceResponse.success(ruleXml);
		} else {
			serviceResponse.error("Cannot find task. Please contact your system administrator.");
		}
		
		return serviceResponse;
	}

	private ServiceResponse<Boolean> updateQueueTask(String storeId, String taskId, TaskStatus status,
			Integer runAttempt) throws CoreServiceException {
		ImportRuleTask importRuleTask = importRuleTaskService.searchById(storeId, taskId);
		TaskExecutionResult result = importRuleTask.getTaskExecutionResult();
		if (runAttempt != null) {

			result.setRunAttempt(runAttempt);
			result.setTaskErrorMessage(null);
			result.setStateCompleted(null);
		}
		result.setTaskStatus(status);
		importRuleTask.setLastModifiedDate(new DateTime());
		importRuleTask.setLastModifiedBy(utilityService.getUsername());

		importRuleTask = importRuleTaskService.update(importRuleTask);

		ServiceResponse<Boolean> serviceResponse = new ServiceResponse<Boolean>();

		serviceResponse.success(importRuleTask != null
				&& status.equals(importRuleTask.getTaskExecutionResult().getTaskStatus()));

		return serviceResponse;
	}
}
