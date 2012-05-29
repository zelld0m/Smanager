package com.search.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.search.manager.utility.PropsUtils;
import com.search.thread.LoadStoreRuleThread;

public class ForceCacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ForceCacheServlet.class);
	
	private static String[] stores;
	
	static{
		stores = PropsUtils.getValue("store").split(",");
	}
       
    public ForceCacheServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
			List<String> storeList = Arrays.asList(stores);
			String param = request.getParameter("store");
			
			if(StringUtils.isNotEmpty(param)){
				String[] param_ = param.split(",");
				
				for(String par : param_){
					if(!storeList.contains(par)){
						 out.println("Invalid store parameter...");
						 out.close();
						 return;
					}
				}
				
				storeList = Arrays.asList(param_);
			}
				
			for(String store : storeList){
				LoadStoreRuleThread th = new LoadStoreRuleThread(store);
				th.start();
				
				while(true){
					if(!th.isAlive()){
						out.println("Rules successfully loaded to cache...");
						out.close();
						break;
					}
				}
			}	
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}}
