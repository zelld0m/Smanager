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
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.model.RedirectRuleSolr;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;

@Repository("redirectDaoSolr")
public class RedirectDaoSolrImpl extends BaseDaoSolr implements RedirectDao {

	private static final Logger logger = Logger
			.getLogger(RedirectDaoSolrImpl.class);

	@Override
	public List<RedirectRule> getRedirectRules(Store store) throws DaoException {
		List<RedirectRule> redirectRules = new ArrayList<RedirectRule>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + ClientUtils.escapeQueryChars(storeId));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				redirectRules = SolrResultUtil.toRedirectRule(queryResponse
						.getBeans(RedirectRuleSolr.class));
			}
		} catch (Exception e) {
			logger.error("Failed to get redirect rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return redirectRules;
	}

	@Override
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword)
			throws DaoException {
		RedirectRule redirectRule = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + storeId).append(
					" AND searchTerms1:"
							+ ClientUtils.escapeQueryChars(keyword));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<RedirectRule> redirectRules = SolrResultUtil
						.toRedirectRule(queryResponse
								.getBeans(RedirectRuleSolr.class));
				if (redirectRules != null && redirectRules.size() > 0) {
					redirectRule = redirectRules.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get redirect rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return redirectRule;
	}

	@Override
	public RedirectRule getRedirectRuleByName(Store store, String name)
			throws DaoException {
		RedirectRule redirectRule = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			name = StringUtils.lowerCase(StringUtils.trim(name));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + storeId).append(
					" AND ruleName:" + ClientUtils.escapeQueryChars(name));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<RedirectRule> redirectRules = SolrResultUtil
						.toRedirectRule(queryResponse
								.getBeans(RedirectRuleSolr.class));
				if (redirectRules != null && redirectRules.size() > 0) {
					redirectRule = redirectRules.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get redirect rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return redirectRule;
	}

	@Override
	public RedirectRule getRedirectRuleById(Store store, String id)
			throws DaoException {
		RedirectRule redirectRule = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			id = StringUtils.trim(id);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + storeId).append(
					" AND ruleId:" + ClientUtils.escapeQueryChars(id));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<RedirectRule> redirectRules = SolrResultUtil
						.toRedirectRule(queryResponse
								.getBeans(RedirectRuleSolr.class));
				if (redirectRules != null && redirectRules.size() > 0) {
					redirectRule = redirectRules.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get redirect rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return redirectRule;
	}

	@Override
	public boolean loadRedirectRules(Store store) throws DaoException {

		try {
			RedirectRule redirectRuleFilter = new RedirectRule();
			redirectRuleFilter.setStoreId(store.getStoreId());
			redirectRuleFilter.setRuleName(""); // ALL
			int page = 1;

			while (true) {
				SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
						redirectRuleFilter, page, MAX_ROWS);
				RecordSet<RedirectRule> recordSet = daoService
						.searchRedirectRule(criteria, MatchType.LIKE_NAME);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<RedirectRule> redirectRules = recordSet.getList();
					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocsRedirectRule(redirectRules);
					solrServers.getCoreInstance(
							Constants.Core.REDIRECT_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					if (redirectRules.size() < MAX_ROWS) {
						solrServers
								.getCoreInstance(
										Constants.Core.REDIRECT_RULE_CORE
												.getCoreName()).softCommit();
						return true;
					}
					page++;
				} else {
					if (page != 1) {
						solrServers
								.getCoreInstance(
										Constants.Core.REDIRECT_RULE_CORE
												.getCoreName()).softCommit();
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load redirect rules by store." + e, e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadRedirectRuleByName(Store store, String name)
			throws DaoException {
		try {
			List<RedirectRule> redirectRules = null;
			RedirectRule redirectRuleFilter = new RedirectRule();
			redirectRuleFilter.setStoreId(store.getStoreId());
			redirectRuleFilter.setRuleName(name);
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
					redirectRuleFilter, null, null, 0, 0);

			RecordSet<RedirectRule> recordSet = daoService.searchRedirectRule(
					criteria, MatchType.LIKE_NAME);

			if (recordSet != null && recordSet.getTotalSize() > 0) {
				redirectRules = recordSet.getList();

				List<SolrInputDocument> solrInputDocuments = SolrDocUtil
						.composeSolrDocsRedirectRule(redirectRules);

				if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
					solrServers.getCoreInstance(
							Constants.Core.REDIRECT_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.REDIRECT_RULE_CORE.getCoreName())
							.softCommit();
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load redirect rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadRedirectRuleById(Store store, String id)
			throws DaoException {
		try {
			List<RedirectRule> redirectRules = null;
			RedirectRule redirectRuleFilter = new RedirectRule();
			redirectRuleFilter.setStoreId(store.getStoreId());
			redirectRuleFilter.setRuleId(id);
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
					redirectRuleFilter, null, null, 0, 0);

			RecordSet<RedirectRule> recordSet = daoService.searchRedirectRule(
					criteria, MatchType.MATCH_ID);

			if (recordSet != null && recordSet.getTotalSize() > 0) {
				redirectRules = recordSet.getList();

				List<SolrInputDocument> solrInputDocuments = SolrDocUtil
						.composeSolrDocsRedirectRule(redirectRules);

				if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
					solrServers.getCoreInstance(
							Constants.Core.REDIRECT_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					// solrServers.getCoreInstance(
					// Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					// .softCommit();
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load redirect rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetRedirectRules(Store store) throws DaoException {

		try {
			if (deleteRedirectRules(store)) {
				return loadRedirectRules(store);
			}
		} catch (Exception e) {
			logger.error("Failed to reset redirect rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetRedirectRuleByName(Store store, String name)
			throws DaoException {

		try {
			if (deleteRedirectRuleByName(store, name)) {
				return loadRedirectRuleByName(store, name);
			}
		} catch (Exception e) {
			logger.error("Failed to reset redirect rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetRedirectRuleById(Store store, String id)
			throws DaoException {

		try {
			if (deleteRedirectRuleById(store, id)) {
				return loadRedirectRuleById(store, id);
			}
		} catch (Exception e) {
			logger.error("Failed to reset redirect rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public Map<String, Boolean> resetRedirectRulesById(Store store,
			Collection<String> ids) throws DaoException {
		Map<String, Boolean> idStatus = getKeywordStatusMap((List<String>) ids);

		for (String id : ids) {
			boolean hasError = false;
			try {
				String storeId = StringUtils.lowerCase(StringUtils.trim(store
						.getStoreId()));
				id = StringUtils.trim(id);

				StringBuffer strQuery = new StringBuffer();
				strQuery.append(
						"storeId:" + ClientUtils.escapeQueryChars(storeId))
						.append(" AND ruleId:"
								+ ClientUtils.escapeQueryChars(id));

				solrServers.getCoreInstance(
						Constants.Core.REDIRECT_RULE_CORE.getCoreName())
						.deleteByQuery(strQuery.toString());

				List<RedirectRule> redirectRules = null;
				RedirectRule redirectRuleFilter = new RedirectRule();
				redirectRuleFilter.setStoreId(store.getStoreId());
				redirectRuleFilter.setRuleId(id);
				SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
						redirectRuleFilter, null, null, 0, 0);

				RecordSet<RedirectRule> recordSet = daoService
						.searchRedirectRule(criteria, MatchType.MATCH_ID);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					redirectRules = recordSet.getList();

					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocsRedirectRule(redirectRules);

					if (solrInputDocuments != null
							&& solrInputDocuments.size() > 0) {
						solrServers
								.getCoreInstance(
										Constants.Core.REDIRECT_RULE_CORE
												.getCoreName()).addDocs(
										solrInputDocuments);
					}
				}
			} catch (Exception e) {
				logger.error("Failed to reset redirect rules by ruleIds", e);
				hasError = true;
			}

			idStatus.put(id, !hasError);
		}

		try {
			solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					.softCommit();
		} catch (Exception e) {
			logger.error("Failed to reset redirect rules by ruleIds", e);
			return null;
		}

		return idStatus;
	}

	@Override
	public boolean deleteRedirectRules(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + storeId);

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					.softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete redirect rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteRedirectRuleByName(Store store, String name)
			throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			name = StringUtils.trim(name);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND ruleName:"
							+ ClientUtils.escapeQueryChars(name));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					.softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete redirect rules by ruleName", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteRedirectRuleById(Store store, String id)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			id = StringUtils.trim(id);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND ruleId:" + ClientUtils.escapeQueryChars(id));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.REDIRECT_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			// solrServers.getCoreInstance(
			// Constants.Core.REDIRECT_RULE_CORE.getCoreName())
			// .softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete redirect rules by ruleId", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateRedirectRule(RedirectRule redirectRule)
			throws DaoException {

		if (redirectRule == null) {
			return false;
		}

		try {
			SolrInputDocument solrInputDocument = SolrDocUtil
					.composeSolrDoc(redirectRule);
			if (solrInputDocument != null) {
				solrServers.getCoreInstance(
						Constants.Core.REDIRECT_RULE_CORE.getCoreName())
						.addDoc(solrInputDocument);
				solrServers.getCoreInstance(
						Constants.Core.REDIRECT_RULE_CORE.getCoreName())
						.softCommit();
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to update redirect rule. " + e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean commitRedirectRule() throws DaoException {
		try {
			return commit(solrServers
					.getCoreInstance(Constants.Core.REDIRECT_RULE_CORE
							.getCoreName()));
		} catch (SolrServerException e) {
			logger.error("Failed to commit redirect rules. " + e.getMessage(),
					e);
			return false;
		}
	}

}
