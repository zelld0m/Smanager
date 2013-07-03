package com.search.ws;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.search.manager.schema.MessagesConfig;
import com.search.manager.schema.RelevancyConfig;

public class ConfigServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String fileSeparator;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public void init(final ServletConfig config) throws ServletException {
		fileSeparator = System.getProperty("file.separator");
		
		// server config
		String conf = config.getInitParameter("config-folder") + fileSeparator + config.getInitParameter("config-file");
		logger.info("config file: " + conf);
		ConfigManager.getInstance(conf);
		
		// enterprise search config
		conf = config.getInitParameter("config-folder") + fileSeparator + config.getInitParameter("enterpriseSearch-config-file");
		logger.info("enterprise search config file: " + conf);
		EnterpriseSearchConfigManager.getInstance(conf);

		// relevancy config
		conf = config.getInitParameter("config-folder") + fileSeparator + config.getInitParameter("relevancy-config-file");
		logger.info("relevancy config file: " + conf);
		RelevancyConfig.getInstance(conf);
		
		// messages config
		conf = config.getInitParameter("config-folder") + fileSeparator + config.getInitParameter("messages-config-file");
		logger.info("messages config file: " + conf);
		MessagesConfig.getInstance(conf);
		
	}
}