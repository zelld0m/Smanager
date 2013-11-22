package com.search.manager.web.service;

import java.util.Map;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.response.ServiceResponse;

@Service("bannerRuleItemServiceSpGui")
@RemoteProxy(name = "BannerRuleItemServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "bannerRuleItemService"))
public class BannerRuleItemServiceGuiImpl implements BannerRuleItemServiceGui {

	@Autowired
	// @Qualifier("bannerRuleItemServiceSolr")
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;

	private static final String MSG_FAILED_ADD_RULE_ITEM = "Failed to add banner rule item %s";
	private static final String MSG_FAILED_UPDATE_RULE_ITEM = "Failed to update banner item %s in %s";

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRuleItem> addRuleItem(String storeId,
			Map<String, String> params) {
		ServiceResponse<BannerRuleItem> serviceResponse = new ServiceResponse<BannerRuleItem>();
		String imageAlias = params.get("imageAlias");
		try {
			BannerRuleItem bannerRuleItem = bannerRuleItemService.addRuleItem(
					storeId, params);
			if (bannerRuleItem != null) {
				serviceResponse.success(bannerRuleItem);
			} else {
				serviceResponse.error(String.format(MSG_FAILED_ADD_RULE_ITEM,
						imageAlias));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_ADD_RULE_ITEM, imageAlias), e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Integer> getTotalRuleItems(String storeId,
			String ruleId) {
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();

		try {
			serviceResponse.success(bannerRuleItemService.getTotalRuleItems(
					storeId, ruleId));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("getTotalRuleItems()", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByFilter(
			String storeId, String ruleId, String filter, String dateFilter,
			String imageSize, int page, int pageSize) {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();

		try {
			serviceResponse.success(bannerRuleItemService.getRuleItemsByFilter(
					storeId, ruleId, filter, dateFilter, imageSize, page,
					pageSize));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: getRuleItemsByFilter()", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByImageId(
			String storeId, String imageId, int page, int pageSize) {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();
		try {
			serviceResponse.success(bannerRuleItemService
					.getRuleItemsByImageId(storeId, imageId, page, pageSize));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: getRuleItemsByImageId()", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByRuleId(
			String storeId, String ruleId, int page, int pageSize) {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();
		try {
			serviceResponse.success(bannerRuleItemService.getRuleItemsByRuleId(
					storeId, ruleId, page, pageSize));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: getRuleItemsByRuleId()", e);
		}

		return null;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getAllRuleItems(
			String storeId, String ruleId) {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();
		try {
			serviceResponse.success(bannerRuleItemService.getAllRuleItems(
					storeId, ruleId));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: getAllRuleItems()", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRuleItem> getRuleItemByMemberId(
			String storeId, String ruleId, String memberId) {
		ServiceResponse<BannerRuleItem> serviceResponse = new ServiceResponse<BannerRuleItem>();
		try {
			bannerRuleItemService.getRuleItemByMemberId(storeId, ruleId,
					memberId);
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: getRuleItemByMemberId()", e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRuleItem> updateRuleItem(String storeId,
			Map<String, String> params) {
		ServiceResponse<BannerRuleItem> serviceResponse = new ServiceResponse<BannerRuleItem>();

		String ruleName = params.get("ruleName");
		String imageAlias = params.get("imageAlias");

		try {
			BannerRuleItem bannerRuleItem = bannerRuleItemService
					.updateRuleItem(storeId, params);

			if (bannerRuleItem != null) {
				serviceResponse.success(bannerRuleItem);
			} else {
				serviceResponse.error(String.format(
						MSG_FAILED_UPDATE_RULE_ITEM, imageAlias, ruleName));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(String.format(MSG_FAILED_UPDATE_RULE_ITEM,
					imageAlias, ruleName));
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Boolean> deleteRuleItemsByImageSize(String storeId,
			String ruleId, String imageSize) {
		ServiceResponse<Boolean> serviceResponse = new ServiceResponse<Boolean>();

		try {
			serviceResponse.success(bannerRuleItemService
					.deleteRuleItemsByImageSize(storeId, ruleId, imageSize));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: deleteRuleItemsByImageSize()", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Boolean> deleteRuleItemByMemberId(String storeId,
			String ruleId, String memberId, String alias, String imageSize) {
		ServiceResponse<Boolean> serviceResponse = new ServiceResponse<Boolean>();

		try {
			serviceResponse.success(bannerRuleItemService
					.deleteRuleItemByMemberId(storeId, ruleId, memberId, alias,
							imageSize));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("ERROR: deleteRuleItemByMemberId()", e);
		}

		return serviceResponse;
	}

}
