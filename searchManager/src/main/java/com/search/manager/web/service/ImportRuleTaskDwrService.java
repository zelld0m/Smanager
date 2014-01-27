package com.search.manager.web.service;

import java.util.List;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImportRuleTask;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.response.ServiceResponse;

public interface ImportRuleTaskDwrService {

    ServiceResponse<List<ImportRuleTask>> getTasks(String storeId, String ruleEntityType, String ruleId)
            throws CoreServiceException;

    ServiceResponse<Boolean> cancelTask(String storeId, String taskId);

    ServiceResponse<Boolean> resetAttempts(String storeId, String taskId);

	ServiceResponse<ImportRuleTask> getTask(String storeId, String taskId);

	ServiceResponse<RuleXml> getRule(String taskId);

}