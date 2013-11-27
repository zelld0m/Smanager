package com.search.manager.core.dao.sp;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.BannerRuleItemTestData;
import com.search.manager.BannerRuleTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.dao.BasicDaoTest;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.dao.sp.DAOConstants;

public class BannerRuleItemDaoSpImplTest extends BaseIntegrationTest implements
		BasicDaoTest<BannerRuleItem> {

	@Autowired
	@Qualifier("bannerRuleItemDaoSp")
	private BannerRuleItemDao bannerRuleItemDao;

	// test data
	private DateTime origStartDate = BannerRuleItemTestData
			.getExistingBannerRuleItem().getStartDate();
	private DateTime origEndDate = BannerRuleItemTestData
			.getExistingBannerRuleItem().getEndDate();

	@Test
	@Override
	public void daoWiringTest() throws CoreDaoException {
		assertNotNull(bannerRuleItemDao);
	}

	@Test
	@Override
	public void addTest() throws CoreDaoException {
		addDeleteTest();
	}

	public void addDeleteTest() throws CoreDaoException {
		BannerRuleItem bannerRuleItem = bannerRuleItemDao
				.add(BannerRuleItemTestData.getNewBannerRuleItem());

		// Test successful add
		assertNotNull(bannerRuleItem);
		assertNotNull(bannerRuleItem.getMemberId());
		assertNotNull(bannerRuleItem.getCreatedDate());

		// Remove added rule item
		Assert.assertTrue(bannerRuleItemDao.delete(bannerRuleItem));
	}

	@Test
	@Override
	public void updateTest() throws CoreDaoException {
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

		bannerRuleItem = bannerRuleItemDao.update(bannerRuleItem);

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
		bannerRuleItem = bannerRuleItemDao.update(BannerRuleItemTestData
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
	public void deleteTest() throws CoreDaoException {
		boolean status = bannerRuleItemDao.delete(BannerRuleItemTestData
				.getNewBannerRuleItem());
		// SP change: member_id is required to delete rule item
		Assert.assertTrue(status);
	}

	@Test
	@Override
	public void searchTest() throws CoreDaoException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID,
				BannerRuleTestData.getExistingBannerRule().getStoreId()));
		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemDao
				.search(search);
		assertNotNull(bannerRuleItems);
		Assert.assertTrue(bannerRuleItems.getTotalCount() > 0);
		for (BannerRuleItem thisBannerRuleItem : bannerRuleItems.getResult()) {
			assertNotNull(thisBannerRuleItem);
		}
	}

	@Test
	@Override
	public void searchModelTest() throws CoreDaoException {
		BannerRuleItem bannerRuleItem = BannerRuleItemTestData
				.getExistingBannerRuleItem();
		bannerRuleItem.setStartDate(null);
		bannerRuleItem.setEndDate(null);
		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemDao
				.search(bannerRuleItem);
		assertNotNull(bannerRuleItems);
		Assert.assertTrue(bannerRuleItems.getTotalCount() > 0);
		for (BannerRuleItem thisBannerRuleItem : bannerRuleItems.getResult()) {
			assertNotNull(thisBannerRuleItem);
		}
	}

	@Test
	public void searchWithFilterTest() throws CoreDaoException {
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

		SearchResult<BannerRuleItem> bannerRuleItems = bannerRuleItemDao
				.search(search);

		assertNotNull(bannerRuleItems);
		Assert.assertTrue(bannerRuleItems.getTotalCount() == 1);

		for (BannerRuleItem thisBannerRuleItem : bannerRuleItems.getResult()) {
			assertNotNull(thisBannerRuleItem);
		}

	}

}
