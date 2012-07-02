package com.search.manager.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter=BeanConverter.class)
public class RedirectRuleCondition extends ModelBean {

	// increment after every change in model
	private static final long serialVersionUID = -6248904441308276236L;
	
	private String ruleId;
	private Integer sequenceNumber;
	private Map<String,List<String>> conditionMap = new HashMap<String, List<String>>();

	public RedirectRuleCondition() {
	}

	public RedirectRuleCondition(String condition) {
		setCondition(condition);
	}
	
	public String getCondition() {
		// TODO: convert from condition map
		// Category, SubCategory, Class, SubClass, Manufacturer are grouped together
		// e.g. Category:"Systems" AND SubCategory:"Notebook Computers" AND Class:"Intel Core i3 Notebook Computers" AND SubClass:"2.75GHz and up" AND Manufacturer:"Acer"
		// -or- CatCode and Manufacturer are grouped together
		// e.g. CatCode:3F* AND Manufacturer:"Acer" 	<- note the lack of double quotes
		// -or- _FacetTemplate is treated as one (next sprint)
		
		// (TemplateName or *_FacetTemplateName) and af* are grouped together (next sprint)
		
		// Platform, Condition, Availability, License are grouped together
		// special processing
		// if Condition == "Refurbished" set Refurbished_Flag:1
		//				== "Open Box"    set OpenBox_Flag:1
		//              == "Clearance"   set Clearance_Flag:1
		// if License == "Only" set Licence_Flag:1
		//		      == "Include", do not set License_Flag value
		//            else, set Licence_Flag:0
		// if Availability == "In Stock" set InStock:1
		//                 == "Call"     set InStock:0
		return "";
	}
	
	public void setFilter(Map<String, List<String>> filter) {
		// TODO: add validation. no support for this current sprint
		synchronized(conditionMap) {
			conditionMap.clear();
			conditionMap.putAll(filter);
		}
	}
	
	public String getReadableString() {
		StringBuilder builder = new StringBuilder();
		// construct from condition
		return builder.toString();
	}
	
	public void setCondition(String condition) {
		// TODO: create condition map
		// split String into Field:Value tokens

		// special processing for the following:
		// if Refurbished_Flag:1 set Condition to "Refurbished" 
		//	      OpenBox_Flag:1 set Condition to "Open Box"
		//      Clearance_Flag:1 set Condition to "Clearance"
		// if  License_Flag:0 do not set License
		//                 :1 set License to "Only"
		//    no License_Flag set License_Flag to "Include"
		// If InStock:0 set Availability to "In Stock"
		//           :1 set Availability to "Call"
	}

	public Map<String, String> getCNetFilters() {
		// if *_FacetTemplate is present return *_FacetTemplate and Manufacturer fields;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		return map;
	}
	
	public Map<String, String> getIMSFilters() {
		// if Category is present return Category, SubCategory, Class, SubClass and Manufacturer fields;
		// else if CatCode is present return CatCode and Manufacturer fields;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		return map;
	}
	
	public boolean isImsUsingCatCode() {
		return conditionMap.get("CatCode") != null && !conditionMap.get("CatCode").isEmpty() && StringUtils.isNotBlank(conditionMap.get("CatCode").get(0));
	}
	
	public boolean isImsUsingCategory() {
		return conditionMap.get("Category") != null && !conditionMap.get("Category").isEmpty() && StringUtils.isNotBlank(conditionMap.get("Category").get(0));
	}

	public Map<String, String> getTemplateFilters() {
		// if TemplateName or *_FacetTemplateName is present return TemplateName or *_FacetTemplateName and af_* fields and Manufacturer;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		return map;
	}

	public Map<String, String> getFacets() {
		// if any of the following fields are present return them;
		// Platform, Condition, Availability, License
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		return map;
	}
	
	public static void main(String[] args) {
		// if Condition == "Refurbished" set Refurbished_Flag:1
		//				== "Open Box"    set OpenBox_Flag:1
		//              == "Clearance"   set Clearance_Flag:1
		// if License == "Only" set Licence_Flag:1
		//		      == "Include", do not set License_Flag value
		//            else, set Licence_Flag:0
		// if Availability == "In Stock" set InStock:1
		//                 == "Call"     set InStock:0
		String[] conditions = {
				"Category:\"System\" AND SubCategory:\"Notebook Computers\" AND Manufacturer:\"Apple\" AND Refurbished_Flag:1 AND InStock:1",
				"CatCode:3F* AND OpenBox_Flag:1 AND InStock:0",
				"Clearance_Flag:1 AND Licence_Flag:0",
				""
		};
		
		for (String condition: conditions) {
			RedirectRuleCondition rr = new RedirectRuleCondition(condition);
			System.out.println(rr.getCondition());
			System.out.println(rr.getReadableString());
			System.out.println(rr.getIMSFilters());
			System.out.println(rr.getFacets());
		}
	}
	
}
