package com.search.manager.core.dao.solr;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Filter.FilterOperator;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public class BannerRuleItemDaoSolrImplTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("bannerRuleItemDaoSolr")
	private BannerRuleItemDao bannerRuleItemDao;

	// Test Data
	private BannerRuleItem bannerRuleItem;
	private BannerRule bannerRule;
	private ImagePath imagePath;
	private DateTime startDate;
	private DateTime endDate;

	private DateTime startDateUpdated = new DateTime();
	private DateTime endDateUpdated = new DateTime();

	@Before
	public void initData() {
		bannerRuleItem = new BannerRuleItem();
		bannerRule = new BannerRule();
		imagePath = new ImagePath();

		startDate = new DateTime();
		endDate = new DateTime();

		bannerRule.setStoreId("pcmall");
		bannerRule.setRuleId("ruleId1234");
		bannerRule.setRuleName("ruleName");

		imagePath.setId("imagePathId1234");
		imagePath.setPath("path");
		imagePath.setSize("size");
		imagePath.setPathType(ImagePathType.IMAGE_LINK);
		imagePath.setAlias("alias");

		bannerRuleItem.setRule(bannerRule);
		bannerRuleItem.setMemberId("memberId1234");
		bannerRuleItem.setPriority(1);
		bannerRuleItem.setStartDate(startDate);
		bannerRuleItem.setEndDate(endDate);
		bannerRuleItem.setImageAlt("imageAlt");
		bannerRuleItem.setLinkPath("linkPath");
		bannerRuleItem.setOpenNewWindow(false);
		bannerRuleItem.setDescription("description");
		bannerRuleItem.setDisabled(false);
		bannerRuleItem.setImagePath(imagePath);
	}

	@Test
	public void daoWiringTest() {
		assertNotNull(bannerRuleItemDao);
	}

	@Test
	public void addTest() throws CoreDaoException {
		BannerRuleItem addedBannerRuleItem = bannerRuleItemDao
				.add(bannerRuleItem);
		assertNotNull(addedBannerRuleItem);
	}

	@Test
	public void updateTest() throws CoreDaoException {
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
		bannerRuleItem.setStartDate(startDateUpdated);
		bannerRuleItem.setEndDate(endDateUpdated);
		bannerRuleItem.setImageAlt("imageAlt - updated");
		bannerRuleItem.setLinkPath("linkPath - updated");
		bannerRuleItem.setOpenNewWindow(true);
		bannerRuleItem.setDescription("description - updated");
		bannerRuleItem.setDisabled(true);
		bannerRuleItem.setImagePath(imagePath);

		// update
		bannerRuleItem = bannerRuleItemDao.update(bannerRuleItem);
		assertNotNull(bannerRuleItem);
	}

	@Test
	public void deleteTest() throws CoreDaoException, InterruptedException {
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
		bannerRuleItemDelete = bannerRuleItemDao.add(bannerRuleItemDelete);
		assertNotNull(bannerRuleItemDelete);

		Thread.sleep(12000);

		// search
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter("ruleId", "ruleId-delete"));
		SearchResult<BannerRuleItem> searchResult = bannerRuleItemDao
				.search(search);
		assertNotNull(searchResult);
		Assert.assertEquals(1, searchResult.getTotalCount());

		Thread.sleep(12000);

		// delete
		// TODO ignore date
		bannerRuleItemDelete.setStartDate(null);
		bannerRuleItemDelete.setEndDate(null);
		boolean status = bannerRuleItemDao.delete(bannerRuleItemDelete);
		Assert.assertTrue(status);

		Thread.sleep(12000);
		// search
		searchResult = bannerRuleItemDao.search(search);
		Assert.assertEquals(0, searchResult.getTotalCount());
	}

	@Test
	public void searchTest() throws CoreDaoException {
		Search search = new Search(BannerRuleItem.class);
		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemDao
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
	public void searchWithFilterTest() throws CoreDaoException {
		Search search = new Search(BannerRuleItem.class);

		bannerRuleItem.setRule(bannerRule);
		// bannerRuleItemUpdated.setMemberId("memberId1234");
		bannerRuleItem.setPriority(2);
		bannerRuleItem.setStartDate(startDateUpdated);
		bannerRuleItem.setEndDate(endDateUpdated);
		bannerRuleItem.setImageAlt("imageAlt - updated");
		bannerRuleItem.setLinkPath("linkPath - updated");
		bannerRuleItem.setOpenNewWindow(true);
		bannerRuleItem.setDescription("description - updated");
		bannerRuleItem.setDisabled(true);
		bannerRuleItem.setImagePath(imagePath);

		// Filters
		List<Filter> filters = new ArrayList<Filter>();
		// banner rule field
		filters.add(new Filter("store", "pcmall"));
		filters.add(new Filter("ruleId", "ruleId1234"));
		filters.add(new Filter("ruleName", "ruleName - updated"));

		// image path field
		filters.add(new Filter("imagePathId", "imagePathId1234 - updated"));
		filters.add(new Filter("path", "path - updated"));
		filters.add(new Filter("size", "size - updated"));
		filters.add(new Filter("pathType", ImagePathType.UPLOAD_LINK));
		filters.add(new Filter("alias", "alias - updated"));

		// banner rule item field
		filters.add(new Filter("memberId", "memberId1234"));
		filters.add(new Filter("priority", 2));
		filters.add(new Filter("startDate", startDateUpdated,
				FilterOperator.LESS_OR_EQUAL));
		filters.add(new Filter("endDate", endDateUpdated,
				FilterOperator.LESS_OR_EQUAL));
		filters.add(new Filter("imageAlt", "imageAlt - updated"));
		filters.add(new Filter("linkPath", "linkPath - updated"));
		filters.add(new Filter("openNewWindow", true));
		filters.add(new Filter("description", "description - updated"));
		filters.add(new Filter("disabled", true));

		search.addFilters(filters);

		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemDao
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
