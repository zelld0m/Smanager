package com.search.manager.model;

public class CategoryCNET {
	
	private String id;
	private String name;
	private String displayName;
	private String parentId;
	private String store;
	
	public CategoryCNET(){}
	
	public CategoryCNET(String id, String name, String displayName, String parentId, String store){
		this.id=id;
		this.name=name;
		this.displayName=displayName;
		this.parentId=parentId;
		this.store=store;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}
}
