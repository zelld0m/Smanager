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
import com.search.manager.service.CatalogService;
import com.search.manager.service.UtilityService;

@Controller
@RequestMapping("/catalog")
@Scope(value="prototype")
public class CatalogController {

	private static final Logger logger = Logger.getLogger(CatalogController.class);
	
	@Autowired private DaoCacheService daoCacheService;
	@SuppressWarnings("unused")
	@Autowired private CatalogService catalogService;
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model,@PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Catalog");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "catalog/catalog";
	}
}
