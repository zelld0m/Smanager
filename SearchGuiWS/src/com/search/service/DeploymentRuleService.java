package com.search.service;

import java.util.List;
import java.util.Map;

public interface DeploymentRuleService {
	/*public boolean publishElevateRules(String store, List<String> list);
	public boolean publishExcludeRules(String store, List<String> list);
	public boolean publishDemoteRules(String store, List<String> list);
	public boolean publishFacetSortRules(String store, List<String> list);
	public boolean publishRedirectRules(String store, List<String> list);
	public boolean publishRankingRules(String store, List<String> list);*/
	
	public Map<String,Boolean> publishElevateRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishExcludeRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishDemoteRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishFacetSortRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishRedirectRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishRankingRulesMap(String store, List<String> list);
	
	/*public boolean recallElevateRules(String store, List<String> list);
	public boolean recallExcludeRules(String store, List<String> list);
	public boolean recallDemoteRules(String store, List<String> list);
	public boolean recallFacetSortRules(String store, List<String> list);
	public boolean recallRedirectRules(String store, List<String> list);
	public boolean recallRankingRules(String store, List<String> list);*/
	
	public Map<String,Boolean> recallElevateRulesMap(String store, List<String> list);
	public Map<String,Boolean> recallExcludeRulesMap(String store, List<String> list);
	public Map<String,Boolean> recallDemoteRulesMap(String store, List<String> list);
	public Map<String,Boolean> recallFacetSortRulesMap(String store, List<String> list);
	public Map<String,Boolean> recallRedirectRulesMap(String store, List<String> list);
	public Map<String,Boolean> recallRankingRulesMap(String store, List<String> list);
	
	public boolean loadElevateRules(String store);
	public boolean loadExcludeRules(String store);
	public boolean loadDemoteRules(String store);
	public boolean loadFacetSortRules(String store);
	public boolean loadRedirectRules(String store);
	public boolean loadRankingRules(String store);
	
	/*public boolean unpublishElevateRules(String store, List<String> list);
	public boolean unpublishExcludeRules(String store, List<String> list);
	public boolean unpublishDemoteRules(String store, List<String> list);
	public boolean unpublishFacetSortRules(String store, List<String> list);
	public boolean unpublishRedirectRules(String store, List<String> list);
	public boolean unpublishRankingRules(String store, List<String> list);*/
	
	public Map<String,Boolean> unpublishElevateRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishExcludeRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishDemoteRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishFacetSortRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishRedirectRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishRankingRulesMap(String store, List<String> list);
}
