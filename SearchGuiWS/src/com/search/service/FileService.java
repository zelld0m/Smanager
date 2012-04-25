package com.search.service;

import java.util.List;
import java.util.Map;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;

public interface FileService {
	public boolean createBackup(String store, List<String> list, RuleEntity ruleEntity) throws Exception;
	public boolean removeBackup(String store, List<String> list, RuleEntity ruleEntity) throws Exception;
	public boolean removeBackup(String store, String ruleId, RuleEntity ruleEntity) throws Exception;
	public Map<String,List<Object>> readBackup(String store, List<String> list, RuleEntity ruleEntity) throws Exception;
	public List<BackupInfo> getBackupInfo(String store, List<String> list, RuleEntity ruleEntity) throws Exception;
}
