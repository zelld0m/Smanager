package com.search.manager.core.service.sp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.BannerRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Filter.MatchType;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.RecordSet;
import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.report.statistics.util.BannerStatisticsUtil;
import com.search.manager.service.UtilityService;

@Service("bannerRuleServiceSp")
public class BannerRuleServiceSpImpl implements BannerRuleService {

	@Autowired
	@Qualifier("bannerRuleDaoSp")
	protected BannerRuleDao bannerRuleDao;
	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	protected RuleStatusService ruleStatusService;

	// a setter method so that the Spring container can 'inject'
	public void setBannerRuleDao(BannerRuleDao bannerRuleDao) {
		this.bannerRuleDao = bannerRuleDao;
	}

	public void setBannerRuleItemService(
			BannerRuleItemService bannerRuleItemService) {
		this.bannerRuleItemService = bannerRuleItemService;
	}

	public void setRuleStatusService(RuleStatusService ruleStatusService) {
		this.ruleStatusService = ruleStatusService;
	}

	@Override
	public BannerRule add(BannerRule model) throws CoreServiceException {
		try {
			// TODO validation here...
			// TODO add spring transaction...
			// Validate required fields.

			// Set CreatedBy and CreatedDate
			DateTime createdDate = new DateTime();
			if (StringUtils.isBlank(model.getCreatedBy())) {
				model.setCreatedBy(UtilityService.getUsername());
			}
			if (model.getCreatedDate() == null) {
				model.setCreatedDate(createdDate);
			}

			model = bannerRuleDao.add(model);

			if (model != null) {
				// Add rule status
				RuleStatus ruleStatus = new RuleStatus(RuleEntity.BANNER,
						UtilityService.getStoreId(), model.getRuleId(),
						model.getRuleName(), UtilityService.getUsername(),
						UtilityService.getUsername(), RuleStatusEntity.ADD,
						RuleStatusEntity.UNPUBLISHED);
				ruleStatus.setCreatedBy(UtilityService.getUsername());
				ruleStatus.setCreatedDate(createdDate);
				ruleStatusService.add(ruleStatus);
			}

			return model;
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<BannerRule> add(Collection<BannerRule> models)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		try {
			return (List<BannerRule>) bannerRuleDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRule update(BannerRule model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for update.

			// Set LastModifiedBy and LastModifiedDate
			if (StringUtils.isBlank(model.getLastModifiedBy())) {
				model.setLastModifiedBy(UtilityService.getUsername());
			}
			if (model.getLastModifiedDate() == null) {
				model.setLastModifiedDate(new DateTime());
			}

			return bannerRuleDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<BannerRule> update(Collection<BannerRule> models)
			throws CoreServiceException {
		try {
			return (List<BannerRule>) bannerRuleDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(BannerRule model) throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<BannerRule, Boolean> delete(Collection<BannerRule> models)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

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

	@Override
	public SearchResult<BannerRule> search(BannerRule model)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRule searchById(String storeId, String id)
			throws CoreServiceException {

		if (StringUtils.isBlank(storeId) || StringUtils.isBlank(id)) {
			return null;
		}

		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, id));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.MATCH_ID.getIntValue()));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		SearchResult<BannerRule> searchResult = search(search);

		if (searchResult.getTotalCount() > 0) {
			return (BannerRule) CollectionUtils
					.get(searchResult.getResult(), 0);
		}

		return null;
	}

	// BannerRuleService specific method here...

	@Override
	public BannerRule transfer(BannerRule bannerRule)
			throws CoreServiceException {

		// Validate required fields for transfer method.
		if (StringUtils.isNotBlank(bannerRule.getStoreId())
				&& StringUtils.isNotBlank(bannerRule.getRuleId())
				&& StringUtils.isNotBlank(bannerRule.getRuleName())
				&& StringUtils.isNotBlank(bannerRule.getCreatedBy())
				&& bannerRule.getCreatedDate() != null) {
			try {
				bannerRule = bannerRuleDao.add(bannerRule);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return null;
	}

	@Override
	public BannerRule addRule(String ruleName) throws CoreServiceException {
		BannerRule bannerRule = new BannerRule(UtilityService.getStoreId(),
				ruleName, UtilityService.getUsername());

		return add(bannerRule);
	}

	@Override
	public SearchResult<BannerRule> getAllRules(String storeId,
			String searchText, int page, int pageSize)
			throws CoreServiceException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_SEARCH_TEXT, searchText));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.LIKE_NAME.getIntValue()));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		return search(search);
	}

	@Override
	public SearchResult<BannerRule> getRulesByImageId(String storeId,
			String imagePathId, String imageAlias, int page, int pageSize)
			throws CoreServiceException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID,
				imagePathId));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		return search(search);
	}

	@Override
	public BannerRule getRuleByName(String storeId, String ruleName)
			throws CoreServiceException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_SEARCH_TEXT, ruleName));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.LIKE_NAME.getIntValue()));
		search.setPageNumber(1);
		search.setMaxRowCount(1);
		SearchResult<BannerRule> searchResult = search(search);
		if (searchResult.getTotalCount() > 0) {
			return (BannerRule) CollectionUtils
					.get(searchResult.getResult(), 0);
		}
		return null;
	}

	@Override
	public BannerRule getRuleById(String storeId, String ruleId)
			throws CoreServiceException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.MATCH_ID.getIntValue()));
		search.setPageNumber(1);
		search.setMaxRowCount(1);
		SearchResult<BannerRule> searchResult = search(search);
		if (searchResult.getTotalCount() > 0) {
			return (BannerRule) CollectionUtils
					.get(searchResult.getResult(), 0);
		}

		return null;
	}

	@Override
	public Integer getTotalRulesByImageId(String storeId, String imagePathId,
			String imageAlias) throws CoreServiceException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID,
				imagePathId));
		SearchResult<BannerRule> searchResult = search(search);

		return searchResult.getTotalCount();
	}

	@Override
	public List<String> copyToRule(String storeId, String[] keywords,
			Map<String, String> params) throws CoreServiceException {
		List<String> copiedToKeywordList = new ArrayList<String>();

		for (String ruleName : keywords) {
			try {
				addRule(ruleName);
			} catch (CoreServiceException e) {
				// attempt to add..
			}
			BannerRule bannerRule = getRuleByName(storeId, ruleName);
			if (bannerRule != null) {
				RuleStatus ruleStatus = ruleStatusService.getRuleStatus(
						RuleEntity.getValue(RuleEntity.BANNER.getCode()),
						bannerRule.getRuleId());

				if (ruleStatus != null && !ruleStatus.isLocked()) {
					params.put("ruleId", bannerRule.getRuleId());
					params.put("ruleName", bannerRule.getRuleName());

					BannerRuleItem bannerRuleItem = bannerRuleItemService
							.addRuleItem(storeId, params);
					if (bannerRuleItem != null) {
						copiedToKeywordList.add(ruleName);
					}
				}
			}
		}

		return copiedToKeywordList;
	}

	// Banner statistic

	@Override
	public RecordSet<BannerStatistics> getBannerStats(String storeId,
			String keyword, String memberId, String startDateText,
			String endDateText, boolean aggregate) throws CoreServiceException {
		RecordSet<BannerStatistics> rs = null;

		DateTime startDateTime = JodaDateTimeUtil.toDateTimeFromStorePattern(
				storeId, startDateText, JodaPatternType.DATE);
		DateTime endDateTime = JodaDateTimeUtil.toDateTimeFromStorePattern(
				storeId, endDateText, JodaPatternType.DATE);

		if (startDateTime == null && endDateTime == null) {
			endDateTime = startDateTime = DateTime.now();
		}

		List<BannerStatistics> list = new ArrayList<BannerStatistics>();

		try {
			list = StringUtils.isNotBlank(keyword) ? BannerStatisticsUtil
					.getStatsPerBannerByKeyword(storeId, keyword,
							startDateTime.toDate(), endDateTime.toDate(),
							aggregate) : BannerStatisticsUtil
					.getStatsPerKeywordByMemberId(storeId, memberId,
							startDateTime.toDate(), endDateTime.toDate());

			rs = new RecordSet<BannerStatistics>(list,
					(Integer) CollectionUtils.size(list));
		} catch (FileNotFoundException e) {
			throw new CoreServiceException(
					"File not found for getStatsPerKeyword", e);
		} catch (Exception e) {
			throw new CoreServiceException("Exception for getStatsPerKeyword",
					e);
		}

		return rs;
	}

	@Override
	public RecordSet<BannerStatistics> getStatsByKeyword(String storeId,
			String keyword, String startDateText, String endDateText,
			boolean aggregate) throws CoreServiceException {
		return getBannerStats(storeId, keyword, null, startDateText,
				endDateText, aggregate);
	}

	@Override
	public RecordSet<BannerStatistics> getStatsByMemberId(String storeId,
			String memberId, String startDateText, String endDateText)
			throws CoreServiceException {
		return getBannerStats(storeId, null, memberId, startDateText,
				endDateText, false);
	}

}
