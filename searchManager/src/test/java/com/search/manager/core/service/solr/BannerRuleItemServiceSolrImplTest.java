package com.search.manager.core.service.solr;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.BannerRuleItemTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Filter.FilterOperator;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;

public class BannerRuleItemServiceSolrImplTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("bannerRuleItemServiceSolr")
	private BannerRuleItemService bannerRuleItemService;

	// test data
	private DateTime origStartDate = BannerRuleItemTestData
			.getExistingBannerRuleItem().getStartDate();
	private DateTime origEndDate = BannerRuleItemTestData
			.getExistingBannerRuleItem().getEndDate();

	@Test
	public void daoWiringTest() {
		assertNotNull(bannerRuleItemService);
	}

	@Test
	public void addTest() throws CoreServiceException {
		BannerRuleItem addedBannerRuleItem = bannerRuleItemService
				.add(BannerRuleItemTestData.getExistingBannerRuleItem());
		assertNotNull(addedBannerRuleItem);
	}

	@Test
	public void updateTest() throws CoreServiceException, InterruptedException {
		BannerRuleItem bannerRuleItem = BannerRuleItemTestData
				.getExistingBannerRuleItem();
		BannerRule bannerRule = bannerRuleItem.getRule();
		ImagePath imagePath = bannerRuleItem.getImagePath();

		// Update fields
		// bannerRule.setStoreId("pcmall");
		// bannerRule.setRuleId("ruleId1234");
		bannerRule.setRuleName("ruleName - updated");

		imagePath.setId("imagePathId1234 - updated");
		imagePath.setPath("path - updated");
		imagePath.setSize("size - updated");
		imagePath.setPathType(ImagePathType.UPLOAD_LINK);
		imagePath.setAlias("alias - updated");

		bannerRuleItem.setRule(bannerRule);
		// bannerRuleItemUpdated.setMemberId("memberId1234");
		bannerRuleItem.setPriority(2);
		bannerRuleItem.setStartDate(new DateTime().plus(1));
		bannerRuleItem.setEndDate(new DateTime().plus(15));
		bannerRuleItem.setImageAlt("imageAlt - updated");
		bannerRuleItem.setLinkPath("linkPath - updated");
		bannerRuleItem.setOpenNewWindow(true);
		bannerRuleItem.setDescription("description - updated");
		bannerRuleItem.setDisabled(true);
		bannerRuleItem.setImagePath(imagePath);

		bannerRuleItem = bannerRuleItemService.update(bannerRuleItem);

		Thread.sleep(12000);

		// Test successful update
		assertNotNull(bannerRuleItem);
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
	}

	@Test
	public void deleteTest() throws CoreServiceException, InterruptedException {
		BannerRule bannerRule = new BannerRule();
		bannerRule = new BannerRule();
		bannerRule.setStoreId("pcmall");
		bannerRule.setRuleId("ruleId-delete");
		bannerRule.setRuleName("ruleName-delete");

		ImagePath imagePath = new ImagePath();
		imagePath.setId("imagePathId-delete");
		imagePath.setPath("path-delete");
		imagePath.setSize("size-delete");
		imagePath.setPathType(ImagePathType.IMAGE_LINK);
		imagePath.setAlias("alias-delete");

		BannerRuleItem bannerRuleItemDelete = new BannerRuleItem();
		bannerRuleItemDelete.setRule(bannerRule);
		bannerRuleItemDelete.setMemberId("memberId-delete");
		bannerRuleItemDelete.setPriority(1);
		bannerRuleItemDelete.setStartDate(new DateTime());
		bannerRuleItemDelete.setEndDate(new DateTime());
		bannerRuleItemDelete.setImageAlt("imageAlt-delete");
		bannerRuleItemDelete.setLinkPath("linkPath-delete");
		bannerRuleItemDelete.setOpenNewWindow(false);
		bannerRuleItemDelete.setDescription("description-delete");
		bannerRuleItemDelete.setDisabled(false);
		bannerRuleItemDelete.setImagePath(imagePath);

		// add
		bannerRuleItemDelete = bannerRuleItemService.add(bannerRuleItemDelete);
		assertNotNull(bannerRuleItemDelete);

		Thread.sleep(12000);

		// search
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter("ruleId", "ruleId-delete"));
		SearchResult<BannerRuleItem> searchResult = bannerRuleItemService
				.search(search);
		assertNotNull(searchResult);
		Assert.assertEquals(1, searchResult.getTotalCount());

		Thread.sleep(12000);

		// delete
		// TODO ignore date
		bannerRuleItemDelete.setStartDate(null);
		bannerRuleItemDelete.setEndDate(null);
		boolean status = bannerRuleItemService.delete(bannerRuleItemDelete);
		Assert.assertTrue(status);

		Thread.sleep(12000);
		// search
		searchResult = bannerRuleItemService.search(search);
		Assert.assertEquals(0, searchResult.getTotalCount());
	}

	@Test
	public void searchTest() throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
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
		Search search = new Search(BannerRuleItem.class);

		// Filters
		List<Filter> filters = new ArrayList<Filter>();

		// banner rule field
		filters.add(new Filter("store", BannerRuleItemTestData
				.getExistingBannerRuleItem().getRule().getStoreId()));
		filters.add(new Filter("ruleId", BannerRuleItemTestData
				.getExistingBannerRuleItem().getRule().getRuleId()));
		filters.add(new Filter("ruleName", BannerRuleItemTestData
				.getExistingBannerRuleItem().getRule().getRuleName()));

		// image path field
		filters.add(new Filter("imagePathId", BannerRuleItemTestData
				.getExistingBannerRuleItem().getImagePath().getId()));
		filters.add(new Filter("path", BannerRuleItemTestData
				.getExistingBannerRuleItem().getImagePath().getPath()));
		filters.add(new Filter("size", BannerRuleItemTestData
				.getExistingBannerRuleItem().getImagePath().getSize()));
		filters.add(new Filter("pathType", BannerRuleItemTestData
				.getExistingBannerRuleItem().getImagePath().getPathType()));
		filters.add(new Filter("alias", BannerRuleItemTestData
				.getExistingBannerRuleItem().getImagePath().getAlias()));

		// banner rule item field
		filters.add(new Filter("memberId", BannerRuleItemTestData
				.getExistingBannerRuleItem().getMemberId()));
		filters.add(new Filter("priority", BannerRuleItemTestData
				.getExistingBannerRuleItem().getPriority()));
		filters.add(new Filter("startDate", BannerRuleItemTestData
				.getExistingBannerRuleItem().getStartDate(),
				FilterOperator.LESS_OR_EQUAL));
		filters.add(new Filter("endDate", BannerRuleItemTestData
				.getExistingBannerRuleItem().getEndDate(),
				FilterOperator.LESS_OR_EQUAL));
		filters.add(new Filter("imageAlt", BannerRuleItemTestData
				.getExistingBannerRuleItem().getImageAlt()));
		filters.add(new Filter("linkPath", BannerRuleItemTestData
				.getExistingBannerRuleItem().getLinkPath()));
		filters.add(new Filter("openNewWindow", BannerRuleItemTestData
				.getExistingBannerRuleItem().getOpenNewWindow()));
		filters.add(new Filter("description", BannerRuleItemTestData
				.getExistingBannerRuleItem().getDescription()));
		filters.add(new Filter("disabled", BannerRuleItemTestData
				.getExistingBannerRuleItem().getDisabled()));

		search.addFilters(filters);

		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemService
				.search(search);
		assertNotNull(bannerRuleItems);
		if (bannerRuleItems != null) {
			Assert.assertTrue(bannerRuleItems.getTotalCount() == 1);
			for (BannerRuleItem thisBannerRuleItem : bannerRuleItems
					.getResult()) {
				assertNotNull(thisBannerRuleItem);
			}
		}
	}
}
