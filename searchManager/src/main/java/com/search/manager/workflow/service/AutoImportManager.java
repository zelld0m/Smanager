package com.search.manager.workflow.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ExportRuleMap;

public interface AutoImportManager {

	public ExportRuleMap getExportRuleMap(RuleEntity ruleEntity, String sourceStoreId, String sourceRuleId, String targetStore, String importAsId);
	
	public String getImportAsId(RuleEntity ruleEntity, RuleStatus ruleStatus, String sourceStoreId, String sourceRuleId, String targetStore, String importAsId);
	
	public RuleStatus getTargetRuleStatus(RuleEntity ruleEntity, String storeId, String ruleId, String ruleName, String targetStoreId) throws CoreServiceException;
}
