package com.search.manager.workflow.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.utility.PropertiesUtils;
import com.search.manager.workflow.model.ImportTaskManager;

public class ImportRulesJob {

	private static final Logger logger =
			LoggerFactory.getLogger(ImportRulesJob.class);

	@Autowired
	private ImportTaskManager importTaskManager;

	public void importRules() {

		if("true".equals(PropertiesUtils.getValue("enableAutoImportJob"))) {
			logger.info("Running ImportRules job...");
			try {
				importTaskManager.importRules();
			} catch (CoreServiceException e) {
				logger.error("error in ImportRulesJob.importRules(", e);
			}
		} else {
			logger.info("ImportRulesJob not enabled....");
		}
	}

}
