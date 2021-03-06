package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections.CollectionUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.RuleEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;

@XmlRootElement(name = "rankingrule")
@DataTransferObject(converter = BeanConverter.class)
public class RankingRuleXml extends RuleXml {

	private static final long serialVersionUID = 1L;
	private String description;
	private DateTime startDate;
	private DateTime endDate;
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
			this.setStartDate(rr.getStartDate());
			this.setEndDate(rr.getEndDate());
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

	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
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
	
	@XmlTransient
	public String getFormattedStartDate() {
//		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(), JodaPatternType.DATE);
		return "";
	}
	
	@XmlTransient
	public String getFormattedEndDate() {
//		return JodaDateTimeUtil.formatFromStorePattern(getEndDate(), JodaPatternType.DATE);
		return "";
	}
}