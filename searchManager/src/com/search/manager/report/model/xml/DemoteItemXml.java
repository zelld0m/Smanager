package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;

@XmlRootElement(name="item")
@DataTransferObject(converter = BeanConverter.class)
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
			this.setRuleCondition(dr.getCondition());
			
		}else{
			this.setEdp(dr.getEdp()) ;
		}
		this.setExpiryDateTime(dr.getExpiryDateTime());
		this.setCreatedBy(dr.getCreatedBy());
		this.setLastModifiedBy(dr.getLastModifiedBy());
		this.setCreatedDateTime(dr.getCreatedDateTime());
		this.setLastModifiedDateTime(dr.getLastModifiedDateTime());
		this.location = dr.getLocation();
	}
	
	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}	
}