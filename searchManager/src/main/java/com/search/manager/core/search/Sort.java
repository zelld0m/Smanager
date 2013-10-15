package com.search.manager.core.search;

public class Sort {

	protected String property;
	protected boolean desc = false;
	protected boolean ignoreCase = false;

	public Sort(String property, boolean desc, boolean ignoreCase) {
		this.property = property;
		this.desc = desc;
		this.ignoreCase = ignoreCase;
	}

	public Sort(String property, boolean desc) {
		this.property = property;
		this.desc = desc;
	}

	public Sort(String property) {
		this.property = property;
	}

	public static Sort asc(String property) {
		return new Sort(property);
	}

	public static Sort asc(String property, boolean ignoreCase) {
		return new Sort(property, ignoreCase);
	}

	public static Sort desc(String property) {
		return new Sort(property, true);
	}

	public static Sort desc(String property, boolean ignoreCase) {
		return new Sort(property, true, ignoreCase);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
}
