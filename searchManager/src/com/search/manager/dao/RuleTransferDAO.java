package com.search.manager.dao;

import com.search.manager.enums.RuleStatusSortType;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.webservice.model.RuleEntity;

public interface RuleTransferDAO {
	public RecordSet<RuleStatus> getExportList(String store, RuleEntity ruleEntity, RuleStatusSortType sortType);
	public RecordSet<RuleStatus> getImportList(String store, RuleEntity ruleEntity, RuleStatusSortType sortType);
}