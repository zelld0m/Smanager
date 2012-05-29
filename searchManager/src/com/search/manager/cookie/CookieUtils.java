package com.search.manager.cookie;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

public class CookieUtils {

	public static Cookie expireNow(String cookieName, String path){
		Cookie cookie = new Cookie(cookieName, "");
		cookie.setMaxAge(-1);
		if (StringUtils.isNotBlank(path)) cookie.setPath(path);
		cookie.setComment("EXPIRING COOKIE at " + System.currentTimeMillis());
		
		return cookie;
	}
}
