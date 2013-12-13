package com.search.manager.workflow.service;

import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.model.DeploymentModel;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;

public interface WorkflowService {

	public RuleStatus processRuleStatus(String storeId, String username, String ruleType, String ruleRefId, String description, Boolean isDelete);
	
	public String generateImportAsId(RuleEntity ruleEntity, String ruleName);
	
	public RecordSet<DeploymentModel> publishRule(String storeId, String storeName, String userName, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException;
	
	public RecordSet<DeploymentModel> publishRuleNoLock(String store, String username, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException;
}
