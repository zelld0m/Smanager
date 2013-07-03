package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.service.UtilityService;

@Controller
@RequestMapping("/")
public class DashboardController{
	
	private static final Logger logger = Logger.getLogger(DashboardController.class);
	
	@Autowired private DaoCacheService daoCacheService;

	@RequestMapping
	public String execute(HttpServletRequest request,HttpServletResponse response,Model model) {
		model.addAttribute("keyword", StringUtils.defaultIfBlank(request.getParameter("keyword"), "apple"));
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Dashboard");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "dashboard/dashboard";
	}
}