package com.search.manager.core.service.sp;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.BannerRuleItemTestData;
import com.search.manager.BannerRuleTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.response.ServiceResponse;

public class BannerRuleItemServiceSpImplTest extends BaseIntegrationTest {

	private static final Logger logger = LoggerFactory
			.getLogger(BannerRuleItemServiceSpImplTest.class);

	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;

	// test data
	private DateTime origStartDate = BannerRuleItemTestData
			.getExistingBannerRuleItem().getStartDate();
	private DateTime origEndDate = BannerRuleItemTestData
			.getExistingBannerRuleItem().getEndDate();

	@Test
	public void testWiring() {
		assertNotNull(bannerRuleItemService);
	}

	/* Basic Test */

	@Test
	public void addDeleteTest() throws CoreServiceException {
		BannerRuleItem bannerRuleItem = bannerRuleItemService
				.add(BannerRuleItemTestData.getNewBannerRuleItem());

		// Test successful add
		assertNotNull(bannerRuleItem);
		assertNotNull(bannerRuleItem.getMemberId());
		assertNotNull(bannerRuleItem.getCreatedDate());

		// Remove added rule item
		Assert.assertTrue(bannerRuleItemService.delete(bannerRuleItem));
	}

	@Test
	public void updateTest() throws CoreServiceException {
		BannerRuleItem bannerRuleItem = BannerRuleItemTestData
				.getExistingBannerRuleItem();

		// Update fields
		bannerRuleItem.setPriority(2);
		bannerRuleItem.setStartDate(new DateTime().plus(1));
		bannerRuleItem.setEndDate(new DateTime().plus(15));
		bannerRuleItem.setImageAlt("imageAlt - updated");
		bannerRuleItem.setLinkPath("linkPath - updated");
		bannerRuleItem.setOpenNewWindow(true);
		bannerRuleItem.setDescription("description - updated");
		bannerRuleItem.setDisabled(true);

		bannerRuleItem = bannerRuleItemService.update(bannerRuleItem);

		// Test successful update
		assertNotNull(bannerRuleItem);
		assertNotNull(bannerRuleItem.getLastModifiedDate());

		// priority logic in SP level (Ignore Test)
		// Assert.assertTrue(2 == bannerRuleItem.getPriority());
		Assert.assertFalse(origStartDate.isEqual(bannerRuleItem.getStartDate()));
		Assert.assertFalse(origEndDate.isEqual(bannerRuleItem.getEndDate()
				.plus(30)));
		Assert.assertEquals("imageAlt - updated", bannerRuleItem.getImageAlt());
		Assert.assertEquals("linkPath - updated", bannerRuleItem.getLinkPath());
		Assert.assertTrue(true == bannerRuleItem.getOpenNewWindow());
		Assert.assertEquals("description - updated",
				bannerRuleItem.getDescription());
		Assert.assertTrue(true == bannerRuleItem.getDisabled());

		// Revert
		bannerRuleItem = bannerRuleItemService.update(BannerRuleItemTestData
				.getExistingBannerRuleItem());

		// Test successful revert
		assertNotNull(bannerRuleItem);
		assertNotNull(bannerRuleItem.getLastModifiedDate());

		Assert.assertTrue(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getPriority() == bannerRuleItem.getPriority());
		Assert.assertEquals(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getImageAlt(), bannerRuleItem.getImageAlt());
		Assert.assertEquals(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getLinkPath(), bannerRuleItem.getLinkPath());
		Assert.assertTrue(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getOpenNewWindow() == bannerRuleItem.getOpenNewWindow());
		Assert.assertEquals(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getDescription(), bannerRuleItem.getDescription());
		Assert.assertTrue(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getDisabled() == bannerRuleItem.getDisabled());
	}

	@Ignore
	@Test
	public void deleteTest() throws CoreServiceException {
		boolean status = bannerRuleItemService.delete(BannerRuleItemTestData
				.getNewBannerRuleItem());
		// SP change: member_id is required to delete rule item
		Assert.assertTrue(status);
	}

	@Test
	public void searchTest() throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID,
				BannerRuleTestData.getExistingBannerRule().getStoreId()));
		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemService
				.search(search);
		assertNotNull(bannerRuleItems);
		if (bannerRuleItems != null) {
			Assert.assertTrue(bannerRuleItems.getTotalCount() > 0);
			for (BannerRuleItem thisBannerRuleItem : bannerRuleItems
					.getResult()) {
				assertNotNull(thisBannerRuleItem);
			}
		}
	}

	@Test
	public void searchWithFilterTest() throws CoreServiceException {
		BannerRuleItem bannerRuleItem = BannerRuleItemTestData
				.getExistingBannerRuleItem();

		Search search = new Search(BannerRule.class);
		// Filters
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter(DAOConstants.PARAM_RULE_ID, bannerRuleItem
				.getRule().getRuleId()));
		filters.add(new Filter(DAOConstants.PARAM_RULE_NAME, bannerRuleItem
				.getRule().getRuleName()));
		filters.add(new Filter(DAOConstants.PARAM_STORE_ID, bannerRuleItem
				.getRule().getStoreId()));
		filters.add(new Filter(DAOConstants.PARAM_MEMBER_ID, bannerRuleItem
				.getMemberId()));
		// filters.add(new Filter(DAOConstants.PARAM_START_DATE,
		// bannerRuleItem.getStartDate()));
		// filters.add(new Filter(DAOConstants.PARAM_END_DATE,
		// bannerRuleItem.getEndDate()));
		// filters.add(new Filter(DAOConstants.PARAM_DISABLED,
		// bannerRuleItem.getDisabled()));
		filters.add(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID, bannerRuleItem
				.getImagePath().getId()));
		filters.add(new Filter(DAOConstants.PARAM_IMAGE_SIZE, bannerRuleItem
				.getImagePath().getSize()));
		search.addFilters(filters);

		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemService
				.search(search);

		assertNotNull(bannerRuleItems);
		Assert.assertTrue(bannerRuleItems.getTotalCount() == 1);

		for (BannerRuleItem thisBannerRuleItem : bannerRuleItems.getResult()) {
			assertNotNull(thisBannerRuleItem);
		}

	}

	/* BannerRuleItemService Specific Method Test */

	@Ignore
	@Test
	public void addRuleItemTest() throws CoreServiceException {
		// TODO mock UtilityService ->
		// RequestContextHolder.currentRequestAttributes()
		Map<String, String> params = new HashMap<String, String>();

		params.put("ruleId", BannerRuleItemTestData.getNewBannerRuleItem()
				.getRule().getRuleId());
		params.put("ruleName", BannerRuleItemTestData.getNewBannerRuleItem()
				.getRule().getRuleName());
		params.put("priority", BannerRuleItemTestData.getNewBannerRuleItem()
				.getPriority() + "");
		params.put("startDate", BannerRuleItemTestData.getNewBannerRuleItem()
				.getStartDate().toString());
		params.put("endDate", BannerRuleItemTestData.getNewBannerRuleItem()
				.getEndDate().toString());
		params.put("imageAlt", BannerRuleItemTestData.getNewBannerRuleItem()
				.getImageAlt());
		params.put("linkPath", BannerRuleItemTestData.getNewBannerRuleItem()
				.getLinkPath());
		params.put("description", BannerRuleItemTestData.getNewBannerRuleItem()
				.getDescription());
		params.put("imagePathId", BannerRuleItemTestData.getNewBannerRuleItem()
				.getImagePath().getId());
		params.put("imagePath", BannerRuleItemTestData.getNewBannerRuleItem()
				.getImagePath().getPath());
		params.put("imageSize", BannerRuleItemTestData.getNewBannerRuleItem()
				.getImagePath().getSize());
		params.put("imageAlias", BannerRuleItemTestData.getNewBannerRuleItem()
				.getImagePath().getAlias());
		params.put("disable", BannerRuleItemTestData.getNewBannerRuleItem()
				.getDisabled() + "");
		params.put("openNewWindow", BannerRuleItemTestData
				.getNewBannerRuleItem().getOpenNewWindow() + "");

		ServiceResponse<BannerRuleItem> serviceResponse = bannerRuleItemService
				.addRuleItem(BannerRuleItemTestData.getNewBannerRuleItem()
						.getRule().getStoreId(), params);
		assertNotNull(serviceResponse);

		// Test successful add
		assertNotNull(serviceResponse.getData());
		assertNotNull(serviceResponse.getData().getMemberId());
		assertNotNull(serviceResponse.getData().getCreatedDate());

		// Remove added rule item
		Assert.assertTrue(bannerRuleItemService.delete(serviceResponse
				.getData()));
	}

	@Test
	public void getTotalRuleItemsTest() throws CoreServiceException {
		ServiceResponse<Integer> serviceResponse = bannerRuleItemService
				.getTotalRuleItems(BannerRuleItemTestData
						.getExistingBannerRuleItem().getRule().getStoreId(),
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getRule().getRuleId());
		assertNotNull(serviceResponse);
		Assert.assertTrue(serviceResponse.getData() > 0);
	}

	@Ignore
	@Test
	public void getRuleItemsByFilterTest() throws CoreServiceException {
		// TODO date filter: startDate and endDate
		// filter: active, expired, disabled and date
		String filter = "active";
		String dateFilter = "";
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = bannerRuleItemService
				.getRuleItemsByFilter(BannerRuleItemTestData
						.getExistingBannerRuleItem().getRule().getStoreId(),
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getRule().getRuleId(), filter, dateFilter,
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getImagePath().getSize(), 1, 10);
		assertNotNull(serviceResponse);
		assertNotNull(serviceResponse.getData());
		Assert.assertTrue(serviceResponse.getData().getTotalCount() > 0);
	}

	@Test
	public void getRuleItemsByImageIdTest() throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = bannerRuleItemService
				.getRuleItemsByImageId(BannerRuleItemTestData
						.getExistingBannerRuleItem().getRule().getStoreId(),
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getImagePath().getId(), 1, 10);
		assertNotNull(serviceResponse);
		assertNotNull(serviceResponse.getData());
		Assert.assertTrue(serviceResponse.getData().getTotalCount() > 0);
	}

	@Test
	public void getRuleItemsByRuleIdTest() throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = bannerRuleItemService
				.getRuleItemsByRuleId(BannerRuleItemTestData
						.getExistingBannerRuleItem().getRule().getStoreId(),
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getRule().getRuleId(), 1, 10);
		assertNotNull(serviceResponse);
		for (BannerRuleItem bannerRuleItem : serviceResponse.getData()
				.getResult()) {
			assertNotNull(bannerRuleItem);
			logger.info(bannerRuleItem.toJson());
		}
	}

	@Test
	public void getAllRuleItemsTest() throws CoreServiceException {
		ServiceResponse<SearchResult<BannerRuleItem>> serviceResponse = bannerRuleItemService
				.getAllRuleItems(BannerRuleItemTestData
						.getExistingBannerRuleItem().getRule().getStoreId(),
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getRule().getRuleId());
		assertNotNull(serviceResponse);
		for (BannerRuleItem bannerRuleItem : serviceResponse.getData()
				.getResult()) {
			assertNotNull(bannerRuleItem);
			logger.info(bannerRuleItem.toJson());
		}
	}

	@Test
	public void getRuleItemByMemberIdTest() throws CoreServiceException {
		ServiceResponse<BannerRuleItem> serviceResponse = bannerRuleItemService
				.getRuleItemByMemberId(BannerRuleItemTestData
						.getExistingBannerRuleItem().getRule().getStoreId(),
						BannerRuleItemTestData.getExistingBannerRuleItem()
								.getRule().getRuleId(), BannerRuleItemTestData
								.getExistingBannerRuleItem().getMemberId());
		assertNotNull(serviceResponse);
		assertNotNull(serviceResponse.getData());
		Assert.assertEquals(BannerRuleItemTestData.getExistingBannerRuleItem()
				.getMemberId(), serviceResponse.getData().getMemberId());
		logger.info(serviceResponse.getData().toJson());
	}

	public void updateRuleItemTest() throws CoreServiceException {
		// TODO
		// bannerRuleItemService.updateRuleItem(storeId, params);
	}

	@Ignore
	@Test
	public void deleteRuleItemsByImageSizeTest() throws CoreServiceException {
		// TODO check method behavior
		// Add new bannerRuleItem
		BannerRuleItem bannerRuleItem = BannerRuleItemTestData
				.getNewBannerRuleItem();
		bannerRuleItem = bannerRuleItemService.add(bannerRuleItem);

		// Test successfully added.
		assertNotNull(bannerRuleItem);
		assertNotNull(bannerRuleItem.getMemberId());
		assertNotNull(bannerRuleItem.getCreatedDate());

		// Test delete by image size.
		ServiceResponse<Boolean> serviceResponse = bannerRuleItemService
				.deleteRuleItemsByImageSize(bannerRuleItem.getRule()
						.getStoreId(), bannerRuleItem.getRule().getRuleId(),
						bannerRuleItem.getImagePath().getSize());
		assertNotNull(serviceResponse);
		assertNotNull(serviceResponse.getData());
		Assert.assertTrue(serviceResponse.getData());
	}

	@Ignore
	@Test
	public void deleteRuleItemByMemberIdTest() throws CoreServiceException {
		// TODO
		// Add new bannerRuleItem
		BannerRuleItem bannerRuleItem = BannerRuleItemTestData
				.getNewBannerRuleItem();
		bannerRuleItem = bannerRuleItemService.add(bannerRuleItem);

		// Test successfully added.
		assertNotNull(bannerRuleItem);
		assertNotNull(bannerRuleItem.getMemberId());
		assertNotNull(bannerRuleItem.getCreatedDate());

		// Delete
		ServiceResponse<Boolean> serviceResponse = bannerRuleItemService
				.deleteRuleItemByMemberId(
						bannerRuleItem.getRule().getStoreId(), bannerRuleItem
								.getRule().getRuleId(), bannerRuleItem
								.getMemberId(), bannerRuleItem.getImagePath()
								.getAlias(), bannerRuleItem.getImagePath()
								.getSize());

		// Test successful delete
		assertNotNull(serviceResponse);
		assertNotNull(serviceResponse.getData());
		Assert.assertTrue(serviceResponse.getData());
	}
	
}
