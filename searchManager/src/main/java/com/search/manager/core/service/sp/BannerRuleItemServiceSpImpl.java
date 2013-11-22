package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
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
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.service.UtilityService;

@Service("bannerRuleItemServiceSp")
public class BannerRuleItemServiceSpImpl implements BannerRuleItemService {

	@Autowired
	@Qualifier("bannerRuleItemDaoSp")
	protected BannerRuleItemDao bannerRuleItemDao;
	@Autowired
	@Qualifier("imagePathServiceSp")
	protected ImagePathService imagePathService;
	@Autowired
	@Qualifier("bannerRuleServiceSp")
	protected BannerRuleService bannerRuleService;

	// a setter method so that the Spring container can 'inject'
	public void setBannerRuleItemDao(BannerRuleItemDao bannerRuleItemDao) {
		this.bannerRuleItemDao = bannerRuleItemDao;
	}

	public void setImagePathService(ImagePathService imagePathService) {
		this.imagePathService = imagePathService;
	}

	public void setBannerRuleService(BannerRuleService bannerRuleService) {
		this.bannerRuleService = bannerRuleService;
	}

	@Override
	public BannerRuleItem add(BannerRuleItem model) throws CoreServiceException {
		// TODO add Spring transaction propagation
		try {
			// TODO validation here...

			// Validate required fields.

			BannerRule bannerRule = model.getRule();
			ImagePath imagePath = model.getImagePath();

			if (StringUtils.isBlank(model.getRule().getRuleId())) {
				bannerRule = bannerRuleService.add(model.getRule());
			}
			if (StringUtils.isBlank(model.getImagePath().getId())) {
				imagePath = imagePathService.add(model.getImagePath());
			}

			// Set CreatedBy and CreatedDate
			if (StringUtils.isBlank(model.getCreatedBy())) {
				model.setCreatedBy(UtilityService.getUsername());
			}
			if (model.getCreatedDate() == null) {
				model.setCreatedDate(new DateTime());
			}

			if (bannerRule != null && imagePath != null) {
				return bannerRuleItemDao.add(model);
			}
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}

		return null;
	}

	@Override
	public List<BannerRuleItem> add(Collection<BannerRuleItem> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			return (List<BannerRuleItem>) bannerRuleItemDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRuleItem update(BannerRuleItem model)
			throws CoreServiceException {
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

			return bannerRuleItemDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<BannerRuleItem> update(Collection<BannerRuleItem> models)
			throws CoreServiceException {
		try {
			return (List<BannerRuleItem>) bannerRuleItemDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(BannerRuleItem model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for deletion.

			return bannerRuleItemDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<BannerRuleItem, Boolean> delete(Collection<BannerRuleItem> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for deletion.

			return bannerRuleItemDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

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

	@Override
	public SearchResult<BannerRuleItem> search(BannerRuleItem model)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return bannerRuleItemDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRuleItem searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO validation here...

		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_MEMBER_ID, id));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		SearchResult<BannerRuleItem> searchResult = search(search);

		if (searchResult.getTotalCount() > 0) {
			return (BannerRuleItem) CollectionUtils.get(
					searchResult.getResult(), 0);
		}

		return null;
	}

	// BannerRuleItemService specific method here...

	@Override
	public BannerRuleItem transfer(BannerRuleItem bannerRuleItem)
			throws CoreServiceException {
		BannerRule bannerRule = bannerRuleItem.getRule();
		ImagePath imagePath = bannerRuleItem.getImagePath();
		// validation required fields for transfer.
		
		if (bannerRule != null && imagePath != null
				&& StringUtils.isNotBlank(bannerRule.getRuleId())
				&& StringUtils.isNotBlank(imagePath.getId())
				&& StringUtils.isNotBlank(bannerRuleItem.getCreatedBy())
				&& StringUtils.isNotBlank(bannerRuleItem.getMemberId())) {
			try {
				return bannerRuleItemDao.add(bannerRuleItem);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return null;
	}

	@Override
	public List<BannerRuleItem> getActiveBannerRuleItems(String storeId,
			String keyword, DateTime currentDate) throws CoreServiceException {
		if (StringUtils.isBlank(storeId) || StringUtils.isBlank(keyword)
				|| currentDate == null) {
			return null;
		}

		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_NAME, keyword));
		search.addFilter(new Filter(DAOConstants.PARAM_DISABLED, 0));
		search.addFilter(new Filter(DAOConstants.PARAM_START_DATE,
				JodaDateTimeUtil.toSqlDate(currentDate)));
		search.addFilter(new Filter(DAOConstants.PARAM_START_DATE,
				JodaDateTimeUtil.toSqlDate(currentDate)));

		SearchResult<BannerRuleItem> searchResult = search(search);

		if (searchResult.getTotalCount() > 0) {
			return searchResult.getResult();
		}

		return null;
	}

	@Override
	public BannerRuleItem addRuleItem(String storeId, Map<String, String> params)
			throws CoreServiceException {
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
			try {
				newImagePath = imagePathService.add(newImagePath);

				if (newImagePath != null) {
					newImagePath = imagePathService.getImagePath(storeId,
							imagePath);
				} else {
					throw new CoreServiceException("Failed to add image link.");
				}
			} catch (CoreServiceException e) {
				throw new CoreServiceException("Failed to add image link.");
			}
		}

		BannerRuleItem bannerRuleItem = new BannerRuleItem(rule, null,
				priority, startDT, endDT, imageAlt, linkPath, description,
				newImagePath, disable, openNewWindow);

		return add(bannerRuleItem);
	}

	@Override
	public Integer getTotalRuleItems(String storeId, String ruleId)
			throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		SearchResult<BannerRuleItem> bannerRuleItems = search(search);

		return bannerRuleItems.getTotalCount();
	}

	@Override
	public SearchResult<BannerRuleItem> getRuleItemsByFilter(String storeId,
			String ruleId, String filter, String dateFilter, String imageSize,
			int page, int pageSize) throws CoreServiceException {
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
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_SIZE, StringUtils
				.isNotBlank(imageSize) ? imageSize : null));
		search.addFilter(new Filter(DAOConstants.PARAM_DISABLED, BooleanUtils
				.toIntegerObject(disabled, 1, 0, null)));
		search.addFilter(new Filter(DAOConstants.PARAM_START_DATE,
				JodaDateTimeUtil.toSqlDate(startDate)));
		search.addFilter(new Filter(DAOConstants.PARAM_END_DATE,
				JodaDateTimeUtil.toSqlDate(endDate)));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		return search(search);
	}

	@Override
	public SearchResult<BannerRuleItem> getRuleItemsByImageId(String storeId,
			String imageId, int page, int pageSize) throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID, imageId));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		return search(search);
	}

	@Override
	public SearchResult<BannerRuleItem> getRuleItemsByRuleId(String storeId,
			String ruleId, int page, int pageSize) throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.setPageNumber(page);
		search.setMaxRowCount(pageSize);

		return search(search);
	}

	@Override
	public SearchResult<BannerRuleItem> getAllRuleItems(String storeId,
			String ruleId) throws CoreServiceException {
		return getRuleItemsByRuleId(storeId, ruleId, -1, -1);
	}

	@Override
	public BannerRuleItem getRuleItemByMemberId(String storeId, String ruleId,
			String memberId) throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
		search.addFilter(new Filter(DAOConstants.PARAM_MEMBER_ID, memberId));

		SearchResult<BannerRuleItem> bannerRuleItems = search(search);
		if (bannerRuleItems.getTotalCount() > 0) {
			return (BannerRuleItem) CollectionUtils.get(
					bannerRuleItems.getResult(), 0);
		}

		return null;
	}

	@Override
	public BannerRuleItem updateRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException {
		String username = UtilityService.getUsername();

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
			bannerRule.setRuleName(ruleName);
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
				ImagePath imagePathUpdated = imagePathService
						.updateImagePathAlias(imagePathId, imageAlias);

				if (imagePathUpdated == null) {
					throw new CoreServiceException(
							"Error: updateImagePathAlias()");
				}
			} else {
				// Do not update any image path assoc details
			}

			// Update banner item
			return update(bannerRuleItem);
		}

		return null;
	}

	@Override
	public Boolean deleteRuleItemsByImageSize(String storeId, String ruleId,
			String imageSize) throws CoreServiceException {
		BannerRuleItem bannerRuleItem = new BannerRuleItem(ruleId, storeId);
		bannerRuleItem.setImagePath(new ImagePath(storeId, null, null,
				imageSize, null, null));

		return delete(bannerRuleItem);
	}

	@Override
	public Boolean deleteRuleItemByMemberId(String storeId, String ruleId,
			String memberId, String alias, String imageSize)
			throws CoreServiceException {
		BannerRuleItem bannerRuleItem = new BannerRuleItem(ruleId, storeId,
				memberId);
		bannerRuleItem.setImagePath(new ImagePath(storeId, null, null,
				imageSize, null, alias));

		return delete(bannerRuleItem);
	}

}
