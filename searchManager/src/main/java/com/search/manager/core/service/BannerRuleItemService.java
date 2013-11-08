package com.search.manager.core.service;

import java.util.Map;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.SearchResult;
import com.search.manager.response.ServiceResponse;

public interface BannerRuleItemService extends GenericService<BannerRuleItem> {

	// Add BannerRuleItemService specific method here...

	ServiceResponse<BannerRuleItem> addRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException;

	ServiceResponse<Integer> getTotalRuleItems(String storeId, String ruleId)
			throws CoreServiceException;

	ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByFilter(
			String storeId, String ruleId, String filter, String dateFilter,
			String imageSize, int page, int pageSize)
			throws CoreServiceException;

	ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByImageId(
			String storeId, String imageId, int page, int pageSize)
			throws CoreServiceException;

	ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByRuleId(
			String storeId, String ruleId, int page, int pageSize)
			throws CoreServiceException;

	ServiceResponse<SearchResult<BannerRuleItem>> getAllRuleItems(
			String storeId, String ruleId) throws CoreServiceException;

	ServiceResponse<BannerRuleItem> getRuleItemByMemberId(String storeId,
			String ruleId, String memberId) throws CoreServiceException;

	ServiceResponse<BannerRuleItem> updateRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException;

	ServiceResponse<Boolean> deleteRuleItemsByImageSize(String storeId,
			String ruleId, String imageSize) throws CoreServiceException;

	ServiceResponse<Boolean> deleteRuleItemByMemberId(String storeId,
			String ruleId, String memberId, String alias, String imageSize)
			throws CoreServiceException;

}
