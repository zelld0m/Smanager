package com.search.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.search.manager.utility.PropsUtils;
import com.search.webservice.SearchGuiService;
import com.search.webservice.SearchGuiServiceImpl;

public class ForceCacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected final transient Logger logger = Logger.getLogger(this.getClass());

	private static SearchGuiService searchGuiService;
	
	private static String token;
	private static String store;
	
	static{
		try {
			token = PropsUtils.getValue("token");
			store = PropsUtils.getValue("store");
			searchGuiService = new SearchGuiServiceImpl();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
       
    public ForceCacheServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			synchronized (this) {	
				logger.info("########### Start loading to cache");
//				
//				if(searchGuiService.loadElevateList(store, token)){
//					if(searchGuiService.loadExcludeList(store, token)){
//						if(searchGuiService.loadRelevancyList(store, token)){
//							searchGuiService.loadRelevancyDetails(store, token);
//						}
//					}
//				}

				logger.info("########### Done loading to cache");
			}
		}catch (Exception e) {
			logger.error(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}}
