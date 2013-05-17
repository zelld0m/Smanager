package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class ImagePath extends ModelBean{
	private static final long serialVersionUID = 716880772182956579L;
	
	public String id;
	public String path;
	public ImagePathType pathType;
	public String storeId;
	public String alias;
	
	public ImagePath() {
		// TODO Auto-generated constructor stub
	}
	
	public ImagePath(String id, String path, ImagePathType pathType,
			String alias) {
		super();
		this.id = id;
		this.path = path;
		this.pathType = pathType;
		this.alias = alias;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public ImagePathType getPathType() {
		return pathType;
	}
	
	public void setPathType(ImagePathType pathType) {
		this.pathType = pathType;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
}