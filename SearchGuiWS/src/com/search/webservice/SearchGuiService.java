package com.search.webservice;

import java.util.List;
import com.search.manager.model.BackupInfo;
import com.search.webservice.model.TransportList;

public interface SearchGuiService{
	public boolean deployRules(TransportList list);
	public boolean recallRules(TransportList list);
	public List<BackupInfo> getBackupInfo(TransportList list);
}
