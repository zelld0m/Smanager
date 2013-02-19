package com.search.service;

import java.util.List;
import java.util.Map;

public interface DeploymentRuleService {
	
	public Map<String,Boolean> publishElevateRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishExcludeRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishDemoteRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishFacetSortRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishRedirectRulesMap(String store, List<String> list);
	public Map<String,Boolean> publishRankingRulesMap(String store, List<String> list);
	
	public boolean loadElevateRules(String store);
	public boolean loadExcludeRules(String store);
	public boolean loadDemoteRules(String store);
	public boolean loadFacetSortRules(String store);
	public boolean loadRedirectRules(String store);
	public boolean loadRankingRules(String store);
	
	public Map<String,Boolean> unpublishElevateRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishExcludeRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishDemoteRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishFacetSortRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishRedirectRulesMap(String store, List<String> list);
	public Map<String,Boolean> unpublishRankingRulesMap(String store, List<String> list);
}
