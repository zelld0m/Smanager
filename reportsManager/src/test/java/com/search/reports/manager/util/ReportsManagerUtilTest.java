package com.search.reports.manager.util;

import com.search.reports.manager.model.Report;
import com.search.reports.manager.model.builder.ReportBuilder;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 *
 */
public class ReportsManagerUtilTest {

    @Test
    public void testIsAReportHeader() {
        assertEquals(true, ReportsManagerUtil.isAReportHeader("Rank"));
    }

    @Test
    public void testIsAReportHeader_Return_False() {
        assertEquals(false, ReportsManagerUtil.isAReportHeader("Banner"));
    }

    @Test
    public void testIsReportObjectEmpty() {
        Report report = ReportBuilder.create().rank("1").build();
        assertEquals(false, ReportsManagerUtil.isReportObjectEmpty(report));
    }

    @Test
    public void testIsReportObjectEmpty_Rank_Is_0_Return_True() {
        Report report = ReportBuilder.create().rank("0").build();
        assertEquals(true, ReportsManagerUtil.isReportObjectEmpty(report));
    }

    @Test
    public void testIsReportObjectEmpty_Rank_Is_Not_A_Number_Return_True() {
        Report report = ReportBuilder.create().rank("0a").build();
        assertEquals(true, ReportsManagerUtil.isReportObjectEmpty(report));
    }
    
    @Test
    public void testIsReportObjectEmpty_Return_True() {
        Report report = new Report();
        assertEquals(true, ReportsManagerUtil.isReportObjectEmpty(report));
    }

    @Test
    public void testIsReportObjectEmpty_Argument_Null_Return_True() {
        assertEquals(true, ReportsManagerUtil.isReportObjectEmpty(null));
    }
}
