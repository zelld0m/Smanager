package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.SortType;

@XmlRootElement(name="group")
@XmlType(propOrder={"groupName", "groupType", "sortType", "groupItem"})
@DataTransferObject(converter = BeanConverter.class)
public class FacetSortItemXml extends BaseEntityXml{

	private static final long serialVersionUID = 1L;

	private String groupName;
	private FacetGroupType groupType;
	private SortType sortType;
	private List<String> groupItem;

	public FacetSortItemXml(){
		super();
	}

	public FacetSortItemXml(String groupName, List<String> items, SortType sortType, SortType defaultSortType) {
		super();
		this.groupName = groupName;
		this.groupType = FacetGroupType.get(groupName);
		this.groupItem = items;
		this.sortType = sortType==null? defaultSortType: sortType;
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

	@XmlElement(name="item")
	public List<String> getGroupItem() {
		return groupItem;
	}

	public void setGroupItem(List<String> groupItem) {
		this.groupItem = groupItem;
	}
}