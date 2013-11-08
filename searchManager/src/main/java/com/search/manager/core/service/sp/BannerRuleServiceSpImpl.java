package com.search.manager.core.service.sp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.BannerRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.DeploymentService;
import com.search.manager.service.UtilityService;

@Service("bannerRuleServiceSp")
@RemoteProxy(name = "BannerRuleServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "bannerRuleService"))
public class BannerRuleServiceSpImpl implements BannerRuleService {

	// TODO Transfer to message configuration file
	private static final String MSG_FAILED_ADD_RULE = "Failed to add banner rule %s";
	private static final String MSG_FAILED_RETRIVAL = "Unable to retrieve all rules.";
	private static final String MSG_FAILED_RETRIVAL_BY_NAME = "Failed to retrieve banner rule by name. Name: '%s'";
	private static final String MSG_FAILED_RETRIVAL_BY_ID = "Failed to retrieve banner rule by id. ID: '%s'";
	private static final String MSG_FAILED_GET_RULE_WITH_IMAGE = "Failed to retrieve banner rule using '%s'";
	private static final String MSG_FAILED_COUNT_RETRIVAL_BY_IMAGE_PATH_ID = "Failed to retrieve total banner count by image path.";

	@Autowired
	@Qualifier("bannerRuleDaoSp")
	private BannerRuleDao bannerRuleDao;
	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;
	// TODO @Autowired
	private DeploymentService deploymentService;

	@RemoteMethod
	@Override
	public BannerRule add(BannerRule model) throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public BannerRule update(BannerRule model) throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public boolean delete(BannerRule model) throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public SearchResult<BannerRule> search(Search search)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	// BannerRuleService specific method here...

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRule> addRule(String ruleName)
			throws CoreServiceException {
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		BannerRule bannerRule = new BannerRule(storeId, ruleName, username);
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();

		try {
			bannerRule = add(bannerRule);
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
			String storeId, String searchText, int page, int pageSize)
			throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRule>> serviceResponse = new ServiceResponse<SearchResult<BannerRule>>();

		Search search = new Search(BannerRule.class);
		// TODO Remove DAOConstants and MatchType
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_SEARCH_TEXT, searchText));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.LIKE_NAME));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		try {
			serviceResponse.success(search(search));
		} catch (CoreServiceException e) {
			serviceResponse.error(MSG_FAILED_RETRIVAL, e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRule>> getRulesByImageId(
			String storeId, String imagePathId, String imageAlias, int page,
			int pageSize) throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRule>> serviceResponse = new ServiceResponse<SearchResult<BannerRule>>();
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID,
				imagePathId));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		try {
			serviceResponse.success(search(search));
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
			String ruleName) throws CoreServiceException {
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_SEARCH_TEXT, ruleName));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.LIKE_NAME.getIntValue()));
		search.setPageNumber(1);
		search.setMaxRowCount(1);
		try {
			SearchResult<BannerRule> searchResult = search(search);
			if (searchResult.getTotalCount() > 0) {
				serviceResponse.success((BannerRule) CollectionUtils.get(
						searchResult.getResult(), 0));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_RETRIVAL_BY_NAME, ruleName), e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRule> getRuleById(String storeId, String ruleId)
			throws CoreServiceException {
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.MATCH_ID.getIntValue()));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		try {
			SearchResult<BannerRule> searchResult = search(search);
			if (searchResult.getTotalCount() > 0) {
				serviceResponse.success((BannerRule) CollectionUtils.get(
						searchResult.getResult(), 0));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_RETRIVAL_BY_ID, ruleId), e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Integer> getTotalRulesByImageId(String storeId,
			String imagePathId, String imageAlias) throws CoreServiceException {
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID,
				imagePathId));
		try {
			SearchResult<BannerRule> searchResult = search(search);
			serviceResponse.success(searchResult.getTotalCount());
		} catch (CoreServiceException e) {
			serviceResponse
					.error(MSG_FAILED_COUNT_RETRIVAL_BY_IMAGE_PATH_ID, e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<List<String>> copyToRule(String storeId,
			String[] keywords, Map<String, String> params)
			throws CoreServiceException {
		ServiceResponse<List<String>> serviceResponse = new ServiceResponse<List<String>>();
		List<String> copiedToKeywordList = new ArrayList<String>();

		for (String ruleName : keywords) {
			addRule(ruleName);
			ServiceResponse<BannerRule> searviceResponse = getRuleByName(
					storeId, ruleName);
			BannerRule bannerRule = searviceResponse.getData();

			if (bannerRule != null) {
				RuleStatus ruleStatus = deploymentService.getRuleStatus(
						RuleEntity.getValue(RuleEntity.BANNER.getCode()),
						bannerRule.getRuleId());

				if (ruleStatus != null && !ruleStatus.isLocked()) {
					params.put("ruleId", bannerRule.getRuleId());
					params.put("ruleName", bannerRule.getRuleName());
					ServiceResponse<BannerRuleItem> bannerRuleItem = bannerRuleItemService
							.addRuleItem(storeId, params);
					if (bannerRuleItem.getStatus() == ServiceResponse.SUCCESS) {
						copiedToKeywordList.add(ruleName);
					}
				}
			}
		}

		serviceResponse.success(copiedToKeywordList);
		return serviceResponse;
	}

	// Banner statistic

	@Override
	public ServiceResponse<RecordSet<BannerStatistics>> getStatsByMemberId(
			String storeId, String memberId, String startDateText,
			String endDateText) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<RecordSet<BannerStatistics>> getBannerStats(
			String storeId, String keyword, String memberId,
			String startDateText, String endDateText, boolean aggregate)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
