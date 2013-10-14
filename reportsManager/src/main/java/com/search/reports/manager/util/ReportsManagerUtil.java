package com.search.reports.manager.util;

import com.google.common.base.Strings;
import com.search.reports.manager.model.Report;
import com.search.reports.manager.model.ReportHeader;
import java.util.Date;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
public class ReportsManagerUtil {

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

    /**
     * <p>
     * Checks whether a {@link Report} object is empty or not.
     * </p>
     * <p>
     * An empty {@link Report} object is determined if the object is null or if all of
     * it's attributes is null, empty or 0.
     * </p>
     *
     * @param report the {@link Report} object
     * @return <i>true</i> if the {@link Report} object is empty, else <i>false</i>
     */
    public static boolean isReportObjectEmpty(Report report) {
        if (report == null) {
            return true;
        }
        
        String rank = report.getRank();
        String sku = report.getSku();
        String name = report.getName();
        Date expiration = report.getExpiration();

        return (Strings.isNullOrEmpty(rank) || isNumberEmpty(rank))
                && (Strings.isNullOrEmpty(sku) || isNumberEmpty(sku))
                && Strings.isNullOrEmpty(name)
                && expiration == null ? true : false;
    }

    /**
     * Helper method for checking if a number is 0
     *
     * @param numberAsString the number value as String
     * @return <i>true</i> if the number is zero, else <i>false</i>
     */
    private static boolean isNumberEmpty(String numberAsString) {
        try {
            return Double.parseDouble(numberAsString) == 0.0 ? true : false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
