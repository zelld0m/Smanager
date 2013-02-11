package com.search.manager.solr.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Repository;

import com.mall.model.FacetSortRuleSolr;
import com.mall.util.SolrDocUtil;
import com.mall.util.SolrResultUtil;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleType;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.proxy.constants.Constants;

@Repository("facetSortDaoSolr")
public class FacetSortDaoSolrImpl extends BaseDaoSolr implements FacetSortDao {

	private static final Logger logger = Logger
			.getLogger(FacetSortDaoSolrImpl.class);

	@Override
	public List<FacetSort> getFacetSortRules(Store store) throws DaoException {
		List<FacetSort> facetSorts = new ArrayList<FacetSort>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.info(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				facetSorts = SolrResultUtil.toFacetSortRule(queryResponse
						.getBeans(FacetSortRuleSolr.class));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return facetSorts;
	}

	@Override
	public FacetSort getFacetSortRule(Store store, String name,
			RuleType ruleType) throws DaoException {
		FacetSort facetSort = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			String strRuleType = StringUtils.lowerCase(StringUtils
					.trim(ruleType.getDescription()));
			name = StringUtils.lowerCase(StringUtils.trim(name));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND ruleName1:"
							+ ClientUtils.escapeQueryChars(name))
					.append(" AND ruleType1:"
							+ ClientUtils.escapeQueryChars(strRuleType));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.info(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<FacetSort> facetSorts = SolrResultUtil
						.toFacetSortRule(queryResponse
								.getBeans(FacetSortRuleSolr.class));

				if (facetSorts != null && facetSorts.size() > 0) {
					facetSort = facetSorts.get(0);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return facetSort;
	}

	@Override
	public FacetSort getFacetSortRuleById(Store store, String id)
			throws DaoException {
		FacetSort facetSort = null;

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			id = StringUtils.trim(id);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND facetSortId:"
							+ ClientUtils.escapeQueryChars(id));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(strQuery.toString());
			logger.info(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<FacetSort> facetSorts = SolrResultUtil
						.toFacetSortRule(queryResponse
								.getBeans(FacetSortRuleSolr.class));

				if (facetSorts != null && facetSorts.size() > 0) {
					facetSort = facetSorts.get(0);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return facetSort;
	}

	@Override
	public boolean loadFacetSortRules(Store store) throws DaoException {

		try {
			List<FacetSort> facetSorts = null;

			FacetSort facetSortFilter = new FacetSort();
			facetSortFilter.setStore(store);
			SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(
					facetSortFilter, null, null, 0, 0);

			RecordSet<FacetSort> recordSet = daoService.searchFacetSort(
					criteria, null);

			if (recordSet != null && recordSet.getTotalSize() > 0) {
				facetSorts = recordSet.getList();
				List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
				solrInputDocuments.addAll(SolrDocUtil
						.composeSolrDocsFacetSort(facetSorts));
				if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
					solrServers.getCoreInstance(
							Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
							.commit();
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadFacetSortRuleByName(Store store, String name)
			throws DaoException {

		try {
			List<FacetSort> facetSorts = null;
			name = StringUtils.trim(name);

			FacetSort facetSortFilter = new FacetSort();
			facetSortFilter.setStore(store);
			facetSortFilter.setRuleName(name);
			SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(
					facetSortFilter, null, null, 0, 0);

			RecordSet<FacetSort> recordSet = daoService.searchFacetSort(
					criteria, MatchType.LIKE_NAME);

			if (recordSet != null && recordSet.getTotalSize() > 0) {
				facetSorts = recordSet.getList();
				List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
				solrInputDocuments.addAll(SolrDocUtil
						.composeSolrDocsFacetSort(facetSorts));
				if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
					solrServers.getCoreInstance(
							Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
							.commit();
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadFacetSortRuleById(Store store, String id)
			throws DaoException {

		try {
			List<FacetSort> facetSorts = null;
			id = StringUtils.trim(id);

			FacetSort facetSortFilter = new FacetSort();
			facetSortFilter.setStore(store);
			facetSortFilter.setRuleId(id);
			SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(
					facetSortFilter, null, null, 0, 0);

			RecordSet<FacetSort> recordSet = daoService.searchFacetSort(
					criteria, MatchType.MATCH_ID);

			if (recordSet != null && recordSet.getTotalSize() > 0) {
				facetSorts = recordSet.getList();
				List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
				solrInputDocuments.addAll(SolrDocUtil
						.composeSolrDocsFacetSort(facetSorts));
				if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
					solrServers.getCoreInstance(
							Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
							.commit();
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetFacetSortRules(Store store) throws DaoException {

		try {
			if (deleteFacetSortRules(store)) {
				return loadFacetSortRules(store);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetFacetSortRulesByName(Store store, String name)
			throws DaoException {

		try {
			if (deleteFacetSortRuleByName(store, name)) {
				return loadFacetSortRuleByName(store, name);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetFacetSortRulesById(Store store, String id)
			throws DaoException {

		try {
			if (deleteFacetSortRuleById(store, id)) {
				return loadFacetSortRuleById(store, id);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteFacetSortRules(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
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
	public boolean deleteFacetSortRuleByName(Store store, String name)
			throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			name = StringUtils.lowerCase(StringUtils.trim(name));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND ruleName1:"
							+ ClientUtils.escapeQueryChars(name));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
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
	public boolean deleteFacetSortRuleById(Store store, String id)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			id = StringUtils.trim(id);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND facetSortId:"
							+ ClientUtils.escapeQueryChars(id));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
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
	public boolean updateFacetSortRule(FacetSort facetSort) throws DaoException {

		if (facetSort == null) {
			return false;
		}

		try {
			SolrInputDocument solrInputDocument = SolrDocUtil
					.composeSolrDoc(facetSort);
			if (solrInputDocument != null) {
				solrServers.getCoreInstance(
						Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
						.addDoc(solrInputDocument);
				solrServers.getCoreInstance(
						Constants.Core.FACET_SORT_RULE_CORE.getCoreName())
						.softCommit();
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

}