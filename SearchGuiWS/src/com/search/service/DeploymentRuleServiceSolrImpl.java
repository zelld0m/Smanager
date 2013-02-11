package com.search.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.service.SolrService;

public class DeploymentRuleServiceSolrImpl implements DeploymentRuleService {

	private Logger logger = Logger
			.getLogger(DeploymentRuleServiceSolrImpl.class);

	private SolrService solrService;
	private DaoService daoService;
	private DaoService daoServiceStg;
	private FileService fileService;

	public void setSolrService(SolrService solrService) {
		this.solrService = solrService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public void setDaoServiceStg(DaoService daoServiceStg) {
		this.daoServiceStg = daoServiceStg;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public Map<String, Boolean> publishElevateRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);
		List<ElevateResult> elevatedList = null;
		ElevateResult elevateFilter = new ElevateResult();

		try {
			// create backup
			fileService.createBackup(store, keywords, RuleEntity.ELEVATE);

			for (String key : keywords) {
				try {
					StoreKeyword storeKeyword = new StoreKeyword(store, key);
					daoService.clearElevateResult(storeKeyword); // prod

					// retrieve staging data then push to prod
					daoService.addKeyword(storeKeyword);
					elevateFilter.setStoreKeyword(storeKeyword);
					SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
							elevateFilter, null, null, 0, 0);
					elevatedList = daoServiceStg.getElevateResultList(criteria)
							.getList();

					if (elevatedList != null && elevatedList.size() > 0) {
						for (ElevateResult elevateResult : elevatedList) {
							daoService.addElevateResult(elevateResult);
						}
					}

					if (!solrService.resetElevateRules(storeKeyword)) {
						logger.error("Failed to index elevate rule: " + key);
					}

					keywordStatus.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to publish elevate rule: " + key, e);
					keywordStatus.put(key, false);
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> publishExcludeRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);
		List<ExcludeResult> excludedList = null;
		ExcludeResult excludeFilter = new ExcludeResult();

		try {
			// create backup
			fileService.createBackup(store, keywords, RuleEntity.EXCLUDE);

			for (String key : keywords) {
				try {
					StoreKeyword storeKeyword = new StoreKeyword(store, key);
					daoService.clearExcludeResult(storeKeyword); // prod

					// retrieve staging data then push to prod
					daoService.addKeyword(storeKeyword);
					excludeFilter.setStoreKeyword(storeKeyword);
					SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
							excludeFilter, null, null, 0, 0);
					excludedList = daoServiceStg.getExcludeResultList(criteria)
							.getList();

					if (excludedList != null && excludedList.size() > 0) {
						for (ExcludeResult excludeResult : excludedList) {
							daoService.addExcludeResult(excludeResult);
						}
					}

					if (!solrService.resetExcludeRules(storeKeyword)) {
						logger.error("Failed to index exclude rule: " + key);
					}

					keywordStatus.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to publish exclude rule: " + key, e);
					keywordStatus.put(key, false);
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> publishDemoteRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);
		List<DemoteResult> demotedList = null;
		DemoteResult demoteFilter = new DemoteResult();

		try {
			// create backup
			fileService.createBackup(store, keywords, RuleEntity.DEMOTE);

			for (String key : keywords) {
				try {
					StoreKeyword storeKeyword = new StoreKeyword(store, key);
					daoService.clearDemoteResult(storeKeyword); // prod

					// retrieve staging data then push to prod
					daoService.addKeyword(storeKeyword);
					demoteFilter.setStoreKeyword(storeKeyword);
					SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
							demoteFilter, null, null, 0, 0);
					demotedList = daoServiceStg.getDemoteResultList(criteria)
							.getList();

					if (demotedList != null && demotedList.size() > 0) {
						for (DemoteResult demoteResult : demotedList) {
							daoService.addDemoteResult(demoteResult);
						}
					}

					if (!solrService.resetDemoteRules(storeKeyword)) {
						logger.error("Failed to index demote rule: " + key);
					}

					keywordStatus.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to publish demote rule: " + key, e);
					keywordStatus.put(key, false);
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> publishFacetSortRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			// Create backup
			fileService.createBackup(store, keywords, RuleEntity.FACET_SORT);

			for (String key : keywords) {
				FacetSort facetSort = new FacetSort(key, store);
				int result = -1;

				daoService.deleteFacetSort(facetSort); // prod

				// retrieve staging data then push to prod
				FacetSort addFacetSort = daoServiceStg.getFacetSort(facetSort);

				if (addFacetSort != null) {
					try {
						result += daoService.addFacetSort(addFacetSort); // prod

						// add facet groups
						FacetGroup facetGroup = new FacetGroup(key, "");
						SearchCriteria<FacetGroup> criteria = new SearchCriteria<FacetGroup>(
								facetGroup);
						RecordSet<FacetGroup> addFacetSortGroups = daoServiceStg
								.searchFacetGroup(criteria, MatchType.MATCH_ID);

						if (addFacetSortGroups != null) {
							List<FacetGroup> addFsGs = addFacetSortGroups
									.getList();

							for (FacetGroup fg : addFsGs) {
								result += daoService.addFacetGroup(fg); // prod
							}
						}

						// add facet group items
						FacetGroupItem facetGroupItem = new FacetGroupItem(key,
								"");
						SearchCriteria<FacetGroupItem> criteria2 = new SearchCriteria<FacetGroupItem>(
								facetGroupItem);
						RecordSet<FacetGroupItem> addFsGroupItems = daoServiceStg
								.searchFacetGroupItem(criteria2,
										MatchType.MATCH_ID);

						if (addFsGroupItems != null) {
							result += daoService
									.addFacetGroupItems(addFsGroupItems
											.getList()); // prod
						}

						if (!solrService.resetFacetSortRuleById(
								new Store(store), key)) {
							logger.error("Failed to index facet sort rule: "
									+ key);
						}

						keywordStatus.put(key, (result > 0));
					} catch (DaoException e) {
						keywordStatus.put(key, false);
						logger.error("Failed during addRule()", e);

						try {
							daoService
									.deleteFacetSort(new FacetSort(key, store));
						} catch (DaoException de) {
							logger.error(
									"Unable to complete process, need to manually delete rule",
									de);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> publishRedirectRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			// Create backup
			fileService
					.createBackup(store, keywords, RuleEntity.QUERY_CLEANING);

			for (String key : keywords) {
				try {
					RedirectRule redirectRule = new RedirectRule();
					redirectRule.setRuleId(key);
					redirectRule.setStoreId(store);
					daoService.deleteRedirectRule(redirectRule); // prod

					// retrieve staging data then push to prod
					RedirectRule addRedirectRule = daoServiceStg
							.getRedirectRule(redirectRule);

					if (addRedirectRule != null) {
						List<String> searchTerms = addRedirectRule
								.getSearchTerms();

						if (CollectionUtils.isNotEmpty(searchTerms)) {
							for (String keyword : searchTerms) {
								daoService.addKeyword(new StoreKeyword(store,
										keyword));
							}
						}

						daoService.addRedirectRule(addRedirectRule); // prod

						// add redirect keyword
						if (CollectionUtils.isNotEmpty(searchTerms)) {
							for (String keyword : searchTerms) {
								RedirectRule rule = new RedirectRule();
								rule.setRuleId(addRedirectRule.getRuleId());
								rule.setStoreId(addRedirectRule.getStoreId());
								rule.setSearchTerm(keyword);
								rule.setLastModifiedBy("SYSTEM");
								daoService.addRedirectKeyword(rule);
							}
						}

						// add rule condition
						RedirectRule rule = new RedirectRule();
						rule.setRuleId(addRedirectRule.getRuleId());
						rule.setStoreId(addRedirectRule.getStoreId());
						RecordSet<RedirectRuleCondition> conditionSet = daoServiceStg
								.getRedirectConditions(new SearchCriteria<RedirectRule>(
										rule, null, null, 0, 0));
						if (conditionSet != null
								&& conditionSet.getTotalSize() > 0) {
							for (RedirectRuleCondition condition : conditionSet
									.getList()) {
								condition.setLastModifiedBy("SYSTEM");
								daoService.addRedirectCondition(condition);
							}
						}

						if (!solrService.resetRedirectRuleById(
								new Store(store), key)) {
							logger.error("Failed to index redirect rule: "
									+ key);
						}
					}
					keywordStatus.put(key, true);
				} catch (Exception e) {
					keywordStatus.put(key, false);
					logger.error("Failed to publish redirect rule: " + key, e);
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> publishRankingRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			// Create backup
			fileService.createBackup(store, keywords, RuleEntity.RANKING_RULE);

			for (String key : keywords) {
				try {
					Relevancy relevancy = new Relevancy();
					relevancy.setRelevancyId(key);
					relevancy.setStore(new Store(store));
					daoService.deleteRelevancy(relevancy); // prod

					// retrieve staging data then push to prod
					Relevancy addRelevancy = daoServiceStg
							.getRelevancyDetails(relevancy);

					if (addRelevancy != null) {
						// add relevancy
						daoService.addRelevancy(addRelevancy);
						// add relevancy keywords
						RecordSet<RelevancyKeyword> relevancyKeywords = daoServiceStg
								.getRelevancyKeywords(addRelevancy);
						if (relevancyKeywords.getTotalSize() > 0) {
							for (RelevancyKeyword rk : relevancyKeywords
									.getList()) {
								daoService.addKeyword(new StoreKeyword(store,
										rk.getKeyword().getKeywordId()));
								daoService.addRelevancyKeyword(rk);
							}
						}
						// save relevancy fields
						RelevancyField rf = new RelevancyField();
						rf.setRelevancy(addRelevancy);

						Map<String, String> relevancyFields = addRelevancy
								.getParameters();
						if (relevancyFields != null) {
							for (String field : relevancyFields.keySet()) {
								String value = relevancyFields.get(field);
								if (StringUtils.isNotBlank(value)) {
									rf.setFieldName(field);
									rf.setFieldValue(value);
									daoService.addRelevancyField(rf);
								}
							}
						}
					}

					if (!solrService.resetRelevancyRuleById(new Store(store),
							key)) {
						logger.error("Failed to index relevancy rule: " + key);
					}

					keywordStatus.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to publish relevancy rule: " + key, e);
					keywordStatus.put(key, false);
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> recallElevateRulesMap(String store,
			List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> recallExcludeRulesMap(String store,
			List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> recallDemoteRulesMap(String store,
			List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> recallFacetSortRulesMap(String store,
			List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> recallRedirectRulesMap(String store,
			List<String> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> recallRankingRulesMap(String store,
			List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean loadElevateRules(String store) {
		try {
			solrService.loadElevateRules(new Store(store));
		} catch (DaoException e) {
			logger.error(e);
		}

		return false;
	}

	@Override
	public boolean loadExcludeRules(String store) {
		try {
			solrService.loadExcludeRules(new Store(store));
		} catch (DaoException e) {
			logger.error(e);
		}

		return false;
	}

	@Override
	public boolean loadDemoteRules(String store) {
		try {
			solrService.loadDemoteRules(new Store(store));
		} catch (DaoException e) {
			logger.error(e);
		}

		return false;
	}

	@Override
	public boolean loadFacetSortRules(String store) {
		try {
			solrService.loadFacetSortRules(new Store(store));
		} catch (DaoException e) {
			logger.error(e);
		}

		return false;
	}

	@Override
	public boolean loadRedirectRules(String store) {
		try {
			solrService.loadRedirectRules(new Store(store));
		} catch (DaoException e) {
			logger.error(e);
		}

		return false;
	}

	@Override
	public boolean loadRankingRules(String store) {
		try {
			solrService.loadRelevancyRules(new Store(store));
		} catch (DaoException e) {
			logger.error(e);
		}

		return false;
	}

	@Override
	public Map<String, Boolean> unpublishElevateRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			if (CollectionUtils.isNotEmpty(keywords)) {
				for (String key : keywords) {
					try {
						StoreKeyword storeKeyword = new StoreKeyword(store, key);
						daoService.clearElevateResult(storeKeyword); // prod

						if (!solrService.resetElevateRules(storeKeyword)) {
							logger.error("Failed to index(unpublish) elevate rule: "
									+ key);
						}

						keywordStatus.put(key, true);
					} catch (Exception e) {
						logger.error(
								"Failed to unpublish elevate rule: " + key, e);
						keywordStatus.put(key, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> unpublishExcludeRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			if (CollectionUtils.isNotEmpty(keywords)) {
				for (String key : keywords) {
					try {
						StoreKeyword storeKeyword = new StoreKeyword(store, key);
						daoService.clearExcludeResult(storeKeyword); // prod

						if (!solrService.resetExcludeRules(storeKeyword)) {
							logger.error("Failed to index(unpublish) exclude rule: "
									+ key);
						}

						keywordStatus.put(key, true);
					} catch (Exception e) {
						logger.error(
								"Failed to unpublish exclude rule: " + key, e);
						keywordStatus.put(key, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> unpublishDemoteRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			if (CollectionUtils.isNotEmpty(keywords)) {
				for (String key : keywords) {
					try {
						StoreKeyword storeKeyword = new StoreKeyword(store, key);
						daoService.clearDemoteResult(storeKeyword); // prod

						if (!solrService.resetDemoteRules(storeKeyword)) {
							logger.error("Failed to index(unpublish) demote rule: "
									+ key);
						}

						keywordStatus.put(key, true);
					} catch (Exception e) {
						logger.error("Failed to unpublish demote rule: " + key,
								e);
						keywordStatus.put(key, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> unpublishFacetSortRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			if (CollectionUtils.isNotEmpty(keywords)) {
				for (String key : keywords) {
					try {
						int result = -1;

						FacetSort facetSort = new FacetSort();
						facetSort.setRuleId(key);
						facetSort.setStore(new Store(store));
						result = daoService.deleteFacetSort(facetSort); // prod

						if (!solrService.resetFacetSortRuleById(
								new Store(store), key)) {
							logger.error("Failed to index(unpublish) facet sort rule: "
									+ key);
						}

						keywordStatus.put(key, (result > 0));
					} catch (Exception e) {
						logger.error("Failed to unpublish facet sort rule: "
								+ key, e);
						keywordStatus.put(key, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> unpublishRedirectRulesMap(String store,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			if (CollectionUtils.isNotEmpty(keywords)) {
				for (String key : keywords) {
					try {
						RedirectRule delRel = new RedirectRule();
						delRel.setRuleId(key);
						delRel.setStoreId(store);
						// get list of keywords for ranking rule
						List<StoreKeyword> sks = new ArrayList<StoreKeyword>();
						RedirectRule rule = new RedirectRule();
						rule.setRuleId(key);
						rule.setStoreId(store);
						SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
								rule, null, null, 0, 0);

						for (StoreKeyword keyword : daoService
								.getRedirectKeywords(criteria,
										MatchType.MATCH_ID, ExactMatch.SIMILAR)
								.getList()) {
							sks.add(keyword);
						}
						daoService.deleteRedirectRule(delRel); // prod

						if (!solrService.resetRedirectRuleById(
								new Store(store), key)) {
							logger.error("Failed to index(unpublish) redirect rule: "
									+ key);
						}

						keywordStatus.put(key, true);
					} catch (Exception e) {
						logger.error("Failed to unpublish redirect rule: "
								+ key, e);
						keywordStatus.put(key, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	@Override
	public Map<String, Boolean> unpublishRankingRulesMap(String strStore,
			List<String> keywords) {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap(keywords);

		try {
			Store store = new Store(strStore);

			if (CollectionUtils.isNotEmpty(keywords)) {
				for (String key : keywords) {
					try {
						Relevancy relevancy = new Relevancy();
						relevancy.setRelevancyId(key);
						relevancy.setStore(store);

						// get list of keywords for ranking rule
						List<StoreKeyword> storeKeywords = new ArrayList<StoreKeyword>();

						for (RelevancyKeyword keyword : daoService
								.getRelevancyKeywords(relevancy).getList()) {
							storeKeywords.add(new StoreKeyword(strStore,
									keyword.getKeyword().getKeywordId()));
						}

						daoService.deleteRelevancy(relevancy); // prod

						if (!solrService.resetRelevancyRuleById(store, key)) {
							logger.error("Failed to index(unpublish) relevancy rule: "
									+ key);
						}

						keywordStatus.put(key, true);
					} catch (Exception e) {
						logger.error("Failed to unpublish relevancy rule: "
								+ key, e);
						keywordStatus.put(key, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return keywordStatus;
	}

	private Map<String, Boolean> getKeywordStatusMap(List<String> keywords) {
		Map<String, Boolean> keywordStatus = new HashMap<String, Boolean>();

		for (String key : keywords) {
			keywordStatus.put(key, false);
		}

		return keywordStatus;
	}

}
