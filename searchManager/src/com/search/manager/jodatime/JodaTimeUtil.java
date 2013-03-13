package com.search.manager.jodatime;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.search.ws.ConfigManager;

public class JodaTimeUtil {

	public static DateTime toDateTime(Timestamp timestamp) {
		return (timestamp==null? null: new DateTime(timestamp.getTime()));
	}
	
	public static DateTime toDateTime(Date date) {
		return (date==null? null: new DateTime(date.getTime()));
	}
	
	private static DateTime toDateTime(String storeId, String pattern, String dateTimeText){
		ConfigManager configManager = ConfigManager.getInstance();
		
		if(StringUtils.isNotBlank(storeId)){
			pattern = configManager.getStoreParameter(storeId, "datetimeformat");
		}

		if(StringUtils.isBlank(pattern) || StringUtils.isBlank(dateTimeText)){
			return null;
		}
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		
		return formatter.parseDateTime(dateTimeText);
	}
	
	public static DateTime toDateTimeFromPattern(String pattern, String dateTimeText){
		return toDateTime(null, pattern, dateTimeText);
	}

	public static DateTime toDateTimeFromStorePattern(String storeId, String dateTimeText){
		return toDateTime(storeId, null, dateTimeText);
	}
	
	private static String formatDateTime(String storeId, String pattern, DateTime dateTime){
		ConfigManager configManager = ConfigManager.getInstance();
		
		if(StringUtils.isNotBlank(storeId)){
			pattern = configManager.getStoreParameter(storeId, "datetimeformat");
		}

		if(StringUtils.isBlank(pattern) || dateTime==null){
			return null;
		}
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		
		return formatter.print(dateTime);
	}
	
	public static String formatDateTimeFromPattern(String pattern, DateTime dateTime){
		return formatDateTime(null, pattern, dateTime);
	}

	public static String formatDateTimeFromStorePattern(String storeId, DateTime dateTime){
		return formatDateTime(storeId, null, dateTime);
	}
	
	public static String getRemainingDateTimeText(DateTime startDateTime, DateTime endDateTime) {
		Period period = new Period(startDateTime, endDateTime, PeriodType.dayTime());

		PeriodFormatter formatter = new PeriodFormatterBuilder()
		        .appendDays().appendSuffix(" day ", " days ")
		        .appendHours().appendSuffix(" hr ", " hrs ")
		        .appendMinutes().appendSuffix(" min ", " mins ")
		        .appendSeconds().appendSuffix(" sec ", " secs ")
		        .toFormatter();

		return formatter.print(period);
	}
}