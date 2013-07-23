package com.search.manager.report.statistics.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.report.statistics.util.BannerStatisticsUtil;

/**
 * Test class for retrieving a banner statistic.
 *
 * @author Philip Mark Gutierrez
 * @since July 09, 2013
 * @version 0.0.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BannerStatisticsUtil.class)
public class BannerStatisticsUtilTest {    
    private static final String STORE_ID = "pcmall";

    @Before
    public void initialize() {
        Whitebox.setInternalState(BannerStatisticsUtil.class,
                "FILE_LOCATION",
                "src/test/resources/home/solr/utilities/banner-stats/{0}/{1}/{2}.csv");
        Whitebox.setInternalState(BannerStatisticsUtil.class,
                "FILE_CANNOT_BE_FOUND_MESSAGE", "File {0} cannot be found.");
    }

    @Test
    public void testGetStatsPerBannerByKeyword_isAggregated_Equals_False()
            throws Exception {
        // July 21, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 20, 0, 0);

        // July 21, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 21, 0, 0);
        String keyword = "test123";

        List<BannerStatistics> statsPerBannerByKeyword =
                BannerStatisticsUtil.getStatsPerBannerByKeyword(STORE_ID,
                keyword, startDate.toDate(), endDate.toDate());


        // expected size=2
        assertEquals(statsPerBannerByKeyword.size(), 2);

        BannerStatistics bannerStats1 = statsPerBannerByKeyword.get(0);
        // expected keyword="test123", clicks=2, impressions=0
        assertEquals(bannerStats1.getKeyword(), keyword);
        assertEquals(bannerStats1.getClicks(), 2);
        assertEquals(bannerStats1.getImpressions(), 0);

        BannerStatistics bannerStats2 = statsPerBannerByKeyword.get(1);
        // expected keyword="test123", clicks=4, impressions=2
        assertEquals(bannerStats2.getKeyword(), keyword);
        assertEquals(bannerStats2.getClicks(), 4);
        assertEquals(bannerStats2.getImpressions(), 2);
    }

    @Test
    public void testGetStatsPerBannerByKeyword_isAggregated_Equals_True()
            throws Exception {
        // July 21, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 20, 0, 0);

        // July 21, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 21, 0, 0);
        String keyword = "test123";

        List<BannerStatistics> statsPerBannerByKeyword =
                BannerStatisticsUtil.getStatsPerBannerByKeyword(STORE_ID,
                keyword, startDate.toDate(), endDate.toDate(), true);

        // expected size=1
        assertEquals(statsPerBannerByKeyword.size(), 1);

        BannerStatistics bannerStats1 = statsPerBannerByKeyword.get(0);
        // expected keyword="test123", clicks=6, impressions=2
        assertEquals(bannerStats1.getKeyword(), keyword);
        assertEquals(bannerStats1.getClicks(), 6);
        assertEquals(bannerStats1.getImpressions(), 2);
    }

    @Test
    public void testStatsPerKeywordByMemberId_isAggregated_Equals_False()
            throws Exception {
        // July 21, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 20, 0, 0);

        // July 21, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 21, 0, 0);

        // member ID of cable
        final String memberId = "abc123";

        List<BannerStatistics> statsPerBannerByMemberId =
                BannerStatisticsUtil.getStatsPerKeywordByMemberId(STORE_ID,
                memberId, startDate.toDate(), endDate.toDate());

        // expected size=2
        assertEquals(statsPerBannerByMemberId.size(), 2);

        BannerStatistics bannerStats1 = statsPerBannerByMemberId.get(0);
        // expected keyword="test123", memberId=abc123, clicks=2, impressions=0
        assertEquals(bannerStats1.getKeyword(), "test123");
        assertEquals(bannerStats1.getClicks(), 2);
        assertEquals(bannerStats1.getImpressions(), 0);

        BannerStatistics bannerStats2 = statsPerBannerByMemberId.get(1);
        // expected keyword="test123", memberId=abc123, clicks=4, impressions=2
        assertEquals(bannerStats2.getKeyword(), "test123");
        assertEquals(bannerStats2.getClicks(), 4);
        assertEquals(bannerStats2.getImpressions(), 2);
    }

    @Test
    public void testStatsPerKeywordByMemberId_isAggregated_Equals_True()
            throws Exception {
        // July 21, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 20, 0, 0);

        // July 21, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 21, 0, 0);

        // member ID of cable
        final String memberId = "abc123";

        List<BannerStatistics> statsPerBannerByMemberId =
                BannerStatisticsUtil.getStatsPerKeywordByMemberId(STORE_ID,
                memberId, startDate.toDate(), endDate.toDate(), true);

        // expected size=1
        assertEquals(statsPerBannerByMemberId.size(), 1);

        BannerStatistics bannerStats1 = statsPerBannerByMemberId.get(0);
        // expected keyword="test123", memberId=abc123, clicks=6, impressions=2
        assertEquals(bannerStats1.getKeyword(), "test123");
        assertEquals(bannerStats1.getClicks(), 6);
        assertEquals(bannerStats1.getImpressions(), 2);
    }
}
