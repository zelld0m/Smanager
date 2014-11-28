package com.search.manager.core.enums;

public enum KeywordAttributeType {

	CATEGORY("Category", "KEY_CAT"),
	BRAND("Brand", "KEY_BRAND"),
	SUGGESTION("Suggestion", "KEY_SUGG"),
	SECTION("Section", "KEY_SECT"),
	SECTION_ITEM("Section Item", "KEY_SECT_ITEM");

	private String name;
	private String code;

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

	public static KeywordAttributeType find(String code) {
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
