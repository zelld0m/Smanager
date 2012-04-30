package com.search.ws.client;

import java.util.List;
import java.util.Map;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;

public interface SearchGuiClientService {
	public boolean deployRules(String store, List<String> ruleRefIdList, RuleEntity entity);
	public boolean recallRules(String store, List<String> ruleRefIdList, RuleEntity entity);
	public boolean unDeployRules(String store, List<String> ruleRefIdList, RuleEntity entity);
	public Map<String,Boolean> deployRulesMap(String store, List<String> ruleRefIdList, RuleEntity entity);
	public Map<String,Boolean> recallRulesMap(String store, List<String> ruleRefIdList, RuleEntity entity);
	public Map<String,Boolean> unDeployRulesMap(String store, List<String> ruleRefIdList, RuleEntity entity);
	public List<BackupInfo> getBackupInfo(String store, List<String> ruleRefIdList, RuleEntity entity);
}
