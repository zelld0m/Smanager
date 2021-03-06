package com.search.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        creatorParams =
        @Param(name = "beanName", value = "categoryService"))
public class CategoryService {

	private static final Logger logger =
            LoggerFactory.getLogger(CategoryService.class);

	@Autowired
	private UtilityService utilityService;
	@Autowired
	private SearchHelper searchHelper;
	@Autowired
	private ConfigManager configManager;
	
    @RemoteMethod
    public static List<String> getIMSCategories() throws DataException {
        return CatCodeUtil.getIMSCategoryNextLevel("", "", "");
    }

    @RemoteMethod
    public static List<String> getIMSSubcategories(String category) throws DataException {
        return CatCodeUtil.getIMSCategoryNextLevel(category, "", "");
    }

    @RemoteMethod
    public static List<String> getIMSClasses(String category, String subcategory) throws DataException {
        return CatCodeUtil.getIMSCategoryNextLevel(category, subcategory, "");
    }

    @RemoteMethod
    public static List<String> getIMSMinors(String category, String subcategory, String className) throws DataException {
        return CatCodeUtil.getIMSCategoryNextLevel(category, subcategory, className);
    }

    @RemoteMethod
    public List<String> getIMSManufacturers(String catcode, String category, String subcategory, String className, String subclass) {
        List<String> filters = new ArrayList<String>();
        if (StringUtils.isNotBlank(catcode)) {
            filters.add(String.format("CatCode: %s", catcode));
        }
        if (StringUtils.isNotBlank(category)) {
            filters.add(String.format("Category: \"%s\"", category));
        }
        if (StringUtils.isNotBlank(subcategory)) {
            filters.add(String.format("SubCategory: \"%s\"", subcategory));
        }
        if (StringUtils.isNotBlank(className)) {
            filters.add(String.format("Class: \"%s\"", className));
        }
        if (StringUtils.isNotBlank(subclass)) {
            filters.add(String.format("SubClass: \"%s\"", subclass));
        }
        return searchHelper.getFacetValues(utilityService.getServerName(), utilityService.getStoreId(), "Manufacturer", filters);
    }

    @RemoteMethod
    public static List<String> getCNETLevel1Categories(String store) throws DataException {
        return CatCodeUtil.getCNETNextLevel("", "", store);
    }

    @RemoteMethod
    public static List<String> getCNETLevel2Categories(String level1Category, String store) throws DataException {
        return CatCodeUtil.getCNETNextLevel(level1Category, "", store);
    }

    @RemoteMethod
    public static List<String> getCNETLevel3Categories(String level1Category, String level2Category, String store) throws DataException {
        return CatCodeUtil.getCNETNextLevel(level1Category, level2Category, store);
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
        rr.setStoreId(utilityService.getStoreId());
        rr.setFilter(filter);
        utilityService.setFacetTemplateValues(rr);
        filters.add(rr.getConditionForSolr());
        return searchHelper.getFacetValues(utilityService.getServerName(), utilityService.getStoreId(), "Manufacturer", filters);
    }

    @RemoteMethod
    public List<String> getTemplateNamesByStore(String storeId) throws DataException {
    	
    	try {
    		if(utilityService.getStoreFacetTemplateType(storeId).equalsIgnoreCase("IMS")) {
    			return getIMSTemplateNamesByStore(storeId);
    		} else if(utilityService.getStoreFacetTemplateType(storeId).equalsIgnoreCase("CNET")) {
    			return getCNETTemplateNamesByStore(storeId);
    		}
    	} catch(Exception e) {
    		logger.error("Error in getTemplateNamesByStore(): " + e + ". store = " + storeId);
    	}
    	
    	return null;
    }
    
    @RemoteMethod
    public static List<String> getIMSTemplateNamesByStore(String storeId) throws DataException {
        return CatCodeUtil.getAllIMSTemplatesByStore(storeId);
    }

    @RemoteMethod
    public static List<String> getCNETTemplateNamesByStore(String storeId) throws DataException {
        return CatCodeUtil.getAllCNETTemplatesByStore(storeId);
    }

    @RemoteMethod
    public Map<String, Attribute> getIMSTemplateAttributes(String templateName) throws DataException {
        Map<String, Attribute> attrMap = new LinkedHashMap<String, Attribute>();
        String storeId = utilityService.getStoreId();

        ArrayList<String> filters = new ArrayList<String>();
        filters.add("TemplateName:\"" + templateName + "\"");
        ArrayList<String> fields = new ArrayList<String>();

        for (Attribute a : CatCodeUtil.getIMSTemplateAttributeByStore(storeId, templateName)) {
            attrMap.put(a.getAttributeName(), a);
            fields.add(a.getAttributeName());
        }

        Map<String, List<String>> map = searchHelper.getFacetValues(utilityService.getServerName(), utilityService.getStoreId(),
                fields, filters, false);

        for (Attribute a : attrMap.values()) {
            List<String> values = map.get(a.getAttributeName());
            if (values != null && !a.isRange()) {
                for (String value : values) {
                    a.addAttributeValue(value);
                }
            }
        }

        return attrMap;
    }

    @RemoteMethod
    public Map<String, Attribute> getCNETTemplateAttributes(String templateName) throws DataException {
        // TODO: merge with above method
        Map<String, Attribute> attrMap = new LinkedHashMap<String, Attribute>();
        String storeId = utilityService.getStoreId();

        ArrayList<String> filters = new ArrayList<String>();
        ArrayList<String> fields = new ArrayList<String>();

        for (Attribute a : CatCodeUtil.getCNETTemplateAttributeByStore(storeId, templateName)) {
            attrMap.put(a.getAttributeName(), a);
            fields.add(a.getAttributeName());
        }

        String templateNameField = configManager.getStoreParameter(storeId, "facet-template");
        if (StringUtils.isNotEmpty(templateNameField)) {
            filters.add(templateNameField + "Name:\"" + templateName + "\"");

            Map<String, List<String>> map = searchHelper.getFacetValues(utilityService.getServerName(), utilityService.getStoreId(),
                    fields, filters, false);

            for (Attribute a : attrMap.values()) {
                List<String> values = map.get(a.getAttributeName());
                if (values != null && !a.isRange()) {
                    for (String value : values) {
                        a.addAttributeValue(value);
                    }
                }
            }
        }

        return attrMap;
    }

    public static Map<String, Attribute> getIMSTemplateAttributesMap(String storeId, String templateName) throws DataException {
        Map<String, Attribute> attrMap = new HashMap<String, Attribute>();
        ArrayList<String> filters = new ArrayList<String>();
        filters.add("TemplateName:\"" + templateName + "\"");

        for (Attribute a : CatCodeUtil.getIMSTemplateAttributeByStore(storeId, templateName)) {
            attrMap.put(a.getAttributeName(), new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
        }

        return attrMap;
    }

    public static Map<String, Attribute> getCNETTemplateAttributesMap(String storeId, String templateName) throws DataException {
        Map<String, Attribute> attrMap = new HashMap<String, Attribute>();
        ArrayList<String> filters = new ArrayList<String>();
        filters.add("TemplateName:\"" + templateName + "\"");

        for (Attribute a : CatCodeUtil.getCNETTemplateAttributeByStore(storeId, templateName)) {
            attrMap.put(a.getAttributeName(), new Attribute(a.getAttributeName(), a.getAttributeDisplayName()));
        }

        return attrMap;
    }
}