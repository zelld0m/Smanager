package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Group implements Serializable {

	private static final long serialVersionUID = -3394868762818647375L;

	private String groupId;
	private String permissionId;
	
	public Group(){
	}

	public Group(String groupId, String permissionId) {
		super();
		this.groupId = groupId;
		this.permissionId = permissionId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

}