package com.search.manager.workflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
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
}