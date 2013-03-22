package com.search.manager.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.search.manager.jodatime.JodaTimeUtil;

public class PageRequestInterceptor extends HandlerInterceptorAdapter{
	private static final Logger logger = Logger.getLogger(PageRequestInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		logger.info(String.format("after completion %s", request.getRequestURI()));
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {

		logger.info(String.format("Pre handling", request.getRequestURI()));
		JodaTimeUtil.setTimeZoneID("UTC", null);
		return true;
	}      
}