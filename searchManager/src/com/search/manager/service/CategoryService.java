package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.CategoryList;

@Service(value = "categoryService")
@RemoteProxy(
		name = "CategoryServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "categoryService")
	)
public class CategoryService {
	private static final Logger logger = Logger.getLogger(CategoryService.class);
	
	@Autowired private DaoService daoService;
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	
	@RemoteMethod
	public CategoryList getCategories(String categoryCode) {
		try {
			return daoService.getCategories(categoryCode);
		} catch (DaoException e) {
			logger.error("Failed during getCategories()",e);
		}
		return null;
	}	

}