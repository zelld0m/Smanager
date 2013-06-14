package com.search.manager.model;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;
import com.search.manager.report.model.xml.RuleItemXml;

public class SearchResult extends ModelBean {

	private static final long serialVersionUID = 6843636825614943522L;

	protected StoreKeyword storeKeyword;
	protected String edp;
	protected DateTime expiryDate;
	protected MemberTypeEntity entity;
	protected RedirectRuleCondition condition;
	protected String memberId;
	
	public SearchResult() {}
	
	public SearchResult(StoreKeyword storeKeyword, RuleItemXml xml) {
		this.storeKeyword = storeKeyword;
		this.edp = xml.getEdp();
		this.expiryDate = xml.getExpiryDate();
		this.entity = xml.getMemberType();
		this.condition = xml.getRuleCondition();
		this.memberId = xml.getMemberId();
		this.lastModifiedBy = xml.getLastModifiedBy();
		this.lastModifiedDate = xml.getLastModifiedDate();
		this.createdBy = xml.getCreatedBy();
		this.createdDate = xml.getCreatedDate();
	}
	
	public SearchResult(StoreKeyword storeKeyword, String edp, String comment, String createdBy, String lastModifiedBy, DateTime expiryDateTime, DateTime createdDateTime, DateTime lastModifiedDateTime, String memberTypeId, String memberId) {
		if (memberTypeId.equals(MemberTypeEntity.FACET.toString())) {
			entity = MemberTypeEntity.FACET;
			condition = new RedirectRuleCondition(edp);
			if (storeKeyword != null) {
				condition.setStoreId(storeKeyword.getStoreId());
			}
			this.edp = "";
		} else {
			entity = MemberTypeEntity.PART_NUMBER;
			this.edp = edp;
		}
		this.storeKeyword= storeKeyword;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.expiryDate = expiryDateTime;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = lastModifiedDateTime;
		this.memberId = memberId;
	}
	
	public StoreKeyword getStoreKeyword() {
		return storeKeyword;
	}
	
	public void setStoreKeyword(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(DateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getEdp() {
		return edp;
	}
	
	public void setEdp(String edp) {
		this.edp = edp;
	}

	public MemberTypeEntity getEntity() {
		return entity;
	}

	public void setEntity(MemberTypeEntity entity) {
		this.entity = entity;
	}

	public RedirectRuleCondition getCondition() {
		return condition;
	}

	public void setCondition(RedirectRuleCondition condition) {
		this.condition = condition;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public MemberTypeEntity getMemberType() {
		return this.entity;
	}

	public void setMemberType(MemberTypeEntity memberType) {
		this.entity = memberType;
	}
}