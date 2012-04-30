package com.search.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.search.manager.enums.RuleEntity;
import com.search.manager.utility.PropsUtils;
import com.search.thread.LoadRuleThread;

public class ForceCacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(ForceCacheServlet.class);

	private static String store;
	
	static{
		try {
			store = PropsUtils.getValue("store");
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
       
    public ForceCacheServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
				// TODO: put some checking if parameter will force a reload or perform the reload at once.
				LoadRuleThread th1 = new LoadRuleThread(store, RuleEntity.ELEVATE);
				LoadRuleThread th2 = new LoadRuleThread(store, RuleEntity.EXCLUDE);
				LoadRuleThread th3 = new LoadRuleThread(store, RuleEntity.QUERY_CLEANING);
				LoadRuleThread th4 = new LoadRuleThread(store, RuleEntity.RANKING_RULE);
				
				th1.start();
				th2.start();
				th3.start();
				th4.start();
				
				while(true){
					if(!th1.isAlive() && !th2.isAlive() && !th3.isAlive() && !th4.isAlive()){
						logger.info("########## Rules successfully loaded to cache ...");	
						PrintWriter out = response.getWriter();
					    out.println("Rules successfully loaded to cache ...");
					    out.close();
						break;
					}
				}
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}}
