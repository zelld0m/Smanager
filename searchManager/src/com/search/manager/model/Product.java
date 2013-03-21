package com.search.manager.model;

import org.apache.commons.lang.BooleanUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.jodatime.JodaTimeUtil;

@DataTransferObject(converter = BeanConverter.class)
public class Product extends ModelBean {

	private static final long serialVersionUID = -7549453734255029359L;

	private String dpNo;
	private String edp;
	private String manufacturer;
	private String mfrPN;
	private String name;
	private String description;
	private String imagePath;
	private String store;
	@DateTimeFormat private DateTime expiryDateTime;
	private MemberTypeEntity memberTypeEntity;
	private RedirectRuleCondition condition;
	private String memberId;
	private Boolean forceAdd;

	public Product() {
		super();
	}

	public Product(String dpNo, String edp, String manufacturer, String mfrPN,
			String name, String description, String imagePath, DateTime expiryDateTime, DateTime createdDateTime,
			DateTime lastModifiedDateTime, String comment, String lastModifiedBy,
			String createdBy) {
		super();
		this.dpNo = dpNo;
		this.edp = edp;
		this.manufacturer = manufacturer;
		this.mfrPN = mfrPN;
		this.name = name;
		this.description = description;
		this.imagePath = imagePath;
		this.expiryDateTime = expiryDateTime;
		this.createdDateTime = createdDateTime;
		this.lastModifiedDateTime = lastModifiedDateTime;
		this.comment = comment;
		this.lastModifiedBy = lastModifiedBy;
		this.createdBy = createdBy;
	}
	
	public Product(SearchResult e){
		super();
		this.setEdp(e.getEdp());
		this.setExpiryDateTime(e.getExpiryDateTime());
		this.setCreatedDateTime(e.getCreatedDateTime());
		this.setLastModifiedDateTime(e.getLastModifiedDateTime());
		this.setComment(e.getComment());
		this.setCondition(e.getCondition());
		this.setLastModifiedBy(e.getLastModifiedBy());
		this.setCreatedBy(e.getCreatedBy());
		this.setMemberId(e.getMemberId());
		this.setMemberTypeEntity(e.getMemberType());
		this.setStore(e.getStoreKeyword().getStoreId());
	}
	
	public String getDpNo() {
		return dpNo;
	}
	
	public void setDpNo(String dpNo) {
		this.dpNo = dpNo;
	}
	
	public String getEdp() {
		return edp;
	}
	
	public void setEdp(String edp) {
		this.edp = edp;
	}
	
	public String getManufacturer() {
		return manufacturer;
	}
	
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public String getMfrPN() {
		return mfrPN;
	}
	
	public void setMfrPN(String mfrPN) {
		this.mfrPN = mfrPN;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}
	
	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}
	
	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public DateTime getExpiryDateTime() {
		return expiryDateTime;
	}

	public void setExpiryDateTime(DateTime expiryDateTime) {
		this.expiryDateTime = expiryDateTime;
	}

	public boolean getIsExpired() {
		DateTime expiration = getExpiryDateTime();
		return (expiration!=null? DateTime.now().isAfter(expiration) :false);
	}
	
	public String getValidityText() {
		DateTime expiration = getExpiryDateTime();
		if(expiration!=null)
			return JodaTimeUtil.getRemainingDateTimeText(expiration, DateTime.now());
		return "";
	}

	public RedirectRuleCondition getCondition() {
		return condition;
	}

	public void setCondition(RedirectRuleCondition condition) {
		this.condition = condition;
	}

	public MemberTypeEntity getMemberTypeEntity() {
		return memberTypeEntity;
	}

	public void setMemberTypeEntity(MemberTypeEntity memberTypeEntity) {
		this.memberTypeEntity = memberTypeEntity;
	}
	
	public MemberTypeEntity getMemberType() {
		return memberTypeEntity;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Boolean getForceAdd() {
		return forceAdd;
	}

	public void setForceAdd(Boolean forceAdd) {
		this.forceAdd = forceAdd;
	}
	
	public boolean isForceAdd(){
		return BooleanUtils.isTrue(getForceAdd());
	}
}