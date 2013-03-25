package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

@DataTransferObject(converter = BeanConverter.class)
public class ElevateProduct extends Product {

	private static final long serialVersionUID = -4043292541092899423L;

	private Integer location;
	private Boolean foundFlag;
	
	public ElevateProduct(){
		super();
	}
	
	public ElevateProduct(String dpNo, String edp, String manufacturer, String mfrPN,
			String name, String description, String imagePath, DateTime expiryDateTime, DateTime createdDateTime,
			DateTime lastModifiedDateTime, String comment, String lastModifiedBy,
			String createdBy, Integer location) {
		super(dpNo, edp, manufacturer, mfrPN, name, description, imagePath, expiryDateTime, createdDateTime, lastModifiedDateTime, comment, lastModifiedBy, createdBy);
		this.location = location;
	}
	
	public ElevateProduct(ElevateResult e){
		super();
		this.setEdp(e.getEdp());
		this.setExpiryDateTime(e.getExpiryDateTime());
		this.setCreatedDateTime(e.getCreatedDateTime());
		this.setLastModifiedDateTime(e.getLastModifiedDateTime());
		this.setComment(e.getComment());
		this.setLastModifiedBy(e.getLastModifiedBy());
		this.setCreatedBy(e.getCreatedBy());
		this.setCondition(e.getCondition());
		this.setMemberTypeEntity(e.getElevateEntity());
		this.setMemberId(e.getMemberId());
		this.setForceAdd(e.getForceAdd());
		this.setStore(e.getStoreKeyword().getStoreId());
		this.setLocation(e.getLocation());
	}
	
	public Integer getLocation() {
		return location;
	}
	
	public void setLocation(Integer location) {
		this.location = location;
	}

	public Boolean getFoundFlag() {
		return foundFlag;
	}

	public void setFoundFlag(Boolean foundFlag) {
		this.foundFlag = foundFlag;
	}
}