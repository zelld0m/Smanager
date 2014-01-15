package com.search.manager.web.service;

import java.util.Map;

import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.SearchResult;
import com.search.manager.response.ServiceResponse;

public interface BannerRuleItemServiceGui {

	// Add BannerRuleItemServiceGui specific method here...

	ServiceResponse<BannerRuleItem> addRuleItem(String storeId,
			Map<String, String> params);

	ServiceResponse<Integer> getTotalRuleItems(String storeId, String ruleId);

	ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByFilter(
			String storeId, String ruleId, String filter, String dateFilter,
			String imageSize, int page, int pageSize);

	ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByImageId(
			String storeId, String imageId, int page, int pageSize);

	ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByRuleId(
			String storeId, String ruleId, int page, int pageSize);

	ServiceResponse<SearchResult<BannerRuleItem>> getAllRuleItems(
			String storeId, String ruleId);

	ServiceResponse<BannerRuleItem> getRuleItemByMemberId(String storeId,
			String ruleId, String memberId);

	ServiceResponse<BannerRuleItem> updateRuleItem(String storeId,
			Map<String, String> params);

	ServiceResponse<Boolean> deleteRuleItemsByImageSize(String storeId,
			String ruleId, String imageSize);

	ServiceResponse<Boolean> deleteRuleItemByMemberId(String storeId,
			String ruleId, String memberId, String alias, String imageSize);

}
