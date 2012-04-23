package com.search.webservice.model;

import java.util.List;

import com.search.manager.enums.RuleEntity;

public class TransportList extends UserToken{

	private static final long serialVersionUID = -1763176326995632123L;

	private List<String> list;
	private RuleEntity ruleEntity;

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public RuleEntity getRuleEntity() {
		return ruleEntity;
	}

	public void setRuleEntity(RuleEntity ruleEntity) {
		this.ruleEntity = ruleEntity;
	}
}
