package com.search.manager.enums;

public enum CatCodes {
	CATEGORY_CODES(2, "Category Codes"),
	CATEGORY_OVERRIDE_RULES(3, "Category Override Rules"),
	TEMPLATE_USED(4, "Template-Used for Cat Override"),
	SOLR_TEMPLATE_MASTER(5, "Solr_Template_Master"),
	SOLR_ATTRIBUTE_MASTER(6, "Solr_Attribute_Master"),
	SOLR_TEMPLATE_ATTRIBUTE(8, "Solr_Template_Attribute_XRef"),
	SOLR_ATTRIBUTE_RANGE(9, "Solr_Attribute_Range_Values"),
	SOLR_SEARCH_NAV(11, "SOLR_Search_Nav"),
	ALTERNATE_CNET(0, "Alternate CNET"),
	WORKBOOK_OBJECTS(999, "Workbook objects"),
	WORKBOOK_OBJECTS_CNET_ALTERNATE(998, "Workbook objects cnet altername"),
	LEVEL1(997, "Level 1"),
	LEVEL2(996, "Level 2"),
	LEVEL3(995, "Level 3"),
	LEVEL4(994, "Level 4");
	
	private final int code;
	private final String value;
   
	private CatCodes(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}
	
	public String getCodeStr() {
		return String.valueOf(code);
	}

	public String getValue() {
		return value;
	}
}