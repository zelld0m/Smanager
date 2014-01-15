package com.search.manager.workflow.service;

import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.workflow.model.ImportRuleTask;

public interface ImportRuleTaskService {
    public void addImportRuleTask(ImportRuleTask importRuleTask);
    public RecordSet<ImportRuleTask> getImportRuleTasks(SearchCriteria<ImportRuleTask> criteria);
	public ImportRuleTask update(ImportRuleTask importRuleTask);
}