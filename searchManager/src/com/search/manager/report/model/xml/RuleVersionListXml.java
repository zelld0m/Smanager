package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="versions")
@XmlSeeAlso(RuleXml.class)
public class RuleVersionListXml<T>{
	
	private List<T> versions;
	private long nextVersion;
	private String ruleId;
	private String ruleName;
	
	private RuleVersionListXml(List<T> versions, int nextVersion) {
		super();
		this.versions = versions;
		this.nextVersion = nextVersion;
	}

	public RuleVersionListXml(int nextVersion){
		this(new ArrayList<T>(), nextVersion);
	}
	
	public RuleVersionListXml(){
		this(new ArrayList<T>(), 1);
	}
	
	@XmlElementRefs({
		@XmlElementRef(name="elevate", type=ElevateRuleXml.class),
		@XmlElementRef(name="exclude", type=ExcludeRuleXml.class),
		@XmlElementRef(name="demote", type=DemoteRuleXml.class),
		@XmlElementRef(name="facetsort", type=FacetSortRuleXml.class),
		@XmlElementRef(name="querycleaning", type=RedirectRuleXml.class),
		@XmlElementRef(name="rankingrule", type=RankingRuleXml.class)
	})
	public List<T> getVersions() {
		return versions;
	}

	public void setVersions(List<T> versions) {
		this.versions = versions;
	}
	
	@XmlAttribute
	public long getNextVersion() {
		return nextVersion;
	}
	
	public void setNextVersion(long nextVersion) {
		this.nextVersion = nextVersion;
	}

	@XmlAttribute(name="id")
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	@XmlAttribute(name="name")
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}