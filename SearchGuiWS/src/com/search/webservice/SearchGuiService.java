package com.search.webservice;

import java.util.List;
import java.util.Map;

import com.search.manager.model.BackupInfo;
import com.search.webservice.model.TransportList;

public interface SearchGuiService{
	public boolean deployRules(TransportList list);
	public boolean recallRules(TransportList list);
	public boolean unDeployRules(TransportList list);
	public List<BackupInfo> getBackupInfo(TransportList list);
	public Map<String,Boolean> deployRulesMap(TransportList list);
	public Map<String,Boolean> recallRulesMap(TransportList list);
	public Map<String,Boolean> unDeployRulesMap(TransportList list);
}
