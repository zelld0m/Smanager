package com.search.manager.solr.model;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class RelevancyRuleSolr {

	@Field
	private String relevancyId;
	@Field
	private String relevancyName;
	@Field
	private String store;
	@Field
	private String alternateQuery;
	@Field
	private String boostFunction;
	@Field
	private String boostQuery;
	@Field
	private String minimumToMatch;
	@Field
	private String ruleId;
	@Field
	private String tieBreaker;
	@Field
	private String parameters;
	@Field
	private List<String> relKeyword;
	@Field
	private String phraseFields;
	@Field
	private String phraseSlop;
	@Field
	private String queryFields;
	@Field
	private String querySlop;
	@Field
	private String phraseBigramFields;
	@Field
	private String phraseBigramSlop;
	@Field
	private String phraseTrigramFields;
	@Field
	private String phraseTrigramSlop;

	public String getRelevancyId() {
		return relevancyId;
	}

	public void setRelevancyId(String relevancyId) {
		this.relevancyId = relevancyId;
	}

	public String getRelevancyName() {
		return relevancyName;
	}

	public void setRelevancyName(String relevancyName) {
		this.relevancyName = relevancyName;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getAlternateQuery() {
		return alternateQuery;
	}

	public void setAlternateQuery(String alternateQuery) {
		this.alternateQuery = alternateQuery;
	}

	public String getBoostFunction() {
		return boostFunction;
	}

	public void setBoostFunction(String boostFunction) {
		this.boostFunction = boostFunction;
	}

	public String getBoostQuery() {
		return boostQuery;
	}

	public void setBoostQuery(String boostQuery) {
		this.boostQuery = boostQuery;
	}

	public String getMinimumToMatch() {
		return minimumToMatch;
	}

	public void setMinimumToMatch(String minimumToMatch) {
		this.minimumToMatch = minimumToMatch;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getTieBreaker() {
		return tieBreaker;
	}

	public void setTieBreaker(String tieBreaker) {
		this.tieBreaker = tieBreaker;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public List<String> getRelKeyword() {
		return relKeyword;
	}

	public void setRelKeyword(List<String> relKeyword) {
		this.relKeyword = relKeyword;
	}

	public String getPhraseFields() {
		return phraseFields;
	}

	public void setPhraseFields(String phraseFields) {
		this.phraseFields = phraseFields;
	}

	public String getPhraseSlop() {
		return phraseSlop;
	}

	public void setPhraseSlop(String phraseSlop) {
		this.phraseSlop = phraseSlop;
	}

	public String getQueryFields() {
		return queryFields;
	}

	public void setQueryFields(String queryFields) {
		this.queryFields = queryFields;
	}

	public String getQuerySlop() {
		return querySlop;
	}

	public void setQuerySlop(String querySlop) {
		this.querySlop = querySlop;
	}

	public String getPhraseBigramFields() {
		return phraseBigramFields;
	}

	public void setPhraseBigramFields(String phraseBigramFields) {
		this.phraseBigramFields = phraseBigramFields;
	}

	public String getPhraseBigramSlop() {
		return phraseBigramSlop;
	}

	public void setPhraseBigramSlop(String phraseBigramSlop) {
		this.phraseBigramSlop = phraseBigramSlop;
	}

	public String getPhraseTrigramFields() {
		return phraseTrigramFields;
	}

	public void setPhraseTrigramFields(String phraseTrigramFields) {
		this.phraseTrigramFields = phraseTrigramFields;
	}

	public String getPhraseTrigramSlop() {
		return phraseTrigramSlop;
	}

	public void setPhraseTrigramSlop(String phraseTrigramSlop) {
		this.phraseTrigramSlop = phraseTrigramSlop;
	}

}
