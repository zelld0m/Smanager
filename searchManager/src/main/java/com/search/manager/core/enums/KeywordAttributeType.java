package com.search.manager.core.enums;

public enum KeywordAttributeType {

	CATEGORY("Category", "KEY_CAT"),
	BRAND("Brand", "KEY_BRAND"),
	SUGGESTION("Suggestion", "KEY_SUGGESTION"),
	SECTION("Section", "KEY_SECTION"),
	SECTION_ITEM("Section Item", "KEY_SEC_ITEM"),
	OVERRIDE_PRIORITY("Override Priority", "OVERRIDE_PRIORITY");

	private String name;
	private String code;
	public static KeywordAttributeType[] PARENT_TYPES = {CATEGORY, BRAND, SUGGESTION, SECTION};
	
	private KeywordAttributeType(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public String getCode() {
		return code;
	}

	public static KeywordAttributeType find(int ordinal) {
		for (int i = 0; i < values().length; i++) {
			if (i == ordinal)
				return values()[i];
		}
		return null;
	}

	public static KeywordAttributeType findbyCode(String code) {
		if(code == null)
			return null;

		KeywordAttributeType[] list = values();

		for (int i = 0; i < list.length; i++) {
			if(code.equals(list[i].getCode())){
				return list[i];
			}
		}
		return null;
	}
}
