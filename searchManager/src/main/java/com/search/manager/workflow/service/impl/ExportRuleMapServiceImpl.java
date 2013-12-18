package com.search.manager.workflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.ExportRuleMapDAO;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.service.ExportRuleMapService;

@Service
public class ExportRuleMapServiceImpl implements ExportRuleMapService{

	@Autowired
	private ExportRuleMapDAO exportRuleMapDAO;
	
	public ExportRuleMap getExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException {
		return exportRuleMapDAO.getExportRuleMap(exportRuleMap);
	}
	
	public ExportRuleMap getExportRuleMap(ImportRuleTask importRuleTask) throws DaoException {
		ExportRuleMap exportRuleMap = new ExportRuleMap(importRuleTask.getSourceStoreId(), importRuleTask.getSourceRuleId(), importRuleTask.getSourceRuleName(), importRuleTask.getTargetStoreId(), importRuleTask.getTargetRuleId(), importRuleTask.getTargetRuleName(), importRuleTask.getRuleEntity());
		
		return getExportRuleMap(exportRuleMap);
	}
}
