package com.search.manager.report.statistics.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsDateAndTimeUtils {
	public static final String DATE_FORMAT_STRING_YYYYMMDD = "yyyyMMdd";
	public static final String DATE_FORMAT_STRING_YYYYMM = "yyyyMM";
	
	public static String formatYYYYMMDD(Date date) {
		return new SimpleDateFormat(DATE_FORMAT_STRING_YYYYMMDD).format(date);
	}
	
	public static String formatYYYYMM(Date date) {
		return new SimpleDateFormat(DATE_FORMAT_STRING_YYYYMM).format(date);
	}
}
