package com.search.manager.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.response.ServiceResponse;
import com.search.manager.web.service.ImportRuleTaskDwrService;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.model.TaskExecutionResult;
import com.search.manager.workflow.model.TaskStatus;
import com.search.manager.workflow.service.ImportRuleTaskService;

@Component
@RemoteProxy(name = "ImportRuleTaskService", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "importRuleTaskService"))
public class ImportRuleTaskDwrServiceImpl implements ImportRuleTaskDwrService{

    @Autowired
    private ImportRuleTaskService importRuleTaskService;

    @Override
    @RemoteMethod
    public ServiceResponse<List<ImportRuleTask>> getTasks(String storeId, String ruleEntityType, String ruleId) {
        ImportRuleTask importRuleTask = new ImportRuleTask();
        importRuleTask.setTargetStoreId(storeId);
        importRuleTask.setTargetRuleId(ruleId);
        importRuleTask.setRuleEntity(RuleEntity.find(ruleEntityType));
        SearchCriteria<ImportRuleTask> criteria = new SearchCriteria<ImportRuleTask>(importRuleTask);
        RecordSet<ImportRuleTask> recordSet = importRuleTaskService.getImportRuleTasks(criteria);
        
        List<ImportRuleTask> importRuleTaskList = new ArrayList<ImportRuleTask>(recordSet.getList());
        
        CollectionUtils.filter(importRuleTaskList, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                ImportRuleTask task = (ImportRuleTask) object;   
                TaskExecutionResult result = task.getTaskExecutionResult();
                TaskStatus taskStatus = result.getTaskStatus();
                return taskStatus!=TaskStatus.COMPLETED;
            }
        });
        
        ServiceResponse<List<ImportRuleTask>> serviceResponse = new ServiceResponse<List<ImportRuleTask>>();
        serviceResponse.success(importRuleTaskList);
        return serviceResponse;
    }
}