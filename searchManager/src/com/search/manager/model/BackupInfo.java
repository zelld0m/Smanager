package com.search.manager.model;

import java.util.Date;

public class BackupInfo {
	
	private String ruleId;
	private Date dateCreated;
	private boolean hasBackup;
	private long fileSize;
	
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
	public boolean isHasBackup() {
		return hasBackup;
	}
	public void setHasBackup(boolean hasBackup) {
		this.hasBackup = hasBackup;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
