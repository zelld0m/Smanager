package com.search.manager.core.model;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.enums.KeywordAttributeType;
import com.search.manager.report.model.xml.KeywordAttributeXML;

@DataTransferObject(converter = BeanConverter.class)
public class KeywordAttribute extends ModelBean{

	private static final long serialVersionUID = 3469870725120787439L;
	
	private String keywordAttributeId;
	private String keywordId;
	private String typeaheadRuleId;
	private String inputParamEnumId;
	private String inputValue;
	private Integer priority;
	private String parentAttributeId;
	private Boolean disabled;
	private KeywordAttributeType keywordAttributeType;
	private List<KeywordAttribute> keywordAttributeItems;
	
	public KeywordAttribute() {
		super();
	}
	
	public KeywordAttribute(KeywordAttributeXML xml) {
		super();
		
		this.inputValue = xml.getInputValue();
		this.priority = xml.getPriority();
		this.disabled = xml.getDisabled();
		this.inputParamEnumId = xml.getInputParamEnumId();
		this.keywordAttributeType = xml.getKeywordAttributeType();
	}
	
	public String getKeywordAttributeId() {
		return keywordAttributeId;
	}
	public void setKeywordAttributeId(String keywordAttributeId) {
		this.keywordAttributeId = keywordAttributeId;
	}
	
	public String getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(String keywordId) {
		this.keywordId = keywordId;
	}
	
	public String getTypeaheadRuleId() {
		return typeaheadRuleId;
	}
	public void setTypeaheadRuleId(String typeaheadRuleId) {
		this.typeaheadRuleId = typeaheadRuleId;
	}
	
	public String getInputParamEnumId() {
		return inputParamEnumId;
	}
	public void setInputParamEnumId(String inputParamEnumId) {
		this.inputParamEnumId = inputParamEnumId;
	}
	
	public String getInputValue() {
		return inputValue;
	}
	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
	
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public String getParentAttributeId() {
		return parentAttributeId;
	}
	
	public void setParentAttributeId(String parentAttributeId) {
		this.parentAttributeId = parentAttributeId;
	}
	
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	
	public KeywordAttributeType getKeywordAttributeType() {
		return keywordAttributeType;
	}
	public void setKeywordAttributeType(KeywordAttributeType keywordAttributeType) {
		this.keywordAttributeType = keywordAttributeType;
	}
	
	public List<KeywordAttribute> getKeywordAttributeItems() {
		return keywordAttributeItems;
	}
	public void setKeywordAttributeItems(
			List<KeywordAttribute> keywordAttributeItems) {
		this.keywordAttributeItems = keywordAttributeItems;
	}
		
	public void addKeywordAttribute(KeywordAttribute item) {
		if(keywordAttributeItems == null) {
			keywordAttributeItems = new ArrayList<KeywordAttribute>();
		}
		
		keywordAttributeItems.add(item);
	}
	
	public String[] getKeywordItemValues() {
		
		if(keywordAttributeItems != null) {
			String[] items = new String[keywordAttributeItems.size()];
			
			for(int i=0; i<keywordAttributeItems.size(); i++) {
				items[i] = keywordAttributeItems.get(i).inputValue;
			}
			
			return items;
			
		}
		
		return null;
	}
}
