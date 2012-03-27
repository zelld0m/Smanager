package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@Scope(value="prototype")
public class DashboardController {

	@RequestMapping
	public String execute(HttpServletRequest request,HttpServletResponse response,Model model) {
		model.addAttribute("keyword", StringUtils.defaultIfBlank(request.getParameter("keyword"), "apple"));
		return "dashboard/dashboard";
	}
	
}
