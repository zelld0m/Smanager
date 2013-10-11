package com.search.reports.manager.model;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
public enum ReportHeader {

    RANK, SKU, NAME, EXPIRATION;
    
    public static boolean isAReportHeader(String value) {
        ReportHeader[] reportHeaders = values();
        
        for (ReportHeader reportHeader : reportHeaders) {
            if (reportHeader.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        
        return false;
    }
}
