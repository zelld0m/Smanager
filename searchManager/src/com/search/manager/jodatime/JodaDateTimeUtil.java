package com.search.manager.jodatime;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.search.manager.service.UtilityService;
import com.search.ws.ConfigManager;

public class JodaDateTimeUtil {
	private static final Logger logger = Logger.getLogger(JodaDateTimeUtil.class);
	
	public static DateTimeZone getTimeZone(){
		return DateTimeZone.getDefault();
	}
	
	public static String getTimeZoneID(){
		return getTimeZone().getID();
	}
	
	public static Timestamp toSqlDate(DateTime dateTime, JodaPatternType jodaPatternType){
		if(dateTime==null) return null;
		
		DateTime dTime = dateTime.withZone(DateTimeZone.getDefault());
		
		if((jodaPatternType==null || JodaPatternType.DATE_TIME.equals(jodaPatternType))){
			logger.info(String.format("-DTZ- Joda timezone conversion to %s", dTime.getZone().getID()));
			logger.info(String.format("-DTZ- DateTime millis conversion from %s to %s", String.valueOf(dateTime.getMillis()), String.valueOf(dTime.getMillis())));
			return new Timestamp(dTime.getMillis());
		}else{
			return new Timestamp(dTime.toDateMidnight().getMillis());
		}
	}
	
	public static Timestamp toSqlDate(DateTime dateTime){
		return toSqlDate(dateTime, JodaPatternType.DATE_TIME);
	}
	
	public static DateTimeZone setTimeZoneID(String timeZoneId, String defaultTimeZoneId){
		DateTimeZone defaultJodaTimeZone = DateTimeZone.getDefault();
		DateTimeZone jodaTimeZone = DateTimeZone.getDefault();
		
		try {
			jodaTimeZone = DateTimeZone.forID(timeZoneId);
			logger.info(String.format("-DTZ- Attempt to set timezone to user-defined: %s", timeZoneId));
		} catch (IllegalArgumentException ue) {
			logger.error(String.format("-DTZ- Failed to set Joda timezone to user-defined: %s", timeZoneId));
			try {
				jodaTimeZone = DateTimeZone.forID(defaultTimeZoneId);
				logger.info(String.format("-DTZ- Attempt to set timezone to store default: %s", timeZoneId));
			} catch (IllegalArgumentException se) {
				jodaTimeZone = defaultJodaTimeZone;
				logger.error(String.format("-DTZ- Failed to set Joda timezone to store default: %s; attempt to set timezone to default %s", defaultTimeZoneId, defaultJodaTimeZone.getID()));
			}
		}
		
		DateTimeZone.setDefault(jodaTimeZone);
		logger.info(String.format("-DTZ- Joda timezone set to %s", jodaTimeZone.getID()));
		return jodaTimeZone;
	}
	
	public static DateTime toDateTime(Timestamp timestamp) {
		ConfigManager cm = ConfigManager.getInstance();
		DateTimeZone systemDateTimeZone = DateTimeZone.forID(cm.getSystemTimeZoneId());
		return (timestamp==null? null: new DateTime(timestamp, systemDateTimeZone).withZone(DateTimeZone.getDefault()));
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
		
		return formatter.withZone(DateTimeZone.getDefault()).parseDateTime(dateTimeText);
	}
	
	public static DateTime toDateTimeFromPattern(String pattern, String dateTimeText){
		return toDateTime(null, pattern, dateTimeText, null);
	}

	public static DateTime toDateTimeFromStorePattern(String storeId, String dateTimeText, JodaPatternType patternType){
		return toDateTime(storeId, null, dateTimeText, patternType.equals(JodaPatternType.DATE) ? "date-format":"datetime-format");
	}
	
	public static DateTime toDateTimeFromStorePattern(String dateTimeText, JodaPatternType patternType){
		String storeId = UtilityService.getStoreId();
		return toDateTimeFromStorePattern(storeId, dateTimeText, patternType);
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
		
		return formatter.withZone(DateTimeZone.getDefault()).print(dateTime);
	}
	
	public static String formatDateTimeFromPattern(String pattern, DateTime dateTime){
		return StringUtils.isNotBlank(pattern)? formatDateTime(null, pattern, dateTime, null): "";
	}
	
	public static String formatFromStorePattern(DateTime dateTime, JodaPatternType patternType){
		String storeId = UtilityService.getStoreId();
		return StringUtils.isNotBlank(storeId)? formatFromStorePattern(storeId, dateTime, patternType): "";
	}
	
	public static String formatFromStorePattern(String storeId, DateTime dateTime, JodaPatternType patternType){
		return formatDateTime(storeId, null, dateTime, JodaPatternType.DATE.equals(patternType)? "date-format": "datetime-format");
	}
	
	public static String formatFromStorePatternWithZone(DateTime dateTime, JodaPatternType patternType){
		String storeId = UtilityService.getStoreId();
		return StringUtils.isNotBlank(storeId)? String.format("%s [%s]", formatFromStorePattern(storeId, dateTime, patternType), getTimeZoneID()): "";
	}
	
	public static String getRemainingDays(DateTime startDateTime, DateTime endDateTime) {
		
		if(endDateTime == null){
			endDateTime = DateTime.now();
		}
		
		if(startDateTime!=null &&  Days.daysBetween(startDateTime.toDateMidnight(), endDateTime.toDateMidnight()).getDays() > 0){
			Period period = new Period(startDateTime.toDateMidnight(), endDateTime.toDateMidnight(), PeriodType.days());
			
			PeriodFormatter formatter = new PeriodFormatterBuilder()
			.appendDays().appendSuffix(" day ", " days ")
			.toFormatter();
			
			return formatter.print(period);
		}else if(startDateTime.isEqualNow()){
			return "Today";
		}
		
		return StringUtils.EMPTY;
	}
}