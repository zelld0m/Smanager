package com.search.manager.web.service;

import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.model.RecordSet;
import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.response.ServiceResponse;

@Service("bannerRuleServiceGui")
@RemoteProxy(name = "BannerRuleServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "bannerRuleService"))
public class BannerRuleServiceGuiImpl implements BannerRuleServiceGui {

	@Autowired
	// @Qualifier("bannerRuleServiceSolr")
	@Qualifier("bannerRuleServiceSp")
	private BannerRuleService bannerRuleService;

	// TODO Transfer to message configuration file
	private static final String MSG_FAILED_ADD_RULE = "Failed to add banner rule %s";
	private static final String MSG_FAILED_RETRIVAL = "Unable to retrieve all rules.";
	private static final String MSG_FAILED_RETRIVAL_BY_NAME = "Failed to retrieve banner rule by name. Name: '%s'";
	private static final String MSG_FAILED_RETRIVAL_BY_ID = "Failed to retrieve banner rule by id. ID: '%s'";
	private static final String MSG_FAILED_GET_RULE_WITH_IMAGE = "Failed to retrieve banner rule using '%s'";
	private static final String MSG_FAILED_COUNT_RETRIVAL_BY_IMAGE_PATH_ID = "Failed to retrieve total banner count by image path.";

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRule> addRule(String ruleName) {
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();
		try {
			BannerRule bannerRule = bannerRuleService.addRule(ruleName);
			if (bannerRule != null) {
				serviceResponse.success(bannerRule);
			} else {
				serviceResponse.error(String.format(MSG_FAILED_ADD_RULE,
						ruleName));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(String.format(MSG_FAILED_ADD_RULE, ruleName));
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRule>> getAllRules(
			String storeId, String searchText, int page, int pageSize) {
		ServiceResponse<SearchResult<BannerRule>> serviceResponse = new ServiceResponse<SearchResult<BannerRule>>();
		try {
			serviceResponse.success(bannerRuleService.getAllRules(storeId,
					searchText, page, pageSize));
		} catch (CoreServiceException e) {
			serviceResponse.error(MSG_FAILED_RETRIVAL, e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRule>> getRulesByImageId(
			String storeId, String imagePathId, String imageAlias, int page,
			int pageSize) {
		ServiceResponse<SearchResult<BannerRule>> serviceResponse = new ServiceResponse<SearchResult<BannerRule>>();

		try {
			serviceResponse.success(bannerRuleService.getRulesByImageId(
					storeId, imagePathId, imageAlias, page, pageSize));
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_GET_RULE_WITH_IMAGE, imageAlias),
					e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRule> getRuleByName(String storeId,
			String ruleName) {
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();
		try {
			serviceResponse.success(bannerRuleService.getRuleByName(storeId,
					ruleName));
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_RETRIVAL_BY_NAME, ruleName), e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRule> getRuleById(String storeId, String ruleId) {
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();
		try {
			serviceResponse.success(bannerRuleService.getRuleById(storeId,
					ruleId));
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_RETRIVAL_BY_ID, ruleId), e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Integer> getTotalRulesByImageId(String storeId,
			String imagePathId, String imageAlias) {
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();

		try {
			serviceResponse.success(bannerRuleService.getTotalRulesByImageId(
					storeId, imagePathId, imageAlias));
		} catch (CoreServiceException e) {
			serviceResponse
					.error(MSG_FAILED_COUNT_RETRIVAL_BY_IMAGE_PATH_ID, e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<List<String>> copyToRule(String storeId,
			String[] keywords, Map<String, String> params) {
		ServiceResponse<List<String>> serviceResponse = new ServiceResponse<List<String>>();
		try {
			serviceResponse.success(bannerRuleService.copyToRule(storeId,
					keywords, params));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("copyToRule()", e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<RecordSet<BannerStatistics>> getBannerStats(
			String storeId, String keyword, String memberId,
			String startDateText, String endDateText, boolean aggregate) {
		ServiceResponse<RecordSet<BannerStatistics>> serviceResponse = new ServiceResponse<RecordSet<BannerStatistics>>();
		try {
			serviceResponse.success(bannerRuleService.getBannerStats(storeId,
					keyword, memberId, startDateText, endDateText, aggregate));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("bannerRuleService()", e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<RecordSet<BannerStatistics>> getStatsByKeyword(
			String storeId, String keyword, String startDateText,
			String endDateText, boolean aggregate) {
		ServiceResponse<RecordSet<BannerStatistics>> serviceResponse = new ServiceResponse<RecordSet<BannerStatistics>>();
		try {
			serviceResponse.success(bannerRuleService.getStatsByKeyword(
					storeId, keyword, startDateText, endDateText, aggregate));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("getStatsByKeyword()", e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<RecordSet<BannerStatistics>> getStatsByMemberId(
			String storeId, String memberId, String startDateText,
			String endDateText) {
		ServiceResponse<RecordSet<BannerStatistics>> serviceResponse = new ServiceResponse<RecordSet<BannerStatistics>>();
		try {
			serviceResponse.success(bannerRuleService.getStatsByMemberId(
					storeId, memberId, startDateText, endDateText));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("getStatsByMemberId()", e);
		}
		return serviceResponse;
	}

}
