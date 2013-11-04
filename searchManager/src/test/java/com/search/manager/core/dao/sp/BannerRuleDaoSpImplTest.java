package com.search.manager.core.dao.sp;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

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

	// Test Data
	private BannerRule bannerRule;

	@Before
	public void initData() {
		bannerRule = new BannerRule();

		bannerRule.setStoreId("ecost");
		bannerRule.setRuleName("ruleName1234");
	}

	@Test
	public void daoWiringTest() {
		assertNotNull(bannerRuleDao);
	}

	@Test
	public void addTest() throws CoreDaoException {
		BannerRule addedBannerRule = bannerRuleDao.add(bannerRule);
		assertNotNull(addedBannerRule);
	}

	@Test
	@ExpectedException(CoreDaoException.class)
	public void updateTest() throws CoreDaoException {
		bannerRule.setRuleName("ruleName1234-Updated");
		bannerRuleDao.update(bannerRule);
	}
	
	@Test
	public void deleteTest() throws CoreDaoException {
		boolean status = bannerRuleDao.delete(bannerRule);
		Assert.assertTrue(status);
	}

	@Test
	public void searchTest() throws CoreDaoException {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, bannerRule
				.getStoreId()));
		SearchResult<BannerRule> bannerRules = bannerRuleDao.search(search);
		assertNotNull(bannerRules);
		if (bannerRules != null) {
			Assert.assertTrue(bannerRules.getTotalCount() > 0);
			for (BannerRule thisBannerRuleItem : bannerRules.getResult()) {
				assertNotNull(thisBannerRuleItem);
			}
		}
	}

	@Test
	public void searchWithFilterTest() throws CoreDaoException {
		Search search = new Search(BannerRule.class);
		// Filters
		List<Filter> filters = new ArrayList<Filter>();
		// banner rule field
		filters.add(new Filter("store", bannerRule.getStoreId()));
		filters.add(new Filter("ruleName", bannerRule.getRuleName()));
		search.addFilters(filters);
		SearchResult<BannerRule> bannerRules = bannerRuleDao.search(search);
		assertNotNull(bannerRules);
		if (bannerRules != null) {
			Assert.assertTrue(bannerRules.getTotalCount() == 1);
			for (BannerRule thisBannerRuleItem : bannerRules.getResult()) {
				assertNotNull(thisBannerRuleItem);
			}
		}
	}
}
