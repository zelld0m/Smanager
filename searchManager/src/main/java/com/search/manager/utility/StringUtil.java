package com.search.manager.utility;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class StringUtil {
	
	public static final String REGEX_NOHTML = "<[\\x20-\\x7E]*?>";
	public static final String REGEX_ALLOWEDURL = "((([fF][tT][pP])|([hH][tT][tT][pP])|([hH][tT][tT][pP][sS]))://)?([\\w\\x25\\x2E\\x2F\\x3D\\x3F\\x5F\\x26\\x2D]*)";
	public static final String REGEX_VALIDEMAIL = "(([\\w\\x2D\\x2E\\x5F]*)@([\\w\\x2D\\x2E\\x5F]*))";
	public static final String REGEX_NEWVALIDEMAIL ="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
    private StringUtil()
    {
    }	
	
    /**
     * Check if expression result is not null otherwise use the default value
     * 
     * @param expression 
     * @param defaultValue - value to use instead 
     * @return
     */
    public static String ifNull(String exp, String defaultValue) {
    	return exp == null ? defaultValue : exp;
    }
    
    /**
     * Check if a String variable is blank or null
     * 
     * @param str the string to check
     * @return true or false
     */
	public static final boolean isBlank(String str) {
		return str == null || str != null && str.trim().length() == 0;
	}
	
	/**
	 * Converts String formatted string into an actual Java Date object
	 * 
	 * @param date - the string date value
	 * @param format - the format of the string date value
	 * @return java.util.Date object 
	 * @throws ParseException
	 */
	public static final Date strToDate(String date, String format)
			throws ParseException {
		DateFormat dt = new SimpleDateFormat(format);
		return dt.parse(date);
	}
	
	public static final String strToLocalFormat(String date, String dateFormat)
		throws ParseException {
		Date newDate = strToDate(date, dateFormat);
		DateFormat localFormat = DateFormat.getDateInstance();
		return localFormat.format(newDate);
	}
	

	
	/**
	 * Converts Date object to string
	 * 
	 * @param date - date object value
	 * @param format - the target format 
	 * @return date formatted string
	 * @throws ParseException
	 */
	public static final String dateToStr(Date date, String format)
			throws ParseException {
		DateFormat dt = new SimpleDateFormat(format);
		return dt.format(date);
	}

	/**
	 * Generates SHA-512 based Hash String    
	 * 
	 * @param salt - the private encryption key
	 * @param input - the target string to be encrypted 
	 * @return
	 */
	public static final String getHashString(String salt, String input) {
		String md5string = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			StringBuilder sb = new StringBuilder();
			byte buf[] = (new StringBuilder()).append(salt).append(input)
					.toString().getBytes();
			
			byte md5[] = md.digest(buf);
			for (int i = 0; i < md5.length; i++) {
				String tmpStr = (new StringBuilder()).append("0").append(
						Integer.toHexString(0xff & md5[i])).toString();
				sb.append(tmpStr.substring(tmpStr.length() - 2));
			}

			md5string = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5string;
	}
	
	/**
	 * Get the substring of a string    
	 * 
	 * @param value - String to be substring
	 * @param start - start index
	 * @param end - end index 
	 * @return
	 */
	public static final String getSubString(String value, int start, int end) {
		if(value==null){
			return "";
		}
		String newValue = new String(value);
		if(end > value.length()){
			if(start < 0){
				return newValue.substring(0, value.length());
			}else{
				return newValue.substring(start, value.length());
			}
		}else{
			return newValue.substring(start, end);
		}
		
	}
	
	/**
	 * Pad characters to the left    
	 * 
	 * @param value - String value to pad
	 * @param pad - String pad
	 * @param numberOfPad - number of padding 
	 * @return
	 */
	public static final String padLeft(String value, String pad, int numberOfPad){		
		
		while(value.length() < numberOfPad){
			value = pad + value; 
		}
		
		return value;
	}
	
	/**
	 * Translates a string into application/x-www-form-urlencoded format for URL Links     
	 * 
	 * @param param - String URL to encode
	 * @return
	 */
	public static final String encodeURL(String param){		
		try {
			param = URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		return param;
	}	
	
	/**
	 * Check if string has html tags
	 * 
	 * @param input
	 * @return true if String has TAGS
	 */
	public static final boolean hasHTMLTag(String input) {
		if (isBlank(input)) return false; 
//		return Pattern.compile(REGEX_NOHTML).matcher(input).find();
		return Pattern.compile(REGEX_NOHTML).matcher(input + ">").find(); //appended a '>' symbol to close tag before checking 
	}	
	
	public static final boolean hasXSSTag(String input) {
		String out = XSSUtil.stripCTRLChars((String)input);
		if (XSSUtil.hasWatchListStrOnEachTag(out)) {
			return true;
		}
		if (XSSUtil.hasBlackListedTags( out )) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Check if string is an allowable URL 
	 * 
	 * @param input
	 * @return true if string is allowable url
	 */
	public static final boolean isAllowedURL(String input) {
		if (isBlank(input)) return false;
		
		String out = input;
		out = XSSUtil.stripCTRLChars(out);
		
		if (XSSUtil.hasBlackListedTagEvents(out)) {
			return false;
		}
		
		if (Pattern.matches(REGEX_ALLOWEDURL, out)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if string is a valid URL 
	 * 
	 * @param input
	 * @return true if string is valid url
	 */
	public static final boolean isValidEmail(String input) {
		if (isBlank(input)) return false;
		
		if (Pattern.matches(REGEX_VALIDEMAIL, input)) {
			return true;
		}
		
		return false;
	}	
	/**
	 * Check if string is a valid URL 
	 * 
	 * @param input
	 * @return true if string is valid url
	 */
	public static final boolean isValidEmailAddress(String input) {
		if (isBlank(input)) return false;
		
		if (Pattern.matches(REGEX_NEWVALIDEMAIL, input)) {
			return true;
		}
		
		return false;
	}	
	public static final String removeInvalidCharFileName(String fileName) {
		String retStr = fileName.replace('/', ' ');
		retStr = retStr.replace('\n', ' ');
		retStr = retStr.replace('\r', ' ');
		retStr = retStr.replace('\t', ' ');
		retStr = retStr.replace('\0', ' ');
		retStr = retStr.replace('\f', ' ');
		retStr = retStr.replace('`', ' ');
		retStr = retStr.replace('?', ' ');
		retStr = retStr.replace('*', ' ');
		retStr = retStr.replace('\\', ' ');
		retStr = retStr.replace('<', ' ');
		retStr = retStr.replace('>', ' ');
		retStr = retStr.replace('|', ' ');
		retStr = retStr.replace('*', ' ');
		retStr = retStr.replace(':', ' ');
		
		return retStr;
	}
	
	public static final String emptyNullString(String str) {
		if(str==null) return "";
		else return str;
	}

	/**
	 * @param address1
	 * @param address2
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @return consolidated address
	 */
	public static final String consolidateAddress(String address1, String address2, 
			String city, String stateDesc, String zipCode, String country) {
		StringBuilder addressInfo = new StringBuilder();
		boolean flag=false;
		if(address1!=null && address1.trim().length()>0) {
			addressInfo.append(address1.trim());
			flag=true;
		}
		
		if(address2!=null && address2.trim().length()>0) {
			addressInfo.append(flag?" ":"");
			addressInfo.append(address2.trim());
		}

		if(city!=null && city.trim().length()>0) {
			addressInfo.append(flag?" ":"");
			addressInfo.append(city.trim());
		}
		
		if(stateDesc!=null && stateDesc.trim().length()>0) {
			addressInfo.append(flag?", ":"");
			addressInfo.append(stateDesc.trim());
		}
		
		if(zipCode!=null && zipCode.trim().length()>0) {
			addressInfo.append(flag?" ":"");
			addressInfo.append(zipCode.trim());
		}
		
		//if(country!=null && country.trim().length()>0) {
		//	addressInfo.append(flag?" ":"");
		//	addressInfo.append(country.trim());
		//}
		return addressInfo.toString();
	}	
	
	public static final String formatPhoneNumber(String area, String phone) {
		if(phone==null || phone.trim().length()<=0)  
			return null;
		if(area == null || area.trim().length()<=0) 
			return formatPhoneNumber(phone);
		
		return formatPhoneNumber(area + phone);
	}
	
	public static final boolean ipAddressInRange(String ipAddress, String ipAddressLow, String ipAddressHigh) {
		 InetAddress ipLow = null;
		 InetAddress ipHigh = null; 
		 InetAddress ip = null; 
		try {
			ipLow = InetAddress.getByName(ipAddressLow);
			ipHigh = InetAddress.getByName(ipAddressHigh);
			ip = InetAddress.getByName(ipAddress);
		 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		 long tested = toLong(ip);
		 long low  = toLong(ipLow);
		 long high  = toLong(ipHigh);
	 
		 long lowerLimit = Math.min(low, high);
		 long upperLimit = Math.max(low, high);
	 
		 return ((lowerLimit <= tested) && (tested <= upperLimit)) ? true : false;
	}
	
	  /**
     * Use this method to convert an InetAddress in its long representation.
     * i.e.: b[0]*256^3 + b[1]*256^2 + b[2]*256^1 + b[3]*256^0.
     *
     * @param inetAddress - The inetAddress to be converted.
     * @return long - The long representation of the IP Address
     */
    private static long toLong(InetAddress inetAddress) {
        long compacted = 0;
        byte[] bytes = inetAddress.getAddress();
        for (int i=0; i<bytes.length; i++) {
            if (bytes[i] < 0) {
            	compacted += (256+bytes[i]) * Math.pow(256, 4-i-1);

            }
            else {
                compacted += bytes[i] * Math.pow(256, 4-i-1);
            }
        }
        return compacted;
    }
			
	public static final String formatPhoneNumber(String phone) {
		if(phone==null || phone.trim().length()<=0) return phone;
		
		try {
			StringBuilder str = new StringBuilder();
			if(phone!=null && phone.length()>=3)
				str.append("(" + phone.substring(0,3) + ")");
			if(phone!=null && phone.length()>=6)
				str.append(" " + phone.substring(3,6));
			if(phone!=null && phone.length()>=10)
				str.append("-" + phone.substring(6));
			return str.toString();
		}catch(Exception e){
			return phone;
		}
		
	}
	public static final String shortenText(String str, int length ) {
		if(str!=null && str.length()>length)
			return str.substring(0, length) + "...";
		else
			return str;
	}
	
	public static final String toHtmlText(String str) {
		if(str==null) return str;
		
		//.replaceAll(String.valueOf(String.valueOf((char)34)), "&quot;")
		return encodeHtml(str)
					.replaceAll("\r\n","\n") //added for IE textarea compatibility
					.replaceAll("\n", "<br/>")
					.replaceAll("\r", "<br/>")
					.replaceAll("\t", "&nbsp;")
					.replaceAll("<br/> ", "<br/>&nbsp;");
	}
	
	public static final String encodeQuote(String str) {
		if(str==null) return str;
		
		return encodeHtml(str)
					.replaceAll(String.valueOf(String.valueOf((char)34)), "&quot;");
	}
	
	public static final String encodeHtml(String str) {
		if(str==null) return str;
		
		//.replaceAll(String.valueOf(String.valueOf((char)34)), "&quot;")
		return str.replaceAll(String.valueOf(String.valueOf((char)38)), "&amp;")
					.replaceAll(String.valueOf(String.valueOf((char)60)), "&lt;")
					.replaceAll(String.valueOf(String.valueOf((char)62)), "&gt;")
					.replaceAll(String.valueOf(String.valueOf((char)8216)), "&lsquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8217)), "&rsquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8220)), "&ldquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8221)), "&rdquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8218)), "&sbquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8222)), "&bdquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8242)), "&prime;")
					.replaceAll(String.valueOf(String.valueOf((char)8243)), "&Prime;")
					.replaceAll(String.valueOf(String.valueOf((char)160)), "&nbsp;")
					.replaceAll(String.valueOf(String.valueOf((char)8208)), "-")
					.replaceAll(String.valueOf(String.valueOf((char)8211)), "&ndash;")
					.replaceAll(String.valueOf(String.valueOf((char)8212)), "&mdash;")
					.replaceAll(String.valueOf(String.valueOf((char)8194)), "&ensp;")
					.replaceAll(String.valueOf(String.valueOf((char)8195)), "&emsp;")
					.replaceAll(String.valueOf(String.valueOf((char)8201)), "&thinsp;")
					.replaceAll(String.valueOf(String.valueOf((char)166)), "&brvbar;")
					.replaceAll(String.valueOf(String.valueOf((char)8226)), "&bull;")
					.replaceAll(String.valueOf(String.valueOf((char)8227)), "-")
					.replaceAll(String.valueOf(String.valueOf((char)8230)), "&hellip;")
					.replaceAll(String.valueOf(String.valueOf((char)710)), "&circ;")
					.replaceAll(String.valueOf(String.valueOf((char)168)), "&uml;")
					.replaceAll(String.valueOf(String.valueOf((char)732)), "&tilde;")
					.replaceAll(String.valueOf(String.valueOf((char)8249)), "&lsaquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8250)), "&rsaquo;")
					.replaceAll(String.valueOf(String.valueOf((char)171)), "&laquo;")
					.replaceAll(String.valueOf(String.valueOf((char)187)), "&raquo;")
					.replaceAll(String.valueOf(String.valueOf((char)8254)), "&oline;")
					.replaceAll(String.valueOf(String.valueOf((char)191)), "&iquest;")
					.replaceAll(String.valueOf(String.valueOf((char)161)), "&iexcl;")
					.replaceAll(String.valueOf(String.valueOf((char)8253)), "-");
	}	
	
	public static final String decodeHtml(String str) {
		if(str==null) return str;
		
		return str.replaceAll("&amp;", String.valueOf(String.valueOf((char)38)))
					.replaceAll("&lt;",String.valueOf(String.valueOf((char)60)))
					.replaceAll("&gt;",String.valueOf(String.valueOf((char)62)))
					.replaceAll("&lsquo;",String.valueOf(String.valueOf((char)8216)))
					.replaceAll("&rsquo;",String.valueOf(String.valueOf((char)8217)))
					.replaceAll("&ldquo;",String.valueOf(String.valueOf((char)8220)))
					.replaceAll("&rdquo;",String.valueOf(String.valueOf((char)8221)))
					.replaceAll("&sbquo;",String.valueOf(String.valueOf((char)8218)))
					.replaceAll("&bdquo;",String.valueOf(String.valueOf((char)8222)))
					.replaceAll("&prime;",String.valueOf(String.valueOf((char)8242)))
					.replaceAll("&Prime;",String.valueOf(String.valueOf((char)8243)))
					.replaceAll("&nbsp;",String.valueOf(String.valueOf((char)160)));
	}	
	
	public static final String stripXSSChars(String str) {
		if(str==null) return str;
		return str.replaceAll("\\\"", "")
					.replaceAll("'", "")
					.replaceAll("&", "")
					.replaceAll("<", "")
					.replaceAll(">", "");
	}
	
	public static final String encodeUniCode(String str) {
		if(str==null) return str;
		
		StringBuilder strbuilder = new StringBuilder();
		char[] charArray = str.toCharArray();
		int len = charArray.length;

		for(int x=0;x<len;++x) {
			if(charArray[x]>=127)
				strbuilder.append("&#" + (int)charArray[x] + ";");
			else
				strbuilder.append(charArray[x]);
		}
		
		return strbuilder.toString();
	}
	
	public static final String appendAppostrophe(String str) {
		if (str==null || str.length()<=0) return str;
		if(String.valueOf(str.charAt(str.length()-1)).equalsIgnoreCase("s")){
			return str + "'";
		} else {
			return str + "'s";
		}
			
	}

    /* replace multiple whitespaces between words with single blank */
    public static String itrim(String source) {
    	if (source==null) 
    		return source;
    	
    	return source
	    	.replaceAll("\r\n", "\n") 					//ie firefox issue of newline
	    	.replaceAll("\n", String.valueOf((char)5)) 	//masked newline
	    	.replaceAll("\\s+", " ")					//remove whitespaces between words
	    	.replaceAll(String.valueOf((char)5), "\n") 					//unmasked newline
	    	.trim();
    }
    public static String convertCommonSymble(String source) {
    	
    	if (source==null) 
    		return source;

    	String str = source 
    						//single quotes
    						.replaceAll("\u2018", "'")	
    						.replaceAll("\u2019", "'")
    						.replaceAll("\u201B", "'")
    						.replaceAll("\u02C8", "'")
    						.replaceAll("\u02CA", "'")
    						.replaceAll("\u02CB", "'")
    						.replaceAll("\u02BC", "'")
    						.replaceAll("\u02BD", "'")
    						.replaceAll("\u02BE", "'")
    						.replaceAll("\u02BF", "'")
    						
    						//comma				
    						.replaceAll("\u201A", ",")
    						.replaceAll("\u02CC", ",")
    						.replaceAll("\u02CE", ",")
    						.replaceAll("\u02CF", ",")
    						
    						//double quotes
    						.replaceAll("\u201C", "\"")
    						.replaceAll("\u201D", "\"")
    						.replaceAll("\u201F", "\"")
    						.replaceAll("\u02EE", "\"")
    						
    						//dash
    						.replaceAll("\u2010", "-")
    						.replaceAll("\u2011", "-")
    						.replaceAll("\u2012", "-")
    						.replaceAll("\u2013", "-")
    						.replaceAll("\u2014", "-")
    						.replaceAll("\u2015", "-")
    						
    						//underline
    						.replaceAll("\u02CD", "_")
    						
    						//colon
    						.replaceAll("\u02D0", ":")
    						.replaceAll("\u02F8", ":")
    						.replaceAll("\u0703", ":")
    						.replaceAll("\u0704", ":");
    	
    	return str;
    }

    public String stripHTMLTags(String str) {
    	if (str == null) return null;
    	return str.replaceAll("\\<.*?>","");
    }
    
	public static String escapeKeyword(String keyword) {
		Pattern p = Pattern.compile("(\\w*)(\\W*)(.*)");
		StringBuilder builder = new StringBuilder();
		String str = keyword.replaceAll("\\s", "_");
		while (StringUtils.isNotBlank(str)) {
			Matcher m = p.matcher(str);
			if (m.matches()) {
				if(builder.length()>1) 
					builder.append(".");
				
				builder.append(m.group(1));
				if (StringUtils.isNotBlank(m.group(2))) {
					builder.append(".").append(Hex.encodeHexString(m.group(2).getBytes()));
				}
				str = m.group(3);
			}
			else {
				builder.append(str);
				break;
			}
		}
		return builder.toString();
	}

    public static String[] toLowerCase(String[] strings) {
        if (strings != null) {
            String[] lower = new String[strings.length];
            int idx = 0;

            for (String string : strings) {
                lower[idx++] = lowercaseTransformer.apply(string);
            }

            return lower;
        } else
            return null;
    }

	public static Predicate<String> createIContainsPredicate(String target) {
	    return new IContainsPredicate(target);
	}

	public static Function<String, String> lowercaseTransformer = new Function<String, String>() {
	    public String apply(String orig) {
	        if (orig != null) {
	            return orig.toLowerCase();
	        }
	        
	        return orig;
	    }
	};

    private static class IContainsPredicate implements Predicate<String> {
        private String target;
        private IContainsPredicate(String target) {
            this.target = lowercaseTransformer.apply(target);
        }
        public boolean apply(String str) {
            return lowercaseTransformer.apply(str).contains(target);
        }
    }
}
