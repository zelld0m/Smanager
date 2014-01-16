package com.search.manager.web.service;

import java.util.List;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.response.ServiceResponse;
import com.search.manager.workflow.model.ImportRuleTask;

public interface ImportRuleTaskDwrService {
    ServiceResponse<List<ImportRuleTask>> getTasks(String storeId, String ruleEntityType, String ruleId) throws CoreServiceException;
}