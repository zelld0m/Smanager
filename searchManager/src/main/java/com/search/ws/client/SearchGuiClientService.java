package com.search.ws.client;

import java.util.List;
import java.util.Map;

import com.search.manager.enums.RuleEntity;

public interface SearchGuiClientService {

	public Map<String, Boolean> deployRulesMap(String store,
			List<String> ruleRefIdList, RuleEntity entity);

	public Map<String, Boolean> unDeployRulesMap(String store,
			List<String> ruleRefIdList, RuleEntity entity);

}
