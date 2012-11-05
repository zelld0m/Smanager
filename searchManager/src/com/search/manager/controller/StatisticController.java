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

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.service.UtilityService;

@Controller
@RequestMapping("/")
@Scope(value="prototype")
public class StatisticController {

	private static final Logger logger = Logger.getLogger(StatisticController.class);
	
	@Autowired DaoCacheService daoCacheService;
	
	@RequestMapping(value="/topkeyword/{store}")
	public String topKeyword(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Top Keyword");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "statistic/topkeyword";
	}
	
	@RequestMapping(value="/zeroresult/{store}")
	public String zeroResult(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Zero Result");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "statistic/zeroresult";
	}
	
	@RequestMapping(value="/keywordtrends/{store}")
	public String keywordTrends(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Keyword Trends");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "statistic/keywordtrends";
	}
	
	@RequestMapping(value="/reportgenerator/{store}")
	public String reportGenerator(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Report Generator");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "statistic/reportgenerator";
	}
}
