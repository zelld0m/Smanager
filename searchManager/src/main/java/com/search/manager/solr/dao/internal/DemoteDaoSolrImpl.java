package com.search.manager.solr.dao.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.search.manager.core.model.Store;
import com.search.manager.dao.DaoException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.dao.BaseDaoSolr;
import com.search.manager.solr.dao.DemoteDao;
import com.search.manager.solr.model.RuleSolrResult;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;

@Repository("demoteDaoSolr")
public class DemoteDaoSolrImpl extends BaseDaoSolr implements DemoteDao {

    private static final Logger logger =
            LoggerFactory.getLogger(DemoteDaoSolrImpl.class);

    @Override
    public List<DemoteResult> getDemoteRules(Store store) throws DaoException {
        List<DemoteResult> demoteResults = new ArrayList<DemoteResult>();

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
                    Constants.Core.DEMOTE_RULE_CORE.getCoreName()).query(
                    solrQuery);

            if (queryResponse != null) {
                demoteResults = SolrResultUtil.toDemoteResult(queryResponse
                        .getBeans(RuleSolrResult.class));
            }
        } catch (Exception e) {
            logger.error("Failed to get demote rules by store", e);
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
                    Constants.Core.DEMOTE_RULE_CORE.getCoreName()).query(
                    solrQuery);

            if (queryResponse != null) {
                demoteResults = SolrResultUtil.toDemoteResult(queryResponse
                        .getBeans(RuleSolrResult.class));
            }
        } catch (Exception e) {
            logger.error("Failed to get demote rules by storeKeyword", e);
            throw new DaoException(e.getMessage(), e);
        }

        return demoteResults;
    }

    @Override
    public List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        List<DemoteResult> demoteResults = new ArrayList<DemoteResult>();

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
            QueryResponse queryResponse = solrServers.getCoreInstance(
                    Constants.Core.DEMOTE_RULE_CORE.getCoreName()).query(
                    solrQuery);

            if (queryResponse != null) {
                demoteResults = SolrResultUtil.toDemoteResult(queryResponse
                        .getBeans(RuleSolrResult.class));
            }
        } catch (Exception e) {
            logger.error("Failed to get expired demote rules by storeKeyword",
                    e);
            throw new DaoException(e.getMessage(), e);
        }

        return demoteResults;
    }

    @Override
    public boolean loadDemoteRules(Store store) throws DaoException {
        try {
            StoreKeyword storeKeyword = new StoreKeyword(store, null);
            DemoteResult demoteFilter = new DemoteResult();
            demoteFilter.setStoreKeyword(storeKeyword);
            int page = 1;

            while (true) {
                SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
                        demoteFilter, page, MAX_ROWS);
                RecordSet<DemoteResult> recordSet = daoService
                        .getDemoteResultListNew(criteria);

                if (recordSet != null && recordSet.getTotalSize() > 0) {
                    List<DemoteResult> demoteResults = recordSet.getList();
                    List<SolrInputDocument> solrInputDocuments = SolrDocUtil
                            .composeSolrDocs(demoteResults);
                    solrServers.getCoreInstance(
                            Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                            .addDocs(solrInputDocuments);
                    if (demoteResults.size() < MAX_ROWS) {
                        solrServers.getCoreInstance(
                                Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    page++;
                } else {
                    if (page != 1) {
                        solrServers.getCoreInstance(
                                Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(
                    "Failed to load demote rules by store." + e.getMessage(), e);
            throw new DaoException(e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean loadDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        try {
            DemoteResult demoteFilter = new DemoteResult();
            demoteFilter.setStoreKeyword(storeKeyword);
            int page = 1;

            while (true) {
                SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
                        demoteFilter, page, MAX_ROWS);
                RecordSet<DemoteResult> recordSet = daoService
                        .getDemoteResultListNew(criteria);

                if (recordSet != null && recordSet.getTotalSize() > 0) {
                    List<DemoteResult> demoteResults = recordSet.getList();
                    List<SolrInputDocument> solrInputDocuments = SolrDocUtil
                            .composeSolrDocs(demoteResults);
                    solrServers.getCoreInstance(
                            Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                            .addDocs(solrInputDocuments);
                    if (demoteResults.size() < MAX_ROWS) {
                        solrServers.getCoreInstance(
                                Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    page++;
                } else {
                    if (page != 1) {
                        solrServers.getCoreInstance(
                                Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load demote rules by storeKeyword", e);
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
            logger.error("Failed to reset demote rules by store", e);
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
            logger.error("Failed to reset demote rules by storeKeyword", e);
            throw new DaoException(e.getMessage(), e);
        }

        return false;
    }

    @Override
    public Map<String, Boolean> resetDemoteRules(Store store,
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
                        Constants.Core.DEMOTE_RULE_CORE.getCoreName())
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
                        logger.error(
                                "Failed to load demote rules by storeKeyword",
                                e);
                    }

                    if (!hasError && solrInputDocuments != null
                            && solrInputDocuments.size() > 0) {
                        try {
                            solrServers.getCoreInstance(
                                    Constants.Core.DEMOTE_RULE_CORE
                                    .getCoreName()).addDocs(
                                    solrInputDocuments);
                        } catch (Exception e) {
                            logger.error(
                                    "Failed to load demote rules by storeKeyword",
                                    e);
                            hasError = true;
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("Failed to load demote rules by storeKeyword", e);
                hasError = true;
            }

            keywordStatus.put(keyword, !hasError);
        }

        try {
            solrServers.getCoreInstance(
                    Constants.Core.DEMOTE_RULE_CORE.getCoreName()).softCommit();
        } catch (Exception e) {
            logger.error("Failed to load demote rules by storeKeyword", e);
        }

        return keywordStatus;
    }

    @Override
    public boolean deleteDemoteRules(Store store) throws DaoException {

        try {
            String storeId = StringUtils.lowerCase(StringUtils.trim(store
                    .getStoreId()));

            StringBuffer strQuery = new StringBuffer();
            strQuery.append(String.format("store: %s",
                    ClientUtils.escapeQueryChars(storeId)));

            UpdateResponse updateResponse = solrServers.getCoreInstance(
                    Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                    .deleteByQuery(strQuery.toString());

            if (updateResponse.getStatus() == 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to delete demote rules by store", e);
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
            strQuery.append(String.format("store: %s AND keyword1: %s",
                    ClientUtils.escapeQueryChars(storeId),
                    ClientUtils.escapeQueryChars(keyword)));

            UpdateResponse updateResponse = solrServers.getCoreInstance(
                    Constants.Core.DEMOTE_RULE_CORE.getCoreName())
                    .deleteByQuery(strQuery.toString());

            if (updateResponse.getStatus() == 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to delete demote rules by storeKeyword", e);
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
            logger.error("Failed to update demote rules by storeKeyword", e);
            throw new DaoException(e.getMessage(), e);
        }

        return true;
    }

    @Override
    public boolean commitDemoteRule() {
        try {
            return commit(solrServers
                    .getCoreInstance(Constants.Core.DEMOTE_RULE_CORE
                    .getCoreName()));
        } catch (SolrServerException e) {
            logger.error("Failed to commit demote rules", e);
            return false;
        }
    }
}