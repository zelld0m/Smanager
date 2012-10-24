package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="versions")
@XmlSeeAlso(RuleVersionXml.class)
public class RuleVersionListXml<T>{
	
	private List<T> versions;
	private int nextVersion;
	
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
		@XmlElementRef(name="demote", type=DemoteRuleXml.class)
	})
	public List<T> getVersions() {
		return versions;
	}

	public void setVersions(List<T> versions) {
		this.versions = versions;
	}
	
	@XmlAttribute
	public int getNextVersion() {
		return nextVersion;
	}
	
	public void setNextVersion(int nextVersion) {
		this.nextVersion = nextVersion;
	}
}