package com.search.manager.report.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.search.manager.enums.MemberTypeEntity;

@XmlSeeAlso({
	ElevateItemXml.class,
	ExcludeItemXml.class,
	DemoteItemXml.class
})
public class RuleItemXml extends BaseEntityXml{
	
	private static final long serialVersionUID = 1L;
	private String memberId;
	private MemberTypeEntity memberType;
	private String edp;
	private String condition;
	private Date expiryDate;
	
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
}