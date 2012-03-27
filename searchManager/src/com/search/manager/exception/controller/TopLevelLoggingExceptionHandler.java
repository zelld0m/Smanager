package com.search.manager.exception.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * A top level Spring MVC exception handler that handles all exceptions from
 * out Spring MVC controllers.
 */
public class TopLevelLoggingExceptionHandler implements HandlerExceptionResolver {

	private static final String VIEW_NAME = "exception/exception";
	/** A logger. */
	private static final Logger logger = Logger.getLogger(TopLevelLoggingExceptionHandler.class);
	
	public TopLevelLoggingExceptionHandler() {
		logger.info("Constructed top level error handler.");
	}
	
	/**
	 * Log all application exceptions and return them at the top level.
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

		// log the exception
		logger.error("Caught exception at top level.", e);
		
		// return the exception view
		ModelAndView modelAndView = new ModelAndView(VIEW_NAME);
		return modelAndView;
	}
	
	/**
	 * Log HTTP exceptions forwarded by the Web Container, via our error entries in the web.xml  
	 */
	@RequestMapping
	public String handleError(HttpServletRequest httpRequest, Model model) {
		String requestUri = httpRequest.getRequestURI();
		logger.error("Error with URI [" + requestUri + "].");
		
		return VIEW_NAME;
	}
}