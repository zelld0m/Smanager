package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;

@XmlRootElement(name = "rankingrule")
@DataTransferObject(converter = BeanConverter.class)
public class RankingRuleXml extends RuleXml {

	private static final long serialVersionUID = 1L;
	private String description;
	private DateTime startDateTime;
	private DateTime endDateTime;
	private RuleKeywordXml ruleKeyword;
	private Map<String, String> parameters;

	public RankingRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.RANKING_RULE);
	}

	public RankingRuleXml(String store, long version, String name, String notes, String username, Relevancy rr) {
		super(store, name == null ? rr.getRuleName() : name, notes, username);

		if(rr!=null){
			this.setRuleId(rr.getRuleId());
			this.setRuleName(rr.getRuleName());
			this.setStartDateTime(rr.getStartDateTime());
			this.setEndDateTime(rr.getEndDateTime());
			this.setDescription(rr.getDescription());
			List<String> keywords = new ArrayList<String>();
			if (CollectionUtils.isNotEmpty(rr.getRelKeyword())) {
				for (RelevancyKeyword keyword: rr.getRelKeyword()) {
					keywords.add(DAOUtils.getKeywordId(keyword.getKeyword()));
				}
			}
			this.setRuleKeyword(new RuleKeywordXml(keywords));
			this.setParameters(rr.getParameters());
			this.setCreatedBy(rr.getCreatedBy());
			this.setCreatedDate(rr.getCreatedDate());
			this.setLastModifiedBy(rr.getLastModifiedBy());
			this.setLastModifiedDate(rr.getLastModifiedDate());
		}

		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(DateTime.now());
	}
	
	public RankingRuleXml(String store, Relevancy relevancy) {
		this(store, 0, "", "", "", relevancy);
	}

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

	@XmlElementRef(type=RuleKeywordXml.class)
	public RuleKeywordXml getRuleKeyword() {
		return ruleKeyword;
	}

	public void setRuleKeyword(RuleKeywordXml ruleKeyword) {
		this.ruleKeyword = ruleKeyword;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	//TODO: Use util or joda tlds	
//	@XmlTransient
//	public String getFormattedStartDate() {
//		if(getStore()==null) return StringUtils.EMPTY;
//		return DateAndTimeUtils.formatDateUsingConfig(getStore(), getStartDate());
//	}
//	
//	@XmlTransient
//	public String getFormattedEndDate() {
//		if(getStore()==null) return StringUtils.EMPTY;
//		return DateAndTimeUtils.formatDateUsingConfig(getStore(), getEndDate());
//	}
}