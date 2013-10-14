package com.search.reports.manager.util;

import com.search.reports.manager.model.ReportHeader;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 14, 2013
 * @version 1.0
 */
public class ReportHeaders {

    /**
     * Checks whether a value is a report header
     *
     * @see ReportHeader
     * @param value the value
     * @return <i>true</i> if the value is a report header, else <i>false</i>
     */
    public static boolean isAReportHeader(String value) {
        return ReportHeader.isAReportHeader(value);
    }
}
