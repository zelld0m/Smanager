package com.search.manager.core.service;

import java.util.List;
import java.util.Map;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.model.RecordSet;
import com.search.manager.report.statistics.model.BannerStatistics;

public interface BannerRuleService extends GenericService<BannerRule> {

	// Add BannerRuleService specific method here...

	BannerRule transfer(BannerRule bannerRule) throws CoreServiceException;
	
	BannerRule addRule(String ruleName) throws CoreServiceException;

	SearchResult<BannerRule> getAllRules(String storeId, String searchText,
			int page, int pageSize) throws CoreServiceException;

	SearchResult<BannerRule> getRulesByImageId(String storeId,
			String imagePathId, String imageAlias, int page, int pageSize)
			throws CoreServiceException;

	BannerRule getRuleByName(String storeId, String ruleName)
			throws CoreServiceException;

	BannerRule getRuleById(String storeId, String ruleId)
			throws CoreServiceException;

	Integer getTotalRulesByImageId(String storeId, String imagePathId,
			String imageAlias) throws CoreServiceException;

	List<String> copyToRule(String storeId, String[] keywords,
			Map<String, String> params) throws CoreServiceException;

	// Banner statistic

	RecordSet<BannerStatistics> getBannerStats(String storeId, String keyword,
			String memberId, String startDateText, String endDateText,
			boolean aggregate) throws CoreServiceException;

	RecordSet<BannerStatistics> getStatsByKeyword(String storeId,
			String keyword, String startDateText, String endDateText,
			boolean aggregate) throws CoreServiceException;

	RecordSet<BannerStatistics> getStatsByMemberId(String storeId,
			String memberId, String startDateText, String endDateText)
			throws CoreServiceException;

}
