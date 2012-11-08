package com.search.manager.report.model.xml;

import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.model.Relevancy;

@XmlRootElement(name = "rankingrule")
@DataTransferObject(converter = BeanConverter.class)
public class RankingRuleXml extends RuleVersionXml {

	private static final long serialVersionUID = 1L;
	private String description;
	private Date startDate;
	private Date endDate;
	private RuleKeywordXml ruleKeyword;
	private Map<String, String> parameters;

	public RankingRuleXml() {
		super(serialVersionUID);
	}

	public RankingRuleXml(String store, long version, String name, String notes, String username,
			Relevancy rr) {
		super(store, name, notes, username);

		if(rr!=null){
			this.setRuleId(rr.getRuleId());
			this.setRuleName(rr.getRuleName());
			this.setStartDate(rr.getStartDate());
			this.setEndDate(rr.getEndDate());
			this.setDescription(rr.getDescription());
			this.setRuleKeyword(new RuleKeywordXml(rr.getKeywords()));
			this.setParameters(rr.getParameters());
		}

		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(new Date());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
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
}