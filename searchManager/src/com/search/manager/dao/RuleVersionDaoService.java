package com.search.manager.dao;

import java.util.List;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;

public interface RuleVersionDaoService {
	public boolean createRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String notes) throws Exception;
	public boolean deleteRuleVersion(String store, RuleEntity ruleEntity, String ruleId, int version) throws Exception;
	public boolean restoreRuleVersion(String store, RuleEntity ruleEntity, String ruleId, int version);	
	public List<RuleVersionInfo> getRuleVersionList(String store, String ruleType, String ruleId) throws Exception;
	
	public RedirectRule readQueryCleaningRuleVersion(String store, String ruleId, int version);
	public Relevancy readRankingRuleVersion(String store, String ruleId, int version);
	public List<ElevateProduct> readElevateRuleVersion(String store, String ruleId, int version, String server);
	public List<Product> readExcludeRuleVersion(String store, String ruleId, int version, String server);
	public List<DemoteProduct> readDemoteRuleVersion(String store, String ruleId, int version, String server);
	public List<FacetSort> readFacetSortRuleVersion(String store, String ruleId, int version, String server);
}