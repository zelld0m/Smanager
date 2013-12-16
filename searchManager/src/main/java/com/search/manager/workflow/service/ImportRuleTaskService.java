package com.search.manager.workflow.service;

import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.workflow.model.ImportRuleTask;

public interface ImportRuleTaskService {
    void addImportRuleTask(ImportRuleTask importRuleTask);
    RecordSet<ImportRuleTask> getImportRuleTasks(SearchCriteria<ImportRuleTask> criteria);
}