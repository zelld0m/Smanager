package com.search.manager.dao.sp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public class DAOUtils {

	private static final Logger logger = Logger.getLogger(DAOUtils.class);

	private static final SecureRandom random = new SecureRandom();

	private static final String ENCODING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final int 	BASE = 62; // ENCODING.length();
	public  static final String MAC;
	public  static final int 	MAX_RANDOM_INT = 238327; // decodeString("zzz");
	
	// get MAC Address
	static {
		long l = 0;
		try {
			byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			for (int i = 0; i < mac.length; i++) {
				l *= 0x100;
				l += (long)(mac[i] & 0xFF);
			}
		} catch (Exception e) {
			logger.error("Unable to generate unique id for this machine!", e);
			l = 0;
		}
		MAC = (l == 0) ? null : StringUtils.leftPad(encodeString(l), 9, "0");
	}
	
	private static char toEncodedChar(int n) {
		return ENCODING.charAt(n);
	}
	
	private static String encodeString(long d) {
	    int r = (int)(d % BASE);
		String result = "";
		if (d-r == 0) {
		    result += toEncodedChar(r);
		}
		else {
		    result = encodeString((d-r) / BASE) + toEncodedChar(r);
		}
		return result;
	}

	private static int toDecodedChar(char c) {
		int index = ENCODING.indexOf(c);
		if (index < 0) {
			throw new IllegalArgumentException("Illegal encoding");
		}
		return index;
	}
	
	@SuppressWarnings("unused")
	private static int decodeString(String s) {
		int r = 0;
		if (StringUtils.isNotBlank(s)) {
			if (s.length() == 1) {
				r = toDecodedChar(s.charAt(0));
			}else {
				int base = 1;
				for (int i = s.length() - 1; i > 0; i--) {
					base *= BASE;
				}
				r = toDecodedChar(s.charAt(0)) * base + decodeString(s.substring(1));
			}
		}
		return r;
	}
	
	public static String generateUniqueId() {
		if (MAC == null) {
			throw new RuntimeException("MAC is unknown. Cannot generate unique id.");
		}
		StringBuilder builder = new StringBuilder();
		builder.append(MAC) // mac address
			   .append(StringUtils.leftPad(encodeString(Calendar.getInstance().getTimeInMillis()), 8, "0")) // timestamp
			   .append(StringUtils.leftPad(encodeString(random.nextInt(MAX_RANDOM_INT)), 3, "0")); // random int
		return builder.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static int getUpdateCount(Map<String,Object> result) {
		int i = -1;
    	if (result != null) {
    		try {
        		i = ((List<Integer>)result.get(DAOConstants.RESULT_SET_RESULT)).get(0);
    		}
    		catch (Exception e) {
    			logger.error("failed to get update count" , e);
    		}
    	}
		return i;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> RecordSet<T> getRecordSet(Map<String,Object> result) {
	    ArrayList<T> list = new ArrayList<T>();
	    int size = 0;
	    if (result != null) {
	    	list.addAll((List<T>)result.get(DAOConstants.RESULT_SET_1));
	    	size = ((List<Integer>)result.get(DAOConstants.RESULT_SET_TOTAL)).get(0);
	    }
		return new RecordSet<T>(list, size);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(Map<String,Object> result) {
	    ArrayList<T> list = new ArrayList<T>();
	    if (result != null) {
	    	list.addAll((List<T>)result.get(DAOConstants.RESULT_SET_1));
	    }
		return list;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Returns the first matching record if present; otherwise, null;
	 */
	public static <T> T getItem(Map<String,Object> result) {
		T item = null;
        if (result != null) {
        	List<T> list = (List<T>)result.get(DAOConstants.RESULT_SET_1);
        	if (!list.isEmpty()) {
            	item = list.get(0);
        	}
        }
    	return item;
	}
	
	public static String getStoreId(Store store) {
		return StringUtils.lowerCase(StringUtils.trim(store.getStoreId()));
	}
	
	public static String getStoreId(StoreKeyword storeKeyword) {
		return StringUtils.lowerCase(StringUtils.trim(storeKeyword.getStoreId()));
	}

	public static String getKeywordId(Keyword keyword) {
		return StringUtils.lowerCase(StringUtils.trim(keyword.getKeywordId()));
	}
	
	public static String getKeywordId(StoreKeyword storeKeyword) {
		return StringUtils.lowerCase(StringUtils.trim(storeKeyword.getKeywordId()));
	}

}
