package com.search.manager.core.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.annotation.SolrCore;
import com.search.manager.report.model.xml.KeywordAttributeXML;
import com.search.manager.report.model.xml.TypeaheadRuleXml;

@SolrCore(name = "typeaheadpub")
@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadRule extends ModelBean {

    private static final long serialVersionUID = 7840812498092574024L;

    private String ruleId;
    private String storeId;
    private String ruleName;
    private Integer priority;
    private Boolean disabled;
    private List<KeywordAttribute> sectionList;

    public TypeaheadRule() {
        super();
    }

    public TypeaheadRule(TypeaheadRuleXml xml) {
        super();

        this.ruleId = xml.getRuleId();
        this.storeId = xml.getStore();
        this.ruleName = xml.getRuleName();
        this.createdBy = xml.getCreatedBy();
        this.priority = xml.getPriority();
        this.disabled = xml.getDisabled();
        
        initSections(xml);
    }

    public String getRuleId() {
        return ruleId;
    }

    @Field("ruleId")
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getStoreId() {
        return storeId;
    }

    @Field("store")
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getRuleName() {
        return ruleName;
    }

    @Field("keyword")
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getPriority() {
        return priority;
    }

    @Field("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    @Field("disabled")
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getId() {
        return storeId + "_" + ruleName;
    }

    @Field("id")
    public void setId(String id) {
        // for solr field processing
    }

	public List<KeywordAttribute> getSectionList() {
		return sectionList;
	}
	public void setSectionList(List<KeywordAttribute> sectionList) {
		this.sectionList = sectionList;
	}
    
    public void initSections(TypeaheadRuleXml xml) {
    	// Restore section items
        if(xml.getKeywordAttributes() != null && xml.getKeywordAttributes().size() > 0) {
        	
        	List<KeywordAttribute> sectionList = new ArrayList<KeywordAttribute>();
        	
        	for(KeywordAttributeXML sectionXML : xml.getKeywordAttributes()) {
        		KeywordAttribute section = new KeywordAttribute(sectionXML);
        		section.setTypeaheadRuleId(xml.getRuleId());
        		
        		if(sectionXML.getKeywordAttributeItems() != null && sectionXML.getKeywordAttributeItems().size() > 0) {
        			List<KeywordAttribute> sectionItemList = new ArrayList<KeywordAttribute>();
        			
        			for(KeywordAttributeXML sectionItemXML : sectionXML.getKeywordAttributeItems()) {
        				KeywordAttribute sectionItem = new KeywordAttribute(sectionItemXML);
        				sectionItem.setTypeaheadRuleId(xml.getRuleId());
        				sectionItemList.add(sectionItem);
        			}
        			
        			section.setKeywordAttributeItems(sectionItemList);
        		}
        		
        		sectionList.add(section);
        	}
        	this.sectionList = sectionList;
        }
    }
}
