package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Category implements Serializable {

	private static final long serialVersionUID = -4043292541092899423L;

	private String catCode;
	private String catName;
	
	public Category(){
	}

	public Category(String catCode, String catName) {
		this.catCode = catCode;
		this.catName = catName;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getCatCode() {
		return catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

}