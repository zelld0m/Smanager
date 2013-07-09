package com.search.manager.core.statistics.report.test;

import java.util.Collection;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.report.statistics.util.BannerStatisticsUtil;

/**
 * Test class for retrieving a banner statistic.
 * 
 * @author Philip Mark Gutierrez
 * @since July 09, 2013
 * @version 0.0.1
 */
public class BannerStatisticsTest {
	@SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory
	        .getLogger(BannerStatisticsTest.class);
	private static final String STORE_ID = "pcmall";

	@Test
	public void testGetStatsPerBannerByKeyword() throws Exception {
		// June 07, 2013 00:00:00
		DateTime startDate = new DateTime(2013, 6, 07, 0, 0);

		// July 31, 2013 00:00:00
		DateTime endDate = new DateTime(2013, 7, 31, 0, 0);

		Collection<BannerStatistics> statsPerBannerByKeyword = BannerStatisticsUtil
		        .getStatsPerBannerByKeyword(STORE_ID, "lcd",
		                startDate.toDate(), endDate.toDate());
		
		Assert.assertFalse(statsPerBannerByKeyword.isEmpty());
//		for (BannerStatistics bannerStatistics : statsPerBannerByKeyword) {
//			logger.info(bannerStatistics.toString());
//		}
	}

	@Test
	public void testStatsPerKeywordByMemberId() throws Exception {
		// June 07, 2013 00:00:00
		DateTime startDate = new DateTime(2013, 6, 07, 0, 0);

		// July 31, 2013 00:00:00
		DateTime endDate = new DateTime(2013, 7, 31, 0, 0);

		// member ID of lcd pink flower
		final String memberId = "0bfxpQFWl0OAyBgoS91K";

		Collection<BannerStatistics> statsPerBannerByKeyword = BannerStatisticsUtil
		        .getStatsPerKeywordByMemberId(STORE_ID, memberId,
		                startDate.toDate(), endDate.toDate());
		
		Assert.assertFalse(statsPerBannerByKeyword.isEmpty());
//		for (BannerStatistics bannerStatistics : statsPerBannerByKeyword) {
//			logger.info(bannerStatistics.toString());
//		}
	}
}
