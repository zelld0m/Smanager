package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.file.SpellRuleDAO;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.response.ServiceResponse;
import com.search.manager.utility.CsvTransformer;
import com.search.manager.utility.FileTransferUtils;
import com.search.manager.xml.file.SpellIndex;

@Service(value = "spellRuleService")
@RemoteProxy(name = "SpellRuleServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "spellRuleService"))
public class SpellRuleService {

    private static final Logger logger = LoggerFactory.getLogger(SpellRuleService.class);

    @Autowired
    private SpellIndex spellIndex;

    @Autowired
    private SpellRuleDAO spellRuleDAO;

    @RemoteMethod
    public ServiceResponse<RecordSet<SpellRule>> getSpellRule(String ruleId, String searchTerm, String suggestion,
            String status, int pageNumber, int itemsPerPage) {
        ServiceResponse<RecordSet<SpellRule>> response = new ServiceResponse<RecordSet<SpellRule>>();

        try {
            SpellRule rule = new SpellRule(ruleId, UtilityService.getStoreId());

            if (StringUtils.isNotEmpty(searchTerm))
                rule.setSearchTerms(new String[] { searchTerm });

            if (StringUtils.isNotEmpty(suggestion))
                rule.setSuggestions(new String[] { suggestion });

            if (StringUtils.isNotEmpty(status))
                rule.setStatus(status);

            response.success(spellRuleDAO.getSpellRule(new SearchCriteria<SpellRule>(rule, pageNumber, itemsPerPage)));
        } catch (DaoException e) {
            logger.error("Failed during getSpellRule()", e);
            response.error("Failed to retrieve Did You Mean rules.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> addSpellRuleBatch(SpellRule[] spellRules) {
        int errorLevel = 0;
        String store = UtilityService.getStoreId();
        ServiceResponse<Void> response = new ServiceResponse<Void>();

        try {
            // Check for duplicate search terms.
            List<String> duplicates = null;

            try {
                duplicates = checkDuplicatedSearchTerms(store, spellRules, true);
            } catch (DaoException e) {
                response.error("Error occured in during spell rule creation.");
                errorLevel = 1;
            }

            if (errorLevel == 0 && duplicates.size() > 0) {
                response.error("Duplicate search terms exist.", duplicates);
                errorLevel = 1;
            } else if (errorLevel == 0) {
                for (SpellRule rule : spellRules) {
                    if (!addSpellRule(rule.getSearchTerms(), rule.getSuggestions())) {
                        response.error("Error occured in during spell rule creation.");
                        errorLevel = 2;
                        break;
                    }
                }

                if (errorLevel == 0) {
                    spellIndex.save(store);
                    response.success(null);
                }
            }
        } catch (DaoException ex) {
            logger.error("Error occured in addSpellRuleBatch()", ex);
            errorLevel = 2;
            response.error("Error occured in during spell rule creation.");
        }

        if (errorLevel == 2) {
            try {
                spellIndex.rollback(store);
            } catch (Exception e) {
                logger.error("Unable to rollback spell index for " + store, e);
            }
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> updateSpellRuleBatch(Integer maxSuggest, SpellRule[] spellRules, SpellRule[] deleted) {
        String store = UtilityService.getStoreId();
        ServiceResponse<Void> response = new ServiceResponse<Void>();
        int errorLevel = 0;

        try {
            deleteSpellRules(store, deleted);

            List<String> duplicates = null;

            try {
                duplicates = checkDuplicatedSearchTerms(store, spellRules, true);
            } catch (DaoException e) {
                response.error("Error occured in during spell rule creation.");
                errorLevel = 1;
            }

            if (errorLevel == 0 && duplicates.size() > 0) {
                response.error("Duplicate search terms exist.", duplicates);
                errorLevel = 1;
            } else if (errorLevel == 0) {
                for (SpellRule rule : spellRules) {
                    if (!updateSpellRule(rule.getRuleId(), rule.getSearchTerms(), rule.getSuggestions())) {
                        response.error("Error occured in during spell rule update.");
                        errorLevel = 2;
                        break;
                    }
                }

                if (errorLevel == 0) {
                    SpellRules rules = spellIndex.get(store);

                    if (rules != null) {
                        rules.setMaxSuggest(maxSuggest);
                    }

                    spellIndex.save(UtilityService.getStoreId());
                    response.success(null);
                }
            }
        } catch (DaoException e) {
            logger.error("Error occured in updateSpellRuleBatch()", e);
            response.error("Error occured in during spell rule update.");
            errorLevel = 2;
        }

        if (errorLevel == 2) {
            try {
                spellIndex.rollback(store);
            } catch (Exception e) {
                logger.error("Unable to rollback spell index for " + store, e);
            }
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Integer> getMaxSuggest() {
        ServiceResponse<Integer> response = new ServiceResponse<Integer>();

        try {
            response.success(spellRuleDAO.getMaxSuggest(UtilityService.getStoreId()));
        } catch (DaoException e) {
            response.error("Unable to retrieve maximum suggestions count.");
        }

        return response;
    }

    private boolean addSpellRule(String[] searchTerms, String[] suggestions) throws DaoException {
        String username = UtilityService.getUsername();
        String storeId = UtilityService.getStoreId();
        SpellRule rule = new SpellRule();

        rule.setStoreId(storeId);
        rule.setCreatedBy(username);
        rule.setLastModifiedBy(username);
        rule.setSearchTerms(searchTerms);
        rule.setSuggestions(suggestions);
        rule.setStatus("new");

        return spellRuleDAO.addSpellRule(rule) > 0;
    }

    private boolean updateSpellRule(String ruleId, String[] searchTerms, String[] suggestions) throws DaoException {
        SpellRule rule = new SpellRule();

        rule.setRuleId(ruleId);
        rule.setStoreId(UtilityService.getStoreId());
        rule.setLastModifiedBy(UtilityService.getUsername());
        rule.setSearchTerms(searchTerms);
        rule.setSuggestions(suggestions);

        return spellRuleDAO.updateSpellRule(rule) > 0;
    }

    private void deleteSpellRules(String storeId, SpellRule[] rules) throws DaoException {
        for (SpellRule rule : rules) {
            rule.setStoreId(storeId);
            spellRuleDAO.deleteSpellRule(rule);
        }
    }

    private List<String> checkDuplicatedSearchTerms(String store, SpellRule[] spellRules, boolean isUpdate)
            throws DaoException {
        List<String> searchTerms = new ArrayList<String>();
        List<String> duplicates = new ArrayList<String>();

        // Check for duplicate search terms.
        for (SpellRule rule : spellRules) {
            List<String> curTerms = Arrays.asList(rule.getSearchTerms());
            Collection<String> inter = CollectionUtils.intersection(searchTerms, curTerms);

            if (inter.size() == 0) {
                searchTerms.addAll(curTerms);
                duplicates.addAll(checkDuplicatedSearchTerms(store, isUpdate ? rule.getRuleId() : null,
                        rule.getSearchTerms()));
            } else {
                searchTerms.addAll(curTerms);
                duplicates.addAll(inter);
            }
        }

        return duplicates;
    }

    private List<String> checkDuplicatedSearchTerms(String store, String ruleId, final String[] searchTerms)
            throws DaoException {
        List<String> duplicates = new ArrayList<String>();

        for (String searchTerm : searchTerms) {
            if (spellRuleDAO.isDuplicateSearchTerm(store, searchTerm, ruleId)) {
                duplicates.add(searchTerm);
            }
        }

        return duplicates;
    }

    @RemoteMethod
    public FileTransfer downloadRules(String customFilename) {
        List<SpellRule> rules = spellRuleDAO.getActiveRules(UtilityService.getStoreId());
        return FileTransferUtils.downloadCsv(new CsvTransformer<SpellRule>() {
            @Override
            protected String[] toStringArray(SpellRule rule) {
                return new String[] {
                        rule.getRuleId(),
                        StringUtils.join(rule.getSearchTerms(), ','),
                        StringUtils.join(rule.getSuggestions(), ','),
                        rule.getStatus(),
                        rule.getCreatedBy(),
                        rule.getCreatedDate().toString(),
                        rule.getLastModifiedBy(),
                        rule.getLastModifiedDate().toString()
                };
            }
        }.getCsvStream(rules), "ID,SEARCH TERMS,SUGGESTIONS,STATUS,CREATED BY,CREATED DATE,LAST MODIFIED BY,LAST MODIFIED DATE", "DidYouMean", customFilename);
    }
}
