package com.search.manager.schema.model.bf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.GenericType;
import com.search.manager.utility.DateAndTimeUtils;
import org.slf4j.LoggerFactory;

@DataTransferObject(converter = BeanConverter.class)
public class DateConstant extends Constant {

    private static final org.slf4j.Logger logger =
            LoggerFactory.getLogger(DateConstant.class);
    private static final long serialVersionUID = 1L;
    // constant: now
    private final static String DATE_NOW = "NOW";
    // units: MINUTE and MINUTES; MILLI, MILLIS, MILLISECOND, and MILLISECONDS
    private final static List<String> DATE_UNITS = new ArrayList<String>();

    static {
        CollectionUtils.addAll(DATE_UNITS, new String[]{
            "YEAR", "YEARS",
            "MONTH", "MONTHS",
            "DAY", "DAYS",
            "DATE",
            "HOUR", "HOURS",
            "MINUTE", "MINUTES",
            "SECOND", "SECONDS",
            "MILLISECOND", "MILLISECONDS",
            "MILLI", "MILLIS"
        });
    }

    public DateConstant(String value) {
        super(value);
        genericType = GenericType.DATE;
    }

    public static boolean isValidConstant(String value) throws SchemaException {
        boolean isValid = true;
        value = StringUtils.trimToNull(value);
        if (StringUtils.isNotBlank(value)) {
            if (!StringUtils.equals(value, DATE_NOW)) {
                if (StringUtils.startsWith(value, DATE_NOW)) {
                    // remove DATE_NOW
                    value = value.substring(DATE_NOW.length());
                } else {
                    Matcher m = Pattern.compile("(\\d{4}\\-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,7})??Z|[+-]\\d{2}:\\d{2})(.*)").matcher(value);
                    if (m.matches()) {
                        isValid = DateAndTimeUtils.isValidDateIso8601Format(m.group(1));
                        value = m.group(3);
                    }
                }
                if (isValid && StringUtils.isNotBlank(value)) {
                    // evaluate date operations
                    String[] values = value.split("(?=[\\+\\-\\/])");
                    for (String str : values) {
                        if (StringUtils.isNotBlank(str)) {
                            Matcher m = Pattern.compile("([\\+\\-\\/])(\\d)*(.*)").matcher(str);
                            if (m.matches()) {
                                String operator = m.group(1);
                                String intValue = m.group(2);
                                if (StringUtils.isBlank(operator) || !DATE_UNITS.contains(m.group(3))) {
                                    isValid = false;
                                } else if (operator.equals("/")) {
                                    if (StringUtils.isNotBlank(intValue)) {
                                        isValid = false;
                                    }
                                } else {
                                    if (StringUtils.isBlank(intValue) || !StringUtils.isNumeric(intValue)) {
                                        isValid = false;
                                    }
                                }
                            } else {
                                isValid = false;
                            }
                        }
                    }
                }
            }
        } else {
            isValid = false;
        }
        /*if (!isValid) {
         throw new SchemaException("Invalid Date Format: " + value + "! Date should be either in ISO8601 Canonical Date Format (e.g. 2000-01-01T00:00:00Z) or the value \"NOW\". ");
         }*/
        return isValid;
    }

    @Override
    public boolean validate() throws SchemaException {
        boolean valid = StringUtils.isNotEmpty(value);
        valid &= isValidConstant(value);
        return valid;
    }

    public static void main(String[] args) {
        try {
            System.out.println(URLEncoder.encode("Printers & Print", "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println(ClientUtils.escapeQueryChars("Manufacturer:\"Apple\""));
        if (true) {
            return;
        }
        String[] dates = {
            "1995-12-31T23:59:59Z",
            "1995-12-31T23:59:59.9Z",
            "1995-12-31T23:59:59.99Z",
            "1995-12-31T23:59:59.999Z",
            "1995-12-31T23:59:59.9999999Z",
            "NOW",
            "1995-12-31T23:59:59.9999999Z+1DAY",
            "/HOUR",
            "/DAY",
            "+2YEARS",
            "-1DAY",
            "/DAY+6MONTHS+3DAYS",
            "+6MONTHS+3DAYS-2MILLI/DAY",
            "1995-12-31T23:59:59.Z", // error
            "1995-12-31T23:59:59.9999999+DAY", //  error
            "++6MONTHS", // error
            "/1HOUR", // error
        };

        for (String date : dates) {
            DateConstant constant = new DateConstant(date);
            try {
                constant.validate();

                logger.info(String.format("%s is a valid date", constant.getValue()));
            } catch (Exception e) {
                logger.error(String.format("%s is not a valid date", constant.getValue()), e);
            }
        }
    }
}
