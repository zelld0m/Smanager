package com.search.manager.core.statistics.report.test;

import java.util.Collection;
import org.joda.time.DateTime;

import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.report.statistics.util.BannerStatisticsUtil;
import com.search.manager.report.statistics.util.PropertiesUtils;
import java.util.List;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
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
@PrepareForTest({PropertiesUtils.class, BannerStatisticsUtil.class})
@SuppressStaticInitializationFor(
        {"com.search.manager.report.statistics.util.PropertiesUtils",
    "com.search.manager.report.statistics.util.BannerStatisticsUtil"})
public class BannerStatisticsTest {

    @SuppressWarnings("unused")
    private static final Logger logger =
            Logger.getLogger(BannerStatisticsTest.class.getName());
    private static final String STORE_ID = "pcmall";

    @Test
    public void testGetStatsPerBannerByKeyword() throws Exception {
        // do the mocking
        bannerStatisticsTestHelper();

        // July 16, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 16, 0, 0);

        // July 17, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 17, 0, 0);

        Collection<BannerStatistics> statsPerBannerByKeyword =
                BannerStatisticsUtil.getStatsPerBannerByKeyword(STORE_ID,
                "coy2", startDate.toDate(), endDate.toDate());
        
        Assert.assertEquals(statsPerBannerByKeyword.size(), 4);
        PowerMock.verify(PropertiesUtils.class, BannerStatisticsUtil.class);
    }

    @Test
    public void testStatsPerKeywordByMemberId() throws Exception {
        // do the mocking
        bannerStatisticsTestHelper();

        // July 16, 2013 00:00:00
        DateTime startDate = new DateTime(2013, 7, 16, 0, 0);

        // July 17, 2013 00:00:00
        DateTime endDate = new DateTime(2013, 7, 17, 0, 0);

        // member ID of cable
        final String memberId = "0064dYW1P0OBpaRiyRLD";

        List<BannerStatistics> statsPerBannerByMemberId =
                BannerStatisticsUtil.getStatsPerKeywordByMemberId(STORE_ID,
                memberId, startDate.toDate(), endDate.toDate());
        
        Assert.assertEquals(statsPerBannerByMemberId.get(0).getMemberId(), 
                memberId);
        PowerMock.verify(BannerStatisticsUtil.class);
    }

    /**
     * Helper method for mocking PropertiesUtils and BannerStatisticsUtil
     */
    private void bannerStatisticsTestHelper() {
        // mock the properties utils
        mockPropertiesUtilsHelper();

        // replay
        PowerMock.replay(PropertiesUtils.class, BannerStatisticsUtil.class);

        // helper method for mocking banner statisticsUtil
        mockBannerStatisticsUtilHelper();
    }

    /**
     * Helper method for mocking PropertiesUtils
     */
    private void mockPropertiesUtilsHelper() {
        PowerMock.mockStaticPartial(PropertiesUtils.class,
                "getString");

        EasyMock.expect(PropertiesUtils.getString("fileLocation")).
                andReturn(
                "src/test/resources/home/solr/utilities/banner-stats/{0}/{1}/{2}.csv").
                anyTimes();

        EasyMock.expect(PropertiesUtils.getString("fileNotFoundMessage")).
                andReturn("File {0} cannot be found.").anyTimes();
    }

    /**
     * Helper method for mocking BannerStatisticsUtil
     */
    private void mockBannerStatisticsUtilHelper() {
        Whitebox.setInternalState(BannerStatisticsUtil.class,
                "FILE_LOCATION", PropertiesUtils.getString("fileLocation"));
        Whitebox.setInternalState(BannerStatisticsUtil.class,
                "FILE_CANNOT_BE_FOUND_MESSAGE",
                PropertiesUtils.getString("fileNotFoundMessage"));
    }
}
