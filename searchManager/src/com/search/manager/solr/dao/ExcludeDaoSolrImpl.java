package com.search.manager.solr.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.model.RuleSolrResult;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;

@Repository("excludeDaoSolr")
public class ExcludeDaoSolrImpl extends BaseDaoSolr implements ExcludeDao {

	private static final Logger logger = Logger
			.getLogger(ExcludeDaoSolrImpl.class);

	@Override
	public List<ExcludeResult> getExcludeRules(Store store) throws DaoException {
		List<ExcludeResult> excludeResults = new ArrayList<ExcludeResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.info(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				excludeResults = SolrResultUtil.toExcludeResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return excludeResults;
	}

	@Override
	public List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {
		List<ExcludeResult> excludeResults = new ArrayList<ExcludeResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));
			strQuery.append(" AND keyword1:"
					+ ClientUtils.escapeQueryChars(keyword));
			strQuery.append(" AND (expiryDate:[NOW/DAY+1DAY TO *] OR (*:* AND -expiryDate:[* TO *]))");
			
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.info(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				excludeResults = SolrResultUtil.toExcludeResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return excludeResults;
	}

	@Override
	public List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {
		List<ExcludeResult> excludeResults = new ArrayList<ExcludeResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));
			strQuery.append(" AND keyword1:"
					+ ClientUtils.escapeQueryChars(keyword));
			strQuery.append(" AND expiryDate:[* TO NOW/DAY]");

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.info(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				excludeResults = SolrResultUtil.toExcludeResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return excludeResults;
	}

	@Override
	public boolean loadExcludeRules(Store store) throws DaoException {
		List<String> keywords = null;
		List<Keyword> keywordList = (List<Keyword>) daoService.getAllKeywords(
				store.getStoreId(), RuleEntity.EXCLUDE);

		if (CollectionUtils.isNotEmpty(keywordList)) {
			keywords = new ArrayList<String>();
			for (Keyword key : keywordList) {
				keywords.add(key.getKeywordId());
			}
		}

		if (keywords != null) {
			for (String keyword : keywords) {
				StoreKeyword storeKeyword = new StoreKeyword(
						store.getStoreId(), keyword);
				ExcludeResult excludeFilter = new ExcludeResult();
				excludeFilter.setStoreKeyword(storeKeyword);

				SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
						excludeFilter, null, null, 0, 0);
				List<ExcludeResult> excludeResults = daoService
						.getExcludeResultList(criteria).getList();

				if (excludeResults != null && excludeResults.size() > 0) {
					List<SolrInputDocument> solrInputDocuments = null;
					boolean hasError = false;

					try {
						solrInputDocuments = SolrDocUtil
								.composeSolrDocs(excludeResults);
					} catch (Exception e) {
						hasError = true;
						logger.error(e);
					}
					if (!hasError && solrInputDocuments != null
							&& solrInputDocuments.size() > 0) {
						try {
							solrServers.getCoreInstance(
									Constants.Core.EXCLUDE_RULE_CORE
											.getCoreName()).addDocs(
									solrInputDocuments);
							solrServers.getCoreInstance(
									Constants.Core.EXCLUDE_RULE_CORE
											.getCoreName()).commit();
						} catch (Exception e) {
							logger.error(e);
							throw new DaoException(e.getMessage(), e);
						}
					}
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean loadExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			ExcludeResult excludeFilter = new ExcludeResult();
			excludeFilter.setStoreKeyword(storeKeyword);

			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
					excludeFilter, null, null, 0, 0);
			List<ExcludeResult> excludeResults = daoService
					.getExcludeResultList(criteria).getList();

			if (excludeResults != null && excludeResults.size() > 0) {
				List<SolrInputDocument> solrInputDocuments = null;
				boolean hasError = false;

				try {
					solrInputDocuments = SolrDocUtil
							.composeSolrDocs(excludeResults);
				} catch (Exception e) {
					hasError = true;
					logger.error(e);
				}

				if (!hasError && solrInputDocuments != null
						&& solrInputDocuments.size() > 0) {
					try {
						solrServers.getCoreInstance(
								Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
								.addDocs(solrInputDocuments);
						// solrServers.getCoreInstance(
						// Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
						// .commit();
					} catch (Exception e) {
						logger.error(e);
						throw new DaoException(e.getMessage(), e);
					}
				}

				return !hasError;
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetExcludeRules(Store store) throws DaoException {

		try {
			if (deleteExcludeRules(store)) {
				return loadExcludeRules(store);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			if (deleteExcludeRules(storeKeyword)) {
				return loadExcludeRules(storeKeyword);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public Map<String, Boolean> resetExcludeRules(Store store,
			Collection<String> keywords) throws DaoException {
		Map<String, Boolean> keywordStatus = getKeywordStatusMap((List<String>) keywords);

		for (String keyword : keywords) {
			boolean hasError = false;
			try {
				// delete existing rule/s indexed
				String storeId = StringUtils.lowerCase(StringUtils
						.trim(keyword));
				String key = StringUtils.lowerCase(StringUtils.trim(keyword));

				StringBuffer strQuery = new StringBuffer();
				strQuery.append(
						"store:" + ClientUtils.escapeQueryChars(storeId))
						.append(" AND keyword1:"
								+ ClientUtils.escapeQueryChars(key));

				solrServers.getCoreInstance(
						Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
						.deleteByQuery(strQuery.toString());

				// retrieve new rules
				DemoteResult demoteFilter = new DemoteResult();
				demoteFilter.setStoreKeyword(new StoreKeyword(storeId, key));
				List<SolrInputDocument> solrInputDocuments = null;

				SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
						demoteFilter, null, null, 0, 0);
				List<DemoteResult> demoteResults = daoService
						.getDemoteResultList(criteria).getList();

				if (demoteResults != null && demoteResults.size() > 0) {
					try {
						solrInputDocuments = SolrDocUtil
								.composeSolrDocs(demoteResults);
					} catch (Exception e) {
						hasError = true;
						logger.error(e);
					}

					if (!hasError && solrInputDocuments != null
							&& solrInputDocuments.size() > 0) {
						try {
							solrServers.getCoreInstance(
									Constants.Core.EXCLUDE_RULE_CORE
											.getCoreName()).addDocs(
									solrInputDocuments);
						} catch (Exception e) {
							logger.error(e);
							hasError = true;
						}
					}
				}

			} catch (Exception e) {
				logger.error(e);
				hasError = true;
			}

			keywordStatus.put(keyword, !hasError);
		}

		try {
			solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
					.softCommit();
		} catch (Exception e) {
			logger.error(e);
		}

		return keywordStatus;
	}

	@Override
	public boolean deleteExcludeRules(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
					.softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND keyword1:"
							+ ClientUtils.escapeQueryChars(keyword));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			// solrServers.getCoreInstance(
			// Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
			// .softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateExcludeRule(ExcludeResult excludeResult)
			throws DaoException {

		if (excludeResult == null) {
			return false;
		}

		try {
			List<ExcludeResult> excludeResults = new ArrayList<ExcludeResult>();
			excludeResults.add(excludeResult);

			List<SolrInputDocument> solrInputDocuments = SolrDocUtil
					.composeSolrDocs(excludeResults);
			solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).addDocs(
					solrInputDocuments);
			solrServers.getCoreInstance(
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
					.softCommit();
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return true;
	}

	@Override
	public boolean commitExcludeRule() throws DaoException {
		try {
			return commit(solrServers
					.getCoreInstance(Constants.Core.EXCLUDE_RULE_CORE
							.getCoreName()));
		} catch (SolrServerException e) {
			return false;
		}
	}

}
