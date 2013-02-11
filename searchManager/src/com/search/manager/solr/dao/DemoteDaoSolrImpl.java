package com.search.manager.solr.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Repository;

import com.mall.model.RuleSolrResult;
import com.mall.util.SolrDocUtil;
import com.mall.util.SolrResultUtil;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.proxy.constants.Constants;

@Repository("demoteDaoSolr")
public class DemoteDaoSolrImpl extends BaseDaoSolr implements DemoteDao {

	private static final Logger logger = Logger
			.getLogger(DemoteDaoSolrImpl.class);

	@Override
	public List<DemoteResult> getDemoteRules(Store store) throws DaoException {
		List<DemoteResult> demoteResults = new ArrayList<DemoteResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			SolrQuery query = new SolrQuery();
			query.setQuery(strQuery.toString());
			logger.info(query.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName()).query(query);

			if (queryResponse != null) {
				demoteResults = SolrResultUtil.toDemoteResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return demoteResults;
	}

	@Override
	public List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {
		List<DemoteResult> demoteResults = new ArrayList<DemoteResult>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));
			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));
			strQuery.append(" AND keyword1:"
					+ ClientUtils.escapeQueryChars(keyword));

			SolrQuery query = new SolrQuery();
			query.setQuery(strQuery.toString());
			logger.info(query.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName()).query(query);

			if (queryResponse != null) {
				demoteResults = SolrResultUtil.toDemoteResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return demoteResults;
	}

	@Override
	public boolean loadDemoteRules(Store store) throws DaoException {
		List<String> keywords = null;
		List<Keyword> keywordList = (List<Keyword>) daoService.getAllKeywords(
				store.getStoreId(), RuleEntity.DEMOTE);

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
				DemoteResult demoteFilter = new DemoteResult();
				demoteFilter.setStoreKeyword(storeKeyword);

				SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
						demoteFilter, null, null, 0, 0);
				List<DemoteResult> demoteResults = daoService
						.getDemoteResultList(criteria).getList();

				List<SolrInputDocument> solrInputDocuments = null;
				boolean hasError = false;

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
									Constants.Core.DEMOTE_RULE_CORE
											.getCoreName()).addDocs(
									solrInputDocuments);
							solrServers.getCoreInstance(
									Constants.Core.DEMOTE_RULE_CORE
											.getCoreName()).softCommit();
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
	public boolean loadDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			DemoteResult demoteFilter = new DemoteResult();
			demoteFilter.setStoreKeyword(storeKeyword);
			List<SolrInputDocument> solrInputDocuments = null;
			boolean hasError = false;

			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
					demoteFilter, null, null, 0, 0);
			List<DemoteResult> demoteResults = daoService.getDemoteResultList(
					criteria).getList();

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
								Constants.Core.DEMOTE_RULE_CORE.getCoreName())
								.addDocs(solrInputDocuments);
						solrServers.getCoreInstance(
								Constants.Core.DEMOTE_RULE_CORE.getCoreName())
								.softCommit();
					} catch (Exception e) {
						logger.error(e);
						hasError = true;
					}
				}

				return !hasError;
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetDemoteRules(Store store) throws DaoException {

		try {
			if (deleteDemoteRules(store)) {
				return loadDemoteRules(store);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			if (deleteDemoteRules(storeKeyword)) {
				return loadDemoteRules(storeKeyword);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteDemoteRules(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName()).softCommit();
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
	public boolean deleteDemoteRules(StoreKeyword storeKeyword)
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
					Constants.Core.DEMOTE_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName()).softCommit();
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
	public boolean updateDemoteRule(DemoteResult demoteResult)
			throws DaoException {

		if (demoteResult == null) {
			return false;
		}

		try {
			List<DemoteResult> demoteResults = new ArrayList<DemoteResult>();
			demoteResults.add(demoteResult);

			List<SolrInputDocument> solrInputDocuments = SolrDocUtil
					.composeSolrDocs(demoteResults);
			solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName()).addDocs(
					solrInputDocuments);
			solrServers.getCoreInstance(
					Constants.Core.DEMOTE_RULE_CORE.getCoreName()).softCommit();
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return true;
	}

}