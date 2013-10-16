package com.search.reports.manager.util;

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
}
