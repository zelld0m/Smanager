package com.search.manager.report.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.search.manager.enums.RuleEntity;

@XmlRootElement(name = "rulefile")
public class RuleFileXml extends RuleXml {

    private static final long serialVersionUID = 1L;

    private String path;

    private String entityType;
    
    private String contentFileName;
    
    private RuleXml content;

    public RuleFileXml() {
    }

    public RuleFileXml(String path) {
        this.path = path;
    }

    public RuleFileXml(String store, long version, String name, String notes, String username, Date date,
            String ruleId, RuleEntity type, RuleXml content) {
        this();
        this.setRuleId(ruleId);
        this.setRuleName(type.getValues().get(0));
        this.setName(name);
        this.setNotes(notes);
        this.setCreatedBy(username);
        this.setCreatedDate(date);
        this.setStore(store);
        this.setVersion(version);
        this.setEntityType(type.getValues().get(0));
        this.setContent(content);
        this.setRuleEntity(type);
    }

    @XmlTransient
    public RuleXml getContent() {
        return content;
    }

    public void setContent(RuleXml content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @XmlTransient
    public String getContentFileName() {
        return contentFileName;
    }

    public void setContentFileName(String contentFileName) {
        this.contentFileName = contentFileName;
    }
}
