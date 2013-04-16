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
		String storeTimeZoneId = cm.getStoreParameter(storeId, "default-timezone");
		String userTimeZoneId = userDetailsImpl.getDateTimeZoneId();
		
		// update if user has defined timezone
		if((StringUtils.isNotBlank(userTimeZoneId) && !DateTimeZone.getDefault().getID().equalsIgnoreCase(userTimeZoneId)) || 
		   (StringUtils.isBlank(userTimeZoneId) && !DateTimeZone.getDefault().getID().equalsIgnoreCase(storeTimeZoneId))){

			DateTimeZone dateTimezone = JodaDateTimeUtil.setTimeZoneID(userTimeZoneId, storeTimeZoneId);
			logger.info(String.format("-DTZ- Joda timezone update to %s : %s", dateTimezone.getID(), DateTimeZone.getDefault().getID().equalsIgnoreCase(dateTimezone.getID())? "SUCCESS": "FAILED"));
		}else{
			String currentTimeZoneId = DateTimeZone.getDefault().getID();
			logger.info(String.format("-DTZ- No timezone update required, currently set to %s: %s", currentTimeZoneId, (currentTimeZoneId.equalsIgnoreCase(userTimeZoneId)? "user-defined" : ""), (currentTimeZoneId.equalsIgnoreCase(storeTimeZoneId)? "store-default" : "")));
		}
				
		return true;
	}      
}