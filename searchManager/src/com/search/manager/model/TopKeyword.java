package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class TopKeyword implements Serializable {

	private static final long serialVersionUID = 5855294022170978094L;

	private String keyword;
	private int count;
	
	public TopKeyword(){
	}

	public TopKeyword(String keyword, int count) {
		super();
		this.keyword = keyword;
		this.count = count;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


}