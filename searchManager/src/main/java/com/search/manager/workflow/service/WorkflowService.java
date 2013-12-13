package com.search.manager.workflow.service;

import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.model.DeploymentModel;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.model.xml.RuleXml;

public interface WorkflowService {

    public boolean exportRule(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, ExportType exportType, String username, String comment) ;
    
	public RuleStatus processRuleStatus(String storeId, String username, String ruleType, String ruleRefId, String description, Boolean isDelete);
	
	public String generateImportAsId(RuleEntity ruleEntity, String ruleName);
	
	public RecordSet<DeploymentModel> publishRule(String storeId, String storeName, String userName, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException;
	
	public RecordSet<DeploymentModel> publishRuleNoLock(String store, String username, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException;
}
