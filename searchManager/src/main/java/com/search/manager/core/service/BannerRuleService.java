package com.search.manager.core.service;

import java.util.List;
import java.util.Map;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.model.RecordSet;
import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.response.ServiceResponse;

public interface BannerRuleService extends GenericService<BannerRule> {

	// Add BannerRuleService specific method here...

	ServiceResponse<BannerRule> addRule(String ruleName)
			throws CoreServiceException;

	ServiceResponse<SearchResult<BannerRule>> getAllRules(String storeId,
			String searchText, int page, int pageSize)
			throws CoreServiceException;

	ServiceResponse<SearchResult<BannerRule>> getRulesByImageId(String storeId,
			String imagePathId, String imageAlias, int page, int pageSize)
			throws CoreServiceException;

	ServiceResponse<BannerRule> getRuleByName(String storeId, String ruleName)
			throws CoreServiceException;

	ServiceResponse<BannerRule> getRuleById(String storeId, String ruleId)
			throws CoreServiceException;

	ServiceResponse<Integer> getTotalRulesByImageId(String storeId,
			String imagePathId, String imageAlias) throws CoreServiceException;

	ServiceResponse<List<String>> copyToRule(String storeId, String[] keywords,
			Map<String, String> params) throws CoreServiceException;

	// Banner statistic

	ServiceResponse<RecordSet<BannerStatistics>> getBannerStats(String storeId,
			String keyword, String memberId, String startDateText,
			String endDateText, boolean aggregate) throws CoreServiceException;

	ServiceResponse<RecordSet<BannerStatistics>> getStatsByKeyword(
			String storeId, String keyword, String startDateText,
			String endDateText, boolean aggregate) throws CoreServiceException;

	ServiceResponse<RecordSet<BannerStatistics>> getStatsByMemberId(
			String storeId, String memberId, String startDateText,
			String endDateText) throws CoreServiceException;

}
