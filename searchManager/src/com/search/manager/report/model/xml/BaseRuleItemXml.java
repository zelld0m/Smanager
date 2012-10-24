package com.search.manager.report.model.xml;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.search.manager.enums.MemberTypeEntity;

@XmlSeeAlso({
	ElevateItemXml.class,
	ExcludeItemXml.class,
	DemoteItemXml.class	
})
public class BaseRuleItemXml implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String memberId;
	private MemberTypeEntity memberType;
	private String edp;
	private String condition;
	private Date expiryDate;
	private String createdBy;
	private String lastModifiedBy;
	private Date lastModifiedDate;
	private Date createdDate;
	
	@XmlAttribute(name="id", required=true)
	public String getMemberId() {
		return memberId;
	}
	
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	@XmlAttribute(name="type", required=true)
	public MemberTypeEntity getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberTypeEntity memberType) {
		this.memberType = memberType;
	}
	
	public String getEdp() {
		return edp;
	}

	public void setEdp(String edp) {
		this.edp = edp;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}