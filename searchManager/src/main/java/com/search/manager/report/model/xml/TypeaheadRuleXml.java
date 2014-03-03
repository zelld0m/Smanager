package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.enums.RuleEntity;

@XmlRootElement(name = "typeaheadRuleXml")
//@XmlType(propOrder={"brands, suggestions"})
@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadRuleXml extends RuleXml{

	
	private static final long serialVersionUID = 1L;
	private static final RuleEntity RULE_ENTITY = RuleEntity.TYPEAHEAD;

	
	private List<TypeaheadSuggestionXml> suggestions;
	private List<TypeaheadBrandXml> brands;
	
	private Integer sortOrder;
	
	public TypeaheadRuleXml() {
		super();
	}
	
	public TypeaheadRuleXml(TypeaheadRule rule, List<TypeaheadBrand> brandList, List<TypeaheadSuggestion> suggestionList) {
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
                
        if(brandList != null && !brandList.isEmpty()) {
        	List<TypeaheadBrandXml> brands = new ArrayList<TypeaheadBrandXml>();
        	
        	for(TypeaheadBrand brand : brandList) {
        		brands.add(new TypeaheadBrandXml(brand));
        	}
        	
        	this.brands = brands;
        }
        
        if(suggestionList != null && !suggestionList.isEmpty()) {
        	List<TypeaheadSuggestionXml> suggestions = new ArrayList<TypeaheadSuggestionXml>();
        	
        	for(TypeaheadSuggestion suggestion : suggestionList) {
        		suggestions.add(new TypeaheadSuggestionXml(suggestion));
        	}
        	
        	this.suggestions = suggestions;
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

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
