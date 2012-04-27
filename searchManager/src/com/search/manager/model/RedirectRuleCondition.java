package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter=BeanConverter.class)
public class RedirectRuleCondition extends ModelBean {

	private static final long serialVersionUID = -6248904441308276235L;
	private String condition;

	public RedirectRuleCondition(String condition) {
		this.condition = condition;
	}
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
}
