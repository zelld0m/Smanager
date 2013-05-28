package com.search.manager.solr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTimeZone;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.SortType;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.solr.constants.Constants;

public class SolrDocUtil {
	private static final Logger logger = Logger.getLogger(SolrDocUtil.class);

	/* For Demote, Exclude and Elevate Rule */

	@SuppressWarnings("unchecked")
	public static List<SolrInputDocument> composeSolrDocs(List<?> results)
			throws Exception {
		if (results == null || results.size() < 1) {
			logger.error("'Results' is null or empty.");
			throw new Exception("'Results' is null or empty.");
		}

		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();

		if (results.get(0) instanceof DemoteResult) {
			logger.info(Constants.Rule.DEMOTE + " result = " + results.size());
			List<DemoteResult> results2 = (List<DemoteResult>) results;
			for (DemoteResult result : results2) {
				SolrInputDocument solrInputDocument = new SolrInputDocument();
				solrInputDocument.addField("location", result.getLocation());
				solrInputDocument.addField("store", result.getStoreKeyword()
						.getStoreId());
				solrInputDocument.addField("keyword", result.getStoreKeyword()
						.getKeywordId());

				if (result.getExpiryDateTime() != null) {
					solrInputDocument.addField("expiryDate", result
							.getExpiryDateTime().withZone(DateTimeZone.UTC));
				}

				solrInputDocument.addField("entity", result.getEntity());
				solrInputDocument.addField("memberId", result.getMemberId());
				solrInputDocument.addField("ruleType",
						Constants.Rule.DEMOTE.getRuleName());

				if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					solrInputDocument.addField("value", result.getEdp());
				} else {
					solrInputDocument.addField("value", result.getCondition()
							.getCondition());
				}

				// Value of 'id' is <store>_<keyword>_<rule>_<meberId>
				String id = result.getStoreKeyword().getStoreId() + "_"
						+ result.getStoreKeyword().getKeyword().getKeyword()
						+ "_" + Constants.Rule.DEMOTE.getRuleName() + "_"
						+ result.getMemberId();
				solrInputDocument.addField("id", id);

				solrInputDocuments.add(solrInputDocument);
			}
		} else if (results.get(0) instanceof ElevateResult) {
			logger.info(Constants.Rule.ELEVATE + " result = " + results.size());
			List<ElevateResult> results2 = (List<ElevateResult>) results;
			for (ElevateResult result : results2) {
				SolrInputDocument solrInputDocument = new SolrInputDocument();

				solrInputDocument.addField("location", result.getLocation());
				solrInputDocument.addField("forceAdd", result.getForceAdd());
				solrInputDocument.addField("store", result.getStoreKeyword()
						.getStoreId());
				solrInputDocument.addField("keyword", result.getStoreKeyword()
						.getKeywordId());
				if (result.getExpiryDateTime() != null) {
					solrInputDocument.addField("expiryDate", result
							.getExpiryDateTime().withZone(DateTimeZone.UTC));
				}
				solrInputDocument.addField("entity", result.getEntity());
				solrInputDocument.addField("memberId", result.getMemberId());
				solrInputDocument.addField("ruleType",
						Constants.Rule.ELEVATE.getRuleName());

				if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					solrInputDocument.addField("value", result.getEdp());
				} else {
					solrInputDocument.addField("value", result.getCondition()
							.getCondition());
				}

				// Value of 'id' is <store>_<keyword>_<rule>_<meberId>
				String id = result.getStoreKeyword().getStoreId() + "_"
						+ result.getStoreKeyword().getKeyword().getKeyword()
						+ "_" + Constants.Rule.ELEVATE.getRuleName() + "_"
						+ result.getMemberId();
				solrInputDocument.addField("id", id);

				solrInputDocuments.add(solrInputDocument);
			}
		} else if (results.get(0) instanceof ExcludeResult) {
			logger.info(Constants.Rule.EXCLUDE + " result = " + results.size());
			List<ExcludeResult> results2 = (List<ExcludeResult>) results;
			for (ExcludeResult result : results2) {
				SolrInputDocument solrInputDocument = new SolrInputDocument();
				solrInputDocument.addField("store", result.getStoreKeyword()
						.getStoreId());
				solrInputDocument.addField("keyword", result.getStoreKeyword()
						.getKeywordId());

				if (result.getExpiryDateTime() != null) {
					solrInputDocument.addField("expiryDate", result
							.getExpiryDateTime().withZone(DateTimeZone.UTC));
				}

				solrInputDocument.addField("entity", result.getEntity());
				solrInputDocument.addField("memberId", result.getMemberId());
				solrInputDocument.addField("ruleType",
						Constants.Rule.EXCLUDE.getRuleName());

				if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					solrInputDocument.addField("value", result.getEdp());
				} else {
					solrInputDocument.addField("value", result.getCondition()
							.getCondition());
				}

				// Value of 'id' is <store>_<keyword>_<rule>_<meberId>
				String id = result.getStoreKeyword().getStoreId() + "_"
						+ result.getStoreKeyword().getKeyword().getKeyword()
						+ "_" + Constants.Rule.EXCLUDE.getRuleName() + "_"
						+ result.getMemberId();
				solrInputDocument.addField("id", id);

				solrInputDocuments.add(solrInputDocument);
			}
		}

		return solrInputDocuments;
	}

	/* For Redirect Rule */

	public static List<SolrInputDocument> composeSolrDocsRedirectRule(
			List<RedirectRule> redirectRules) throws Exception {
		if (redirectRules == null || redirectRules.size() < 1) {
			logger.error("'RedirectRules' is null or empty.");
			throw new Exception("'RedirectRules' is null or empty.");
		}

		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();

		for (RedirectRule redirectRule : redirectRules) {
			SolrInputDocument solrInputDocument = composeSolrDoc(redirectRule);
			solrInputDocuments.add(solrInputDocument);
		}

		return solrInputDocuments;
	}

	public static SolrInputDocument composeSolrDoc(RedirectRule redirectRule)
			throws Exception {
		if (redirectRule == null) {
			logger.error("'RedirectRule' is null or empty.");
			throw new Exception("'RedirectRule' is null or empty.");
		}

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		solrInputDocument.addField("ruleId", redirectRule.getRuleId());
		solrInputDocument.addField("ruleName", redirectRule.getRuleName());
		solrInputDocument.addField("redirectType",
				redirectRule.getRedirectType());
		solrInputDocument.addField("storeId", redirectRule.getStoreId());
		solrInputDocument.addField("priority", redirectRule.getPriority());
		solrInputDocument.addField("searchTerm", redirectRule.getSearchTerm());
		for (String searchTerm : redirectRule.getSearchTerms()) {
			solrInputDocument.addField("searchTerms", searchTerm);
		}
		solrInputDocument.addField("condition", redirectRule.getCondition());
		solrInputDocument.addField("changeKeyword",
				redirectRule.getChangeKeyword());
		solrInputDocument.addField("includeKeyword",
				redirectRule.getIncludeKeyword());
		solrInputDocument
				.addField("redirectUrl", redirectRule.getRedirectUrl());
		solrInputDocument.addField("messageType", redirectRule
				.getReplaceKeywordMessageType() != null ? redirectRule
				.getReplaceKeywordMessageType().getIntValue() : 1);
		solrInputDocument.addField(
				"customText",
				StringUtils.defaultIfBlank(
						redirectRule.getReplaceKeywordMessageCustomText(), ""));

		// Value of 'id' is <storeId>_<searchTerm>_<ruleId>
		String id = redirectRule.getStoreId() + "_"
				+ redirectRule.getSearchTerm() + "_" + redirectRule.getRuleId();

		solrInputDocument.addField("id", id);

		return solrInputDocument;
	}

	/* For Relevancy Rule */

	public static List<SolrInputDocument> composeSolrDocsRelevancy(
			List<Relevancy> relevancies) throws Exception {
		if (relevancies == null) {
			logger.error("'Relevancy' is null or empty.");
			throw new Exception("'Relevancy' is null or empty.");
		}
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();

		for (Relevancy relevancy : relevancies) {
			solrInputDocuments.add(composeSolrDoc(relevancy));
		}

		return solrInputDocuments;
	}

	public static SolrInputDocument composeSolrDoc(Relevancy relevancy)
			throws Exception {
		if (relevancy == null) {
			logger.error("'Relevancy' is null or empty.");
			throw new Exception("'Relevancy' is null or empty.");
		}

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		solrInputDocument.addField("relevancyId", relevancy.getRelevancyId());
		solrInputDocument.addField("relevancyName",
				relevancy.getRelevancyName());
		solrInputDocument.addField("store", relevancy.getStore().getStoreId());
		solrInputDocument.addField("alternateQuery",
				relevancy.getAlternateQuery());
		solrInputDocument.addField("boostFunction",
				relevancy.getBoostFunction());
		solrInputDocument.addField("boostQuery", relevancy.getBoostQuery());
		solrInputDocument.addField("minimumToMatch",
				relevancy.getMinimumToMatch());
		solrInputDocument.addField("ruleId", relevancy.getRuleId());
		solrInputDocument.addField("tieBreaker", relevancy.getTieBreaker());
		solrInputDocument.addField("parameters", relevancy.getParameters());

		List<RelevancyKeyword> relevancyKeywords = relevancy.getRelKeyword();
		if (relevancyKeywords != null && relevancyKeywords.size() > 0) {
			for (RelevancyKeyword relevancyKeyword : relevancyKeywords) {
				solrInputDocument.addField("relKeyword", relevancyKeyword
						.getKeyword().getKeywordId());
			}
		}

		solrInputDocument.addField("phraseFields", relevancy.getPhraseFields());
		solrInputDocument.addField("phraseSlop", relevancy.getPhraseSlop());
		solrInputDocument.addField("queryFields", relevancy.getQueryFields());
		solrInputDocument.addField("querySlop", relevancy.getQuerySlop());

		// Value of 'id' is <storeId>_<relevancyName>_<relevancyId>
		String id = relevancy.getStore().getStoreId() + "_"
				+ relevancy.getRelevancyName() + "_"
				+ relevancy.getRelevancyId();

		solrInputDocument.addField("id", id);

		return solrInputDocument;
	}

	/* For Facet Sort Rule */

	public static List<SolrInputDocument> composeSolrDocsFacetSort(
			List<FacetSort> facetSorts) throws Exception {
		if (facetSorts == null) {
			logger.error("'FacetSorts' is null or empty.");
			throw new Exception("'FacetSorts' is null or empty.");
		}
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();

		for (FacetSort facetSort : facetSorts) {
			solrInputDocuments.add(composeSolrDoc(facetSort));
		}

		return solrInputDocuments;
	}

	public static SolrInputDocument composeSolrDoc(FacetSort facetSort)
			throws Exception {
		if (facetSort == null) {
			logger.error("'FacetSort' is null or empty.");
			throw new Exception("'FacetSort' is null or empty.");
		}

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		solrInputDocument.addField("facetSortId", facetSort.getId());
		solrInputDocument.addField("name", facetSort.getName());
		solrInputDocument.addField("readableString",
				facetSort.getReadableString());
		solrInputDocument.addField("ruleId", facetSort.getRuleId());
		solrInputDocument.addField("ruleName", facetSort.getName());
		solrInputDocument.addField("store", facetSort.getStoreId());

		Map<String, SortType> groupSortType = facetSort.getGroupSortType();
		for (Object key : groupSortType.keySet()) {
			solrInputDocument.addField(key.toString() + "_groupSortType",
					groupSortType.get(key));
		}

		Map<String, List<String>> items = facetSort.getItems();
		for (Object key : items.keySet()) {
			solrInputDocument.addField(key.toString() + "_items",
					items.get(key));
		}

		solrInputDocument.addField("ruleType", facetSort.getRuleType());
		solrInputDocument.addField("sortType", facetSort.getSortType());

		// Value of 'id' is <storeId>_<facetSortName>_<facetSortId>
		String id = facetSort.getStore().getStoreId() + "_"
				+ facetSort.getName() + "_" + facetSort.getId();

		solrInputDocument.addField("id", id);

		return solrInputDocument;
	}

	/* For Spell Rule */

	public static List<SolrInputDocument> composeSolrDocsSpell(
			List<SpellRuleXml> spellRulesXml, String storeId) throws Exception {
		if (spellRulesXml == null) {
			logger.error("'SpellRules' is null or empty.");
			throw new Exception("'SpellRules' is null or empty.");
		}
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();

		for (SpellRuleXml spellRuleXml : spellRulesXml) {
			solrInputDocuments.add(composeSolrDoc(spellRuleXml, storeId));
		}

		return solrInputDocuments;
	}

	public static SolrInputDocument composeSolrDoc(SpellRuleXml spellRuleXml,
			String storeId) throws Exception {
		if (spellRuleXml == null) {
			throw new Exception("'SpellRuleXml' is null or empty.");
		}

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		solrInputDocument.addField("ruleId", spellRuleXml.getRuleId());
		solrInputDocument.addField("store", storeId);
		solrInputDocument.addField("keywords", spellRuleXml.getRuleKeyword());
		solrInputDocument
				.addField("suggests", spellRuleXml.getSuggestKeyword());

		// Value of 'id' is <storeId>_<ruleId>
		String id = storeId + "_" + spellRuleXml.getRuleId();

		solrInputDocument.addField("id", id);

		return solrInputDocument;
	}

	/* For Banner Rule */
	public static List<SolrInputDocument> composeSolrDocsBannerRuleItem(
			List<BannerRuleItem> bannerRuleItems) throws Exception {
		if (bannerRuleItems == null) {
			throw new Exception("'BannerRuleItems' is null or empty.");
		}

		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();

		for (BannerRuleItem bannerRuleItem : bannerRuleItems) {
			solrInputDocuments.add(composeSolrDoc(bannerRuleItem));
		}

		return solrInputDocuments;
	}

	public static SolrInputDocument composeSolrDoc(BannerRuleItem bannerRuleItem)
			throws Exception {
		if (bannerRuleItem == null) {
			throw new Exception("'BannerRuleItem' is null or empty");
		}

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		if (bannerRuleItem.getRule() != null) {
			solrInputDocument.setField("store", bannerRuleItem.getRule()
					.getStoreId());
			solrInputDocument.setField("ruleId", bannerRuleItem.getRule()
					.getRuleId());
			solrInputDocument.setField("ruleName", bannerRuleItem.getRule()
					.getRuleName());
		}

		solrInputDocument.setField("memberId", bannerRuleItem.getMemberId());
		solrInputDocument.setField("priority", bannerRuleItem.getPriority());
		if (bannerRuleItem.getStartDate() != null) {
			solrInputDocument.setField("startDate", bannerRuleItem
					.getStartDate().withZone(DateTimeZone.UTC));
		}
		if (bannerRuleItem.getEndDate() != null) {
			solrInputDocument.setField("endDate", bannerRuleItem.getEndDate()
					.withZone(DateTimeZone.UTC));
		}
		solrInputDocument.setField("imageAlt", bannerRuleItem.getImageAlt());
		solrInputDocument.setField("linkPath", bannerRuleItem.getLinkPath());
		solrInputDocument.setField("openNewWindow",
				bannerRuleItem.getOpenNewWindow());
		solrInputDocument.setField("description",
				bannerRuleItem.getDescription());
		solrInputDocument.setField("disabled", bannerRuleItem.getDisabled());

		if (bannerRuleItem.getImagePath() != null) {
			solrInputDocument.setField("imagePathId", bannerRuleItem
					.getImagePath().getId());
			solrInputDocument.setField("path", bannerRuleItem.getImagePath()
					.getPath());
			solrInputDocument.setField("pathType", bannerRuleItem
					.getImagePath().getPathType());
			solrInputDocument.setField("alias", bannerRuleItem.getImagePath()
					.getAlias());
		}

		// store + "_" + ruleId + "_" + ruleName + "_" + memberId
		String id = bannerRuleItem.getRule().getStoreId() + "_"
				+ bannerRuleItem.getRule().getRuleId() + "_"
				+ bannerRuleItem.getRule().getRuleName()
				+ bannerRuleItem.getMemberId();

		solrInputDocument.setField("id", id);

		return solrInputDocument;
	}
}
