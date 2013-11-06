package com.search.manager;

import org.joda.time.DateTime;

import com.search.manager.core.model.BannerRuleItem;

public class BannerRuleItemTestData {

	public static final String DEFAULT_ID = "bannerruleitem1234";

	public static BannerRuleItem getNewBannerRuleItem() {
		BannerRuleItem bannerRuleItem = new BannerRuleItem();

		bannerRuleItem.setRule(BannerRuleTestData.getExistingBannerRule());
		bannerRuleItem.setImagePath(ImagePathTestData.getExistingImagePath());

		bannerRuleItem.setPriority(1);
		bannerRuleItem.setStartDate(new DateTime());
		bannerRuleItem.setEndDate(new DateTime().plusDays(30));
		bannerRuleItem.setImageAlt("imageAlt");
		bannerRuleItem.setLinkPath("linkPath");
		bannerRuleItem.setOpenNewWindow(false);
		bannerRuleItem.setDescription("description");
		bannerRuleItem.setDisabled(false);

		return bannerRuleItem;
	}

	public static BannerRuleItem getExistingBannerRuleItem() {
		BannerRuleItem bannerRuleItem = getNewBannerRuleItem();
		bannerRuleItem.setMemberId(DEFAULT_ID);

		return bannerRuleItem;
	}

}