package com.search.manager.workflow.service;

import com.search.manager.core.model.ImportRuleTask;
import com.search.manager.dao.DaoException;
import com.search.manager.model.ExportRuleMap;

public interface ExportRuleMapService {

    ExportRuleMap getExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException;

    ExportRuleMap getExportRuleMap(ImportRuleTask importRuleTask) throws DaoException;
    
}
