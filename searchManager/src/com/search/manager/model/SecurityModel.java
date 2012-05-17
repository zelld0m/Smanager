package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter=BeanConverter.class)
public class SecurityModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String username;
	private String status;
	private String dateStarted;
	private String action;
	private String type;
	private boolean isDefault;
	private String roleId;
	private String expired;
	private String lastAccess;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDateStarted() {
		return dateStarted;
	}
	public void setDateStarted(String dateStarted) {
		this.dateStarted = dateStarted;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(String lastAccess) {
		this.lastAccess = lastAccess;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
	}
}
