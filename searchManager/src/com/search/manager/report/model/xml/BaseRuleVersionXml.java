package com.search.manager.report.model.xml;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;

public class BaseRuleVersionXml implements Serializable {
	
	private static final long serialVersionUID = -368623910806297877L;
	
	private String version;
	private String reason;
	private String name;
	private String createdBy;
	private Date createdDate;
	private long serial;
	
	public BaseRuleVersionXml() {
		super();
	}

	public BaseRuleVersionXml(long serial) {
		super();
		this.serial = serial;
	}

	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public long getSerial() {
		return serial;
	}

	@XmlAttribute
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}