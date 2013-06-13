package com.search.manager.report.model.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;

/**
 * Rule version stored in database.
 */
@XmlRootElement(name = "dbruleversion")
public class DBRuleVersion extends RuleXml {

	private static final long serialVersionUID = 1L;

	private String entityType;

	private Map<String, String> props = new HashMap<String, String>();

	public DBRuleVersion() {
	}

	public DBRuleVersion(String store, long version, String name, String notes, String username, DateTime date,
	        String ruleId, RuleEntity type) {
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
		this.setRuleEntity(type);
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}

	@XmlTransient
	@Override
	public RuleEntity getRuleEntity() {
		RuleEntity re = super.getRuleEntity();
		
		if (re == null && entityType != null) {
			re = RuleEntity.find(entityType);
		}
		
		return re;
	}
}
