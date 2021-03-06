package com.search.manager.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.search.ws.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A utility class for Date and Time related functions.
 */
@Component
public final class DateAndTimeUtils {

    private static final Logger logger =
            LoggerFactory.getLogger(DateAndTimeUtils.class);
    
    @Autowired
    private ConfigManager configManager;
    
    /**
     * The format for parsing dates without times.
     */
    public static final String DATE_FORMAT_STRING_MM_DD_YYYY = "MM/dd/yyyy";
    public static final String HYPHENED_DATE_FORMAT_STRING_MM_DD_YYYY = "MM-dd-yyyy";
    public static final String DATE_FORMAT_STRING_YYYYMMDD = "yyyyMMdd";
    /**
     * A format for parsing offer dates with times.
     */
    public static final String DATE_FORMAT_STRING_MM_DD_YYYY_hh_mm_aa = DATE_FORMAT_STRING_MM_DD_YYYY + " hh:mm aa";
    public static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat("hh:mm:ss aa");
    public static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING_MM_DD_YYYY);
    public static final int one_minute = 60 * 1000;

    /**
     * Hide constructor of this utility class.
     */
    private DateAndTimeUtils() {
        // hidden
    }

    /**
     * Parses a Calendar from input.
     *
     * @throws ParseException on parsing problems.
     */
    public static Calendar checkIfValidStartDate(String dateString, String minutesString, String hoursString, String amOrPmString) throws ParseException {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        } else {
            return parseCalendar(dateString, minutesString, hoursString, amOrPmString);
        }
    }

    public static Calendar parseCalendar(String dateString, String minutesString, String hoursString, String amOrPmString) throws ParseException {

        String minutesPad = "";
        if (2 > minutesString.length()) {
            minutesPad = "00".substring(minutesString.length());
        }

        String calendarString = dateString + " " + hoursString + ":" + minutesPad + minutesString + " " + amOrPmString;
        SimpleDateFormat parser = new SimpleDateFormat(DATE_FORMAT_STRING_MM_DD_YYYY_hh_mm_aa);
        Date date = parser.parse(calendarString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    public static Date addYearToDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, year);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Returns a date in month day year time format, eg 01/05/2011 10:14 PM
     */
    public static String formatMMddyyyyhhmmaa(Date date) {
        String dateString = "";
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING_MM_DD_YYYY_hh_mm_aa);
            dateString = dateFormat.format(date);
        }
        return dateString;
    }

    /**
     * Returns a date in month day year time format, eg 01/05/2011
     */
    public static String formatMMddyyyy(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING_MM_DD_YYYY);
        String dateString = dateFormat.format(date);

        return dateString;
    }

    public static Date twoMinutesInTheFuture() {
        long twoMinutesInTheFutureMilis = System.currentTimeMillis() + (2 * 60 * 1000);
        Date twoMinutesInTheFuture = new Date(twoMinutesInTheFutureMilis);
        return twoMinutesInTheFuture;
    }

    public static Date twoMinutesInTheFuture(long curTime) {
        return new Date(curTime + (2 * one_minute));
    }

    public static String getDateStringMMDDYYYY(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_STRING_MM_DD_YYYY);
        return formatter.format(date);
    }

    public static String getHyphenedDateStringMMDDYYYY(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(HYPHENED_DATE_FORMAT_STRING_MM_DD_YYYY);
        return formatter.format(date);
    }

    public static String formatYYYYMMDD(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_STRING_YYYYMMDD);
        return formatter.format(date);
    }

    public static Date parseDateYYYYMMDD(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                DATE_FORMAT_STRING_YYYYMMDD);
        Date value = null;

        try {
            value = formatter.parse(dateStr);
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        }

        return value;
    }

    public static String getDateStringAmPm(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("aa");
        String amPm = formatter.format(date);
        return amPm;
    }

    public static String getDateStringHours(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh");
        String hoursWithLeadingZero = formatter.format(date);
        if (hoursWithLeadingZero.startsWith("0")) {
            return hoursWithLeadingZero.substring(1);
        } else {
            return hoursWithLeadingZero;
        }
    }

    public static String getDateStringMinutes(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm");
        String minutesWithLeadingZero = formatter.format(date);

        return minutesWithLeadingZero;
    }

    public String formatDateUsingConfig(String store, Date date) {
        if (StringUtils.isBlank(store) || date == null) {
            return "";
        }
        
        String dateFormat = configManager.getStoreParameter(store, "dateformat");

        if (StringUtils.isBlank(dateFormat)) {
            return formatMMddyyyy(date);
        }

        try {
            return new SimpleDateFormat(dateFormat).format(date);
        } catch (Exception e) {
            return formatMMddyyyy(date);
        }
    }

    public String formatDateTimeUsingConfig(String store, Date date) {
        String dateFormat = configManager.getStoreParameter(store, "datetimeformat");

        if (date == null) {
            return "";
        }

        if (StringUtils.isBlank(dateFormat)) {
            return formatMMddyyyyhhmmaa(date);
        }

        try {
            return new SimpleDateFormat(dateFormat).format(date);
        } catch (Exception e) {
            return formatMMddyyyyhhmmaa(date);
        }
    }

    public Date toSQLDate(String store, String str) {
        Date convertedDate = null;
        if (StringUtils.isNotBlank(str)) {
            DateFormat formatter = new SimpleDateFormat(configManager.getStoreParameter(store, "dateformat"));

            try {
                convertedDate = formatter.parse(str);
            } catch (ParseException e) {
                logger.error(String.format("Error converting %s", str), e);
            }
        }
        return convertedDate;
    }

    public static Integer compare(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return 0;
        }
        if (date1 == null) {
            return 1;
        }
        if (date2 == null) {
            return -1;
        }
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(date1).compareTo(formatter.format(date2));
    }

    public static Date getDateWithEndingTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    public static String convertToSqlTimestamp(Date date) {
        if (date != null) {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date);
        }
        return null;
    }

    public static String convertToSqlTimestampStartOfDay(Date date) {
        if (date != null) {
            return new SimpleDateFormat("yyyy-MM-dd").format(date) + " 00:00:00.000";
        }
        return null;
    }

    public static String convertToSqlTimestampEndOfDay(Date date) {
        if (date != null) {
            // .999 will automatically round up to the next day
            return new SimpleDateFormat("yyyy-MM-dd").format(date) + " 23:59:59.998";
        }
        return null;
    }

    public Date getDate(String store, Date date) {
        if (StringUtils.isBlank(store) || date == null) {
            return null;
        }

        DateFormat formatter = new SimpleDateFormat(configManager.getStoreParameter(store, "dateformat"));

        try {
            return formatter.parse(formatter.format(date));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if string is a valid ISO 8601 Formatted Date
     * YYYY-MM-DDThh:mm:ss[.nnnnnnn][{+|-}hh:mm] or YYYY-MM-DDThh:mm:ss[.nnnnnnn]Z (UTC,
     * Coordinated Universal Time) e.g. 2004-05-23T14:25:10Z and
     * 2004-05-23T14:25:10.1234567+07:00
     *
     * @param date
     * @return
     */
    public static boolean isValidDateIso8601Format(String date) {
        Matcher m = Pattern.compile("(\\d{4})\\-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(?:\\.(\\d{1,7}))??(?:Z|[+-](\\d{2}):(\\d{2}))??").matcher(date);
        boolean valid = m.matches();
        if (valid) {
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            int minute = Integer.parseInt(m.group(5));
            int second = Integer.parseInt(m.group(6));
            // year cannot be 0
            valid = valid && year > 0;
            // range for month and day
            valid = valid && month > 0 && month < 13 && day > 0 && day < 32 && hour < 24 && minute < 60 && second < 60;
            // month with 30 days only
            valid = valid && !(day == 31 && (month == 4 || month == 6 || month == 9 || month == 11));
            // feb 30 or 31
            valid = valid && !(month == 2 && day > 29);
            // feb 29 in a leap year;
            valid = valid && !(month == 2 && day == 29 && (year % 4 != 0 || (year % 100 == 0 && year % 400 != 0)));
        }
        return valid;
    }

    public static Date getDateYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static String getElapsedTime(Date start, Date end) {
        StringBuilder sb = new StringBuilder();

        long l1 = start.getTime();
        long l2 = end.getTime();
        long diff = l2 - l1;

        long secondInMillis = 1000;
        long minuteInMillis = secondInMillis * 60;
        long hourInMillis = minuteInMillis * 60;
        long dayInMillis = hourInMillis * 24;
        long yearInMillis = dayInMillis * 365;

        long elapsedYears = diff / yearInMillis;
        diff = diff % yearInMillis;
        if (elapsedYears > 0) {
            sb.append(elapsedYears + " yr" + (elapsedYears > 1 ? "s " : " "));
        }

        long elapsedDays = diff / dayInMillis;
        diff = diff % dayInMillis;
        if (elapsedDays > 0) {
            sb.append(elapsedDays + " day" + (elapsedDays > 1 ? "s " : " "));
        }

        long elapsedHours = diff / hourInMillis;
        diff = diff % hourInMillis;
        if (elapsedHours > 0) {
            sb.append(elapsedHours + " hr" + (elapsedHours > 1 ? "s " : " "));
        }

        long elapsedMinutes = diff / minuteInMillis;
        diff = diff % minuteInMillis;
        if (elapsedMinutes > 0) {
            sb.append(elapsedMinutes + " min" + (elapsedMinutes > 1 ? "s " : " "));
        }

        long elapsedSeconds = diff / secondInMillis;
        if (elapsedSeconds > 0) {
            sb.append(elapsedSeconds + " sec" + (elapsedSeconds > 1 ? "s " : " "));
        }

        return sb.append("ago").toString();
    }

    public static void main(String[] args) {
        try {
            logger.info(convertToSqlTimestamp(new Date()));
            logger.info(convertToSqlTimestampStartOfDay(new Date()));
            logger.info(convertToSqlTimestampEndOfDay(new Date()));

            // acceptable iso date 8601 formats
            String[] validDates = {
                "2004-05-23T14:25:10.1234567Z",
                "2004-05-23T14:25:10Z",
                "2004-05-23T14:25:10.1234567+07:00",
                "2004-05-23T14:25:10.1234567",
                "2004-05-23T14:25:10",
                "2012-02-29T14:25:10",
                "1600-02-29T14:25:10"
            };
            for (String date : validDates) {
                logger.info(String.format("pass: %b", isValidDateIso8601Format(date)));
            }

            // invalid date values
            String[] invalidDates = {
                "0-05-23T14:25:10",
                "2012-00-23T14:25:10",
                "2004-05-23T14:25:60Z",
                "2013-02-30T14:25:10",
                "2013-02-29T14:25:10",
                "1800-02-29T14:25:10"
            };
            logger.info("invalid values:");
            for (String date : invalidDates) {
                logger.info(String.format("pass: %b", isValidDateIso8601Format(date)));
            }

        } catch (Exception e) {
            logger.info("Error at DateAndTimeUtils.main()", e);
        }
    }

    public static Date getFirstDayOfMonth(Date date) {
        return DateUtils.truncate(date, Calendar.MONTH);
    }

    public static Date getFirstDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        int dow = cal.get(Calendar.DAY_OF_WEEK);

        if (dow == Calendar.SUNDAY) {
            dow = Calendar.SATURDAY + 1;
        }

        dow = Calendar.MONDAY - dow;

        return DateUtils.addDays(date, dow);
    }

    public static Date asUTC(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar calOrig = Calendar.getInstance();

        cal.setTime(date);

        calOrig.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        calOrig.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        calOrig.set(Calendar.DATE, cal.get(Calendar.DATE));
        calOrig.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        calOrig.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        calOrig.set(Calendar.SECOND, cal.get(Calendar.SECOND));

        return calOrig.getTime();

    }
}
