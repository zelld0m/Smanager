package com.search.manager.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.convert.EnumConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.ReplaceKeywordMessageType;
import com.search.manager.report.model.xml.RedirectRuleXml;

@DataTransferObject(converter = BeanConverter.class)
public class RedirectRule extends ModelBean {

	private static final long serialVersionUID = 4608433178597830828L;

	private static final String ESCAPED_DBL_PIPE_DELIM = "\\|\\|";
	private static final String COMMA = ",";

	@DataTransferObject(converter = EnumConverter.class)
	public enum RedirectType {
		FILTER,
		CHANGE_KEYWORD,
		DIRECT_HIT;

		public String getStringValue() {
			switch (this) {
				case FILTER:
					return "1";
				case CHANGE_KEYWORD:
					return "2";
				case DIRECT_HIT:
					return "3";
				default:
					return null;
			}
		}
		
		public static RedirectType getRedirectType(String string) {
			for (RedirectType rt: RedirectType.values()) {
				if (StringUtils.equals(rt.getStringValue(), string)) {
					return rt;
				}
			}
			return null;
		}
		
		@Override
		public String toString() {
			switch (this) {
			case FILTER:
				return "Filter";
			case CHANGE_KEYWORD:
				return "Change Keyword";
			case DIRECT_HIT:
				return "Direct Hit";
			default:
				return "Unknown";
		}
	}
		
		
	}
	
	private String ruleId;
	private String ruleName;
	private String description;
	private RedirectType redirectType;
	private String storeId;
	private Integer priority;
	private String searchTerm;
	private String condition;
	private String changeKeyword;
	private Boolean includeKeyword;
	private String redirectUrl;
	private String modifiedBy;
	private ReplaceKeywordMessageType replaceKeywordMessageType;
	private String replaceKeywordMessageCustomText;
	
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public RedirectRule() {
		super();
	}
	
	public RedirectRule(String ruleId) {
		this.ruleId = ruleId;
	}

	public RedirectRule(String ruleId, String storeId, String ruleName, String searchTerm, String condition) {
		super();
		this.ruleId = ruleId;
		this.storeId = storeId;
		this.ruleName = ruleName;
		this.searchTerm = searchTerm;
		this.condition = condition;
	}
	
	public RedirectRule(String storeId, String searchTerm) {
		super();
		this.storeId = storeId;
		this.searchTerm = searchTerm;
	}
	
	public RedirectRule(String ruleId, RedirectType redirectType, String ruleName, String description, String storeId,
			Integer priority, String searchTerm, String condition, String createdBy, String modifiedBy, 
			DateTime createdDateTime, DateTime lastModifiedDateTime, String changeKeyword, String redirectUrl, Boolean includeKeyword,
			ReplaceKeywordMessageType replaceKeywordMessageType, String replaceKeywordMessageCustomText) {
		super();
		this.ruleId = ruleId;
		this.redirectType = redirectType;
		this.ruleName = ruleName;
		this.storeId = storeId;
		this.description = description;
		this.priority = priority;
		this.searchTerm = searchTerm;
		this.condition = condition;
		this.createdBy = createdBy;
		this.lastModifiedBy = modifiedBy;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = lastModifiedDateTime;
		this.changeKeyword = changeKeyword;
		this.redirectUrl = redirectUrl;
		this.includeKeyword = includeKeyword;
		this.replaceKeywordMessageType = replaceKeywordMessageType;
		this.replaceKeywordMessageCustomText = replaceKeywordMessageCustomText;
	}
	
	public RedirectRule(String ruleId, String redirectTypeId, String ruleName, String description, String storeId,
			Integer priority, String searchTerm, String condition, String createdBy, String modifiedBy, 
			DateTime createdDateTime, DateTime lastModifiedDateTime, String changeKeyword, String redirectUrl, Boolean includeKeyword,
			ReplaceKeywordMessageType replaceKeywordMessageType, String replaceKeywordMessageCustomText) {
		this(ruleId, RedirectType.getRedirectType(redirectTypeId), ruleName, description, storeId, priority, searchTerm, condition, createdBy, modifiedBy, createdDateTime, lastModifiedDateTime, changeKeyword, redirectUrl, includeKeyword,
				replaceKeywordMessageType, replaceKeywordMessageCustomText);
	}
	
	public RedirectRule(RedirectRuleXml xml) {
		this.ruleId = xml.getRuleId();
		this.ruleName = xml.getRuleName();
		this.description = xml.getDescription();
		this.redirectType = xml.getRedirectType();
		this.storeId = xml.getStore();

		if(xml.getRuleKeyword()!=null){
			if(xml.getRuleKeyword().getKeyword() != null)
			this.searchTerm = StringUtils.join(xml.getRuleKeyword().getKeyword().toArray(), ","); ;
		}
		
		if(xml.getRuleCondition()!=null){
			if(xml.getRuleCondition().getCondition() != null){
				this.condition = StringUtils.join(xml.getRuleCondition().getCondition().toArray(), ",");
			}
		}
		
		this.changeKeyword = xml.getReplacementKeyword();
		this.replaceKeywordMessageType = xml.getReplaceKeywordMessageType();
		this.replaceKeywordMessageCustomText = xml.getReplaceKeywordMessageCustomText();
		
		if(xml.getRuleCondition()!=null)
			this.includeKeyword = xml.getRuleCondition().isIncludeKeyword();
		this.redirectUrl = xml.getDirectHit();
		this.createdBy = xml.getCreatedBy();
		this.createdDate = xml.getCreatedDate();
		this.lastModifiedBy = xml.getLastModifiedBy();
		this.lastModifiedDate = xml.getLastModifiedDate();
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getRedirectTypeId() {
		return redirectType == null ? null : redirectType.getStringValue();
	}
	
	public void setRedirectTypeId(String redirectTypeId) {
		this.redirectType = RedirectType.getRedirectType(redirectTypeId);
	}
	
	public boolean isRedirectToPage() {
		return redirectType == RedirectType.DIRECT_HIT;
	}
	
	public boolean isRedirectFilter() {
		return redirectType == RedirectType.FILTER;
	}
	
	public boolean isRedirectChangeKeyword() {
		return redirectType == RedirectType.CHANGE_KEYWORD;
	}
	
	public String getRedirectToPage() {
		return StringUtils.trimToEmpty(condition);
	}
	
	public List<String> getSearchTerms() {
		ArrayList<String> terms = new ArrayList<String>();
		if (StringUtils.isNotEmpty(searchTerm)) {
			CollectionUtils.addAll(terms, searchTerm.split(COMMA));
		}
		return terms;
	}
	
	public List<String> getConditions() {
		ArrayList<String> conditions = new ArrayList<String>();
		if (StringUtils.isNotEmpty(condition)) {
			CollectionUtils.addAll(conditions, condition.split(ESCAPED_DBL_PIPE_DELIM));
		}
		return conditions;		
	}
	
	public List<String> getReadableConditions(){
		ArrayList<String> conditions = new ArrayList<String>();
		if(StringUtils.isNotEmpty(condition)){
			for(String cond : condition.split(ESCAPED_DBL_PIPE_DELIM)){
				conditions.add((new RedirectRuleCondition(ruleId, cond)).getReadableString());
			}
		}
				
		return conditions;
	}

	public void setChangeKeyword(String changeKeyword) {
		this.changeKeyword = changeKeyword;
	}

	public String getChangeKeyword() {
		return changeKeyword;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setRedirectType(RedirectType redirectType) {
		this.redirectType = redirectType;
	}

	public RedirectType getRedirectType() {
		return redirectType;
	}

	public void setIncludeKeyword(Boolean includeKeyword) {
		this.includeKeyword = includeKeyword;
	}

	public Boolean getIncludeKeyword() {
		return includeKeyword;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public ReplaceKeywordMessageType getReplaceKeywordMessageType() {
		return replaceKeywordMessageType;
	}

	public void setReplaceKeywordMessageType(ReplaceKeywordMessageType replaceKeywordMessageType) {
		this.replaceKeywordMessageType = replaceKeywordMessageType;
	}

	public String getReplaceKeywordMessageCustomText() {
		return replaceKeywordMessageCustomText;
	}

	public void setReplaceKeywordMessageCustomText(
			String replaceKeywordMessageCustomText) {
		this.replaceKeywordMessageCustomText = replaceKeywordMessageCustomText;
	}	
}