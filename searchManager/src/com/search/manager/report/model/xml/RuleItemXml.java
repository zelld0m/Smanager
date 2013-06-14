package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.RedirectRuleCondition;

@XmlSeeAlso({
	ElevateItemXml.class,
	ExcludeItemXml.class,
	DemoteItemXml.class,
	RedirectRuleCondition.class,
	BannerItemXml.class
})
public class RuleItemXml extends BaseEntityXml{
	
	private static final long serialVersionUID = 1L;
	private String memberId;
	private MemberTypeEntity memberType;
	private String edp;
	private String condition;
	private String dpNo;
	private String imagePath;
	private String mfrNo;
	private String manufacturer;
	private RedirectRuleCondition ruleCondition;
	private DateTime expiryDateTime;
	
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
		if (ruleCondition!=null) 
			return ruleCondition.getCondition();
		
		return condition;
	}

	public void setCondition(String condition) {
		setRuleCondition(new RedirectRuleCondition(condition));
		this.condition = condition;
	}

	@XmlTransient
	public RedirectRuleCondition getRuleCondition() {
		return ruleCondition;
	}

	public void setRuleCondition(RedirectRuleCondition ruleCondition) {
		if(ruleCondition!=null)
			this.condition = ruleCondition.getCondition();
			
		this.ruleCondition = ruleCondition;
	}
	
	@XmlAttribute(name="expiryDate")
	public DateTime getExpiryDateTime() {
		return expiryDateTime;
	}

	public void setExpiryDateTime(DateTime expiryDateTime) {
		this.expiryDateTime = expiryDateTime;
	}

	@XmlTransient
	public String getDpNo() {
		return dpNo;
	}

	public void setDpNo(String dpNo) {
		this.dpNo = dpNo;
	}

	@XmlTransient
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@XmlTransient
	public String getMfrNo() {
		return mfrNo;
	}

	public void setMfrNo(String mfrNo) {
		this.mfrNo = mfrNo;
	}

	@XmlTransient
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
}