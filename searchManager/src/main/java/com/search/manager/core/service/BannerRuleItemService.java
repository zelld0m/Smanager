package com.search.manager.core.service;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.SearchResult;

public interface BannerRuleItemService extends GenericService<BannerRuleItem> {

	// Add BannerRuleItemService specific method here...
	
	BannerRuleItem transfer(BannerRuleItem bannerRuleItem)
			throws CoreServiceException;

	List<BannerRuleItem> getActiveBannerRuleItems(String storeId,
			String keyword, DateTime currentDate) throws CoreServiceException;

	BannerRuleItem addRuleItem(String storeId, Map<String, String> params)
			throws CoreServiceException;

	Integer getTotalRuleItems(String storeId, String ruleId)
			throws CoreServiceException;

	SearchResult<BannerRuleItem> getRuleItemsByFilter(String storeId,
			String ruleId, String filter, String dateFilter, String imageSize,
			int page, int pageSize) throws CoreServiceException;

	SearchResult<BannerRuleItem> getRuleItemsByImageId(String storeId,
			String imageId, int page, int pageSize) throws CoreServiceException;

	SearchResult<BannerRuleItem> getRuleItemsByRuleId(String storeId,
			String ruleId, int page, int pageSize) throws CoreServiceException;

	SearchResult<BannerRuleItem> getAllRuleItems(String storeId, String ruleId)
			throws CoreServiceException;

	BannerRuleItem getRuleItemByMemberId(String storeId, String ruleId,
			String memberId) throws CoreServiceException;

	BannerRuleItem updateRuleItem(String storeId, Map<String, String> params)
			throws CoreServiceException;

	Boolean deleteRuleItemsByImageSize(String storeId, String ruleId,
			String imageSize) throws CoreServiceException;

	Boolean deleteRuleItemByMemberId(String storeId, String ruleId,
			String memberId, String alias, String imageSize)
			throws CoreServiceException;

}
