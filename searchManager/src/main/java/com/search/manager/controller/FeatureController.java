package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.service.FeatureService;

@Controller
@RequestMapping("/feature")
@Scope(value="prototype")
public class FeatureController {

	private static final Logger logger = Logger.getLogger(FeatureController.class);
	
	@SuppressWarnings("unused")
	@Autowired private FeatureService featureService;
	
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model,@PathVariable String store){
		model.addAttribute("store", store);

		return "feature/feature";
	}
}
