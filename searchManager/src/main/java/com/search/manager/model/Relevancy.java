package com.search.manager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import jxl.common.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.convert.EnumConverter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;
import com.search.manager.report.model.xml.RankingRuleXml;

@DataTransferObject(converter = BeanConverter.class)
public class Relevancy extends ModelBean {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(Relevancy.class);
	
	private String relevancyId;
	private String relevancyName;
	private String description;
	private Store store;
	private DateTime startDate;
	private String formattedStartDate;
	private String formattedEndDate;
	private DateTime endDate;
	private Map<String, String> fields = new HashMap<String, String>();
	private List<RelevancyKeyword> relKeyword;
	
	@DataTransferObject(converter = EnumConverter.class)
	public enum Parameter {
		
		PARAM_ALTERNATE_QUERY("q.alt"),
		PARAM_QUERY_FIELDS("qf"),
		PARAM_PHRASE_FIELDS("pf"),
		PARAM_BOOST_FUNCTION("bf"),
		PARAM_BOOST_QUERY("bq"),
		PARAM_PHRASE_SLOP("ps"),
		PARAM_QUERY_SLOP("qs"),
		PARAM_MIN_TO_MATCH("mm"),
		PARAM_TIE_BREAKER("tie"),
		PARAM_PHRASE_BIGRAM_FIELDS("pf2"),
		PARAM_PHRASE_BIGRAM_SLOP("ps2"),
		PARAM_PHRASE_TRIGRAM_FIELDS("pf3"),
		PARAM_PHRASE_TRIGRAM_SLOP("ps3");
		
		private String name;
		
		private Parameter(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public static Parameter getParameter(String name) {
			for (Parameter param: Parameter.values()) {
				if (name.equals(param.toString())) {
					return param;
				}
			}
			return null;
		}
	}
	
	public Relevancy() {
	}
	
	public Relevancy(String relevancyId) {
		this.relevancyId = relevancyId;
	}
	
	public Relevancy(String relevancyId, String relevancyName) {
		this.relevancyId = relevancyId;
		this.relevancyName = relevancyName;
	}
	
	public Relevancy(String relevancyId, String relevancyName, String description, Store store, DateTime startDate, DateTime endDate, String comment,
			String createdBy, String lastModifiedBy, DateTime createdDate, DateTime lastModifiedDate) {
		this.relevancyId = relevancyId;
		this.relevancyName = relevancyName;
		this.description = description;
		this.store = store;
		this.startDate = startDate;
		this.endDate = endDate;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Relevancy(RankingRuleXml xml) {
		this.relevancyId = xml.getRuleId();
		this.relevancyName = xml.getRuleName();
		this.description = xml.getDescription();
		this.store = new Store(xml.getStore());
		this.startDate = xml.getStartDate();
		this.endDate = xml.getEndDate();
//		comment = xml.getNotes();
		this.createdBy = xml.getCreatedBy();
		this.lastModifiedBy = xml.getLastModifiedBy();
		this.createdDate = xml.getCreatedDate();
		this.lastModifiedDate = xml.getLastModifiedDate();
		
		Map<String, String> parameter = xml.getParameters();
		if (parameter != null) {
			for(String key: parameter.keySet()) {
				setParameter(key, parameter.get(key));
			}
		}
		
		relKeyword = new ArrayList<RelevancyKeyword>();
		if (xml.getRuleKeyword() != null && CollectionUtils.isNotEmpty(xml.getRuleKeyword().getKeyword())) {
			for (String keyword: xml.getRuleKeyword().getKeyword()) {
				relKeyword.add(new RelevancyKeyword(new Keyword(keyword), this));
			}
		}
	}

	public String getRuleId() {
		return relevancyId;
	}
	
	public void setRuleId(String ruleId) {
		this.relevancyId = ruleId;
	}
	
	public String getRuleName() {
		return relevancyName;
	}
	
	public void setRuleName(String ruleName) {
		this.relevancyName = ruleName;
	}
	
	public void setRelevancyId(String relevancyId) {
		this.relevancyId = relevancyId;
	}
	
	public String getRelevancyId() {
		return relevancyId;
	}
	
	public void setRelevancyName(String relevancyName) {
		this.relevancyName = relevancyName;
	}
	
	public String getRelevancyName() {
		return relevancyName;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}
	
	public Store getStore() {
		return store;
	}
	
	public void setAlternateQuery(String value) {
		fields.put(Parameter.PARAM_ALTERNATE_QUERY.toString(), value);
	}
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public String getAlternateQuery() {
		return fields.get(Parameter.PARAM_ALTERNATE_QUERY.toString());
	}
	
	public void setQueryFields(String value) {
		fields.put(Parameter.PARAM_QUERY_FIELDS.toString(), value);
	}
	
	public String getQueryFields() {
		return fields.get(Parameter.PARAM_QUERY_FIELDS.toString());
	}

	public void setPhraseFields(String value) {
		fields.put(Parameter.PARAM_PHRASE_FIELDS.toString(), value);
	}
	
	public String getPhraseFields() {
		return fields.get(Parameter.PARAM_PHRASE_FIELDS.toString());
	}

	public void setBoostFunction(String value) {
		fields.put(Parameter.PARAM_BOOST_FUNCTION.toString(), value);
	}
	
	public String getBoostFunction() {
		return fields.get(Parameter.PARAM_BOOST_FUNCTION.toString());
	}

	public void setPhraseSlop(String value) {
		fields.put(Parameter.PARAM_PHRASE_SLOP.toString(), value);
	}
	
	public String getPhraseSlop() {
		return fields.get(Parameter.PARAM_PHRASE_SLOP.toString());
	}
	
	public void setQuerySlop(String value) {
		fields.put(Parameter.PARAM_QUERY_SLOP.toString(), value);
	}
	
	public String getQuerySlop() {
		return fields.get(Parameter.PARAM_QUERY_SLOP.toString());
	}
	
	public void setMinimumToMatch(String value) {
		fields.put(Parameter.PARAM_MIN_TO_MATCH.toString(), value);
	}
	
	public String getMinimumToMatch() {
		return fields.get(Parameter.PARAM_MIN_TO_MATCH.toString());
	}
	
	public void setTieBreaker(String value) {
		fields.put(Parameter.PARAM_TIE_BREAKER.toString(), value);
	}
	
	public String getTieBreaker() {
		return fields.get(Parameter.PARAM_TIE_BREAKER.toString());
	}
	
	public void setBoostQuery(String value) {
		fields.put(Parameter.PARAM_BOOST_QUERY.toString(), value);
	}
	
	public String getBoostQuery() {
		return fields.get(Parameter.PARAM_BOOST_QUERY.toString());
	}
	
	public void setPhraseBigramFields(String value) {
		fields.put(Parameter.PARAM_PHRASE_BIGRAM_FIELDS.toString(), value);
	}
	
	public String getPhraseBigramFields() {
		return fields.get(Parameter.PARAM_PHRASE_BIGRAM_FIELDS.toString());
	}
	
	public void setPhraseBigramSlop(String value) {
		fields.put(Parameter.PARAM_PHRASE_BIGRAM_SLOP.toString(), value);
	}
	
	public String getPhraseBigramSlop() {
		return fields.get(Parameter.PARAM_PHRASE_BIGRAM_SLOP.toString());
	}
	
	public void setPhraseTrigramFields(String value) {
		fields.put(Parameter.PARAM_PHRASE_TRIGRAM_FIELDS.toString(), value);
	}
	
	public String getPhraseTrigramFields() {
		return fields.get(Parameter.PARAM_PHRASE_TRIGRAM_FIELDS.toString());
	}
	
	public void setPhraseTrigramSlop(String value) {
		fields.put(Parameter.PARAM_PHRASE_TRIGRAM_SLOP.toString(), value);
	}
	
	public String getPhraseTrigramSlop() {
		return fields.get(Parameter.PARAM_PHRASE_TRIGRAM_SLOP.toString());
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setParameter(String parameterName, String value) {
		Parameter parameter = Parameter.getParameter(parameterName);
		if (parameter != null) {
			fields.put(parameter.toString(), value);
		}
		else {
			logger.warn("Parameter unrecognized: " + parameterName);
		}
	}
	
	public String getParameter(String parameterName) {
		Parameter parameter = Parameter.getParameter(parameterName);
		if (parameter != null) {
			return fields.get(parameter.toString());
		}
		else {
			logger.warn("Parameter unrecognized: " + parameterName);
		}
		return null;
	}

	
	/**
	 * Used for GUI, return all parameters. Unassigned parameters will map to an empty string.
	 * @return
	 */
	public Map<String,String> getParameters() {
		Map<String, String> map = new HashMap<String, String>();
		for (Parameter param: Parameter.values()) {
			String value = fields.get(param.toString());
			map.put(param.toString(), StringUtils.trimToEmpty(value));
		}
		return map;
	}

	public List<RelevancyKeyword> getRelKeyword() {
		return relKeyword;
	}

	public void setRelKeyword(List<RelevancyKeyword> relKeyword) {
		this.relKeyword = relKeyword;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public String getFormattedStartDate() {
		return formattedStartDate;
	}

	public void setFormattedStartDate(String formattedStartDate) {
		this.formattedStartDate = formattedStartDate;
	}

	public String getFormattedEndDate() {
		return formattedEndDate;
	}

	public void setFormattedEndDate(String formattedEndDate) {
		this.formattedEndDate = formattedEndDate;
	}
}