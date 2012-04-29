package com.search.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.search.manager.utility.PropsUtils;
import com.search.service.DeploymentRuleService;
import com.search.service.DeploymentRuleServiceImpl;

public class ForceCacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(ForceCacheServlet.class);


	private static DeploymentRuleService deploymentRuleService;

	private static String store;
	
	static{
		try {
			store = PropsUtils.getValue("store");
			deploymentRuleService = new DeploymentRuleServiceImpl();
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
       
    public ForceCacheServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			synchronized (this) {
				// TODO: put some checking if parameter will force a reload or perform the reload at once.
				processElevateRules();
				processExcludeRules();
				processRedirectRules();
				processRankingRules();
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
	
	public Integer processElevateRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to elevated rules ...");
					deploymentRuleService.loadElevateRules(store);
					logger.info("########### Done loading to elevated rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
	
	public Integer processExcludeRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to excluded rules ...");
					deploymentRuleService.loadExcludeRules(store);
					logger.info("########### Done loading to excluded rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
	
	public Integer processRedirectRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to redirect rules ...");
					deploymentRuleService.loadRedirectRules(store);
					logger.info("########### Done loading to redirect rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
	
	public Integer processRankingRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to ranking rules ...");
					deploymentRuleService.loadRankingRules(store);
					logger.info("########### Done loading to ranking rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}}
