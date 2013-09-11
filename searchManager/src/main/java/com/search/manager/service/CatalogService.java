package com.search.manager.service;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "catalogService")
@RemoteProxy(
        name = "CatalogServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "catalogService"))
public class CatalogService {

    @SuppressWarnings("unused")
    private static final Logger logger =
            LoggerFactory.getLogger(CatalogService.class);
    @Autowired
    private DaoService daoService;

    public DaoService getDaoService() {
        return daoService;
    }

    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }
}