package com.search.manager.workflow.service;

import com.search.manager.dao.DaoException;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.workflow.model.ImportRuleTask;

public interface ExportRuleMapService {

	public ExportRuleMap getExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException;
	
	public ExportRuleMap getExportRuleMap(ImportRuleTask importRuleTask) throws DaoException;
}
