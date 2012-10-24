package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
public class DemoteItemXml extends BaseRuleItemXml{
	
	private static final long serialVersionUID = 1L;
	private int location;

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}	
}