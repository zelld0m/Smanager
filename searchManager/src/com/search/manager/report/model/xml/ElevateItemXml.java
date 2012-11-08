package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Product;

@XmlRootElement(name="item")
@DataTransferObject(converter = BeanConverter.class)
public class ElevateItemXml extends RuleItemXml{
	
	private static final long serialVersionUID = 1L;
	private int location;
	private boolean forceAdd;
	
	public ElevateItemXml(){
		super();
	}
	
	public ElevateItemXml(ElevateResult er, Product p){
		super();
		this.setMemberId(er.getMemberId());
		this.setMemberType(er.getMemberType());
		if(er.getMemberType() == MemberTypeEntity.FACET && er.getCondition()!=null){
			this.setRuleCondition(er.getCondition());
		}else{
			this.setEdp(er.getEdp()) ;
			if(p!=null){
				this.setImagePath(p.getImagePath());
				this.setDpNo(p.getDpNo());
				this.setMfrNo(p.getMfrPN());
				this.setManufacturer(p.getManufacturer());
			}
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