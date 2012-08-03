package com.search.manager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.ws.ConfigManager;

@DataTransferObject(converter=BeanConverter.class)
public class RedirectRuleCondition extends ModelBean {

	// increment after every change in model
	private static final long serialVersionUID = -6248904441308276236L;
	
	private String ruleId;
	private Integer sequenceNumber;
	private String storeId;
	
	private Map<String,List<String>> conditionMap = new HashMap<String, List<String>>();

	public RedirectRuleCondition() {
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
						if (forSolr && isEncloseInQuotes(key) && !StringUtils.startsWith(value, "\"") && !StringUtils.endsWith(value, "\"")) {
							value = String.format("\"%s\"", value);
						}
						builder.append(value);						
					}
				}
				else {
					// TODO: support for multiple values
				}
				builder.append(" AND ");
			}
		}
		// TODO: CNET
		else if (isCNetFilter()) {
			map = getCNetFilters();
			if (CollectionUtils.isNotEmpty(map.get("Level1Category"))) {
				String value = map.get("Level1Category").get(0);
				builder.append("PCMall_FacetTemplate:").append(forSolr ? value.replaceAll(" ", "\\\\ ") : value);
				if (CollectionUtils.isNotEmpty(map.get("Level2Category"))) {
					value = map.get("Level2Category").get(0);
					builder.append(forSolr ? "\\ |\\ " : " | ").append(forSolr ? value.replaceAll(" ", "\\\\ ") : value);
					if (CollectionUtils.isNotEmpty(map.get("Level3Category"))) {
						value = map.get("Level3Category").get(0);
						builder.append(forSolr ? "\\ |\\ " : " | ").append(forSolr ? value.replaceAll(" ", "\\\\ ") : value);
					}
				}
				builder.append(forSolr ? "*" : "").append(" AND ");
			}
			
			String key = "Manufacturer";
			List<String> values = map.get(key);
			if (values != null && values.size() == 1) {
				// temp workaround for old data
				String value = values.get(0);
				if (forSolr && isEncloseInQuotes(key) && !StringUtils.startsWith(value, "\"") && !StringUtils.endsWith(value, "\"")) {
					value = String.format("\"%s\"", value);
				}
				builder.append("Manufacturer:").append(value);
				builder.append(" AND ");
				}
			else {
				// TODO: support for multiple values
			}
		}
		
		// TODO: dynamic attributes
		
		// Platform, Condition, Availability, License are grouped together
		// special processing
		// if Condition == "Refurbished" set Refurbished_Flag:1
		//				== "Open Box"    set OpenBox_Flag:1
		//              == "Clearance"   set Clearance_Flag:1
		// if License == "Show Non-License Products Only" set Licence_Flag:0
		//		      == "Show License Products Only", set Licence_Flag:1
		// if Availability == "In Stock" set InStock:1
		//                 == "Call"     set InStock:0
		
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
			if (value.equals("Show Non-License Products Only")) {
				builder.append("Licence_Flag").append(":0").append(" AND ");
			}
			else if (value.equals("Show License Products Only")) {
				builder.append("Licence_Flag").append(":1").append(" AND ");
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
	
	private static String[] encloseInQuotesList = {
		 "Category", 
		 "SubCategory", 
		 "Class", 
		 "SubClass", 
		 "Manufacturer"
	};
	
	private boolean isEncloseInQuotes(String key) {
		return ArrayUtils.contains(encloseInQuotesList, key);
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
		
		// TODO: dynamic attributes
		String[] arrFieldContains = { "Name", "Description" };
		
		map = getFacets();
		for (String key: map.keySet()) {
			builder.append(key).append(ArrayUtils.contains(arrFieldContains, key) ? " contains " : " is ");
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
			
			// if  Licence_Flag:0 set License to "Show Non-License Products Only"
			//                 :1 set License to "Show License Products Only"
			else if (fieldName.equals("Licence_Flag") && fieldValue.equals("0")) {
				putToConditionMap("License", "Show Non-License Products Only");
			}
			else if (fieldName.equals("Licence_Flag") && fieldValue.equals("1")) {
				putToConditionMap("License", "Show License Products Only");
			}

			// CNET
			// TODO: update when MacMall and other stores support CNET Facet Template
			else if (fieldName.contains("_FacetTemplate")) {
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
			(conditionMap.get("Manufacturer") != null && !conditionMap.get("Manufacturer").isEmpty() && StringUtils.isNotBlank(conditionMap.get("Manufacturer").get(0)) ||
					isImsUsingCatCode() || isImsUsingCategory()));
	}

	public boolean isCNetFilter() {
		return conditionMap.get("Level1Category") != null && !conditionMap.get("Level1Category").isEmpty() && StringUtils.isNotBlank(conditionMap.get("Level1Category").get(0));
	}

	public Map<String, List<String>> getTemplateFilters() {
		// if TemplateName or *_FacetTemplateName is present return TemplateName or *_FacetTemplateName and af_* fields and dynamic attributes;
		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		// TODO: implement
		return map;
	}

	public Map<String, List<String>> getFacets() {
		// if any of the following fields are present return them;
		// Platform, Condition, Availability, License
		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		String[] keys = { "Platform", "Condition", "Availability", "License", "Name", "Description" };
		for (String key: keys) {
			List<String> value = conditionMap.get(key);
			if (value != null && !value.isEmpty()) {
				map.put(key, new ArrayList<String>(value));
			}
		}
		return map;
	}
	
	public static void main(String[] args) {
		ConfigManager.getInstance("C:\\home\\solr\\conf\\solr.xml");
		// if Condition == "Refurbished" set Refurbished_Flag:1
		//				== "Open Box"    set OpenBox_Flag:1
		//              == "Clearance"   set Clearance_Flag:1
		// if License == "Show License Products Only" set Licence_Flag:1
		//		      == "Show Non-License Products Only", set Licence_Flag:0
		//            else, set Licence_Flag:0
		// if Availability == "In Stock" set InStock:1
		//                 == "Call"     set InStock:0
		String[] conditions = {
//				"Category:\"System\" AND SubCategory:\"Notebook Computers\" AND Manufacturer:\"Apple\" AND Refurbished_Flag:1 AND InStock:1",
//				"Manufacturer:Microsoft AND PCMall_FacetTemplate:Games | XBOX 360 Games | XBOX 360 Racing Games*",
//				"PCMall_FacetTemplate:Electronics | Gaming | PC Games & Accessories",
//				"CatCode:31* AND Manufacturer:\"BlackBerry\"",
//				"CatCode:3F* AND OpenBox_Flag:1 AND InStock:0 AND Platform:\"Windows\"",
				"Name:bag ivory AND Description:bag ivory",
//				"Clearance_Flag:1 AND Licence_Flag:0",
//				"Manufacturer:\"Apple\"",
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
