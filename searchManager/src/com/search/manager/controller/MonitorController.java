package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@RequestMapping("/")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model){
		return "monitor/monitor";
	}
}