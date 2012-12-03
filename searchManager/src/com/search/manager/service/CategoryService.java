package com.search.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

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
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CategoryService.class);
	
	@RemoteMethod
	public static List<String> getIMSCategories() throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel("","","");
	}
	
	@RemoteMethod
	public static List<String> getIMSSubcategories(String category) throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel(category,"","");
	}	

	@RemoteMethod
	public static List<String> getIMSClasses(String category, String subcategory) throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel(category,subcategory,"");
	}	

	@RemoteMethod
	public static List<String> getIMSMinors(String category, String subcategory, String className) throws DataException {
		return CatCodeUtil.getIMSCategoryNextLevel(category,subcategory,className);
	}

	@RemoteMethod
	public static List<String> getIMSManufacturers(String catcode, String category, String subcategory, String className, String subclass) {
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
	public static List<String> getCNETLevel1Categories() throws DataException {
		return CatCodeUtil.getCNETNextLevel("","");
	}
	
	@RemoteMethod
	public static List<String> getCNETLevel2Categories(String level1Category) throws DataException {
		return CatCodeUtil.getCNETNextLevel(level1Category, "");
	}
	
	@RemoteMethod
	public static List<String> getCNETLevel3Categories(String level1Category, String level2Category) throws DataException {
		return CatCodeUtil.getCNETNextLevel(level1Category, level2Category);
	}
	
	@RemoteMethod
	public static List<String> getCNETManufacturers(String level1Category, String level2Category, String level3Category) {
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
	public static List<String> getTemplateNamesByStore(String storeId) throws DataException {
		if("macmall".equalsIgnoreCase(storeId)) {
			return getIMSTemplateNames();
		}else if("pcmall".equalsIgnoreCase(storeId) || "pcmallcap".equalsIgnoreCase(storeId) || "sbn".equalsIgnoreCase(storeId)) {
			return getCNETTemplateNames();			
		}else if("onsale".equalsIgnoreCase(storeId)) {
			return null;			
		}else {
			throw new DataException("Unrecognized Store: " + storeId);
		}
	}
	
	@RemoteMethod
	public static List<String> getIMSTemplateNames() throws DataException {
		return CatCodeUtil.getAllIMSTemplates();
	}
	
	@RemoteMethod
	public static List<String> getCNETTemplateNames() throws DataException {
		return CatCodeUtil.getAllCNETTemplates();
	}

	@RemoteMethod
	public static Map<String, Attribute> getIMSTemplateAttributes(String templateName) throws DataException {
		Map <String, Attribute> attrMap = new LinkedHashMap<String, Attribute>();

		ArrayList<String> filters = new ArrayList<String>();
		filters.add("TemplateName:\"" + templateName + "\"");
		ArrayList<String> fields = new ArrayList<String>();

		for (Attribute a: CatCodeUtil.getIMSTemplateAttribute(templateName)) {
			attrMap.put(a.getAttributeName(), new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
			fields.add(a.getAttributeName());
		}	
		
		Map<String,List<String>> map = SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(),
				fields, filters, false);

		for (Attribute a: attrMap.values()) {
			List<String> values = map.get(a.getAttributeName());
			if (values != null) {
				for (String value: values) {
					a.addAttributeValue(value);
				}				
			}
		}
		
		return attrMap;
	}
	
	@RemoteMethod
	public static Map<String, Attribute> getCNETTemplateAttributes(String templateName) throws DataException {
		// TODO: merge with above method
		Map <String, Attribute> attrMap = new LinkedHashMap<String, Attribute>();
		String storeId = UtilityService.getStoreName();

		ArrayList<String> filters = new ArrayList<String>();
		ArrayList<String> fields = new ArrayList<String>();

		for (Attribute a: CatCodeUtil.getCNETTemplateAttribute(templateName)) {
			attrMap.put(a.getAttributeName(), new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
			fields.add(a.getAttributeName());
		}

		String templateNameField = ConfigManager.getInstance().getParameterByCore(storeId, "facet-template");
		if (StringUtils.isNotEmpty(templateNameField)) {
			filters.add(templateNameField + "Name:\"" + templateName + "\"");

			Map<String,List<String>> map = SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(),
					fields, filters,false);

			for (Attribute a: attrMap.values()) {
				List<String> values = map.get(a.getAttributeName());
				if (values != null) {
					for (String value: values) {
						a.addAttributeValue(value);
					}					
				}
			}			
		}
		
		return attrMap;
	}

	public static Map<String, Attribute> getIMSTemplateAttributesMap(String templateName) throws DataException {
		Map <String, Attribute> attrMap = new HashMap<String, Attribute>();

		ArrayList<String> filters = new ArrayList<String>();
		filters.add("TemplateName:\"" + templateName + "\"");

		for (Attribute a: CatCodeUtil.getIMSTemplateAttribute(templateName)) {
			attrMap.put(a.getAttributeName(), new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
		}	

		return attrMap;
	}
	
	public static Map<String, Attribute> getCNETTemplateAttributesMap(String templateName) throws DataException {
		Map <String, Attribute> attrMap = new HashMap<String, Attribute>();

		ArrayList<String> filters = new ArrayList<String>();
		filters.add("TemplateName:\"" + templateName + "\"");

		for (Attribute a: CatCodeUtil.getCNETTemplateAttribute(templateName)) {
			attrMap.put(a.getAttributeName(), new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
		}	

		return attrMap;
	}
	
}