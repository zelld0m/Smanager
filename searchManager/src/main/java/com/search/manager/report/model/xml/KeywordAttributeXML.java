package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.enums.KeywordAttributeType;
import com.search.manager.core.model.KeywordAttribute;

@XmlRootElement(name = "keywordAttribute")
@DataTransferObject(converter = BeanConverter.class)
public class KeywordAttributeXML extends RuleItemXml{
	
	private static final long serialVersionUID = -910645123152272151L;
	private String inputParamEnumId;
	private String inputValue;
	private Integer priority;
	private Boolean disabled;
	private KeywordAttributeType keywordAttributeType;
	private List<KeywordAttributeXML> keywordAttributeItems;
	
	public KeywordAttributeXML() {
		super();
	}
	
	public KeywordAttributeXML(KeywordAttribute rule) {
		super();
		this.setCreatedDate(rule.getCreatedDate());
        this.setCreatedBy(rule.getCreatedBy());
        this.setLastModifiedBy(rule.getLastModifiedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());
        
        this.setKeywordAttributeType(rule.getKeywordAttributeType());
        this.setInputParamEnumId(rule.getInputParamEnumId());
        this.setInputValue(rule.getInputValue());
        this.setDisabled(rule.getDisabled());
        this.setPriority(rule.getPriority());
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
	
	@XmlElementRef(type=KeywordAttributeXML.class)
	public List<KeywordAttributeXML> getKeywordAttributeItems() {
		return keywordAttributeItems;
	}
	public void setKeywordAttributeItems(
			List<KeywordAttributeXML> keywordAttributeItems) {
		this.keywordAttributeItems = keywordAttributeItems;
	}
	
	
}
