package com.search.manager.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.service.RelevancyService;

@Controller
@RequestMapping("/relevancy")
@Scope(value="prototype")
public class RelevancyController {

	@Autowired
	private RelevancyService relevancyService;
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		
		Map<String, String> longFields = new LinkedHashMap<String, String>();
		longFields.put("qf","Query Fields");
		longFields.put("bf","Boost Function");
		longFields.put("pf","Phrase Field");
		longFields.put("bq","Boost Query");
		
		Map<String, String> shortFields = new LinkedHashMap<String, String>();
		shortFields.put("mm","Min To Match");
		shortFields.put("qs","Query Slop");
		shortFields.put("tie","Tie Breaker");
		shortFields.put("ps","Phrase Slop");
		shortFields.put("q.alt","Q Alt");
		
		model.addAttribute("store", store);
		model.addAttribute("longFields", longFields);
		model.addAttribute("shortFields", shortFields);
		return "relevancy/relevancy";
	}
}
