package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;

@XmlRootElement(name="item")
public class DemoteItemXml extends RuleItemXml{
	
	private static final long serialVersionUID = 1L;
	private int location;

	public DemoteItemXml(){
		super();
	}
	
	public DemoteItemXml(DemoteResult dr){
		super();
		this.setMemberId(dr.getMemberId());
		this.setMemberType(dr.getMemberType());
		if(dr.getMemberType() == MemberTypeEntity.FACET && dr.getCondition()!=null){
			this.setCondition(dr.getCondition().getCondition());
		}else{
			this.setEdp(dr.getEdp()) ;
		}
		this.setExpiryDate(dr.getExpiryDate());
		this.setCreatedBy(dr.getCreatedBy());
		this.setLastModifiedBy(dr.getLastModifiedBy());
		this.setCreatedDate(dr.getCreatedDate());
		this.setLastModifiedDate(dr.getLastModifiedDate());
		this.location = dr.getLocation();
	}
	
	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}	
}