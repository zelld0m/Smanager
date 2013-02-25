package com.search.manager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.exception.DataException;
import com.search.manager.service.CategoryService;
import com.search.manager.utility.CatCodeUtil;
import com.search.manager.utility.CatCodeUtil.Attribute;
import com.search.ws.ConfigManager;
import com.search.ws.SolrConstants;

@DataTransferObject(converter=BeanConverter.class)
public class RedirectRuleCondition extends ModelBean {

	// increment after every change in model
	private static final long serialVersionUID = -6248904441308276236L;
	
	private static final Logger logger = Logger.getLogger(RedirectRuleCondition.class);

	private String ruleId;
	private Integer sequenceNumber;
	private String storeId;
	
	private Map<String,List<String>> conditionMap = new HashMap<String, List<String>>();

	public RedirectRuleCondition() {
	}

	public RedirectRuleCondition(Map<String, List<String>> filter) {
		conditionMap.putAll(filter);
	}
	
	public RedirectRuleCondition(String ruleId, String condition) {
		this.ruleId = ruleId;
		setCondition(condition);
	}
	
	public RedirectRuleCondition(String ruleId, Integer sequenceNumber) {
		this.ruleId = ruleId;
		this.sequenceNumber = sequenceNumber;
	}
	
	public RedirectRuleCondition(String ruleId, Integer sequenceNumber, String condition) {
		this.ruleId = ruleId;
		this.sequenceNumber = sequenceNumber;
		setCondition(condition);
	}
	
	public RedirectRuleCondition(String condition) {
		setCondition(condition);
	}
	
	public String getCondition() {
		return getCondition(false);
	}
	
	public String getConditionForSolr() {
		return getCondition(true);
	}

	private String getCondition(boolean forSolr) {
		// TODO: convert from condition map
		// Category, SubCategory, Class, SubClass, Manufacturer are grouped together
		// e.g. Category:"Systems" AND SubCategory:"Notebook Computers" AND Class:"Intel Core i3 Notebook Computers" AND SubClass:"2.75GHz and up" AND Manufacturer:"Acer"
		// -or- CatCode and Manufacturer are grouped together
		// e.g. CatCode:3F* AND Manufacturer:"Acer" 	<- note the lack of double quotes
		// -or- _FacetTemplate is treated as one (next sprint)		
		// (TemplateName or *_FacetTemplateName) and af* are grouped together (next sprint)
		StringBuilder builder = new StringBuilder();
		Map<String,List<String>> map = null;
		
		if (isIMSFilter()) {
			map = getIMSFilters();
			for (String key: map.keySet()) {
				builder.append(key).append(":");
				List<String> values = map.get(key);
				if (values.size() == 1) {
					if ("CatCode".equals(key)) {
						String value = values.get(0);
						if (!value.endsWith("*") && value.length() < 4) {
							value += "*";
						}
						builder.append(value);
					}
					else {
						// TODO: move to a method
						// temp workaround for old data
						String value = values.get(0);
						builder.append(forSolr? ClientUtils.escapeQueryChars(value) : value);						
					}
				}
				else {
					// TODO: support for multiple values
				}
				builder.append(" AND ");
			}
		}
		else if (isCNetFilter()) {
			map = getCNetFilters();
			if (CollectionUtils.isNotEmpty(map.get("Level1Category"))) {
				String value = map.get("Level1Category").get(0);
				
				ConfigManager cm = ConfigManager.getInstance();
				if (cm != null && StringUtils.isNotBlank(storeId)) {
					builder.append(cm.getParameterByCore(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE))
						.append(":").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
					if (CollectionUtils.isNotEmpty(map.get("Level2Category"))) {
						value = map.get("Level2Category").get(0);
						builder.append(forSolr ? ClientUtils.escapeQueryChars(" | ") : " | ").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
						if (CollectionUtils.isNotEmpty(map.get("Level3Category"))) {
							value = map.get("Level3Category").get(0);
							builder.append(forSolr ? ClientUtils.escapeQueryChars(" | ") : " | ").append(forSolr ? ClientUtils.escapeQueryChars(value) : value);
						}
					}
					builder.append(forSolr ? "*" : "").append(" AND ");
				}
			}
			
			String key = "Manufacturer";
			List<String> values = map.get(key);
			if (values != null && values.size() == 1) {
				// temp workaround for old data
				String value = values.get(0);
				builder.append("Manufacturer:").append(forSolr? ClientUtils.escapeQueryChars(value) : value);
				builder.append(" AND ");
				}
			else {
				// TODO: support for multiple values
			}
		}
		
		// TODO: dynamic attributes
		map = getDynamicAttributes();
		if (MapUtils.isNotEmpty(map)) {
			String templateName = null;
			for (String key: map.keySet()) {
				if (StringUtils.equals(key, "TemplateName") || StringUtils.endsWith(key, "_FacetTemplateName")) {
					templateName = map.get(key).get(0);
					String value = templateName;
					builder.append(key).append(":").append(forSolr? ClientUtils.escapeQueryChars(value) : value).append(" AND ");
					break;
				}
			}
			
			if (StringUtils.isNotBlank(templateName)) {
				for (String key: map.keySet()) {
					if (!(StringUtils.equals(key, "TemplateName") || StringUtils.endsWith(key, "_FacetTemplateName"))) {
						List<String> values = map.get(key);
						if (CollectionUtils.isNotEmpty(values)) {
							builder.append(key).append(":");
							
							if (forSolr) {
								builder.append("(");
							}
							for (String value: values) {
								builder.append(forSolr? ClientUtils.escapeQueryChars(value) : value);
								builder.append(forSolr ? " " : " OR ");
							}
							if (forSolr) {
								builder.append(")");
							}
							else {
								builder.replace(builder.length() - 4, builder.length(), "");								
							}
							builder.append(" AND ");										
						}
					}
				}
			}
		}
		
		
		// Platform, Condition, Availability, License, ImageExists are grouped together
		// special processing
		// if Condition == "Refurbished" set Refurbished_Flag:1
		//				== "Open Box"    set OpenBox_Flag:1
		//              == "Clearance"   set Clearance_Flag:1
		// if License == "Non-License Products Only" set Licence_Flag:0
		//		      == "License Products Only", set Licence_Flag:1
		// if Availability == "In Stock" set InStock:1
		//                 == "Call"     set InStock:0
		// if ImageExists == "Products with Image Only" 	set ImageExists:1
		//                == "Products without Image Only"  set ImageExists:0
		
		map = getFacets();
		if (map.containsKey("Condition")) {
			String value = map.get("Condition").get(0);
			if (value.equals("Refurbished")) {
				builder.append("Refurbished_Flag").append(":1").append(" AND ");
			}
			else if (value.equals("Open Box")) {
				builder.append("OpenBox_Flag").append(":1").append(" AND ");
			}
			else if (value.equals("Clearance")) {
				builder.append("Clearance_Flag").append(":1").append(" AND ");
			}
		}
		if (map.containsKey("License")) {
			String value = map.get("License").get(0);
			if (value.equals("Non-License Products Only")) {
				builder.append("Licence_Flag").append(":0").append(" AND ");
			}
			else if (value.equals("License Products Only")) {
				builder.append("Licence_Flag").append(":1").append(" AND ");
			}			
		}
		
		if (map.containsKey("ImageExists")) {
			String value = map.get("ImageExists").get(0);
			if (value.equals("Products Without Image Only")) {
				builder.append("ImageExists").append(":0").append(" AND ");
			}
			else if (value.equals("Products With Image Only")) {
				builder.append("ImageExists").append(":1").append(" AND ");
			}			
		}
		
		if (map.containsKey("Availability")) {
			String value = map.get("Availability").get(0);
			if (value.equals("Call")) {
				builder.append("InStock").append(":0").append(" AND ");
			}
			else if (value.equals("In Stock")) {
				builder.append("InStock").append(":1").append(" AND ");
			}					
		}
		if (map.containsKey("Platform")) {
			builder.append("Platform").append(":").append(map.get("Platform").get(0)).append(" AND ");
		}
		
		String cnetFacet = null;
		if (forSolr && StringUtils.isNotBlank(StringUtils.lowerCase(storeId))) {
			cnetFacet = ConfigManager.getInstance().getParameterByCore(storeId, "facet-name");
		}
		
		if (map.containsKey("Name")) {
			String value = map.get("Name").get(0);
			if (forSolr) {
				value = ClientUtils.escapeQueryChars(value);
			}
			
			if (StringUtils.isNotEmpty(cnetFacet)) {
				builder.append("(").append(cnetFacet).append("_Name").append(":").append(value).append(" OR ");
			}
			builder.append("Name").append(":").append(value);
			if (StringUtils.isNotEmpty(cnetFacet)) {
				builder.append(")");
			}
			builder.append(" AND ");
		}
		if (map.containsKey("Description")) {
			String value = map.get("Description").get(0);
			if (forSolr) {
				value = ClientUtils.escapeQueryChars(value);
			}
			
			if (StringUtils.isNotEmpty(cnetFacet)) {
				builder.append("(").append(cnetFacet).append("_Description").append(":").append(value).append(" OR ");
			}
			builder.append("Description").append(":").append(value);
			if (StringUtils.isNotEmpty(cnetFacet)) {
				builder.append(")");
			}
			builder.append(" AND ");
		}
		
		if (builder.length() > 0) {
			builder.replace(builder.length() - 5, builder.length(), "");
		}
		return builder.toString();		
	}
	
	public void setFilter(Map<String, List<String>> filter) {
		// TODO: add validation. no support for this current sprint
		synchronized(conditionMap) {
			conditionMap.clear();
			conditionMap.putAll(filter);
		}
	}
	
	private String encloseInQuotes(String string) {
		string = StringUtils.trim(string);
		if (StringUtils.isEmpty(string)) {
			return "";
		}
		return (string.startsWith("\"") && string.endsWith("\"")) ? string :
			"\"" + string + "\"";
	}
	
	public String getReadableString() {
		StringBuilder builder = new StringBuilder();
		// construct from condition
		Map<String,List<String>> map = null;
		
		if (isIMSFilter()) {
			map = getIMSFilters();
			
			if (CollectionUtils.isNotEmpty(map.get("CatCode"))) {
				builder.append("Category Code is \"").append(map.get("CatCode").get(0));
				builder.append("\" and ");
			}
			
			else if (CollectionUtils.isNotEmpty(map.get("Category"))) {
				builder.append("Category is \"").append(map.get("Category").get(0));
				if (CollectionUtils.isNotEmpty(map.get("SubCategory"))) {
					builder.append(" > ").append(map.get("SubCategory").get(0));
					if (CollectionUtils.isNotEmpty(map.get("Class"))) {
						builder.append(" > ").append(map.get("Class").get(0));
						if (CollectionUtils.isNotEmpty(map.get("SubClass"))) {
							builder.append(" > ").append(map.get("SubClass").get(0));
						}
					}
				}
				builder.append("\" and ");
			}

			String key = "Manufacturer";
			if (CollectionUtils.isNotEmpty(map.get(key))) {
				builder.append(key).append(" is ");
				List<String> values = map.get(key);
				if (values.size() == 1) {
					builder.append(encloseInQuotes(values.get(0)));
				}
				else {
					// TODO: support for multiple values
				}
				builder.append(" and ");
			}		
		}
		else if (isCNetFilter()) {
			map = getCNetFilters();
			if (CollectionUtils.isNotEmpty(map.get("Level1Category"))) {
				builder.append("Category is \"").append(map.get("Level1Category").get(0));
				if (CollectionUtils.isNotEmpty(map.get("Level2Category"))) {
					builder.append(" > ").append(map.get("Level2Category").get(0));
					if (CollectionUtils.isNotEmpty(map.get("Level3Category"))) {
						builder.append(" > ").append(map.get("Level3Category").get(0));
					}
				}
				builder.append("\" and ");
			}

			String key = "Manufacturer";
			if (CollectionUtils.isNotEmpty(map.get(key))) {
				builder.append(key).append(" is ");
				List<String> values = map.get(key);
				if (values.size() == 1) {
					builder.append(encloseInQuotes(values.get(0)));
				}
				else {
					// TODO: support for multiple values
				}
				builder.append(" and ");
			}
		}
		
		
		map = getDynamicAttributes();
		if (MapUtils.isNotEmpty(map)) {
			boolean isCNET = false;
			String templateName = null;
			for (String key: map.keySet()) {
				if (StringUtils.equals(key, "TemplateName") || StringUtils.endsWith(key, "_FacetTemplateName")) {
					isCNET = StringUtils.endsWith(key, "_FacetTemplateName");
					templateName = map.get(key).get(0);
					break;
				}
			}
			
			// TODO: dynamic attributes
			if (StringUtils.isNotBlank(templateName)) {
				// get readable value
				try {
					builder.append("Template Name is \"").append(templateName).append("\" AND ");
					
					Map<String, Attribute> attributeMap = isCNET ? 
									CategoryService.getCNETTemplateAttributesMap(templateName) :
									CategoryService.getIMSTemplateAttributesMap(templateName);
					for (String key: map.keySet()) {
						if (!(StringUtils.equals(key, "TemplateName") || StringUtils.endsWith(key, "_FacetTemplateName"))) {
							Attribute a = attributeMap.get(key);
							List<String> values = map.get(key);
							if (CollectionUtils.isNotEmpty(values)) {
								builder.append(a == null ? key : a.getAttributeDisplayName()).append(" is ");
								for (String value: values) {
									value = value.substring(value.indexOf("|") + 1);
									builder.append("\"").append(value).append("\"");
									builder.append(" or ");										
								}
								builder.replace(builder.length() - 4, builder.length(), "");
								builder.append(" AND ");										
							}
						}
					}
				} catch (DataException e) {
					logger.error("Failed to get template attributes", e);
				}
			}
		}
		
		String[] arrFieldContains = { "Name", "Description" };
		
		map = getFacets();
		for (String key: map.keySet()) {
			if ("ImageExists".equalsIgnoreCase(key)) {
				builder.append("Product Image");
			}else{
				builder.append(key);
			}
			
			builder.append(ArrayUtils.contains(arrFieldContains, key) ? " contains " : " is ");
			List<String> values = map.get(key);
			if (values.size() == 1) {
				builder.append(encloseInQuotes(values.get(0)));
			}
			else {
				// TODO: support for multiple values
			}
			builder.append(" and ");
		}
		
		if (builder.length() > 0) {
			builder.replace(builder.length() - 5, builder.length(), "");
		}
		
		return builder.toString();
	}
	
	private void putListToConditionMap(String key, String values) {
		List<String> list = conditionMap.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			conditionMap.put(key, list);
		}
		for (String value : values.split(" OR ")) {
			list.add(value);
		}
	}
	
	private void putToConditionMap(String key, String value) {
		List<String> list = conditionMap.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			conditionMap.put(key, list);
		}
		list.add(value);
	}
	
	public void setCondition(String condition) {
		// TODO: create condition map
		// split String into Field:Value tokens

		conditionMap.clear();
		
		int colonPosition = 0;
		int fieldStart = 0;
		int valueEnd = 0;
		
		while (fieldStart < condition.length()) {
			// TODO: what if value contains : and AND
			colonPosition = condition.indexOf(":", fieldStart);
			valueEnd = condition.indexOf(" AND ", colonPosition + 1);
			if (valueEnd < 0) {
				valueEnd = condition.length();
			}

			String fieldName = condition.substring(fieldStart, colonPosition);
			String fieldValue = condition.substring(colonPosition + 1, valueEnd);			

			// special processing for the following:
			// if Refurbished_Flag:1 set Condition to "Refurbished" 
			//	      OpenBox_Flag:1 set Condition to "Open Box"
			//      Clearance_Flag:1 set Condition to "Clearance"
			if (fieldName.equals("Refurbished_Flag") && fieldValue.equals("1")) {
				putToConditionMap("Condition", "Refurbished");
			}
			else if (fieldName.equals("OpenBox_Flag") && fieldValue.equals("1")) {
				putToConditionMap("Condition", "Open Box");				
			}
			else if (fieldName.equals("Clearance_Flag") && fieldValue.equals("1")) {
				putToConditionMap("Condition", "Clearance");
			}
			
			// if  Licence_Flag:0 set License to "Non-License Products Only"
			//                 :1 set License to "License Products Only"
			else if (fieldName.equals("Licence_Flag") && fieldValue.equals("0")) {
				putToConditionMap("License", "Non-License Products Only");
			}
			else if (fieldName.equals("Licence_Flag") && fieldValue.equals("1")) {
				putToConditionMap("License", "License Products Only");
			}
			
			// if  ImageExists:0 set ImageExists to "Products Without Image Only"
			//                 :1 set ImageExists to "Products With Image Only"
			else if (fieldName.equals("ImageExists") && fieldValue.equals("0")) {
				putToConditionMap("ImageExists", "Products Without Image Only");
			}
			else if (fieldName.equals("ImageExists") && fieldValue.equals("1")) {
				putToConditionMap("ImageExists", "Products With Image Only");
			}

			// CNET
			// TODO: update when MacMall and other stores support CNET Facet Template
			else if (fieldName.endsWith("_FacetTemplate")) {
				
				// get store name here
				if (StringUtils.isBlank(storeId)) {
					storeId = StringUtils.lowerCase(StringUtils.substring(fieldName, 0, 
							StringUtils.indexOf(fieldName, "_FacetTemplate")));
				}
				
				if (fieldValue.endsWith("*")) {
					fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
				}
				String[] facets = fieldValue.split("\\ \\|\\ ");
				if (facets.length > 0) {
					if (StringUtils.isNotEmpty(facets[0])) {
						putToConditionMap("Level1Category", facets[0].replaceAll("\\\\", ""));						
					}
				}
				if (facets.length > 1) {
					putToConditionMap("Level2Category", facets[1].replaceAll("\\\\", ""));
				}
				if (facets.length > 2) {
					putToConditionMap("Level3Category", facets[2].replaceAll("\\\\", ""));					
				}
			}

			// Dynamic attributes
			else if (fieldName.startsWith("af_")) {
				putListToConditionMap(fieldName, fieldValue);
			}
			
			// If InStock:0 set Availability to "In Stock"
			//           :1 set Availability to "Call"
			else if (fieldName.equals("InStock") && fieldValue.equals("0")) {
				putToConditionMap("Availability", "Call");
			}
			else if (fieldName.equals("InStock") && fieldValue.equals("1")) {
				putToConditionMap("Availability", "In Stock");
			}
			
			else {
				putToConditionMap(fieldName, fieldValue);				
			}
			
			fieldStart = valueEnd + 5; // 5 = length of " AND "
		}

	}

	public Map<String, List<String>> getCNetFilters() {
		// if TemplateName or *_FacetTemplateName is present return TemplateName or *_FacetTemplateName and af_* fields and dynamic attributes;
		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		if (isCNetFilter()) {
			String[] facetKeys = { "Level1Category", "Level2Category", "Level3Category", "Manufacturer" };
			for (String key: facetKeys) {
				List<String> value = conditionMap.get(key);
				List<String> newValue = new ArrayList<String>();
				if (CollectionUtils.isNotEmpty(value)) {
					for(String tmp:value) {
						newValue.add(tmp.replaceAll("\"", ""));					
					}
					map.put(key, new ArrayList<String>(newValue));
				}
			}
		}
		return map;
	}
	
	public Map<String, List<String>> getIMSFilters() {
		// if Category is present return Category, SubCategory, Class, SubClass and Manufacturer fields;
		// else if CatCode is present return CatCode and Manufacturer fields;
		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		
		if (isIMSFilter()) {
			String[] categoryKeys = { "Category", "SubCategory", "Class", "SubClass", "Manufacturer" };
			String[] catCodeKeys = { "CatCode", "Manufacturer"};
			// TODO: update once CNET filters is available
			for (String key: isImsUsingCategory() ? categoryKeys : catCodeKeys) {
				List<String> value = conditionMap.get(key);
				List<String> newValue = new ArrayList<String>();
				if (CollectionUtils.isNotEmpty(value)) {
					for(String tmp:value) {
						newValue.add(tmp.replaceAll("\"", ""));					
					}
					map.put(key, new ArrayList<String>(newValue));
				}
			}
		}
		return map;
	}
	
	public boolean isImsUsingCatCode() {
		return conditionMap.get("CatCode") != null && !conditionMap.get("CatCode").isEmpty() && StringUtils.isNotBlank(conditionMap.get("CatCode").get(0));
	}
	
	public boolean isImsUsingCategory() {
		return conditionMap.get("Category") != null && !conditionMap.get("Category").isEmpty() && StringUtils.isNotBlank(conditionMap.get("Category").get(0));
	}
	
	public boolean isIMSFilter() {
		return (!isCNetFilter() && 
			(CollectionUtils.isNotEmpty(conditionMap.get("Manufacturer")) && StringUtils.isNotBlank(conditionMap.get("Manufacturer").get(0)) ||
					CollectionUtils.isNotEmpty(conditionMap.get("TemplateName")) ||
					isImsUsingCatCode() || 
					isImsUsingCategory()));
	}

	public boolean isCNetFilter() {
		for (String key : conditionMap.keySet()) {
			if (key.endsWith("FacetTemplateName")) {
				return true;
			}
		}
		return (conditionMap.get("Level1Category") != null && !conditionMap.get("Level1Category").isEmpty() && StringUtils.isNotBlank(conditionMap.get("Level1Category").get(0)));
	}

	public Map<String, List<String>> getFacets() {
		// if any of the following fields are present return them;
		// Platform, Condition, Availability, License, ImageExists
		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		String[] keys = { "Platform", "Condition", "Availability", "License", "ImageExists", "Name", "Description" };
		for (String key: keys) {
			List<String> value = conditionMap.get(key);
			if (value != null && !value.isEmpty()) {
				map.put(key, new ArrayList<String>(value));
			}
		}
		return map;
	}
	
	public Map<String, List<String>> getDynamicAttributes() {
		// if TemplateName or *_FacetTemplateName is present return TemplateName or *_FacetTemplateName and af_* fields and dynamic attributes;
		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		// TODO: implement

		// if any of the following fields are present return them;
		// Platform, Condition, Availability, License
		for (String key: conditionMap.keySet()) {
			if (key.contains("TemplateName") || key.startsWith("af_")) {
				List<String> value = conditionMap.get(key);
				if (value != null && !value.isEmpty()) {
					map.put(key, new ArrayList<String>(value));
				}				
			}
		}
		return map;
	}
	public static void main(String[] args) {
		ConfigManager.getInstance("C:\\home\\solr\\conf\\solr.xml");
		
		// initialize catcodeutil
		boolean initCatCodeUtil = false;
		
		if (initCatCodeUtil) {
			try {
				CatCodeUtil.init2();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		
		// if Condition == "Refurbished" set Refurbished_Flag:1
		//				== "Open Box"    set OpenBox_Flag:1
		//              == "Clearance"   set Clearance_Flag:1
		// if License == "License Products Only" set Licence_Flag:1
		//		      == "Non-License Products Only", set Licence_Flag:0
		//            else, set Licence_Flag:0
		// if Availability == "In Stock" set InStock:1
		//                 == "Call"     set InStock:0
		// if ImageExists == "Products Without Image Only", set ImageExists:0
		//                == "Products With Image Only", set ImageExists:2
		String[] conditions = {
//				"Category:\"System\" AND SubCategory:\"Notebook Computers\" AND Manufacturer:\"Apple\" AND Refurbished_Flag:1 AND InStock:1",
//				"Manufacturer:Microsoft AND PCMall_FacetTemplate:Games | XBOX 360 Games | XBOX 360 Racing Games*",
//				"PCMall_FacetTemplate:Electronics | Gaming | PC Games & Accessories",
//				"CatCode:31* AND Manufacturer:\"BlackBerry\"",
//				"CatCode:3F* AND OpenBox_Flag:1 AND InStock:0 AND Platform:\"Windows\s"",
//				"Name:bag ivory AND Description:bag ivory",
//				"TemplateName:Notebook Computers AND af_Processor1_Value_Attrib:a2|Core i5 OR a2|Core i7",
//				"PCMall_FacetTemplateName:Notebook Computers AND af_Processor1_Value_Attrib:a2|Core i5",
//				"Clearance_Flag:1 AND Licence_Flag:0 AND ImageExists:1",
				"Manufacturer:Apple AND PCMall_FacetTemplate:Data Storage | Network Attached Storage (NAS) AND Description:netbook",
//				""
		};
		
		for (String condition: conditions) {
			System.out.println("***************");
			RedirectRuleCondition rr = new RedirectRuleCondition(condition);
			rr.setStoreId("macmall");
			System.out.println("text: " + condition);
			System.out.println("condition: " + rr.getCondition());
			System.out.println("solr filter: " + rr.getConditionForSolr());
			System.out.println("readable string: " + rr.getReadableString());
			System.out.println("ims filter: " + rr.getIMSFilters());
			System.out.println("cnet filter: " + rr.getCNetFilters());
			System.out.println("facets: " + rr.getFacets());
			System.out.println("dynamic attributes: " + rr.getDynamicAttributes());
			System.out.println("IMS filter: " + rr.isIMSFilter());
			System.out.println("CNET filter: " + rr.isCNetFilter());
		}
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreId() {
		return storeId;
	}
	
}
