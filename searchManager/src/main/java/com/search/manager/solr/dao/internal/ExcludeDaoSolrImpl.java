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
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.dao.BaseDaoSolr;
import com.search.manager.solr.dao.ExcludeDao;
import com.search.manager.solr.model.RuleSolrResult;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository("excludeDaoSolr")
public class ExcludeDaoSolrImpl extends BaseDaoSolr implements ExcludeDao {

    private static final Logger logger =
            LoggerFactory.getLogger(ExcludeDaoSolrImpl.class);

    @Override
    public List<ExcludeResult> getExcludeRules(Store store) throws DaoException {
        List<ExcludeResult> excludeResults = new ArrayList<ExcludeResult>();

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
                    Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).query(
                    solrQuery);

            if (queryResponse != null) {
                excludeResults = SolrResultUtil.toExcludeResult(queryResponse
                        .getBeans(RuleSolrResult.class));
            }
        } catch (Exception e) {
            logger.error("Failed to get exclude rules by store", e);
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
                    Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).query(
                    solrQuery);

            if (queryResponse != null) {
                excludeResults = SolrResultUtil.toExcludeResult(queryResponse
                        .getBeans(RuleSolrResult.class));
            }
        } catch (Exception e) {
            logger.error("Failed to get exclude rules by storeKeyword", e);
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
                    Constants.Core.EXCLUDE_RULE_CORE.getCoreName()).query(
                    solrQuery);

            if (queryResponse != null) {
                excludeResults = SolrResultUtil.toExcludeResult(queryResponse
                        .getBeans(RuleSolrResult.class));
            }
        } catch (Exception e) {
            logger.error("Failed to get expired exclude rules by storeKeyword",
                    e);
            throw new DaoException(e.getMessage(), e);
        }

        return excludeResults;
    }

    @Override
    public boolean loadExcludeRules(Store store) throws DaoException {
        try {
            StoreKeyword storeKeyword = new StoreKeyword(store, null);
            ExcludeResult excludeFilter = new ExcludeResult();
            excludeFilter.setStoreKeyword(storeKeyword);
            int page = 1;

            while (true) {
                SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
                        excludeFilter, page, MAX_ROWS);
                RecordSet<ExcludeResult> recordSet = daoService
                        .getExcludeResultListNew(criteria);

                if (recordSet != null && recordSet.getTotalSize() > 0) {
                    List<ExcludeResult> excludeResults = recordSet.getList();
                    List<SolrInputDocument> solrInputDocuments = SolrDocUtil
                            .composeSolrDocs(excludeResults);
                    solrServers.getCoreInstance(
                            Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                            .addDocs(solrInputDocuments);
                    if (excludeResults.size() < MAX_ROWS) {
                        solrServers.getCoreInstance(
                                Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    page++;
                } else {
                    if (page != 1) {
                        solrServers.getCoreInstance(
                                Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(
                    "Failed to load exclude rules by store." + e.getMessage(),
                    e);
            throw new DaoException(e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean loadExcludeRules(StoreKeyword storeKeyword)
            throws DaoException {

        try {
            ExcludeResult excludeFilter = new ExcludeResult();
            excludeFilter.setStoreKeyword(storeKeyword);
            int page = 1;

            while (true) {
                SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
                        excludeFilter, page, MAX_ROWS);
                RecordSet<ExcludeResult> recordSet = daoService
                        .getExcludeResultListNew(criteria);

                if (recordSet != null && recordSet.getTotalSize() > 0) {
                    List<ExcludeResult> excludeResults = recordSet.getList();
                    List<SolrInputDocument> solrInputDocuments = SolrDocUtil
                            .composeSolrDocs(excludeResults);
                    solrServers.getCoreInstance(
                            Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                            .addDocs(solrInputDocuments);
                    if (excludeResults.size() < MAX_ROWS) {
                        solrServers.getCoreInstance(
                                Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    page++;
                } else {
                    if (page != 1) {
                        solrServers.getCoreInstance(
                                Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                                .softCommit();
                        return true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(
                    "Failed to load exclude rules by storeKeyword."
                    + e.getMessage(), e);
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
            logger.error("Failed to reset exclude rules by store", e);
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
            logger.error("Failed to reset exclude rules by storeKeyword", e);
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
                strQuery.append(String.format("store: %s AND keyword1: %s",
                        ClientUtils.escapeQueryChars(storeId),
                        ClientUtils.escapeQueryChars(key)));

                solrServers.getCoreInstance(
                        Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                        .deleteByQuery(strQuery.toString());

                // retrieve new rules
                ExcludeResult excludeFilter = new ExcludeResult();
                excludeFilter.setStoreKeyword(new StoreKeyword(storeId, key));
                List<SolrInputDocument> solrInputDocuments = null;

                SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
                        excludeFilter, null, null, 0, 0);
                List<ExcludeResult> excludeResults = daoService
                        .getExcludeResultList(criteria).getList();

                if (excludeResults != null && excludeResults.size() > 0) {
                    try {
                        solrInputDocuments = SolrDocUtil
                                .composeSolrDocs(excludeResults);
                    } catch (Exception e) {
                        hasError = true;
                        logger.error(
                                "Failed to reset exclude rules by storeKeyword",
                                e);
                    }

                    if (!hasError && solrInputDocuments != null
                            && solrInputDocuments.size() > 0) {
                        try {
                            solrServers.getCoreInstance(
                                    Constants.Core.EXCLUDE_RULE_CORE
                                    .getCoreName()).addDocs(
                                    solrInputDocuments);
                        } catch (Exception e) {
                            logger.error(
                                    "Failed to reset exclude rules by storeKeyword",
                                    e);
                            hasError = true;
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("Failed to reset exclude rules by storeKeyword", e);
                hasError = true;
            }

            keywordStatus.put(keyword, !hasError);
        }

        try {
            solrServers.getCoreInstance(
                    Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                    .softCommit();
        } catch (Exception e) {
            logger.error("Failed to reset exclude rules by storeKeyword", e);
        }

        return keywordStatus;
    }

    @Override
    public boolean deleteExcludeRules(Store store) throws DaoException {

        try {
            String storeId = StringUtils.lowerCase(StringUtils.trim(store
                    .getStoreId()));

            StringBuffer strQuery = new StringBuffer();
            strQuery.append(String.format("store: %s",
                    ClientUtils.escapeQueryChars(storeId)));

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
            logger.error("Failed to delete exclude rules by store", e);
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
            strQuery.append(String.format("store: %s AND keyword1: %s",
                    ClientUtils.escapeQueryChars(storeId),
                    ClientUtils.escapeQueryChars(keyword)));

            UpdateResponse updateResponse = solrServers.getCoreInstance(
                    Constants.Core.EXCLUDE_RULE_CORE.getCoreName())
                    .deleteByQuery(strQuery.toString());

            if (updateResponse.getStatus() == 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to delete exclude rules by store", e);
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
            logger.error("Failed to update exclude rules by storeKeyword", e);
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
            logger.error("Failed to commit exclude rules", e);
            return false;
        }
    }
}
