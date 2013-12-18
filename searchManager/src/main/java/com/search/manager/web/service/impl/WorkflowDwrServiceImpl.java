package com.search.manager.web.service.impl;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Component;

@Component
@RemoteProxy(name = "WorkflowService", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "workflowDwrService"))
public class WorkflowDwrServiceImpl {
    
}