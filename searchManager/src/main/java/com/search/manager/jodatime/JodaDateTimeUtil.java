package com.search.manager.jodatime;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.search.manager.service.UtilityService;
import com.search.ws.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JodaDateTimeUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(JodaDateTimeUtil.class);

    @Autowired
    private ConfigManager configManager;
    @Autowired
    private UtilityService utilityService;
    
    public static DateTimeZone getTimeZone() {
        return DateTimeZone.getDefault();
    }

    public static String getTimeZoneID() {
        return getTimeZone().getID();
    }

    public Timestamp toSqlDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        /*	For logging display, making sure that zone is set to defined system timezone
         * 	specified in solr.xml and jvm arguments
         */
        DateTimeZone systemDateTimeZone = DateTimeZone.forID(configManager.getSystemTimeZoneId());
        DateTime dTime = dateTime.withZone(systemDateTimeZone);

        logger.info(String.format("-DTZ- Joda timezone to SQL timezone: %s(%s) -> %s(%s)", dateTime.toString(), dateTime.getZone().getID(), dTime.toString(), dTime.getZone().getID()));
        logger.info(String.format("-DTZ- DateTime millis conversion from %s to %s", String.valueOf(dateTime.getMillis()), String.valueOf(dTime.getMillis())));
        return new Timestamp(dTime.getMillis());
    }

    public static DateTimeZone setTimeZoneID(String timeZoneId, String defaultTimeZoneId) {
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

    public DateTime toDateTime(Timestamp timestamp) {
        DateTimeZone systemDateTimeZone = DateTimeZone.forID(configManager.getSystemTimeZoneId());
        return (timestamp == null ? null : new DateTime(timestamp, systemDateTimeZone).withZone(DateTimeZone.getDefault()));
    }

    public static DateTime toDateTime(Date date, DateTimeZone tz) {
        return (date == null ? null : new DateTime(date, tz).withZone(DateTimeZone.getDefault()));
    }

    private DateTime toDateTime(String storeId, String pattern, String dateTimeText, String xmlTag) {
        if (StringUtils.isNotBlank(storeId)) {
            pattern = configManager.getStoreParameter(storeId, xmlTag);
        }

        if (StringUtils.isBlank(pattern) || StringUtils.isBlank(dateTimeText)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);

        return formatter.withZone(DateTimeZone.getDefault()).parseDateTime(dateTimeText);
    }

    public DateTime toDateTimeFromPattern(String pattern, String dateTimeText) {
        return toDateTime(null, pattern, dateTimeText, null);
    }

    public DateTime toDateTimeFromStorePattern(String storeId, String dateTimeText, JodaPatternType patternType) {
        return toDateTime(storeId, null, dateTimeText, patternType.equals(JodaPatternType.DATE) ? "date-format" : "datetime-format");
    }

    public DateTime toDateTimeFromStorePattern(String storeId, String dateTimeText) {
        return toDateTime(storeId, null, dateTimeText, "datetime-format");
    }

    public DateTime toDateTimeFromStorePattern(String dateTimeText, JodaPatternType patternType) {
        String storeId = utilityService.getStoreId();
        return toDateTimeFromStorePattern(storeId, dateTimeText, patternType);
    }

    public DateTime toDateTimeFromStorePattern(String dateTimeText) {
        String storeId = utilityService.getStoreId();
        return toDateTimeFromStorePattern(storeId, dateTimeText, JodaPatternType.DATE_TIME);
    }

    private String formatDateTime(String storeId, String pattern, DateTime dateTime, String xmlTag) {
    	
        if (StringUtils.isNotBlank(storeId)) {
            pattern = configManager.getStoreParameter(storeId, xmlTag);
        }

        if (StringUtils.isBlank(pattern) || dateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);

        return formatter.withZone(DateTimeZone.getDefault()).print(dateTime);
    }

    public String formatDateTimeFromPattern(String pattern, DateTime dateTime) {
        return StringUtils.isNotBlank(pattern) ? formatDateTime(null, pattern, dateTime, null) : "";
    }

    public String formatFromStorePattern(DateTime dateTime, JodaPatternType patternType) {
        String storeId = utilityService.getStoreId();
        return StringUtils.isNotBlank(storeId) ? formatFromStorePattern(storeId, dateTime, patternType) : "";
    }

    public String formatFromStorePattern(String storeId, DateTime dateTime, JodaPatternType patternType) {
        return formatDateTime(storeId, null, dateTime, JodaPatternType.DATE.equals(patternType) ? "date-format" : "datetime-format");
    }

    public String formatFromStorePatternWithZone(DateTime dateTime, JodaPatternType patternType) {
        String storeId = utilityService.getStoreId();
        return StringUtils.isNotBlank(storeId) ? String.format("%s %s", formatFromStorePattern(storeId, dateTime, patternType), getTimeZoneID()) : "";
    }

    public static String getRemainingDays(DateTime startDateTime, DateTime endDateTime) {

        if (startDateTime == null) {
            return StringUtils.EMPTY;
        }

        if (endDateTime == null) {
            endDateTime = DateTime.now();
        }

        if (startDateTime.toDateMidnight().isBefore(endDateTime) || startDateTime.toDateMidnight().isEqual(endDateTime.toDateMidnight())) {
            int days = Days.daysBetween(startDateTime.toDateMidnight(), endDateTime.toDateMidnight()).getDays();
            if (days > 0) {
                Period period = new Period(startDateTime.toDateMidnight(), endDateTime.toDateMidnight(), PeriodType.days());

                PeriodFormatter formatter = new PeriodFormatterBuilder()
                        .appendDays().appendSuffix(" day left", " days left")
                        .toFormatter();

                return formatter.print(period);
            } else if (startDateTime.toDateMidnight().isEqual(DateTime.now().toDateMidnight())) {
                return "Ends Today";
            }
        }

        return StringUtils.EMPTY;
    }
	
	/**
	 *  Convert string to user-defined timezone if set, otherwise store-defined timezone will be applied
	 *  
	 */
	public DateTime toUserDateTimeZone(String storeId, String dateTimeText){
		String dateFormat = configManager.getStoreParameter(storeId, "date-format");
		String dateTimeFormat = configManager.getStoreParameter(storeId, "datetime-format");
		
		DateTimeParser[] parsers = { 
		        DateTimeFormat.forPattern(dateFormat).getParser(),
		        DateTimeFormat.forPattern(dateTimeFormat).getParser() 
		        };
		
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
		logger.info("-DTZ- User Timezone: " + getTimeZone().getID());
		return formatter.parseDateTime(dateTimeText).withZone(getTimeZone());
	} 
}