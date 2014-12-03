package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.model.KeywordAttribute;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.enums.RuleEntity;

@XmlRootElement(name = "typeahead")
@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadRuleXml extends RuleXml{

	private static final long serialVersionUID = 1L;
	private static final RuleEntity RULE_ENTITY = RuleEntity.TYPEAHEAD;
	
	private List<TypeaheadSuggestionXml> suggestions;
	private List<TypeaheadBrandXml> brands;
	private List<KeywordAttributeXML> keywordAttributes;
	private Integer priority;
	private Boolean disabled;
	
	public TypeaheadRuleXml() {
		super();
		this.setRuleEntity(RULE_ENTITY);
	}
	
	public TypeaheadRuleXml(TypeaheadRule rule) {
		super();
		this.setSerial(serialVersionUID);
		this.setRuleEntity(RULE_ENTITY);
		this.setCreatedDate(rule.getCreatedDate());
        this.setCreatedBy(rule.getCreatedBy());
        this.setLastModifiedBy(rule.getLastModifiedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());
        this.setRuleName(rule.getRuleName());
        this.setRuleId(rule.getRuleId());
        this.setStore(rule.getStoreId());
        this.setDisabled(rule.getDisabled());
        this.setPriority(rule.getPriority());
        
        if(rule.getSectionList() != null) {
        	List<KeywordAttributeXML> attributes = new ArrayList<KeywordAttributeXML>();
        	
        	for(KeywordAttribute section : rule.getSectionList()) {
        		KeywordAttributeXML attribute = new KeywordAttributeXML(section);
        		
        		
        		if(section.getKeywordAttributeItems() != null) {
        			List<KeywordAttributeXML> attributeItems = new ArrayList<KeywordAttributeXML>();
        			
        			for(KeywordAttribute sectionItem : section.getKeywordAttributeItems()) {
        				attributeItems.add(new KeywordAttributeXML(sectionItem));
        			}
        			
        			attribute.setKeywordAttributeItems(attributeItems);
        		}
        		
        		attributes.add(attribute);
        	}
        	
        	this.setKeywordAttributes(attributes);
        }
        
	}

	@XmlElementRef(type=TypeaheadSuggestionXml.class)
	public List<TypeaheadSuggestionXml> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<TypeaheadSuggestionXml> suggestions) {
		this.suggestions = suggestions;
	}

	@XmlElementRef(type=TypeaheadBrandXml.class)
	public List<TypeaheadBrandXml> getBrands() {
		return brands;
	}

	public void setBrands(List<TypeaheadBrandXml> brands) {
		this.brands = brands;
	}

	@XmlElementRef(type=KeywordAttributeXML.class)
	public List<KeywordAttributeXML> getKeywordAttributes() {
		return keywordAttributes;
	}

	public void setKeywordAttributes(List<KeywordAttributeXML> keywordAttributes) {
		this.keywordAttributes = keywordAttributes;
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
}
