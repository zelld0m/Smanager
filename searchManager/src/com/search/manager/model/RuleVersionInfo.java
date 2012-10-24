package com.search.manager.model;

import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RuleVersionInfo {
	
	private String ruleId;
	private Date dateCreated;
	private int version;
	private String notes;
	private String name;
	
	@DataTransferObject(converter = BeanConverter.class)
	public static class AccessControl {
		private String currentOwner;
		private boolean locked;
		
		public String getCurrentOwner() {
			return currentOwner;
		}
		
		public void setCurrentOwner(String currentOwner) {
			this.currentOwner = currentOwner;
		}
		
		public boolean isLocked() {
			return locked;
		}
		
		public void setLocked(boolean locked) {
			this.locked = locked;
		}
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}