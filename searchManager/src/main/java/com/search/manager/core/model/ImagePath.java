package com.search.manager.core.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class ImagePath extends ModelBean {
	private static final long serialVersionUID = 716880772182956579L;

	public String storeId;
	public String id;
	public String path;
	public String size;
	public ImagePathType pathType;
	public String alias;

	public ImagePath() {

	}

	public ImagePath(String storeId, String id, String path, String size,
			ImagePathType pathType, String alias, String createdBy,
			String lastModifiedBy) {
		super();
		this.storeId = storeId;
		this.id = id;
		this.path = path;
		this.size = size;
		this.pathType = pathType;
		this.alias = alias;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
	}

	public ImagePath(String storeId, String id, String path, String size,
			ImagePathType pathType, String alias, String createdBy) {
		this(storeId, id, path, size, pathType, alias, createdBy, null);
	}

	public ImagePath(String storeId, String id, String path, String size,
			ImagePathType pathType, String alias) {
		this(storeId, id, path, size, pathType, alias, null, null);
	}

	public ImagePath(String storeId, String id, String path) {
		this(storeId, id, path, null, null, null, null);
	}

	public ImagePath(String storeId, String path) {
		this(storeId, null, path, null, null, null, null);
	}

	public String getId() {
		return id;
	}

	// @Field("imagePathId")
	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	// @Field
	public void setPath(String path) {
		this.path = path;
	}

	public ImagePathType getPathType() {
		return pathType;
	}

	// @Field
	public void setPathType(ImagePathType pathType) {
		this.pathType = pathType;
	}

	public String getAlias() {
		return alias;
	}

	// @Field
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getSize() {
		return size;
	}

	// @Field
	public void setSize(String size) {
		this.size = size;
	}

}