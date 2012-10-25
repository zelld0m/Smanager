package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.ExcludeResult;

@XmlRootElement(name="item")
public class ExcludeItemXml extends RuleItemXml{

	private static final long serialVersionUID = 1L;
		
	public ExcludeItemXml(){
		super();
	}
	
	public ExcludeItemXml(ExcludeResult er){
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
	}
}