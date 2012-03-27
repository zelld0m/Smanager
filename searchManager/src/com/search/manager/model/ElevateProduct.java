package com.search.manager.model;

import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class ElevateProduct extends Product {

	private static final long serialVersionUID = -4043292541092899423L;

	private Integer location;
	
	public ElevateProduct(){
		super();
	}
	
	public ElevateProduct(String dpNo, String edp, String manufacturer, String mfrPN,
			String name, String description, String imagePath, Date expiryDate, Date createdDate,
			Date lastModifiedDate, String comment, String lastModifiedBy,
			String createdBy, Integer location) {
		super(dpNo, edp, manufacturer, mfrPN, name, description, imagePath, expiryDate, createdDate, lastModifiedDate, comment, lastModifiedBy, createdBy);
		this.location = location;
	}
	
	public Integer getLocation() {
		return location;
	}
	
	public void setLocation(Integer location) {
		this.location = location;
	}
	
	//TODO: message should be configurable
	public String getValidityText() {

		if (getExpiryDate()==null) return "";
			
		Date dateNow = new Date();

		long dateNowMS = DateAndTimeUtils.getDate(getStore(), dateNow).getTime();
		long validityMS = DateAndTimeUtils.getDate(getStore(), getExpiryDate()).getTime();
		long diff = validityMS - dateNowMS;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		return diffDays > 0 ? String.format("%s %s", String.valueOf(diffDays), diffDays==1? "day left" : "days left"): (diffDays == 0 ? "Ending Today" : "");
	}
}