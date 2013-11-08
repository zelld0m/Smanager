package com.search.manager.core.service.sp;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.UtilityService;

@Service("bannerRuleItemServiceSp")
@RemoteProxy(name = "BannerRuleItemServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "bannerRuleItemService"))
public class BannerRuleItemServiceSpImpl implements BannerRuleItemService {

	private static final String MSG_FAILED_ADD_RULE_ITEM = "Failed to add banner rule item %s";
	private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
	private static final String MSG_FAILED_UPDATE_RULE_ITEM = "Failed to update banner item %s in %s";

	@Autowired
	@Qualifier("bannerRuleItemDaoSp")
	private BannerRuleItemDao bannerRuleItemDao;
	@Autowired
	@Qualifier("imagePathServiceSp")
	private ImagePathService imagePathService;

	@RemoteMethod
	@Override
	public BannerRuleItem add(BannerRuleItem model) throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleItemDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public BannerRuleItem update(BannerRuleItem model)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleItemDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public boolean delete(BannerRuleItem model) throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleItemDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public SearchResult<BannerRuleItem> search(Search search)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleItemDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	// BannerRuleItemService specific method here...

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRuleItem> addRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException {
		ServiceResponse<BannerRuleItem> serviceResponse = new ServiceResponse<BannerRuleItem>();

		String ruleId = params.get("ruleId");
		String ruleName = params.get("ruleName");
		Integer priority = Integer.parseInt(params.get("priority"));
		String startDate = params.get("startDate");
		String endDate = params.get("endDate");
		String imageAlt = params.get("imageAlt");
		String linkPath = params.get("linkPath");
		String description = params.get("description");
		String imagePathId = params.get("imagePathId");
		String imagePath = params.get("imagePath");
		String imageSize = params.get("imageSize");
		String imageAlias = params.get("imageAlias");
		Boolean disable = BooleanUtils.toBooleanObject(params.get("disable"));
		Boolean openNewWindow = BooleanUtils.toBooleanObject(params
				.get("openNewWindow"));

		BannerRule rule = new BannerRule(storeId, ruleId, ruleName, null);
		DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(
				startDate, JodaPatternType.DATE);
		DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(endDate,
				JodaPatternType.DATE);

		ImagePath newImagePath = new ImagePath(storeId, imagePathId, imagePath,
				imageSize, null, imageAlias);

		if (StringUtils.isBlank(imagePathId)) {
			ServiceResponse<ImagePath> serviceResponseImagePath = imagePathService
					.addImagePathLink(imagePath, imageAlias, imageSize);

			if (serviceResponseImagePath.getStatus() == ServiceResponse.SUCCESS) {
				ServiceResponse<ImagePath> srGetImagePath = imagePathService
						.getImagePath(storeId, imagePath);
				newImagePath = srGetImagePath.getData();
			} else {
				serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE,
						imagePath, imageAlias));
				return serviceResponse;
			}

		}

		BannerRuleItem bannerRuleItem = new BannerRuleItem(rule, null,
				priority, startDT, endDT, imageAlt, linkPath, description,
				newImagePath, disable, openNewWindow);

		bannerRuleItem.setCreatedBy(UtilityService.getUsername());

		try {
			bannerRuleItem = bannerRuleItemDao.add(bannerRuleItem);
			if (bannerRuleItem != null) {
				serviceResponse.success(bannerRuleItem);
			} else {
				serviceResponse.error(String.format(MSG_FAILED_ADD_RULE_ITEM,
						imageAlias));
			}
		} catch (Exception e) {
			serviceResponse.error(
					String.format(MSG_FAILED_ADD_RULE_ITEM, imageAlias), e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Integer> getTotalRuleItems(String storeId,
			String ruleId) throws CoreServiceException {
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();

		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));

		try {
			SearchResult<BannerRuleItem> bannerRuleItems = search(search);
			serviceResponse.success(bannerRuleItems.getTotalCount());
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByFilter(
			String storeId, String ruleId, String filter, String dateFilter,
			String imageSize, int page, int pageSize)
			throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();

		DateTime now = DateTime.now();
		DateTime startDate = null;
		DateTime endDate = null;
		Boolean disabled = null;

		if ("active".equalsIgnoreCase(filter)) {
			startDate = now;
			endDate = now;
			disabled = false;
		} else if ("expired".equalsIgnoreCase(filter)) {
			endDate = now;
		} else if ("disabled".equalsIgnoreCase(filter)) {
			startDate = endDate = now;
			disabled = true;
		} else if ("date".equalsIgnoreCase(filter)) {
			startDate = endDate = now;
			if (StringUtils.isNotBlank(dateFilter)) {
				startDate = JodaDateTimeUtil
						.toUserDateTimeZone(storeId, dateFilter)
						.toDateMidnight().toDateTime();
				endDate = startDate;
			}
			disabled = false;
		}

		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_SIZE, imageSize));
		search.addFilter(new Filter(DAOConstants.PARAM_DISABLED, disabled));
		// TODO date filter: startDate and endDate
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		try {
			serviceResponse.success(search(search));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByImageId(
			String storeId, String imageId, int page, int pageSize)
			throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID, imageId));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		try {
			serviceResponse.success(search(search));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByRuleId(
			String storeId, String ruleId, int page, int pageSize)
			throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = new ServiceResponse<SearchResult<BannerRuleItem>>();
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		try {
			serviceResponse.success(search(search));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getAllRuleItems(
			String storeId, String ruleId) throws CoreServiceException {
		return getRuleItemsByRuleId(storeId, ruleId, -1, -1);
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRuleItem> getRuleItemByMemberId(
			String storeId, String ruleId, String memberId)
			throws CoreServiceException {
		ServiceResponse<BannerRuleItem> serviceResponse = new ServiceResponse<BannerRuleItem>();
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.addFilter(new Filter(DAOConstants.PARAM_MEMBER_ID, memberId));

		try {
			SearchResult<BannerRuleItem> bannerRuleItems = search(search);
			if (bannerRuleItems.getTotalCount() > 0) {
				serviceResponse.success((BannerRuleItem) CollectionUtils.get(
						bannerRuleItems.getResult(), 0));
			}
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<BannerRuleItem> updateRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException {
		// TODO Auto-generated method stub
		String username = UtilityService.getUsername();
		ServiceResponse<BannerRuleItem> serviceResponse = new ServiceResponse<BannerRuleItem>();

		String ruleId = params.get("ruleId");
		String ruleName = params.get("ruleName");
		String memberId = params.get("memberId");
		String priority = params.get("priority");
		String startDate = params.get("startDate");
		String endDate = params.get("endDate");
		String imagePathId = params.get("imagePathId");
		String imagePath = params.get("imagePath");
		String imageAlias = params.get("imageAlias");
		String imageAlt = params.get("imageAlt");
		String linkPath = params.get("linkPath");
		String imageSize = params.get("imageSize");
		String description = params.get("description");
		Boolean disable = BooleanUtils.toBooleanObject(params.get("disable"));
		Boolean openNewWindow = BooleanUtils.toBooleanObject(params
				.get("openNewWindow"));

		if (StringUtils.isNotBlank(ruleId) && StringUtils.isNotBlank(memberId)
				&& StringUtils.isNotBlank(storeId)) {

			BannerRule bannerRule = new BannerRule();
			bannerRule.setRuleId(ruleId);
			bannerRule.setStoreId(storeId);

			DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(
					startDate, JodaPatternType.DATE);
			DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(
					endDate, JodaPatternType.DATE);

			BannerRuleItem bannerRuleItem = new BannerRuleItem();
			bannerRuleItem.setRule(bannerRule);
			bannerRuleItem.setMemberId(memberId);

			if (StringUtils.isNotBlank(priority)
					&& StringUtils.isNumeric(priority)
					&& Integer.parseInt(priority) > 0) {
				bannerRuleItem.setPriority(Integer.parseInt(priority));
			}

			bannerRuleItem.setStartDate(startDT);
			bannerRuleItem.setEndDate(endDT);
			bannerRuleItem.setImageAlt(imageAlt);
			bannerRuleItem.setLinkPath(linkPath);
			bannerRuleItem.setDescription(description);
			bannerRuleItem.setDisabled(disable);
			bannerRuleItem.setOpenNewWindow(openNewWindow);
			bannerRuleItem.setLastModifiedBy(username);

			ImagePath thisImagePath = new ImagePath();
			thisImagePath.setSize(imageSize);
			bannerRuleItem.setImagePath(thisImagePath);
			bannerRuleItem.setLastModifiedBy(username);

			if (StringUtils.isNotBlank(imagePathId)
					&& StringUtils.isBlank(imagePath)
					&& StringUtils.isNotBlank(imageAlias)) {
				// update alias
				ServiceResponse<ImagePath> serviceResponseImagePath = new ServiceResponse<ImagePath>();
				serviceResponseImagePath = imagePathService
						.updateImagePathAlias(imagePathId, imageAlias);

				if (serviceResponseImagePath.getStatus() == ServiceResponse.ERROR) {
					serviceResponse.error(serviceResponseImagePath
							.getErrorMessage().getMessage());
					return serviceResponse;
				}
			} else {
				// Do not update any image path assoc details
			}

			// Update banner item
			try {
				bannerRuleItem = update(bannerRuleItem);
				if (bannerRuleItem != null) {
					serviceResponse.success(bannerRuleItem);
				} else {
					serviceResponse.error(String.format(
							MSG_FAILED_UPDATE_RULE_ITEM, imageAlias, ruleName));
				}
			} catch (CoreServiceException e) {
				serviceResponse.error(String.format(
						MSG_FAILED_UPDATE_RULE_ITEM, imageAlias, ruleName));
			}
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Boolean> deleteRuleItemsByImageSize(String storeId,
			String ruleId, String imageSize) throws CoreServiceException {
		ServiceResponse<Boolean> serviceResponse = new ServiceResponse<Boolean>();

		BannerRuleItem bannerRuleItem = new BannerRuleItem(ruleId, storeId);
		bannerRuleItem.setImagePath(new ImagePath(storeId, null, null,
				imageSize, null, null));
		try {
			serviceResponse.success(delete(bannerRuleItem));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<Boolean> deleteRuleItemByMemberId(String storeId,
			String ruleId, String memberId, String alias, String imageSize)
			throws CoreServiceException {
		ServiceResponse<Boolean> serviceResponse = new ServiceResponse<Boolean>();

		BannerRuleItem bannerRuleItem = new BannerRuleItem(ruleId, storeId,
				memberId);
		bannerRuleItem.setImagePath(new ImagePath(storeId, null, null,
				imageSize, null, alias));

		try {
			serviceResponse.success(delete(bannerRuleItem));
		} catch (CoreServiceException e) {
			// TODO create error message.
			serviceResponse.error("", e);
		}

		return serviceResponse;
	}

}
