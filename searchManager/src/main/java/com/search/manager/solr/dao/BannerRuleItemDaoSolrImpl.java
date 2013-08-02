package com.search.manager.solr.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;

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

@Repository("bannerRuleItemDaoSolr")
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
			strQuery.append(String.format("store: %s",
					ClientUtils.escapeQueryChars(storeId)));

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
	public List<BannerRuleItem> getBannerRuleItemsByRuleId(Store store,
			String ruleId) throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND ruleId: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(ruleId))));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			solrQuery.setSortField("priority", ORDER.asc);
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<BannerRuleItem> bannerRuleItems = SolrResultUtil
						.toBannerRuleItem(queryResponse
								.getBeans(BannerRuleItemSolr.class));

				return bannerRuleItems;
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
	public List<BannerRuleItem> getBannerRuleItemsByRuleName(Store store,
			String ruleName) throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND ruleName1: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(ruleName))));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			solrQuery.setSortField("priority", ORDER.asc);
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<BannerRuleItem> bannerRuleItems = SolrResultUtil
						.toBannerRuleItem(queryResponse
								.getBeans(BannerRuleItemSolr.class));

				return bannerRuleItems;
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
	public List<BannerRuleItem> getActiveBannerRuleItemsByRuleName(Store store,
			String ruleName, DateTime startDate, DateTime endDate)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND ruleName1: %s AND disabled: false",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(ruleName))));

			if (startDate != null) {
				strQuery.append(String.format(" AND startDate:[* TO %s]",
						startDate.withZone(DateTimeZone.UTC)));
			}

			if (endDate != null) {
				strQuery.append(String.format(" AND endDate:{%s-1DAY TO *]",
						endDate.withZone(DateTimeZone.UTC)));
			}

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			solrQuery.setSortField("priority", ORDER.asc);
			QueryResponse queryResponse = solrServers.getCoreInstance(
					Constants.Core.BANNER_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				List<BannerRuleItem> bannerRuleItems = SolrResultUtil
						.toBannerRuleItem(queryResponse
								.getBeans(BannerRuleItemSolr.class));

				return bannerRuleItems;
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
	public BannerRuleItem getBannerRuleItemByMemberId(Store store,
			String memberId) throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND memberId: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(memberId))));

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
	public boolean loadBannerRuleItemsByRuleId(Store store, String ruleId)
			throws DaoException {
		try {
			BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
			BannerRule bannerRule = new BannerRule();
			bannerRule.setStoreId(store.getStoreId());
			bannerRule.setRuleId(ruleId);
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
					"Failed to load banner rule item by store and ruleId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadBannerRuleItemsByRuleName(Store store, String ruleName)
			throws DaoException {
		try {
			BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
			BannerRule bannerRule = new BannerRule();
			bannerRule.setStoreId(store.getStoreId());
			bannerRule.setRuleName(ruleName);
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
					"Failed to load banner rule item by store and ruleName. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean loadBannerRuleItemByMemberId(Store store, String memberId)
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
	public boolean resetBannerRuleItemsByRuleId(Store store, String ruleId)
			throws DaoException {
		try {
			if (deleteBannerRuleItemsByRuleId(store, ruleId)) {
				return loadBannerRuleItemsByRuleId(store, ruleId);
			}
		} catch (Exception e) {
			logger.error(
					"Failed to reset banner rule items by store and ruleId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetBannerRuleItemsByRuleName(Store store, String ruleName)
			throws DaoException {
		try {
			if (deleteBannerRuleItemsByRuleName(store, ruleName)) {
				return loadBannerRuleItemsByRuleName(store, ruleName);
			}
		} catch (Exception e) {
			logger.error(
					"Failed to reset banner rule items by store and ruleName. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetBannerRuleItemByMemberId(Store store, String memberId)
			throws DaoException {
		try {
			if (deleteBannerRuleItemByMemberId(store, memberId)) {
				return loadBannerRuleItemByMemberId(store, memberId);
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
			strQuery.append(String.format("store: %s",
					ClientUtils.escapeQueryChars(storeId)));

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
	public boolean deleteBannerRuleItemsByRuleId(Store store, String ruleId)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			ruleId = StringUtils.trim(ruleId);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND ruleId: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(ruleId))));

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
					"Failed to delete banner rule items by store and ruleId. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteBannerRuleItemsByRuleName(Store store, String ruleName)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));
			ruleName = StringUtils.lowerCase(StringUtils.trim(ruleName));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND ruleName1: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(ruleName))));

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
					"Failed to delete banner rule items by store and ruleName. "
							+ e.getMessage(), e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteBannerRuleItemByMemberId(Store store, String memberId)
			throws DaoException {

		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append(String.format("store: %s AND memberId: %s",
					ClientUtils.escapeQueryChars(storeId),
					ClientUtils.escapeQueryChars(StringUtils.trim(memberId))));

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
