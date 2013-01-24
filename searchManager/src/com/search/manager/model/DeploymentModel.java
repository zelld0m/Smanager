package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter=BeanConverter.class)
public class DeploymentModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String ruleId;
	private int published;
	
	public DeploymentModel() {
		super();
	}
	
	public DeploymentModel(String ruleId, int published) {
		super();
		this.ruleId = ruleId;
		this.published = published;
	}

	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public int getPublished() {
		return published;
	}
	public void setPublished(int published) {
		this.published = published;
	}
}
