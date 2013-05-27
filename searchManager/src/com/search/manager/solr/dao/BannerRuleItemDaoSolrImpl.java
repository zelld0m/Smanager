package com.search.manager.solr.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;

import com.search.manager.dao.DaoException;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.model.BannerRuleItemSolr;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;

public class BannerRuleItemDaoSolrImpl extends BaseDaoSolr implements
		BannerRuleItemDao {

	private static final Logger logger = Logger
			.getLogger(BannerRuleItemDaoSolrImpl.class);

	@Override
	public List<BannerRuleItem> getBannerRuleItems(Store store)
			throws DaoException {
		List<BannerRuleItem> bannerRuleItems = new ArrayList<BannerRuleItem>();

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				bannerRuleItems = SolrResultUtil.toBannerRuleItem(queryResponse
						.getBeans(BannerRuleItemSolr.class));
			}
		} catch (Exception e) {
			logger.error(
					"Failed to get banner rule items by store. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return bannerRuleItems;
	}

	@Override
	public BannerRuleItem getBannerRuleItemMemberId(Store store, String memberId)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND memberId:"
							+ ClientUtils.escapeQueryChars(memberId));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<BannerRuleItem> bannerRuleItems = SolrResultUtil
						.toBannerRuleItem(queryResponse
								.getBeans(BannerRuleItemSolr.class));

				if (bannerRuleItems != null && bannerRuleItems.size() > 0) {
					return bannerRuleItems.get(0);
				}
			}
		} catch (Exception e) {
			logger.error(
					"Failed to get banner rule items by store and memberId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public BannerRuleItem getBannerRuleItemRuleName(Store store, String ruleName)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND ruleName:"
							+ ClientUtils.escapeQueryChars(ruleName));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<BannerRuleItem> bannerRuleItems = SolrResultUtil
						.toBannerRuleItem(queryResponse
								.getBeans(BannerRuleItemSolr.class));

				if (bannerRuleItems != null && bannerRuleItems.size() > 0) {
					return bannerRuleItems.get(0);
				}
			}
		} catch (Exception e) {
			logger.error(
					"Failed to get banner rule item by store and ruleName. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public boolean loadBannerRuleItems(Store store) throws DaoException {

		try {
			BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
			BannerRule bannerRule = new BannerRule();
			bannerRule.setStoreId(store.getStoreId());
			bannerRuleItemFilter.setRule(bannerRule);
			int page = 1;

			while (true) {
				SearchCriteria<BannerRuleItem> searchCriteria = new SearchCriteria<BannerRuleItem>(
						bannerRuleItemFilter, page, MAX_ROWS);

				RecordSet<BannerRuleItem> recordSet = daoService
						.searchBannerRuleItem(searchCriteria);
				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<BannerRuleItem> bannerRuleItems = recordSet.getList();
					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocs(bannerRuleItems);
					solrServers.getCoreInstance(
							Constants.Core.BANNER_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);

					if (bannerRuleItems.size() < MAX_ROWS) {
						solrServers.getCoreInstance(
								Constants.Core.BANNER_RULE_CORE.getCoreName())
								.commit();
						return true;
					}
					page++;
				} else {
					if (page != 1) {
						solrServers.getCoreInstance(
								Constants.Core.BANNER_RULE_CORE.getCoreName())
								.commit();
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error(
					"Failed to load banner rule item by store. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadBannerRuleItem(Store store, String memberId)
			throws DaoException {

		try {
			BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
			BannerRule bannerRule = new BannerRule();
			bannerRule.setStoreId(store.getStoreId());
			bannerRuleItemFilter.setRule(bannerRule);
			bannerRuleItemFilter.setMemberId(memberId);
			int page = 1;

			while (true) {
				SearchCriteria<BannerRuleItem> searchCriteria = new SearchCriteria<BannerRuleItem>(
						bannerRuleItemFilter, page, MAX_ROWS);

				RecordSet<BannerRuleItem> recordSet = daoService
						.searchBannerRuleItem(searchCriteria);
				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<BannerRuleItem> bannerRuleItems = recordSet.getList();
					List<SolrInputDocument> solrInputDocuments = SolrDocUtil
							.composeSolrDocs(bannerRuleItems);
					solrServers.getCoreInstance(
							Constants.Core.BANNER_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);

					if (bannerRuleItems.size() < MAX_ROWS) {
						solrServers.getCoreInstance(
								Constants.Core.BANNER_RULE_CORE.getCoreName())
								.softCommit();
						return true;
					}
					page++;
				} else {
					if (page != 1) {
						solrServers.getCoreInstance(
								Constants.Core.BANNER_RULE_CORE.getCoreName())
								.softCommit();
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error(
					"Failed to load banner rule item by store and memberId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetBannerRuleItems(Store store) throws DaoException {
		try {
			if (deleteBannerRuleItems(store)) {
				return loadBannerRuleItems(store);
			}
		} catch (Exception e) {
			logger.error(
					"Failed to reset banner rule items by store. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetBannerRuleItem(Store store, String memberId)
			throws DaoException {
		try {
			if (deleteBannerRuleItem(store, memberId)) {
				return loadBannerRuleItem(store, memberId);
			}
		} catch (Exception e) {
			logger.error(
					"Failed to reset banner rule item by store and memberId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteBannerRuleItems(Store store) throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + storeId);

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(
					"Failed to delete banner rule items by store. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteBannerRuleItem(Store store, String memberId)
			throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("storeId:" + ClientUtils.escapeQueryChars(storeId))
					.append(" AND memberId:"
							+ ClientUtils.escapeQueryChars(memberId));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).softCommit();

			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(
					"Failed to delete banner rule items by store and memberId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateBannerRuleItem(BannerRuleItem bannerRuleItem)
			throws DaoException {

		if (bannerRuleItem == null) {
			return false;
		}

		try {
			SolrInputDocument solrInputDocument = SolrDocUtil
					.composeSolrDoc(bannerRuleItem);
			if (solrInputDocument != null) {
				solrServers.getCoreInstance(
						Constants.Core.BANNER_RULE_CORE.getCoreName()).addDoc(
						solrInputDocument);
				solrServers.getCoreInstance(
						Constants.Core.BANNER_RULE_CORE.getCoreName())
						.softCommit();
				return true;
			}
		} catch (Exception e) {
			logger.error(
					"Failed to update banner rule item. " + e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean commitBannerRuleItem() throws DaoException {
		try {
			return commit(solrServers
					.getCoreInstance(Constants.Core.BANNER_RULE_CORE
							.getCoreName()));
		} catch (SolrServerException e) {
			logger.error(
					"Failed to commit banner rule items. " + e.getMessage(), e);
			return false;
		}
	}

}
