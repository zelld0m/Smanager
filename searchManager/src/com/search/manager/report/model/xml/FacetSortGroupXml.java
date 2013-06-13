package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.SortType;

@XmlRootElement(name="group")
@XmlType(propOrder={"groupId", "groupName", "groupType", "sortType", "groupItem"})
@DataTransferObject(converter = BeanConverter.class)
public class FacetSortGroupXml extends BaseEntityXml{

	private static final long serialVersionUID = 1L;

	private String groupName;
	private FacetGroupType groupType;
	private SortType sortType;
	private List<String> groupItem;
	private String groupId;

	public FacetSortGroupXml(){
		super();
	}

	public FacetSortGroupXml(String groupName, List<String> items, SortType sortType, 
			SortType defaultSortType, String createdBy, DateTime createdDateTime) {
		super();
		this.groupName = groupName;
		this.groupType = FacetGroupType.get(groupName);
		this.groupItem = items;
		this.sortType = sortType == null? defaultSortType: sortType;
		setCreatedBy(createdBy);
		setCreatedDate(createdDateTime);
	}

	public FacetSortGroupXml(String groupName, FacetGroupType groupType, SortType sortType, 
			List<String> groupItem, String groupId, String createdBy, DateTime createdDateTime) {
		super();
		this.groupName = groupName;
		this.groupType = groupType;
		this.sortType = sortType;
		this.groupItem = groupItem;
		this.groupId = groupId;
		setCreatedBy(createdBy);
		setCreatedDate(createdDateTime);
	}

	@XmlAttribute(name="name")
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@XmlAttribute(name="type")
	public FacetGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(FacetGroupType groupType) {
		this.groupType = groupType;
	}

	@XmlAttribute(name="sort-type")
	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}
	
	@XmlTransient
	public String getSortTypeLabel(){
		if(sortType != null){
			return sortType.getDisplayText();
		}
		return "";
	}
	
	@XmlAttribute(name="group-id")
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@XmlElement(name="item")
	public List<String> getGroupItem() {
		return groupItem;
	}

	public void setGroupItem(List<String> groupItem) {
		this.groupItem = groupItem;
	}
}