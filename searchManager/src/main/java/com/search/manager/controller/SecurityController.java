package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/security")
@Scope(value="prototype")
public class SecurityController {
	
	private static final Logger logger = Logger.getLogger(SecurityController.class);
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		model.addAttribute("timezoneIds", DateTimeZone.getAvailableIDs());
		return "security/security";
	}
}
