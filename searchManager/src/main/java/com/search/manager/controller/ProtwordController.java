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

import com.search.manager.service.ProtwordService;

@Controller
@RequestMapping("/protword")
@Scope(value="prototype")
public class ProtwordController {

	private static final Logger logger = Logger.getLogger(ProtwordController.class);
	
	@SuppressWarnings("unused")
	@Autowired private ProtwordService protwordService;
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);

		return "lexicon/protword";
	}
}
