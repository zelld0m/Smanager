package com.search.manager.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.service.UtilityService;
import com.search.ws.ConfigManager;

public class PageRequestInterceptor extends HandlerInterceptorAdapter{
	private static final Logger logger = Logger.getLogger(PageRequestInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		logger.info(String.format("Prehandle request for %s", request.getRequestURL()));
		
		ConfigManager cm = ConfigManager.getInstance();
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		String storeId = UtilityService.getStoreId();
		String storeTimezoneId = cm.getStoreParameter(storeId, "default-timezone");
		String userTimezoneId = userDetailsImpl.getDateTimeZoneId();
		
		// update if user has defined timezone
		if(StringUtils.isNotBlank(userTimezoneId) && !DateTimeZone.getDefault().getID().equalsIgnoreCase(userTimezoneId) || 
				!DateTimeZone.getDefault().getID().equalsIgnoreCase(storeTimezoneId)){

			DateTimeZone dateTimezone = JodaDateTimeUtil.setTimeZoneID(userTimezoneId, storeTimezoneId);
			UtilityService.setTimeZoneId(dateTimezone.getID());
			logger.info(String.format("Attempted to set timezone to %s : %s", dateTimezone.getID(), dateTimezone.getID().equalsIgnoreCase(UtilityService.getTimeZoneId())? "SUCCESS": "FAILED"));
		}
				
		return true;
	}      
}