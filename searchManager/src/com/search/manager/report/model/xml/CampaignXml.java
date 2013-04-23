package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.Keyword;

@XmlRootElement(name = "campaign")
@DataTransferObject(converter = BeanConverter.class)
public class CampaignXml extends RuleXml {

	private static final long serialVersionUID = 3228546600677044795L;
	
	private DateTime startDateTime;
	private DateTime endDateTime;
	private String description;
	private List<Banner> bannerList;
	
	public CampaignXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.CAMPAIGN);
	}
	
	public CampaignXml(String store, long version, String name, String notes, String username, RuleType ruleType, 
			String ruleId, String ruleName, String description, String linkPath, String imagePath, String thumbnailPath, String imageAlt, List<Keyword> keywordList) {
		super(store, name == null ? ruleName : name, notes, username);
		this.setRuleId(ruleId);
		this.setRuleName(ruleName);
		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(DateTime.now());
		this.description = description;
	}
	
	public CampaignXml(String store, long version, String name, String notes, String username, Campaign campaign) {
		super(store, name == null ? campaign.getRuleName() : name, notes, username);

		if(campaign!=null){
			this.setRuleId(campaign.getRuleId());
			this.setRuleName(campaign.getRuleName());
			this.setStartDateTime(campaign.getStartDateTime());
			this.setEndDateTime(campaign.getEndDateTime());
			this.setDescription(campaign.getDescription());
			
			this.setCreatedBy(campaign.getCreatedBy());
			this.setCreatedDate(campaign.getCreatedDate());
			this.setLastModifiedBy(campaign.getLastModifiedBy());
			this.setLastModifiedDate(campaign.getLastModifiedDate());
		}

		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(DateTime.now());
	}

	public CampaignXml(Campaign campaign){
		this.setStore(campaign.getStore() != null ? campaign.getStore().getStoreId() : "");
		this.setRuleName(campaign.getRuleName());
		this.setRuleId(campaign.getRuleId());
		this.setStartDateTime(campaign.getStartDateTime());
		this.setEndDateTime(campaign.getEndDateTime());
		this.setDescription(campaign.getDescription());
		
		//TODO bannerList
		/*this.ruleType = banner.getRuleType();
		
		Map<String, List<String>> groups = facetSort.getItems();
		Map<String, SortType> groupSorts = facetSort.getGroupSortType();
		List<FacetSortGroupXml> facetSortGroupXmlList = new ArrayList<FacetSortGroupXml>();
		
		if (CollectionUtils.isNotEmpty(facetGroups)) {
			for(FacetGroup facetGroup: facetGroups){
				String mapKey = facetGroup.getName();
				facetSortGroupXmlList.add(new FacetSortGroupXml(mapKey, groups.get(mapKey), groupSorts.get(mapKey), 
						facetSort.getSortType(), facetGroup.getCreatedBy(), facetGroup.getCreatedDateTime()));
			}
		}
		
		this.groups = facetSortGroupXmlList;*/
		
		this.setCreatedBy(campaign.getCreatedBy());
		this.setCreatedDate(campaign.getCreatedDate());
		this.setLastModifiedBy(campaign.getLastModifiedBy());
		this.setLastModifiedDate(campaign.getLastModifiedDate());
	}

	@XmlElementRef
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(DateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public DateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(DateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public List<Banner> getBannerList() {
		return bannerList;
	}

	public void setBannerList(List<Banner> bannerList) {
		this.bannerList = bannerList;
	}
}