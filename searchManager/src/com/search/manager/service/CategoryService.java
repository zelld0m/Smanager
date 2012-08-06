package com.search.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;
import com.search.manager.exception.DataException;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.utility.CatCodeUtil;
import com.search.manager.utility.CatCodeUtil.Attribute;
import com.search.ws.ConfigManager;
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
	public List<String> getIMSCategories() throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel("","","");
	}
	
	@RemoteMethod
	public List<String> getIMSSubcategories(String category) throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel(category,"","");
	}	

	@RemoteMethod
	public List<String> getIMSClasses(String category, String subcategory) throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel(category,subcategory,"");
	}	

	@RemoteMethod
	public List<String> getIMSMinors(String category, String subcategory, String className) throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel(category,subcategory,className);
	}

	@RemoteMethod
	public List<String> getIMSManufacturers(String catcode, String category, String subcategory, String className, String subclass) {
		List<String> filters = new ArrayList<String>();
		if (StringUtils.isNotBlank(catcode)) {
			filters.add(String.format("CatCode: %s", catcode));
		}
		if (StringUtils.isNotBlank(category)) {
			filters.add(String.format("Category: %s", category));
		}
		if (StringUtils.isNotBlank(subcategory)) {
			filters.add(String.format("SubCategory: %s", subcategory));
		}
		if (StringUtils.isNotBlank(className)) {
			filters.add(String.format("Class: %s", className));
		}
		if (StringUtils.isNotBlank(subclass)) {
			filters.add(String.format("SubClass: %s", subclass));
		}
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "Manufacturer", filters);
	}
	
	@RemoteMethod
	public List<String> getCNETLevel1Categories() throws DataException {
		return CatCodeUtil.getCNETNextLevel("","");
	}
	
	@RemoteMethod
	public List<String> getCNETLevel2Categories(String level1Category) throws DataException {
		return CatCodeUtil.getCNETNextLevel(level1Category, "");
	}
	
	@RemoteMethod
	public List<String> getCNETLevel3Categories(String level1Category, String level2Category) throws DataException {
		return CatCodeUtil.getCNETNextLevel(level1Category, level2Category);
	}
	
	@RemoteMethod
	public List<String> getCNETManufacturers(String level1Category, String level2Category, String level3Category) {
		Map<String, List<String>> filter = new HashMap<String, List<String>>();
		ArrayList<String> filters = null;
		if (StringUtils.isNotBlank(level1Category)) {
			filters = new ArrayList<String>();
			filters.add(level1Category);
			filter.put("Level1Category", new ArrayList<String>(filters));
			if (StringUtils.isNotBlank(level2Category)) {
				filters = new ArrayList<String>();
				filters.add(level2Category);
				filter.put("Level2Category", new ArrayList<String>(filters));
				if (StringUtils.isNotBlank(level3Category)) {
					filters = new ArrayList<String>();
					filters.add(level3Category);
					filter.put("Level3Category", new ArrayList<String>(filters));
				}
			}
		}
		filters = new ArrayList<String>();
		RedirectRuleCondition rr = new RedirectRuleCondition();
		rr.setFilter(filter);
		filters.add(rr.getConditionForSolr());
		return SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), "Manufacturer", filters);
	}
	
	@RemoteMethod
	public List<String> getIMSTemplateNames() throws DataException {
		return CatCodeUtil.getAllIMSTemplates();
	}
	
	@RemoteMethod
	public List<String> getCNETTemplateNames() throws DataException {
		return CatCodeUtil.getAllCNETTemplates();
	}

	@RemoteMethod
	public List<Attribute> getIMSTemplateAttributes(String templateName) throws DataException {
		List<Attribute> attrList = new ArrayList<Attribute>();

		ArrayList<String> filters = new ArrayList<String>();
		filters.add("TemplateName:\"" + templateName + "\"");
		ArrayList<String> fields = new ArrayList<String>();

		for (Attribute a: CatCodeUtil.getIMSTemplateAttribute(templateName)) {
			attrList.add(new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
			fields.add(a.getAttributeName());
		}	
		
		Map<String,List<String>> map = SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(),
				fields, filters, false);

		for (Attribute a: attrList) {
			List<String> values = map.get(a.getAttributeName());
			if (values != null) {
				for (String value: values) {
					a.addAttributeValue(value);
				}				
			}
		}
		
		return attrList;
	}

	@RemoteMethod
	public List<Attribute> getCNETTemplateAttributes(String templateName) throws DataException {
		List<Attribute> attrList = new ArrayList<Attribute>();
		String storeId = UtilityService.getStoreName();

		ArrayList<String> filters = new ArrayList<String>();
		ArrayList<String> fields = new ArrayList<String>();

		for (Attribute a: CatCodeUtil.getCNETTemplateAttribute(templateName)) {
			attrList.add(new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
			fields.add(a.getAttributeName());
		}

		String templateNameField = ConfigManager.getInstance().getParameterByCore(storeId, "facet-template");
		if (StringUtils.isNotEmpty(templateNameField)) {
			filters.add(templateNameField + "Name:\"" + templateName + "\"");

			Map<String,List<String>> map = SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(),
					fields, filters,false);

			for (Attribute a: attrList) {
				List<String> values = map.get(a.getAttributeName());
				if (values != null) {
					for (String value: values) {
						a.addAttributeValue(value);
					}					
				}
			}			
		}
		
		return attrList;
	}

}