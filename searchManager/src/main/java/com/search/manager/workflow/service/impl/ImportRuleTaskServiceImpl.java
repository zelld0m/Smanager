package com.search.manager.workflow.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.workflow.dao.ImportRuleTaskDAO;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.service.ImportRuleTaskService;

@Service
public class ImportRuleTaskServiceImpl implements ImportRuleTaskService{
    @Autowired private ImportRuleTaskDAO importRuleTaskDAO;

    @Override
    public void addImportRuleTask(ImportRuleTask importRuleTask) {
         try {
            importRuleTaskDAO.addImportRuleTask(importRuleTask);
        } catch (DaoException e) {
           
        }
    }
    
    @Override
    public RecordSet<ImportRuleTask> getImportRuleTasks(SearchCriteria<ImportRuleTask> criteria){
        RecordSet<ImportRuleTask> recordSet = new RecordSet<ImportRuleTask>(new ArrayList<ImportRuleTask>(), 0);
        try {
            recordSet = importRuleTaskDAO.getImportRuleTask(criteria, null);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return recordSet;
    }
}