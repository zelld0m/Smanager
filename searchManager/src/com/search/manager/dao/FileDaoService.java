package com.search.manager.dao;

import java.util.List;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;

public interface FileDaoService {
	public boolean createBackup(String store, String ruleId, RuleEntity ruleEntity, String userName, String name, String reason) throws Exception;
	public RedirectRule readQueryCleaningRuleVersion(String store, String ruleId, int version);
	public Relevancy readRankingRuleVersion(String store, String ruleId, int version);
	public List<ElevateProduct> readElevateRuleVersion(String store, String ruleId, int version, String server);
	public List<Product> readExcludeRuleVersion(String store, String ruleId, int version, String server);
	public List<DemoteProduct> readDemoteRuleVersion(String store, String ruleId, int version, String server);
	public List<BackupInfo> getBackupInfo(String store, String ruleType, String ruleId) throws Exception;
	public boolean deleteBackup(String storeName, String ruleId, RuleEntity find, int version);
	public boolean restoreRuleVersion(String storeName, String ruleId, int version, RuleEntity find);
	
}
