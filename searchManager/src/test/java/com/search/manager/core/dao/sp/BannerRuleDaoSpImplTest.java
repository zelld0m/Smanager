package com.search.manager.core.dao.sp;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

import com.search.manager.BannerRuleTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.BannerRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.dao.sp.DAOConstants;

public class BannerRuleDaoSpImplTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("bannerRuleDaoSp")
	private BannerRuleDao bannerRuleDao;

	@Test
	public void daoWiringTest() {
		assertNotNull(bannerRuleDao);
	}

	@Test
	public void addTest() throws CoreDaoException {
		BannerRule bannerRule = bannerRuleDao.add(BannerRuleTestData
				.getNewBannerRule());

		// Test successful add
		assertNotNull(bannerRule);
		assertNotNull(bannerRule.getRuleId());
		// TODO assertNotNull(bannerRule.getCreatedDate());
	}

	@Test
	@ExpectedException(CoreDaoException.class)
	public void updateTest() throws CoreDaoException {
		BannerRule bannerRule = BannerRuleTestData.getExistingBannerRule();

		// Update fields
		bannerRule.setRuleName("ruleName1234-Updated");

		bannerRule = bannerRuleDao.update(bannerRule);

		// Test successful update
		assertNotNull(bannerRule);
		assertNotNull(bannerRule.getRuleId());
		assertNotNull(bannerRule.getLastModifiedDate());
		Assert.assertEquals("ruleName1234-Updated", bannerRule.getRuleName());

		// Revert field
		bannerRule = bannerRuleDao.update(BannerRuleTestData
				.getExistingBannerRule());
		assertNotNull(bannerRule);
		assertNotNull(bannerRule.getRuleId());
		assertNotNull(bannerRule.getLastModifiedDate());
		Assert.assertEquals(BannerRuleTestData.getExistingBannerRule()
				.getRuleName(), bannerRule.getRuleName());
	}

	@Test
	public void deleteTest() throws CoreDaoException {
		Assert.assertTrue(bannerRuleDao.delete(BannerRuleTestData
				.getNewBannerRule()));
	}

	@Test
	public void searchTest() throws CoreDaoException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID,
				BannerRuleTestData.getExistingBannerRule().getStoreId()));
		SearchResult<BannerRule> bannerRules = bannerRuleDao.search(search);
		assertNotNull(bannerRules);
		if (bannerRules != null) {
			Assert.assertTrue(bannerRules.getTotalCount() > 0);
			for (BannerRule thisBannerRule : bannerRules.getResult()) {
				assertNotNull(thisBannerRule);
			}
		}
	}

	@Test
	public void searchWithFilterTest() throws CoreDaoException {
		BannerRule bannerRule = BannerRuleTestData.getExistingBannerRule();

		Search search = new Search(BannerRule.class);
		// Filters
		List<Filter> filters = new ArrayList<Filter>();
		// banner rule field
		filters.add(new Filter(DAOConstants.PARAM_STORE_ID, bannerRule
				.getStoreId()));
		filters.add(new Filter(DAOConstants.PARAM_RULE_ID, bannerRule
				.getRuleId()));
		filters.add(new Filter(DAOConstants.PARAM_RULE_NAME, bannerRule
				.getRuleName()));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE, 2));
		search.addFilters(filters);
		SearchResult<BannerRule> bannerRules = bannerRuleDao.search(search);
		assertNotNull(bannerRules);
		Assert.assertTrue(bannerRules.getTotalCount() == 1);

		for (BannerRule thisBannerRule : bannerRules.getResult()) {
			assertNotNull(thisBannerRule);
		}

	}
}
