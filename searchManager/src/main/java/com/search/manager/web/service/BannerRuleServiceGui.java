package com.search.manager.web.service;

import java.util.List;
import java.util.Map;

import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.model.RecordSet;
import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.response.ServiceResponse;

public interface BannerRuleServiceGui {

	// Add BannerRuleServiceGui specific method here...

	ServiceResponse<BannerRule> addRule(String ruleName);

	ServiceResponse<SearchResult<BannerRule>> getAllRules(String storeId,
			String searchText, int page, int pageSize);

	ServiceResponse<SearchResult<BannerRule>> getRulesByImageId(String storeId,
			String imagePathId, String imageAlias, int page, int pageSize);

	ServiceResponse<BannerRule> getRuleByName(String storeId, String ruleName);

	ServiceResponse<BannerRule> getRuleById(String storeId, String ruleId);

	ServiceResponse<Integer> getTotalRulesByImageId(String storeId,
			String imagePathId, String imageAlias);

	ServiceResponse<List<String>> copyToRule(String storeId, String[] keywords,
			Map<String, String> params);

	// Banner statistic

	ServiceResponse<RecordSet<BannerStatistics>> getBannerStats(String storeId,
			String keyword, String memberId, String startDateText,
			String endDateText, boolean aggregate);

	ServiceResponse<RecordSet<BannerStatistics>> getStatsByKeyword(
			String storeId, String keyword, String startDateText,
			String endDateText, boolean aggregate);

	ServiceResponse<RecordSet<BannerStatistics>> getStatsByMemberId(
			String storeId, String memberId, String startDateText,
			String endDateText);

}
