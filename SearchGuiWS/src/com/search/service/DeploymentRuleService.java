package com.search.service;

import java.util.List;

public interface DeploymentRuleService {
	public boolean publishElevateRules(String store, List<String> list);
	public boolean publishExcludeRules(String store, List<String> list);
	public boolean publishRedirectRules(String store, List<String> list);
	public boolean publishRankingRules(String store, List<String> list);
	
	public boolean recallElevateRules(String store, List<String> list);
	public boolean recallExcludeRules(String store, List<String> list);
	public boolean recallRedirectRules(String store, List<String> list);
	public boolean recallRankingRules(String store, List<String> list);
	
	public boolean loadElevateRules(String store);
	public boolean loadExcludeRules(String store);
	public boolean loadRedirectRules(String store);
	public boolean loadRankingRules(String store);
	
	public boolean unpublishElevateRules(String store, List<String> list);
	public boolean unpublishExcludeRules(String store, List<String> list);
	public boolean unpublishRedirectRules(String store, List<String> list);
	public boolean unpublishRankingRules(String store, List<String> list);
}
