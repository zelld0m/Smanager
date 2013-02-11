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
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.proxy.constants.Constants;

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
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			SolrQuery query = new SolrQuery();
			query.setQuery(strQuery.toString());
			logger.info(query.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.query(query);

			if (queryResponse != null) {
				elevateResults = SolrResultUtil.toElevateResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
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
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));
			strQuery.append(" AND keyword1:"
					+ ClientUtils.escapeQueryChars(keyword));

			SolrQuery query = new SolrQuery();
			query.setQuery(strQuery.toString());
			logger.info(query.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.ELEVATE_RULE_CORE.getCoreName())
					.query(query);

			if (queryResponse != null) {
				elevateResults = SolrResultUtil.toElevateResult(queryResponse
						.getBeans(RuleSolrResult.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return elevateResults;
	}

	@Override
	public boolean loadElevateRules(Store store) throws DaoException {
		List<String> keywords = null;
		List<Keyword> keywordList = (List<Keyword>) daoService.getAllKeywords(
				store.getStoreId(), RuleEntity.ELEVATE);

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
				ElevateResult elevateFilter = new ElevateResult();
				elevateFilter.setStoreKeyword(storeKeyword);

				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
						elevateFilter, null, null, 0, 0);
				List<ElevateResult> elevateResults = daoService
						.getElevateResultList(criteria).getList();

				List<SolrInputDocument> solrInputDocuments = null;
				boolean hasError = false;

				if (elevateResults != null && elevateResults.size() > 0) {
					try {
						solrInputDocuments = SolrDocUtil
								.composeSolrDocs(elevateResults);
					} catch (Exception e) {
						hasError = true;
						logger.error(e);
					}

					if (!hasError && solrInputDocuments != null
							&& solrInputDocuments.size() > 0) {
						try {
							solrServers.getCoreInstance(
									Constants.Core.ELEVATE_RULE_CORE
											.getCoreName()).addDocs(
									solrInputDocuments);
							solrServers.getCoreInstance(
									Constants.Core.ELEVATE_RULE_CORE
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
	public boolean loadElevateRules(StoreKeyword storeKeyword)
			throws DaoException {

		try {
			ElevateResult elevateFilter = new ElevateResult();
			elevateFilter.setStoreKeyword(storeKeyword);

			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
					elevateFilter, null, null, 0, 0);
			List<ElevateResult> elevateResults = daoService
					.getElevateResultList(criteria).getList();

			List<SolrInputDocument> solrInputDocuments = null;
			boolean hasError = false;

			if (elevateResults != null && elevateResults.size() > 0) {
				try {
					solrInputDocuments = SolrDocUtil
							.composeSolrDocs(elevateResults);
				} catch (Exception e) {
					hasError = true;
					logger.error(e);
				}

				if (!hasError && solrInputDocuments != null
						&& solrInputDocuments.size() > 0) {
					try {
						solrServers.getCoreInstance(
								Constants.Core.ELEVATE_RULE_CORE.getCoreName())
								.addDocs(solrInputDocuments);
						solrServers.getCoreInstance(
								Constants.Core.ELEVATE_RULE_CORE.getCoreName())
								.softCommit();
					} catch (Exception e) {
						logger.error(e);
						throw new DaoException(e.getMessage(), e);
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
	public boolean resetElevateRules(Store store) throws DaoException {

		try {
			if (deleteElevateRules(store)) {
				return loadElevateRules(store);
			}
		} catch (Exception e) {
			logger.error(e);
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
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteElevateRules(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

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
			logger.error(e);
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
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND keyword1:"
							+ ClientUtils.escapeQueryChars(keyword));

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
			logger.error(e);
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
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return true;
	}

}
