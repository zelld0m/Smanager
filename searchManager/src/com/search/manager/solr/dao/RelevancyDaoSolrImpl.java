package com.search.manager.solr.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.model.RelevancyRuleSolr;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;

@Repository("relevancyDaoSolr")
public class RelevancyDaoSolrImpl extends BaseDaoSolr implements RelevancyDao {

	private static final Logger logger = Logger
			.getLogger(RelevancyDaoSolrImpl.class);

	@Override
	public List<Relevancy> getRelevancyRules(Store store) throws DaoException {
		List<Relevancy> relevancies = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId);

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				relevancies = SolrResultUtil.toRelevancyRule(queryResponse
						.getBeans(RelevancyRuleSolr.class));
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return relevancies;
	}

	@Override
	public Relevancy getDefaultRelevancyRule(Store store) throws DaoException {
		Relevancy defaultRelevancy = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("relevancyId:" + storeId + "_default");

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<Relevancy> relevancies = SolrResultUtil
						.toRelevancyRule(queryResponse
								.getBeans(RelevancyRuleSolr.class));

				if (relevancies != null && relevancies.size() > 0) {
					defaultRelevancy = relevancies.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get default relevancy rule by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return defaultRelevancy;
	}

	@Override
	public Relevancy getRelevancyRule(StoreKeyword storeKeyword)
			throws DaoException {

		Relevancy relevancy = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId)
					.append(" AND relKeyword1:"
							+ ClientUtils.escapeQueryChars(keyword));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<Relevancy> relevancies = SolrResultUtil
						.toRelevancyRule(queryResponse
								.getBeans(RelevancyRuleSolr.class));
				if (relevancies != null && relevancies.size() > 0) {
					relevancy = relevancies.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return relevancy;
	}

	@Override
	public Relevancy getRelevancyRuleByName(Store store, String name)
			throws DaoException {
		Relevancy relevancy = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			name = StringUtils.lowerCase(StringUtils.trim(name));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId).append(
					" AND relevancyName:" + ClientUtils.escapeQueryChars(name));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<Relevancy> relevancies = SolrResultUtil
						.toRelevancyRule(queryResponse
								.getBeans(RelevancyRuleSolr.class));
				if (relevancies != null && relevancies.size() > 0) {
					relevancy = relevancies.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rule by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return relevancy;
	}

	@Override
	public Relevancy getRelevancyRuleById(Store store, String id)
			throws DaoException {
		Relevancy relevancy = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			id = StringUtils.trim(id);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId).append(
					" AND relevancyId:" + ClientUtils.escapeQueryChars(id));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<Relevancy> relevancies = SolrResultUtil
						.toRelevancyRule(queryResponse
								.getBeans(RelevancyRuleSolr.class));
				if (relevancies != null && relevancies.size() > 0) {
					relevancy = relevancies.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return relevancy;
	}

	@Override
	public boolean loadRelevancyRules(Store store) throws DaoException {
		try {
			boolean hasRelevancy = false;
			// load default relevancy
			Relevancy defaultRelevancy = new Relevancy();
			defaultRelevancy
					.setRelevancyId(StringUtils.trim(store.getStoreId())
							+ "_default");
			defaultRelevancy = daoService.getRelevancyDetails(defaultRelevancy);

			if (defaultRelevancy != null) {
				try {
					SolrInputDocument solrInputDocument = SolrDocUtil
							.composeSolrDoc(defaultRelevancy);
					solrServers.getCoreInstance(
							Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
							.addDoc(solrInputDocument);
					hasRelevancy = true;
				} catch (Exception e) {
					logger.error("Failed to load relevancy rules by store. " + e.getMessage(), e);
				}
			}

			Relevancy relevancyFilter = new Relevancy();
			relevancyFilter.setStore(store);
			relevancyFilter.setRelevancyName(""); // ALL
			int page = 1;

			while (true) {
				SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(
						relevancyFilter, page, MAX_ROWS);
				RecordSet<Relevancy> recordSet = daoService.searchRelevancy(
						criteria, MatchType.LIKE_NAME);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					hasRelevancy = true;
					List<Relevancy> relevancies = new ArrayList<Relevancy>();
					// setRelKeyword
					for (Relevancy relevancy : recordSet.getList()) {
						relevancy = daoService.getRelevancyDetails(relevancy);
						relevancy.setRelKeyword(daoService
								.getRelevancyKeywords(relevancy).getList());
						relevancies.add(relevancy);
					}

					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocsRelevancy(relevancies);
					solrServers.getCoreInstance(
							Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);

					if (relevancies.size() < MAX_ROWS) {
						break;
					}
					page++;
				} else {
					break;
				}
			}

			if (hasRelevancy) {
				solrServers.getCoreInstance(
						Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
						.commit();
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadRelevancyRuleByName(Store store, String name)
			throws DaoException {
		try {
			name = StringUtils.trim(name);
			Relevancy relevancyFilter = new Relevancy();
			relevancyFilter.setStore(store);
			relevancyFilter.setRelevancyName(name);
			int page = 1;

			while (true) {
				SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(
						relevancyFilter, page, MAX_ROWS);
				RecordSet<Relevancy> recordSet = daoService.searchRelevancy(
						criteria, MatchType.LIKE_NAME);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<Relevancy> relevancies = new ArrayList<Relevancy>();

					for (Relevancy relevancy : recordSet.getList()) {
						relevancy = daoService.getRelevancyDetails(relevancy);
						relevancy.setRelKeyword(daoService
								.getRelevancyKeywords(relevancy).getList());
						relevancies.add(relevancy);
					}
					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocsRelevancy(relevancies);

					solrServers.getCoreInstance(
							Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);

					if (relevancies.size() < MAX_ROWS) {
						solrServers.getCoreInstance(
								Constants.Core.RELEVANCY_RULE_CORE
										.getCoreName()).commit();
						return true;
					}
					page++;
				} else {
					if (page != 1) {
						solrServers.getCoreInstance(
								Constants.Core.RELEVANCY_RULE_CORE
										.getCoreName()).commit();
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadRelevancyRuleById(Store store, String id)
			throws DaoException {
		try {
			id = StringUtils.trim(id);
			List<Relevancy> relevancies = null;
			Relevancy relevancyFilter = new Relevancy();
			relevancyFilter.setStore(store);
			relevancyFilter.setRelevancyId(id);

			SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(
					relevancyFilter, null, null, 0, 0);
			RecordSet<Relevancy> recordSet = daoService.searchRelevancy(
					criteria, MatchType.MATCH_ID);

			if (recordSet != null && recordSet.getTotalSize() > 0) {
				relevancies = new ArrayList<Relevancy>();
				// setRelKeyword
				for (Relevancy relevancy : recordSet.getList()) {
					relevancy = daoService.getRelevancyDetails(relevancy);
					relevancy.setRelKeyword(daoService.getRelevancyKeywords(
							relevancy).getList());
					relevancies.add(relevancy);
				}

				List<SolrInputDocument> solrInputDocuments = SolrDocUtil
						.composeSolrDocsRelevancy(relevancies);

				if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
					solrServers.getCoreInstance(
							Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
							.softCommit();

					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetRelevancyRules(Store store) throws DaoException {
		try {
			if (deleteRelevancyRules(store)) {
				return loadRelevancyRules(store);
			}
		} catch (Exception e) {
			logger.error("Failed to reset relevancy rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetRelevancyRuleByName(Store store, String name)
			throws DaoException {
		try {
			if (deleteRelevancyRuleByName(store, name)) {
				return loadRelevancyRuleByName(store, name);
			}
		} catch (Exception e) {
			logger.error("Failed to get relevancy rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetRelevancyRuleById(Store store, String id)
			throws DaoException {
		try {
			if (deleteRelevancyRuleById(store, id)) {
				return loadRelevancyRuleById(store, id);
			}
		} catch (Exception e) {
			logger.error("Failed to reset relevancy rule by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public Map<String, Boolean> resetRelevancyRulesById(Store store,
			Collection<String> ids) throws DaoException {
		Map<String, Boolean> idStatus = getKeywordStatusMap((List<String>) ids);

		for (String id : ids) {
			boolean hasError = false;

			try {
				String storeId = StringUtils.lowerCase(StringUtils.trim(store
						.getStoreId()));
				id = StringUtils.trim(id);

				StringBuffer strQuery = new StringBuffer();
				strQuery.append("store:" + storeId).append(
						" AND relevancyId:" + ClientUtils.escapeQueryChars(id));

				solrServers.getCoreInstance(
						Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
						.deleteByQuery(strQuery.toString());

				List<Relevancy> relevancies = null;
				Relevancy relevancyFilter = new Relevancy();
				relevancyFilter.setStore(store);
				relevancyFilter.setRelevancyId(id);

				SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(
						relevancyFilter, null, null, 0, 0);
				RecordSet<Relevancy> recordSet = daoService.searchRelevancy(
						criteria, MatchType.MATCH_ID);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					relevancies = new ArrayList<Relevancy>();
					// setRelKeyword
					for (Relevancy relevancy : recordSet.getList()) {
						relevancy = daoService.getRelevancyDetails(relevancy);
						relevancy.setRelKeyword(daoService
								.getRelevancyKeywords(relevancy).getList());
						relevancies.add(relevancy);
					}

					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocsRelevancy(relevancies);

					if (solrInputDocuments != null
							&& solrInputDocuments.size() > 0) {
						solrServers.getCoreInstance(
								Constants.Core.RELEVANCY_RULE_CORE
										.getCoreName()).addDocs(
								solrInputDocuments);
						solrServers.getCoreInstance(
								Constants.Core.RELEVANCY_RULE_CORE
										.getCoreName()).softCommit();
					}
				}

			} catch (Exception e) {
				logger.error("Failed to reset relevancy rules by ruleIds", e);
				hasError = true;
			}

			idStatus.put(id, !hasError);
		}

		try {
			solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
					.softCommit();
		} catch (Exception e) {
			logger.error("Failed to reset relevancy rules by ruleIds", e);
			return null;
		}

		return idStatus;
	}

	@Override
	public boolean deleteRelevancyRules(Store store) throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId);

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
					.softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete relevancy rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteRelevancyRuleByName(Store store, String name)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			name = StringUtils.lowerCase(StringUtils.trim(name));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId).append(
					" AND relevancyName:" + ClientUtils.escapeQueryChars(name));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
					.softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to reset relevancy rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;

	}

	@Override
	public boolean deleteRelevancyRuleById(Store store, String id)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			id = StringUtils.trim(id);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + storeId).append(
					" AND relevancyId:" + ClientUtils.escapeQueryChars(id));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			// solrServers.getCoreInstance(
			// Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
			// .softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to reset relevancy rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateRelevancyRule(Relevancy relevancy) throws DaoException {
		if (relevancy == null) {
			return false;
		}

		try {
			SolrInputDocument solrInputDocument = SolrDocUtil
					.composeSolrDoc(relevancy);
			if (solrInputDocument != null) {
				solrServers.getCoreInstance(
						Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
						.addDoc(solrInputDocument);
				solrServers.getCoreInstance(
						Constants.Core.RELEVANCY_RULE_CORE.getCoreName())
						.commit();
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to update relevancy rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean commitRelevancyRule() throws DaoException {
		try {
			return commit(solrServers
					.getCoreInstance(Constants.Core.RELEVANCY_RULE_CORE
							.getCoreName()));
		} catch (SolrServerException e) {
			logger.error("Failed to commit relevancy rules", e);
			return false;
		}
	}

}
