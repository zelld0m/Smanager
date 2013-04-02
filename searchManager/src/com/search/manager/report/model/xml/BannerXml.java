package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.model.Banner;
import com.search.manager.model.Keyword;

@XmlRootElement(name = "banner")
@DataTransferObject(converter = BeanConverter.class)
public class BannerXml extends RuleXml {
	
	private static final long serialVersionUID = 6206593542694232854L;

	private String description;
	private String linkPath;
	private String imagePath;
	private String thumbnailPath;
	private String imageAlt;
	private List<Keyword> keywordList;
	
	public BannerXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.BANNER);
	}
	
	public BannerXml(String store, long version, String name, String notes, String username, Banner banner) {
		super(store, name == null ? banner.getRuleName() : name, notes, username);

		if(banner!=null){
			this.setRuleId(banner.getRuleId());
			this.setRuleName(banner.getRuleName());
			this.setDescription(banner.getDescription());
			
			this.setImageAlt(banner.getImageAlt());
			this.setImagePath(banner.getImagePath());
			this.setLinkPath(banner.getLinkPath());
			this.setThumbnailPath(banner.getThumbnailPath());
			
			this.setCreatedBy(banner.getCreatedBy());
			this.setCreatedDateTime(banner.getCreatedDateTime());
			this.setLastModifiedBy(banner.getLastModifiedBy());
			this.setLastModifiedDateTime(banner.getLastModifiedDateTime());
		}

		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDateTime(DateTime.now());
	}
	
	public BannerXml(String store, long version, String name, String notes, String username, RuleType ruleType, 
			String ruleId, String ruleName, String description, String linkPath, String imagePath, String thumbnailPath, String imageAlt, List<Keyword> keywordList) {
		super(store, name == null ? ruleName : name, notes, username);
		this.setRuleId(ruleId);
		this.setRuleName(ruleName);
		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDateTime(DateTime.now());
		this.description = description;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.imageAlt = imageAlt;
		this.keywordList = keywordList;
	}

	public BannerXml(Banner banner){
		this.setStore(banner.getStore() != null ? banner.getStore().getStoreId() : "");
		this.setRuleName(banner.getRuleName());
		this.setRuleId(banner.getRuleId());
		
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
		
		this.setCreatedBy(banner.getCreatedBy());
		this.setCreatedDateTime(banner.getCreatedDateTime());
		this.setLastModifiedBy(banner.getLastModifiedBy());
		this.setLastModifiedDateTime(banner.getLastModifiedDateTime());
	}

	@XmlElementRef
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElementRef
	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	@XmlElementRef
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@XmlElementRef
	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	@XmlElementRef
	public String getImageAlt() {
		return imageAlt;
	}

	public void setImageAlt(String imageAlt) {
		this.imageAlt = imageAlt;
	}

	@XmlElementRef
	public List<Keyword> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<Keyword> keywordList) {
		this.keywordList = keywordList;
	}
	
	
}