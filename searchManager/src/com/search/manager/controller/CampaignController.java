package com.search.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.service.CampaignService;

@Controller
@RequestMapping("/campaign")
@Scope(value="prototype")
public class CampaignController {
	
	@Autowired
	private CampaignService campaignService;
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model,@PathVariable String store){
		model.addAttribute("store", store);
		model.addAttribute("campaignList", campaignService.getCampaignList(store));
		
		return "campaign/campaign";
	}
	
	@RequestMapping(value="/add/{store}")
	public String addCampaign(HttpServletRequest request,HttpServletResponse response, Model model,@PathVariable String store){
		model.addAttribute("store", store);
		return "campaign/addcampaign";
	}
	
}