package com.search.manager.jodatime;

import java.sql.Timestamp;
import java.util.Date;

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

	public static Timestamp toSqlDate(DateTime dateTime){
		if(dateTime==null) return null;

		/*	For logging display, making sure that zone is set to defined system timezone
		 * 	specified in solr.xml and jvm arguments
		 */
		ConfigManager cm = ConfigManager.getInstance();
		DateTimeZone systemDateTimeZone = DateTimeZone.forID(cm.getSystemTimeZoneId());
		DateTime dTime = dateTime.withZone(systemDateTimeZone);

		logger.info(String.format("-DTZ- Joda timezone to SQL timezone: %s(%s) -> %s(%s)", dateTime.toString(), dateTime.getZone().getID(), dTime.toString(), dTime.getZone().getID()));
		logger.info(String.format("-DTZ- DateTime millis conversion from %s to %s", String.valueOf(dateTime.getMillis()), String.valueOf(dTime.getMillis())));
		return new Timestamp(dTime.getMillis());
	}

	public static DateTimeZone setTimeZoneID(String timeZoneId, String defaultTimeZoneId){
		DateTimeZone defaultJodaTimeZone = DateTimeZone.getDefault();
		DateTimeZone jodaTimeZone = DateTimeZone.getDefault();

		try {
			jodaTimeZone = DateTimeZone.forID(timeZoneId);
			logger.info(String.format("-DTZ- Initialize Joda timezone to user-defined: %s", timeZoneId));
		} catch (IllegalArgumentException ue) {
			logger.error(String.format("-DTZ- Failed to initialize Joda timezone to user-defined: %s", timeZoneId));
			try {
				jodaTimeZone = DateTimeZone.forID(defaultTimeZoneId);
				logger.info(String.format("-DTZ- Initialize Joda timezone to store-default: %s", timeZoneId));
			} catch (IllegalArgumentException se) {
				jodaTimeZone = defaultJodaTimeZone;
				logger.error(String.format("-DTZ- Failed to initialize Joda timezone to store-default: %s; Set back Joda timezone to default %s", defaultTimeZoneId, defaultJodaTimeZone.getID()));
			}
		}

		DateTimeZone.setDefault(jodaTimeZone);
		logger.info(String.format("-DTZ- Joda timezone setting to %s", jodaTimeZone.getID()));
		return jodaTimeZone;
	}

	public static DateTime toDateTime(Timestamp timestamp) {
		ConfigManager cm = ConfigManager.getInstance();
		DateTimeZone systemDateTimeZone = DateTimeZone.forID(cm.getSystemTimeZoneId());
		return (timestamp==null? null: new DateTime(timestamp, systemDateTimeZone).withZone(DateTimeZone.getDefault()));
	}

	public static DateTime toDateTime(Date date, DateTimeZone tz) {
		return (date==null? null: new DateTime(date, tz).withZone(DateTimeZone.getDefault()));
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

	public static DateTime toDateTimeFromStorePattern(String storeId, String dateTimeText){
		return toDateTime(storeId, null, dateTimeText, "datetime-format");
	}

	public static DateTime toDateTimeFromStorePattern(String dateTimeText, JodaPatternType patternType){
		String storeId = UtilityService.getStoreId();
		return toDateTimeFromStorePattern(storeId, dateTimeText, patternType);
	}

	public static DateTime toDateTimeFromStorePattern(String dateTimeText){
		String storeId = UtilityService.getStoreId();
		return toDateTimeFromStorePattern(storeId, dateTimeText, JodaPatternType.DATE_TIME);
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
		return StringUtils.isNotBlank(storeId)? String.format("%s %s", formatFromStorePattern(storeId, dateTime, patternType), getTimeZoneID()): "";
	}

	public static String getRemainingDays(DateTime startDateTime, DateTime endDateTime) {

		if(startDateTime == null){
			return StringUtils.EMPTY;
		}

		if(endDateTime == null){
			endDateTime = DateTime.now();
		}

		if (startDateTime.toDateMidnight().isBefore(endDateTime) || startDateTime.toDateMidnight().isEqual(endDateTime.toDateMidnight())){
			int days = Days.daysBetween(startDateTime.toDateMidnight(), endDateTime.toDateMidnight()).getDays();
			if( days > 0){
				Period period = new Period(startDateTime.toDateMidnight(), endDateTime.toDateMidnight(), PeriodType.days());

				PeriodFormatter formatter = new PeriodFormatterBuilder()
				.appendDays().appendSuffix(" day left", " days left")
				.toFormatter();

				return formatter.print(period);
			}else if(startDateTime.toDateMidnight().isEqual(DateTime.now().toDateMidnight())){
				return "Today";
			}
		}

		return StringUtils.EMPTY;
	}
}