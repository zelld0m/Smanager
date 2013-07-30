package com.search.manager.solr.dao.internal;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.dao.BaseDaoSolr;
import com.search.manager.solr.dao.ElevateDao;
import com.search.manager.solr.model.RuleSolrResult;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;

@Repository("elevateDaoSolr")
public class ElevateDaoSolrImpl extends BaseDaoSolr implements ElevateDao {

	private static final Logger logger = Logger
			.getLogger(ElevateDaoSolrImpl.class);

	@Override
	public List<ElevateResult> getElevateRules(Store store) throws DaoException {
		List<ElevateResult> elevateResults = new ArrayList<ElevateResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s",
					ClientUtils.escapeQueryChars(storeId)));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				elevateResults = SolrResultUtil.toElevateResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error("Failed to get elevate rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return elevateResults;
	}

	@Override
	public List<ElevateResult> getElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		List<ElevateResult> elevateResults = new ArrayList<ElevateResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String
					.format("store: %s AND keyword1: %s AND (expiryDate:{%s-1DAY TO *] OR (*:* AND -expiryDate:[* TO *]))",
							ClientUtils.escapeQueryChars(storeId), ClientUtils
									.escapeQueryChars(keyword), DateTime.now()
									.withZone(DateTimeZone.UTC)));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				elevateResults = SolrResultUtil.toElevateResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error("Failed to get elevate rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return elevateResults;
	}

	@Override
	public List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		List<ElevateResult> elevateResults = new ArrayList<ElevateResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String
					.format("store: %s AND keyword1: %s AND expiryDate:[* TO %s-1DAY]",
							ClientUtils.escapeQueryChars(storeId), ClientUtils
									.escapeQueryChars(keyword), DateTime.now()
									.withZone(DateTimeZone.UTC)));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				elevateResults = SolrResultUtil.toElevateResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error("Failed to get expired elevate rules by storeKeyword",
					e);
			throw new DaoException(e.getMessage(), e);
		}

		return elevateResults;
	}

	@Override
	public boolean loadElevateRules(Store store) throws DaoException {
		try {
			StoreKeyword storeKeyword = new StoreKeyword(store, null);
			ElevateResult elevateFilter = new ElevateResult();
			elevateFilter.setStoreKeyword(storeKeyword);
			int page = 1;

			while (true) {
				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
						elevateFilter, page, MAX_ROWS);
				RecordSet<ElevateResult> recordSet = daoService
						.getElevateResultListNew(criteria);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<ElevateResult> elevateResults = recordSet.getList();
					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocs(elevateResults);
					solrServers.getCoreInstance(
							Constants.Core.ELEVATE_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					if (elevateResults.size() < MAX_ROWS) {
						solrServers.getCoreInstance(
								Constants.Core.ELEVATE_RULE_CORE.getCoreName())
								.softCommit();
						return true;
					}
					page++;
				} else {
					if (page != 1) {
						solrServers.getCoreInstance(
								Constants.Core.ELEVATE_RULE_CORE.getCoreName())
								.softCommit();
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error(
					"Failed to load elevate rules by store." + e.getMessage(),
					e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		try {
			ElevateResult elevateFilter = new ElevateResult();
			elevateFilter.setStoreKeyword(storeKeyword);
			int page = 1;

			while (true) {
				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
						elevateFilter, page, MAX_ROWS);
				RecordSet<ElevateResult> recordSet = daoService
						.getElevateResultListNew(criteria);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<ElevateResult> elevateResults = recordSet.getList();
					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocs(elevateResults);
					solrServers.getCoreInstance(
							Constants.Core.ELEVATE_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					if (elevateResults.size() < MAX_ROWS) {
						solrServers.getCoreInstance(
								Constants.Core.ELEVATE_RULE_CORE.getCoreName())
								.softCommit();
						return true;
					}
					page++;
				} else {
					if (page != 1) {
						solrServers.getCoreInstance(
								Constants.Core.ELEVATE_RULE_CORE.getCoreName())
								.softCommit();
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error(
					"Failed to load elevate rules by storeKeyword."
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetElevateRules(Store store) throws DaoException {

		try {
			if (deleteElevateRules(store)) {
				return loadElevateRules(store);
			}
		} catch (Exception e) {
			logger.error("Failed to reset elevate rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetElevateRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			if (deleteElevateRules(storeKeyword)) {
				return loadElevateRules(storeKeyword);
			}
		} catch (Exception e) {
			logger.error("Failed to reset elevate rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public Map<String, Boolean> resetElevateRules(Store store,
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
				strQuery.append(String.format("store: %s AND keyword1: %s",
						ClientUtils.escapeQueryChars(storeId),
						ClientUtils.escapeQueryChars(key)));

				solrServers.getCoreInstance(
						Constants.Core.ELEVATE_RULE_CORE.getCoreName())
						.deleteByQuery(strQuery.toString());

				// retrieve new rules
				ElevateResult elevateFilter = new ElevateResult();
				elevateFilter.setStoreKeyword(new StoreKeyword(storeId, key));
				List<SolrInputDocument> solrInputDocuments = null;

				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
						elevateFilter, null, null, 0, 0);
				List<ElevateResult> elevateResults = daoService
						.getElevateResultList(criteria).getList();

				if (elevateResults != null && elevateResults.size() > 0) {
					try {
						solrInputDocuments = SolrDocUtil
								.composeSolrDocs(elevateResults);
					} catch (Exception e) {
						hasError = true;
						logger.error(
								"Failed to reset elevate rules by storeKeyword",
								e);
					}

					if (!hasError && solrInputDocuments != null
							&& solrInputDocuments.size() > 0) {
						try {
							solrServers.getCoreInstance(
									Constants.Core.ELEVATE_RULE_CORE
											.getCoreName()).addDocs(
									solrInputDocuments);
						} catch (Exception e) {
							logger.error(
									"Failed to reset elevate rules by storeKeyword",
									e);
							hasError = true;
						}
					}
				}

			} catch (Exception e) {
				logger.error("Failed to reset elevate rules by storeKeyword", e);
				hasError = true;
			}

			keywordStatus.put(keyword, !hasError);
		}

		try {
			solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.softCommit();
		} catch (Exception e) {
			logger.error("Failed to reset elevate rules by storeKeyword", e);
		}

		return keywordStatus;
	}

	@Override
	public boolean deleteElevateRules(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s",
					ClientUtils.escapeQueryChars(storeId)));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.softCommit();
			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete elevate rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteElevateRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND keyword1: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(keyword)));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete elevate rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateElevateRule(ElevateResult elevateResult)
			throws DaoException {

		if (elevateResult == null) {
			return false;
		}

		try {
			List<ElevateResult> elevateResults = new ArrayList<ElevateResult>();
			elevateResults.add(elevateResult);

			List<SolrInputDocument> solrInputDocuments = SolrDocUtil
					.composeSolrDocs(elevateResults);
			solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName()).addDocs(
					solrInputDocuments);
			solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.softCommit();
		} catch (Exception e) {
			logger.error("Failed to update elevate rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return true;
	}

	@Override
	public boolean commitElevateRule() throws DaoException {
		try {
			return commit(solrServers
					.getCoreInstance(Constants.Core.ELEVATE_RULE_CORE
							.getCoreName()));
		} catch (SolrServerException e) {
			logger.error("Failed to commit elevate rules", e);
			return false;
		}
	}

}
