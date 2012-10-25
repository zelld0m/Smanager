package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.ElevateResult;

@XmlRootElement(name="item")
public class ElevateItemXml extends RuleItemXml{
	
	private static final long serialVersionUID = 1L;
	private int location;
	private boolean forceAdd;
	
	public ElevateItemXml(){
		super();
	}
	
	public ElevateItemXml(ElevateResult er){
		super();
		this.setMemberId(er.getMemberId());
		this.setMemberType(er.getMemberType());
		if(er.getMemberType() == MemberTypeEntity.FACET && er.getCondition()!=null){
			this.setCondition(er.getCondition().getCondition());
		}else{
			this.setEdp(er.getEdp()) ;
		}
		this.setExpiryDate(er.getExpiryDate());
		this.setCreatedBy(er.getCreatedBy());
		this.setLastModifiedBy(er.getLastModifiedBy());
		this.setCreatedDate(er.getCreatedDate());
		this.setLastModifiedDate(er.getLastModifiedDate());
		this.location = er.getLocation();
		this.forceAdd = er.isForceAdd();
	}
	
	public int getLocation() {
		return location;
	}
	
	public void setLocation(int location) {
		this.location = location;
	}

	public boolean isForceAdd() {
		return forceAdd;
	}

	public void setForceAdd(boolean forceAdd) {
		this.forceAdd = forceAdd;
	}
}