package com.search.manager.model;

import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RelevancyKeyword extends ModelBean {

	private static final long serialVersionUID = 1L;

	private Keyword keyword;
	private Relevancy relevancy;
	private Integer priority;
	
	public RelevancyKeyword() {
	}
	
	public RelevancyKeyword(Keyword keyword, Relevancy relevancy) {
		this.keyword = keyword;
		this.relevancy = relevancy;
	}
	
	public RelevancyKeyword(Keyword keyword, Relevancy relevancy, Integer priority,
			String createdBy, String lastModifiedBy, Date createdDate, Date lastModifiedDate) {
		this.keyword = keyword;
		this.relevancy = relevancy;
		this.priority = priority;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}
	
	public Keyword getKeyword() {
		return keyword;
	}
	
	public void setRelevancy(Relevancy relevancy) {
		this.relevancy = relevancy;
	}
	
	public Relevancy getRelevancy() {
		return relevancy;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
}
