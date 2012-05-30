package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/denied")
public class DeniedController {
	
	@RequestMapping(value="/")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model){
		return "denied/denied";
	}
}
