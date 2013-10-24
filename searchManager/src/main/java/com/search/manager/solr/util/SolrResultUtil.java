package com.search.manager.solr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;

import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.ReplaceKeywordMessageType;
import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.ImagePath;
import com.search.manager.model.ImagePathType;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRule.RedirectType;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.model.BannerRuleItemSolr;
import com.search.manager.solr.model.FacetSortRuleSolr;
import com.search.manager.solr.model.RedirectRuleSolr;
import com.search.manager.solr.model.RelevancyRuleSolr;
import com.search.manager.solr.model.RuleSolrResult;
import com.search.manager.solr.model.SpellRuleSolr;
import com.search.ws.ConfigManager;
import com.search.ws.ConfigManager.PropertyFileType;

public class SolrResultUtil {

	public static List<DemoteResult> toDemoteResult(
			List<RuleSolrResult> ruleSolrResults) {
		List<DemoteResult> demoteResults = new ArrayList<DemoteResult>();

		for (RuleSolrResult ruleSolrResult : ruleSolrResults) {
			DemoteResult demoteResult = new DemoteResult();

			demoteResult.setLocation(ruleSolrResult.getLocation());
			StoreKeyword storeKeyword = new StoreKeyword(
					ruleSolrResult.getStore(), ruleSolrResult.getKeyword());
			demoteResult.setStoreKeyword(storeKeyword);
			demoteResult.setMemberId(ruleSolrResult.getMemberId());
			demoteResult.setExpiryDate(JodaDateTimeUtil.toDateTime(
					ruleSolrResult.getExpiryDateTime(), DateTimeZone.UTC));

			if (ruleSolrResult.getEntity().equals(
					MemberTypeEntity.PART_NUMBER.toString())) {
				demoteResult.setEntity(MemberTypeEntity.PART_NUMBER);
				demoteResult.setEdp(ruleSolrResult.getValue());
			} else if (ruleSolrResult.getEntity().equals(
					MemberTypeEntity.FACET.toString())) {
				demoteResult.setEntity(MemberTypeEntity.FACET);
				demoteResult.setCondition(new RedirectRuleCondition(
						ruleSolrResult.getValue()));
			}

			demoteResults.add(demoteResult);
		}

		return demoteResults;
	}

	public static List<ElevateResult> toElevateResult(
			List<RuleSolrResult> ruleSolrResults) {
		List<ElevateResult> elevateResults = new ArrayList<ElevateResult>();

		for (RuleSolrResult ruleSolrResult : ruleSolrResults) {
			ElevateResult elevateResult = new ElevateResult();

			elevateResult.setLocation(ruleSolrResult.getLocation());
			StoreKeyword storeKeyword = new StoreKeyword(
					ruleSolrResult.getStore(), ruleSolrResult.getKeyword());
			elevateResult.setStoreKeyword(storeKeyword);
			elevateResult.setMemberId(ruleSolrResult.getMemberId());
			elevateResult.setExpiryDate(JodaDateTimeUtil.toDateTime(
					ruleSolrResult.getExpiryDateTime(), DateTimeZone.UTC));
			elevateResult.setForceAdd(ruleSolrResult.getForceAdd());

			if (ruleSolrResult.getEntity().equals(
					MemberTypeEntity.PART_NUMBER.toString())) {
				elevateResult.setEntity(MemberTypeEntity.PART_NUMBER);
				elevateResult.setEdp(ruleSolrResult.getValue());
			} else if (ruleSolrResult.getEntity().equals(
					MemberTypeEntity.FACET.toString())) {
				elevateResult.setEntity(MemberTypeEntity.FACET);
				elevateResult.setCondition(new RedirectRuleCondition(
						ruleSolrResult.getValue()));
			}

			elevateResults.add(elevateResult);
		}

		return elevateResults;
	}

	public static List<ExcludeResult> toExcludeResult(
			List<RuleSolrResult> ruleSolrResults) {
		List<ExcludeResult> excludeResults = new ArrayList<ExcludeResult>();

		for (RuleSolrResult ruleSolrResult : ruleSolrResults) {
			ExcludeResult excludeResult = new ExcludeResult();

			StoreKeyword storeKeyword = new StoreKeyword(
					ruleSolrResult.getStore(), ruleSolrResult.getKeyword());
			excludeResult.setStoreKeyword(storeKeyword);
			excludeResult.setMemberId(ruleSolrResult.getMemberId());
			excludeResult.setExpiryDate(JodaDateTimeUtil.toDateTime(
					ruleSolrResult.getExpiryDateTime(), DateTimeZone.UTC));

			if (ruleSolrResult.getEntity().equals(
					MemberTypeEntity.PART_NUMBER.toString())) {
				excludeResult.setEntity(MemberTypeEntity.PART_NUMBER);
				excludeResult.setEdp(ruleSolrResult.getValue());
			} else if (ruleSolrResult.getEntity().equals(
					MemberTypeEntity.FACET.toString())) {
				excludeResult.setEntity(MemberTypeEntity.FACET);
				excludeResult.setCondition(new RedirectRuleCondition(
						ruleSolrResult.getValue()));
			}

			excludeResults.add(excludeResult);
		}

		return excludeResults;
	}

	public static List<RedirectRule> toRedirectRule(
			List<RedirectRuleSolr> redirectRulesSolr) {
		List<RedirectRule> redirectRules = new ArrayList<RedirectRule>();

		for (RedirectRuleSolr redirectRuleSolr : redirectRulesSolr) {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setRuleId(redirectRuleSolr.getRuleId());
			redirectRule.setRuleName(redirectRuleSolr.getRuleName());

			if (redirectRuleSolr.getRedirectType().equals(
					RedirectType.FILTER.toString())) {
				redirectRule.setRedirectType(RedirectType.FILTER);
			} else if (redirectRuleSolr.getRedirectType().equals(
					RedirectType.DIRECT_HIT.toString())) {
				redirectRule.setRedirectType(RedirectType.DIRECT_HIT);
			} else if (redirectRuleSolr.getRedirectType().equals(
					RedirectType.CHANGE_KEYWORD.toString())) {
				redirectRule.setRedirectType(RedirectType.CHANGE_KEYWORD);
			}

			redirectRule.setStoreId(redirectRuleSolr.getStoreId());
			redirectRule.setPriority(redirectRuleSolr.getPriority());
			redirectRule.setSearchTerm(redirectRuleSolr.getSearchTerm());
			// redirectRule.setSearchTerms(redirectRuleSolr.getSearchTerms());
			redirectRule.setCondition(redirectRuleSolr.getCondition());
			redirectRule.setChangeKeyword(redirectRuleSolr.getChangeKeyword());
			redirectRule
					.setIncludeKeyword(redirectRuleSolr.getIncludeKeyword());
			redirectRule.setRedirectUrl(redirectRuleSolr.getRedirectUrl());
			redirectRule.setReplaceKeywordMessageCustomText(redirectRuleSolr
					.getCustomText());

			if (redirectRuleSolr.getMessageType() != null) {
				redirectRule
						.setReplaceKeywordMessageType(ReplaceKeywordMessageType
								.get(redirectRuleSolr.getMessageType()));
			}

			redirectRules.add(redirectRule);
		}

		return redirectRules;
	}

	public static List<Relevancy> toRelevancyRule(
			List<RelevancyRuleSolr> relevancyRulesSolr) {
		List<Relevancy> relevancies = new ArrayList<Relevancy>();

		for (RelevancyRuleSolr relevancyRuleSolr : relevancyRulesSolr) {
			Relevancy relevancy = new Relevancy();
			relevancy.setRelevancyId(relevancyRuleSolr.getRelevancyId());
			relevancy.setRelevancyName(relevancyRuleSolr.getRelevancyName());
			relevancy.setStore(new Store(relevancyRuleSolr.getStore()));
			relevancy.setAlternateQuery(relevancyRuleSolr.getAlternateQuery());
			relevancy.setBoostFunction(relevancyRuleSolr.getBoostFunction());
			relevancy.setBoostQuery(relevancyRuleSolr.getBoostQuery());
			relevancy.setMinimumToMatch(relevancyRuleSolr.getMinimumToMatch());
			relevancy.setRuleId(relevancyRuleSolr.getRuleId());
			relevancy.setTieBreaker(relevancyRuleSolr.getTieBreaker());
			// relevancy.setParameter(parameterName, value);
			List<String> relKeywords = relevancyRuleSolr.getRelKeyword();
			List<RelevancyKeyword> relevancyKeywords = new ArrayList<RelevancyKeyword>();
			if (relKeywords != null) {
				for (String keyword : relKeywords) {
					RelevancyKeyword relevancyKeyword = new RelevancyKeyword();
					relevancyKeyword.setKeyword(new Keyword(keyword));
					// TODO relevancyKeyword.setRelevancy(relevancy);
					// TODO relevancyKeyword.setPriority(priority);
					relevancyKeywords.add(relevancyKeyword);
				}
				relevancy.setRelKeyword(relevancyKeywords);
			}
			relevancy.setPhraseFields(relevancyRuleSolr.getPhraseFields());
			relevancy.setPhraseSlop(relevancyRuleSolr.getPhraseSlop());
			relevancy.setQueryFields(relevancyRuleSolr.getQueryFields());
			relevancy.setQuerySlop(relevancyRuleSolr.getQuerySlop());
			relevancy.setPhraseBigramFields(relevancyRuleSolr
					.getPhraseBigramFields());
			relevancy.setPhraseBigramSlop(relevancyRuleSolr
					.getPhraseBigramSlop());
			relevancy.setPhraseTrigramFields(relevancyRuleSolr
					.getPhraseTrigramFields());
			relevancy.setPhraseTrigramSlop(relevancyRuleSolr
					.getPhraseTrigramSlop());

			relevancies.add(relevancy);
		}

		return relevancies;
	}

	public static List<FacetSort> toFacetSortRule(
			List<FacetSortRuleSolr> facetSortRulesSolr) {
		List<FacetSort> facetSorts = new ArrayList<FacetSort>();

		for (FacetSortRuleSolr facetSortRuleSolr : facetSortRulesSolr) {
			FacetSort facetSort = new FacetSort();

			facetSort.setId(facetSortRuleSolr.getId());
			facetSort.setName(facetSortRuleSolr.getId());
			facetSort.setRuleId(facetSortRuleSolr.getRuleId());
			facetSort.setRuleName(facetSortRuleSolr.getRuleName());
			facetSort.setRuleType(RuleType.get(Integer
					.parseInt(facetSortRuleSolr.getRuleType())));
			facetSort.setSortType(SortType.get(Integer
					.parseInt(facetSortRuleSolr.getSortType())));
			facetSort.setStore(new Store(facetSortRuleSolr.getStore()));
			// setItems(items);
			Map<String, List<String>> itemsSolr = facetSortRuleSolr.getItems();
			Map<String, List<String>> temp = new HashMap<String, List<String>>();
			if (itemsSolr != null) {
				for (Map.Entry<String, List<String>> entry : itemsSolr
						.entrySet()) {
					temp.put(entry.getKey().replace("_items", ""),
							entry.getValue());
				}
			}
			facetSort.setItems(temp);
			// setGroupSortType(groupSortType);
			Map<String, Integer> groupSortTypeSolr = facetSortRuleSolr
					.getGroupSortType();
			Map<String, SortType> groupSortType = new HashMap<String, SortType>();
			if (groupSortTypeSolr != null) {
				for (Map.Entry<String, Integer> entry : groupSortTypeSolr
						.entrySet()) {
					groupSortType.put(
							entry.getKey().replace("_groupSortType", ""),
							SortType.get(entry.getValue()));
				}
			}
			facetSort.setGroupSortType(groupSortType);

			facetSorts.add(facetSort);
		}

		return facetSorts;
	}

	public static List<SpellRule> toSpellRule(List<SpellRuleSolr> spellRulesSolr) {
		List<SpellRule> spellRules = new ArrayList<SpellRule>();

		for (SpellRuleSolr spellRuleSolr : spellRulesSolr) {
			SpellRule spellRule = new SpellRule();
			spellRule.setRuleId(spellRuleSolr.getRuleId());
			spellRule.setStoreId(spellRuleSolr.getStore());
			spellRule.setSearchTerms(spellRuleSolr.getKeywords());
			spellRule.setSuggestions(spellRuleSolr.getSuggests());
			spellRules.add(spellRule);
		}

		return spellRules;
	}

	public static List<BannerRuleItem> toBannerRuleItem(
			List<BannerRuleItemSolr> bannerRuleItemsSolr) {
		List<BannerRuleItem> bannerRuleItems = new ArrayList<BannerRuleItem>();
		ConfigManager cm = ConfigManager.getInstance();

		for (BannerRuleItemSolr bannerRuleItemSolr : bannerRuleItemsSolr) {
			String autoPrefixProtocol = cm.getProperty(PropertyFileType.SETTINGS,
					bannerRuleItemSolr.getStore(),
					DAOConstants.SETTINGS_AUTOPREFIX_BANNER_LINKPATH_PROTOCOL);
			String protocol = StringUtils.defaultIfBlank(cm.getProperty(PropertyFileType.SETTINGS,
					bannerRuleItemSolr.getStore(),
					DAOConstants.SETTINGS_DEFAULT_BANNER_LINKPATH_PROTOCOL),
					"http");
			Boolean isAutoPrefixProtocol = BooleanUtils.toBoolean(StringUtils
					.defaultIfBlank(autoPrefixProtocol, "false"));

			BannerRuleItem bannerRuleItem = new BannerRuleItem();
			BannerRule bannerRule = new BannerRule();
			ImagePath imagePath = new ImagePath();

			bannerRule.setStoreId(bannerRuleItemSolr.getStore());
			bannerRule.setRuleId(bannerRuleItemSolr.getRuleId());
			bannerRule.setRuleName(bannerRuleItemSolr.getRuleName());
			bannerRuleItem.setRule(bannerRule);

			bannerRuleItem.setMemberId(bannerRuleItemSolr.getMemberId());
			bannerRuleItem.setPriority(bannerRuleItemSolr.getPriority());
			bannerRuleItem.setStartDate(JodaDateTimeUtil.toDateTime(
					bannerRuleItemSolr.getStartDate(), DateTimeZone.UTC));
			bannerRuleItem.setEndDate(JodaDateTimeUtil.toDateTime(
					bannerRuleItemSolr.getEndDate(), DateTimeZone.UTC));
			bannerRuleItem.setImageAlt(bannerRuleItemSolr.getImageAlt());
			bannerRuleItem.setLinkPath((isAutoPrefixProtocol ? protocol + ":"
					: "") + bannerRuleItemSolr.getLinkPath());
			bannerRuleItem.setOpenNewWindow(bannerRuleItemSolr
					.isOpenNewWindow());
			bannerRuleItem.setDescription(bannerRuleItemSolr.getDescription());
			bannerRuleItem.setDisabled(bannerRuleItemSolr.isDisabled());

			imagePath.setId(bannerRuleItemSolr.getImagePathId());
			imagePath.setPath(bannerRuleItemSolr.getPath());
			imagePath.setSize(bannerRuleItemSolr.getSize());
			if (bannerRuleItemSolr.getPathType() != null) {
				if (bannerRuleItemSolr.getPathType().equals(
						ImagePathType.IMAGE_LINK.getDisplayText())) {
					imagePath.setPathType(ImagePathType.IMAGE_LINK);
				} else if (bannerRuleItemSolr.getPathType().equals(
						ImagePathType.UPLOAD_LINK.getDisplayText())) {
					imagePath.setPathType(ImagePathType.UPLOAD_LINK);
				}
			}
			imagePath.setAlias(bannerRuleItemSolr.getAlias());
			bannerRuleItem.setImagePath(imagePath);

			bannerRuleItems.add(bannerRuleItem);
		}

		return bannerRuleItems;
	}

}
