package com.search.manager.core.statistics.report.test;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;

import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.report.statistics.util.BannerStatisticsUtil;
import com.search.manager.report.statistics.util.PropertiesUtils;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 * Test class for retrieving a banner statistic.
 *
 * @author Philip Mark Gutierrez
 * @since July 09, 2013
 * @version 0.0.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BannerStatisticsUtil.class)
@SuppressStaticInitializationFor(
    "com.search.manager.report.statistics.util.BannerStatisticsUtil")
public class BannerStatisticsTest {
    private static final String STORE_ID = "pcmall";

    @Before
    public void initialize() {
        Whitebox.setInternalState(BannerStatisticsUtil.class,
                "FILE_LOCATION",
                "src/test/resources/home/solr/utilities/banner-stats/{0}/{1}/{2}.csv");
        Whitebox.setInternalState(BannerStatisticsUtil.class,
                "FILE_CANNOT_BE_FOUND_MESSAGE",
                PropertiesUtils.getString("File {0} cannot be found."));
    }

    @Test
    public void testGetStatsPerBannerByKeyword() throws Exception {
        // July 16, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 16, 0, 0);

        // July 17, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 17, 0, 0);
        String keyword = "coy2";

        List<BannerStatistics> statsPerBannerByKeyword =
                BannerStatisticsUtil.getStatsPerBannerByKeyword(STORE_ID, 
                keyword, startDate.toDate(), endDate.toDate());

        // expected size=4
        assertEquals(statsPerBannerByKeyword.size(), 4);
        
        BannerStatistics bannerStats1 = statsPerBannerByKeyword.get(0);
        // expected keyword="coy2", clicks=1, impressions=1
        assertEquals(bannerStats1.getKeyword(), keyword);
        assertEquals(bannerStats1.getClicks(), 1);
        assertEquals(bannerStats1.getImpressions(), 1);
        
        BannerStatistics bannerStats2 = statsPerBannerByKeyword.get(1);
        // expected keyword="coy2", clicks=1, impressions=1
        assertEquals(bannerStats2.getKeyword(), keyword);
        assertEquals(bannerStats2.getClicks(), 1);
        assertEquals(bannerStats2.getImpressions(), 1);
        
        BannerStatistics bannerStats3 = statsPerBannerByKeyword.get(2);
        // expected keyword="coy2", clicks=0, impressions=1
        assertEquals(bannerStats3.getKeyword(), keyword);
        assertEquals(bannerStats3.getClicks(), 0);
        assertEquals(bannerStats3.getImpressions(), 1);
        
        BannerStatistics bannerStats4 = statsPerBannerByKeyword.get(3);
        // expected keyword="coy2", clicks=0, impressions=1
        assertEquals(bannerStats4.getKeyword(), keyword);
        assertEquals(bannerStats4.getClicks(), 1);
        assertEquals(bannerStats4.getImpressions(), 1);
    }

    @Test
    public void testStatsPerKeywordByMemberId() throws Exception {
        // July 16, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 16, 0, 0);

        // July 17, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 17, 0, 0);

        // member ID of cable
        final String memberId = "0064dYW1P0OBpaRiyRLD";

        List<BannerStatistics> statsPerBannerByMemberId =
                BannerStatisticsUtil.getStatsPerKeywordByMemberId(STORE_ID,
                memberId, startDate.toDate(), endDate.toDate());
        
        // expected size=1
        assertEquals(statsPerBannerByMemberId.size(), 1);
        
        BannerStatistics bannerStats = statsPerBannerByMemberId.get(0);

        // expected keyword="cable", memberId = "0064dYW1P0OBpaRiyRLD", 
        // clicks=1, impressions=1
        assertEquals(bannerStats.getKeyword(), "cable");
        assertEquals(bannerStats.getMemberId(),
                memberId);
        assertEquals(bannerStats.getClicks(), 1);
        assertEquals(bannerStats.getImpressions(), 1);
    }
}
