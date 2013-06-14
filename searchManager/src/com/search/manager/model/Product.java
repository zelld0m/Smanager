package com.search.manager.model;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.BooleanUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;

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
	private DateTime expiryDate;
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
		this.expiryDate = expiryDateTime;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = lastModifiedDateTime;
		this.comment = comment;
		this.lastModifiedBy = lastModifiedBy;
		this.createdBy = createdBy;
	}
	
	public Product(SearchResult e){
		super();
		this.setEdp(e.getEdp());
		this.setExpiryDate(e.getExpiryDate());
		this.setCreatedDate(e.getCreatedDate());
		this.setLastModifiedDate(e.getLastModifiedDate());
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
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(DateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean getIsExpired() {
		DateTime expiration = getExpiryDate();

		if(expiration!=null && Days.daysBetween(DateTime.now().toDateMidnight(), expiration.toDateMidnight()).getDays() < 0){
			return true;
		}
		
		return false;
	}
	
	public boolean isExpired(){
		return getIsExpired();
	}
	
	public String getValidityText() {
		DateTime expiration = getExpiryDate();
		return expiration!=null ? JodaDateTimeUtil.getRemainingDays(DateTime.now(), expiration): "";
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
	
	public String getFormattedExpiryDateTime(){
		return JodaDateTimeUtil.formatFromStorePattern(getExpiryDate(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedExpiryDate(){
		return JodaDateTimeUtil.formatFromStorePattern(getExpiryDate(), JodaPatternType.DATE);
	}
}