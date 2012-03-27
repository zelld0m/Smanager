package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.utility.DateAndTimeUtils;

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
	private Date   expiryDate;
	private Date   createdDate;
	private Date   lastModifiedDate;
	private String comment;
	private String lastModifiedBy;
	private String createdBy;

	public Product() {
		super();
	}

	public Product(String dpNo, String edp, String manufacturer, String mfrPN,
			String name, String description, String imagePath, Date expiryDate, Date createdDate,
			Date lastModifiedDate, String comment, String lastModifiedBy,
			String createdBy) {
		super();
		this.dpNo = dpNo;
		this.edp = edp;
		this.manufacturer = manufacturer;
		this.mfrPN = mfrPN;
		this.name = name;
		this.description = description;
		this.imagePath = imagePath;
		this.expiryDate = expiryDate;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
		this.comment = comment;
		this.lastModifiedBy = lastModifiedBy;
		this.createdBy = createdBy;
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
	
	public String getFormattedExpiryDate() {
		return DateAndTimeUtils.formatDateUsingConfig(getStore(), getExpiryDate());
	}
	
	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public String getFormattedCreatedDate() {
		return DateAndTimeUtils.formatDateTimeUsingConfig(getStore(), getCreatedDate());
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getFormattedLastModifiedDate() {
		return DateAndTimeUtils.formatDateTimeUsingConfig(getStore(), getLastModifiedDate());
	}
	
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	
	public boolean getIsExpired() {
		
		if (getExpiryDate() != null)
			return DateAndTimeUtils.compare(getExpiryDate(), new Date()) < 0;
		
		return false;
	}
	
}
