package com.search.manager.core.statistics.report.test;

import java.util.Collection;
import org.joda.time.DateTime;

import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.report.statistics.util.BannerStatisticsUtil;
import java.util.Arrays;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test class for retrieving a banner statistic.
 *
 * @author Philip Mark Gutierrez
 * @since July 09, 2013
 * @version 0.0.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BannerStatisticsUtil.class)
public class BannerStatisticsTest {

    @SuppressWarnings("unused")
    private static final Logger logger =
            Logger.getLogger(BannerStatisticsTest.class.getName());
    private static final String STORE_ID = "pcmall";

    @Test
    public void testGetStatsPerBannerByKeyword() throws Exception {
        // June 07, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 6, 7, 0, 0);

        // July 31, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 31, 0, 0);

        PowerMock.mockStaticPartial(BannerStatisticsUtil.class,
                "getStatsPerBannerByKeyword");

        EasyMock.expect(BannerStatisticsUtil.getStatsPerBannerByKeyword(
                STORE_ID, "lcd", startDate.toDate(), endDate.toDate())).
                andReturn(Arrays.asList(new BannerStatistics())).anyTimes();
        PowerMock.replay(BannerStatisticsUtil.class);

        Collection<BannerStatistics> statsPerBannerByKeyword = BannerStatisticsUtil
                .getStatsPerBannerByKeyword(STORE_ID, "lcd",
                startDate.toDate(), endDate.toDate());

        Assert.assertFalse(statsPerBannerByKeyword.isEmpty());
        PowerMock.verify(BannerStatisticsUtil.class);
    }

    @Test
    public void testStatsPerKeywordByMemberId() throws Exception {
        // June 07, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 6, 7, 0, 0);

        // July 31, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 31, 0, 0);

        PowerMock.mockStaticPartial(BannerStatisticsUtil.class,
                "getStatsPerKeywordByMemberId");

        // member ID of lcd pink flower
        final String memberId = "0bfxpQFWl0OAyBgoS91K";

        EasyMock.expect(BannerStatisticsUtil
                .getStatsPerKeywordByMemberId(STORE_ID, memberId,
                startDate.toDate(), endDate.toDate())).andReturn(Arrays.asList(
                new BannerStatistics())).anyTimes();
        PowerMock.replay(BannerStatisticsUtil.class);

        Collection<BannerStatistics> statsPerBannerByKeyword = BannerStatisticsUtil
                .getStatsPerKeywordByMemberId(STORE_ID, memberId,
                startDate.toDate(), endDate.toDate());

        Assert.assertFalse(statsPerBannerByKeyword.isEmpty());
        PowerMock.verify(BannerStatisticsUtil.class);
    }
}
