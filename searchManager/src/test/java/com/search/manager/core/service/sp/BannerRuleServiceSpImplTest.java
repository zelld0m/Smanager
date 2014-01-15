package com.search.manager.core.service.sp;

import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.BannerRuleTestData;
import com.search.manager.ImagePathTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.service.BannerRuleService;

public class BannerRuleServiceSpImplTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("bannerRuleServiceSp")
	private BannerRuleService bannerRuleService;

	@Test
	public void testWiring() {
		assertNotNull(bannerRuleService);
	}

	/* Basic Test */

	/* BannerRuleService Specific Method Test */

	@Ignore
	@Test
	public void addRuleTest() throws CoreServiceException {
		// TODO
	}

	@Ignore
	@Test
	public void getAllRulesTest() throws CoreServiceException {
		// TODO
	}

	@Ignore
	@Test
	public void getRulesByImageIdTest() throws CoreServiceException {
		// TODO
		// bannerRuleService.getRulesByImageId(storeId, imagePathId, imageAlias,
		// page, pageSize);
	}

	@Test
	public void getRuleByNameTest() throws CoreServiceException {
		BannerRule bannerRule = bannerRuleService.getRuleByName(
				BannerRuleTestData.getExistingBannerRule().getStoreId(),
				BannerRuleTestData.getExistingBannerRule().getRuleName());
		assertNotNull(bannerRule);
		Assert.assertEquals(BannerRuleTestData.getExistingBannerRule()
				.getRuleName(), bannerRule.getRuleName());
	}

	@Test
	public void getRuleByIdTest() throws CoreServiceException {
		BannerRule bannerRule = bannerRuleService.getRuleById(
				BannerRuleTestData.getExistingBannerRule().getStoreId(),
				BannerRuleTestData.getExistingBannerRule().getRuleId());
		assertNotNull(bannerRule);
		Assert.assertEquals(BannerRuleTestData.getExistingBannerRule()
				.getRuleId(), bannerRule.getRuleId());
	}

	@Test
	public void getTotalRulesByImageIdTest() throws CoreServiceException {
		// TODO
		Integer totalCount = bannerRuleService.getTotalRulesByImageId(
				BannerRuleTestData.getExistingBannerRule().getRuleId(),
				ImagePathTestData.getExistingImagePath().getId(),
				ImagePathTestData.getExistingImagePath().getAlias());

		Assert.assertTrue(totalCount > 0);
	}

	@Ignore
	@Test
	public void copyToRuleTest() throws CoreServiceException {
		// TODO
	}

}
