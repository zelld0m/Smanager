package com.search.manager.jodatime;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.search.manager.service.UtilityService;
import com.search.ws.ConfigManager;

public class JodaDateTimeUtil {
	
	
	public static DateTimeZone getTimeZone(){
		return DateTimeZone.getDefault();
	}
	
	public static String getTimeZoneID(){
		return getTimeZone().getID();
	}
	
	public static DateTimeZone setTimeZoneID(String timeZoneId, String defaultTimeZoneId){
		DateTimeZone defaultTimeZone = DateTimeZone.UTC;
		
		try {
			defaultTimeZone = DateTimeZone.forID(timeZoneId);
		} catch (IllegalArgumentException ue) {
			try {
				defaultTimeZone = DateTimeZone.forID(defaultTimeZoneId);
			} catch (IllegalArgumentException se) {
				defaultTimeZone = DateTimeZone.UTC;
			}
		}
		
		DateTimeZone.setDefault(defaultTimeZone);
		return defaultTimeZone;
	}
	
	public static DateTime toDateTime(Timestamp timestamp) {
		return (timestamp==null? null: new DateTime(timestamp.getTime(),getTimeZone()));
	}
	
	public static DateTime toDateTime(Date date) {
		return (date==null? null: new DateTime(date.getTime(),getTimeZone()));
	}
	
	private static DateTime toDateTime(String storeId, String pattern, String dateTimeText, String xmlTag){
		ConfigManager configManager = ConfigManager.getInstance();
		
		if(StringUtils.isNotBlank(storeId)){
			pattern = configManager.getStoreParameter(storeId, xmlTag);
		}

		if(StringUtils.isBlank(pattern) || StringUtils.isBlank(dateTimeText)){
			return null;
		}
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		
		return formatter.parseDateTime(dateTimeText);
	}
	
	public static DateTime toDateTimeFromPattern(String pattern, String dateTimeText){
		return toDateTime(null, pattern, dateTimeText, null);
	}

	public static DateTime toDateTimeFromStoreDatePattern(String storeId, String dateTimeText){
		return toDateTime(storeId, null, dateTimeText, "date-format");
	}
	
	public static DateTime toDateTimeFromStoreDateTimePattern(String storeId, String dateTimeText){
		return toDateTime(storeId, null, dateTimeText, "datetime-format");
	}
	
	private static String formatDateTime(String storeId, String pattern, DateTime dateTime, String xmlTag){
		ConfigManager configManager = ConfigManager.getInstance();
		
		if(StringUtils.isNotBlank(storeId)){
			pattern = configManager.getStoreParameter(storeId, xmlTag);
		}

		if(StringUtils.isBlank(pattern) || dateTime==null){
			return null;
		}
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		
		return formatter.print(dateTime);
	}
	
	public static String formatDateTimeFromPattern(String pattern, DateTime dateTime){
		return StringUtils.isNotBlank(pattern)? formatDateTime(null, pattern, dateTime, null): "";
	}

	public static String formatDateTimeFromStorePattern(String storeId, DateTime dateTime){
		return formatDateTime(storeId, null, dateTime, "datetime-format");
	}
	
	public static String formatDateTimeFromStorePattern(DateTime dateTime){
		String storeId = UtilityService.getStoreId();
		return StringUtils.isNotBlank(storeId)? formatDateTimeFromStorePattern(storeId, dateTime): "";
	}
	
	public static String formatDateFromStorePattern(String storeId, DateTime dateTime){
		return formatDateTime(storeId, null, dateTime, "date-format");
	}
	
	public static String formatDateFromStorePattern(DateTime dateTime){
		String storeId = UtilityService.getStoreId();
		return StringUtils.isNotBlank(storeId)? formatDateFromStorePattern(storeId, dateTime): "";
	}

	public static String formatDateFromStorePatternWithZone(DateTime dateTime){
		String storeId = UtilityService.getStoreId();
		return StringUtils.isNotBlank(storeId)? String.format("%s [%s]", formatDateFromStorePattern(storeId, dateTime), getTimeZoneID()): "";
	}
	
	public static String formatDateTimeFromStorePatternWithZone(DateTime dateTime){
		String storeId = UtilityService.getStoreId();
		return StringUtils.isNotBlank(storeId)? String.format("%s [%s]", formatDateTimeFromStorePattern(storeId, dateTime), getTimeZoneID()): "";
	}
	
	public static String getRemainingDateTimeText(DateTime startDateTime, DateTime endDateTime) {
		
		if(endDateTime == null){
			endDateTime = DateTime.now();
		}
		
		if(startDateTime!=null && startDateTime.isBefore(endDateTime)){
			Period period = new Period(startDateTime, endDateTime, PeriodType.dayTime());
			
			PeriodFormatter formatter = new PeriodFormatterBuilder()
			.appendDays().appendSuffix(" day ", " days ")
			.appendHours().appendSuffix(" hr ", " hrs ")
			.appendMinutes().appendSuffix(" min ", " mins ")
			.appendSeconds().appendSuffix(" sec ", " secs ")
			.toFormatter();
			
			return formatter.print(period);
		}
		
		return StringUtils.EMPTY;
	}
}