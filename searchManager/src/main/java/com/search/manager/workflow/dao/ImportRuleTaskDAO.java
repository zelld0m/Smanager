package com.search.manager.workflow.dao;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.RuleStatusDAO.SortOrder;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.workflow.model.ImportRuleTask;

public interface ImportRuleTaskDAO {

	public ImportRuleTask getImportRuleTask(ImportRuleTask importRuleTask) throws DaoException;
	public RecordSet<ImportRuleTask> getImportRuleTask(SearchCriteria<ImportRuleTask> searchCriteria, SortOrder sortOrder) throws DaoException;
	public int updateImportRuleTask(ImportRuleTask importRuleTask) throws DaoException;
	public ImportRuleTask addImportRuleTask(ImportRuleTask importRuleTask) throws DaoException;
}
