package com.search.manager.core.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImportRuleTask;

public interface ImportRuleTaskService extends GenericService<ImportRuleTask> {

	ImportRuleTask searchById(String id) throws CoreServiceException;

}
