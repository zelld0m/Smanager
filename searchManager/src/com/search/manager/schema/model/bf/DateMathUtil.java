package com.search.manager.schema.model.bf;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.lucene.queryParser.ParseException;
import org.apache.solr.util.DateMathParser;

/**
 * 
 * @author IFlores
 *	as of 01/07/2013 searchManager is using Solr 1.4
 *
 *	DateMathParser functions not yet available in the above version
 *	e.g. parseMath
 */

public class DateMathUtil {

	public static TimeZone UTC = TimeZone.getTimeZone("UTC");

	/** Default TimeZone for DateMath rounding (UTC) */
	public static final TimeZone DEFAULT_MATH_TZ = UTC;
	/** Default Locale for DateMath rounding (Locale.ROOT) */
	public static final Locale DEFAULT_MATH_LOCALE = Locale.ROOT;
	
	private static Pattern splitter = Pattern.compile("\\b|(?<=\\d)(?=\\D)");
	
	
	public static boolean isValidDateMath(String math) throws ParseException{
		return DateMathUtil.isValidDateMath(math, DEFAULT_MATH_TZ, DEFAULT_MATH_LOCALE);
	}
	
	/**
	   * Parses a string of commands relative "now" and returns true if math is valid.
	   * 
	   * @exception ParseException positions in ParseExceptions are token positions, not character positions.
	   */
	public static boolean isValidDateMath(String math, TimeZone timeZone, Locale locale) throws ParseException {
		boolean isValid = false;
		
		Calendar cal = Calendar.getInstance(timeZone, locale);
		
		/* check for No-Op */
		if (0==math.length()) {
			return isValid;
		}
		
	    String[] ops = splitter.split(math);
	    int pos = 0;
	    while ( pos < ops.length ) {
	      /*if(StringUtils.isBlank(ops[pos])){
	    	pos++;
	    	continue;  
	      }	*/
	      
	      if (1 != ops[pos].length()) {
	        throw new ParseException(String.format("Multi character command found: \"%s\", pos: %s", ops[pos], pos));
	      }
	      char command = ops[pos++].charAt(0);

	      switch (command) {
	      case '/':
	        if (ops.length < pos + 1) {
	          throw new ParseException(String.format("Need a unit after command: \"%s\", pos: %s", command, pos));
	        }
	        try {
	        	DateMathParser.round(cal, ops[pos++]);
	          isValid = true;
	        } catch (IllegalArgumentException e) {
	          throw new ParseException(String.format("Unit not recognized: \"%s\", pos: %s", ops[pos-1], pos-1));
	        }
	        break;
	      case '+': /* fall through */
	      case '-':
	        if (ops.length < pos + 2) {
	          throw new ParseException(String.format("Need a value and unit for command: \"%s\", pos: %s", command, pos));
	        }
	        int val = 0;
	        try {
	          val = Integer.valueOf(ops[pos++]);
	        } catch (NumberFormatException e) {
	          throw new ParseException(String.format("Not a Number: \"%s\", pos: %s", ops[pos-1], pos-1));
	        }
	        if ('-' == command) {
	          val = 0 - val;
	        }
	        try {
	          String unit = ops[pos++];
	          DateMathParser.add(cal, val, unit);
	          isValid = true;
	        } catch (IllegalArgumentException e) {
	          throw new ParseException(String.format("Unit not recognized: \"%s\", pos: %s", ops[pos-1], pos-1));
	        }
	        break;
	      default:
	        throw new ParseException(String.format("Unrecognized command: \"" + command + "\"", command, pos-1));
	      }
	    }
	    
	    return isValid;
	  }
}
