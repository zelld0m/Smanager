package com.search.manager.workflow.service.impl;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.workflow.dao.impl.ImportRuleTaskDAOImpl;
import com.search.manager.workflow.model.ImportRuleTask;

@Service(value = "ImportRuleTaskService")
@RemoteProxy(name = "ImportRuleTaskServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "importRuleTaskService"))
public class ImportRuleTaskService {
	private static final Logger logger = LoggerFactory
			.getLogger(ImportRuleTaskService.class);
	@Autowired
	private ImportRuleTaskDAOImpl dao;
	private static final int ROW_PER_PAGE=10;

	public RecordSet<ImportRuleTask> getImportRuleTasks(int pageNumber) throws DaoException {
		ImportRuleTask importRuleTask = new ImportRuleTask(null, null, null, null, null, null, null, null, null, null);
		SearchCriteria<ImportRuleTask> criteria = new SearchCriteria<ImportRuleTask>(importRuleTask,pageNumber,ROW_PER_PAGE);		
		RecordSet<ImportRuleTask> recordSet = dao.getImportRuleTask(criteria,null);
		return recordSet;
	}


}