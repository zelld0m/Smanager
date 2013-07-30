package com.search.manager.service;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "featureService")
@RemoteProxy(
        name = "FeatureServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "featureService"))
public class FeatureService {

    private static final Logger logger =
            LoggerFactory.getLogger(FeatureService.class);
    @Autowired
    private DaoService daoService;

    @RemoteMethod
    public int addFeature(String storeId, String keyword, String productId) {
        logger.info(String.format("%s %s %s", storeId, keyword, productId));
        return daoService.hashCode();
    }

    @RemoteMethod
    public int removeFeature(String storeId, String keyword, String productId) {
        logger.info(String.format("%s %s %s", storeId, keyword, productId));
        return 0;
    }

    public DaoService getDaoService() {
        return daoService;
    }

    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }
}