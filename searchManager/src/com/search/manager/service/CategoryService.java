package com.search.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.search.ws.SearchHelper;

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

	@RemoteMethod
	public List<String> getIMSCategories() {
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "Category");
	}
	
	
	@RemoteMethod
	public List<String> getIMSSubcategories(String category) {
		if (StringUtils.isBlank(category)) {
			return new ArrayList<String>();
		}
		List<String> filters = new ArrayList<String>();
		filters.add(String.format("Category: %s", category));
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "SubCategory", filters);
	}	

	@RemoteMethod
	public List<String> getIMSClasses(String category, String subcategory) {
		if (StringUtils.isBlank(category) || StringUtils.isBlank(subcategory)) {
			return new ArrayList<String>();
		}
		List<String> filters = new ArrayList<String>();
		filters.add(String.format("Category: %s", category));
		filters.add(String.format("SubCategory: %s", subcategory));
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "Class", filters);
	}	

	@RemoteMethod
	public List<String> getIMSSubclasses(String category, String subcategory, String className) {
		if (StringUtils.isBlank(category) || StringUtils.isBlank(subcategory) || StringUtils.isBlank(className)) {
			return new ArrayList<String>();
		}
		List<String> filters = new ArrayList<String>();
		filters.add(String.format("Category: %s", category));
		filters.add(String.format("SubCategory: %s", subcategory));
		filters.add(String.format("Class: %s", className));
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "SubClass", filters);
	}

	@RemoteMethod
	public List<String> getIMSManufacturers(String category, String subcategory, String className, String subclass) {
		List<String> filters = new ArrayList<String>();
		if (StringUtils.isNotBlank(category)) {
			filters.add(String.format("Category: %s", category));
		}
		if (StringUtils.isNotBlank(category)) {
			filters.add(String.format("SubCategory: %s", subcategory));
		}
		if (StringUtils.isNotBlank(category)) {
			filters.add(String.format("Class: %s", className));
		}
		if (StringUtils.isNotBlank(subclass)) {
			filters.add(String.format("SubClass: %s", subclass));
		}
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "Manufacturer", filters);
	}
	
}