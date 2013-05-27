package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.response.ServiceResponse;
import com.search.manager.utility.StringUtil;

@Service(value = "spellRuleService")
@RemoteProxy(name = "SpellRuleServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "spellRuleService"))
public class SpellRuleService {

    private static final Logger logger = LoggerFactory.getLogger(SpellRuleService.class);

    private final static String modifiedStatusList = "new\tmodified\tdeleted";

    @Autowired
    private DaoService daoService;

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

            response.success(daoService.getSpellRule(new SearchCriteria<SpellRule>(rule, pageNumber, itemsPerPage)));
        } catch (DaoException e) {
            logger.error("Failed during getSpellRule()", e);
            response.error("Failed to retrieve Did You Mean rules.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> addSpellRuleBatch(SpellRule[] spellRules) {
        String store = UtilityService.getStoreId();
        ServiceResponse<Void> response = new ServiceResponse<Void>();

        try {
            // Check for duplicate search terms.
            List<String> duplicates = checkDuplicatedSearchTerms(store, spellRules, null, false);

            if (duplicates.size() > 0) {
                response.error("Duplicate search terms exist.", duplicates);
            } else {
                for (SpellRule rule : spellRules) {
                    rule.setStatus("new");
                }

                daoService.addSpellRules(Arrays.asList(spellRules));
                response.success(null);
            }
        } catch (DaoException ex) {
            logger.error("Error occured in addSpellRuleBatch()", ex);
            response.error("Error occured in during spell rule creation.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> updateSpellRuleBatch(Integer maxSuggest, SpellRule[] spellRules, SpellRule[] deleted) {
        String store = UtilityService.getStoreId();
        ServiceResponse<Void> response = new ServiceResponse<Void>();

        try {
            List<String> duplicates = checkDuplicatedSearchTerms(store, spellRules, deleted, true);

            if (duplicates.size() > 0) {
                response.error("Duplicate search terms exist.", duplicates);
            } else {
                for (SpellRule rule : spellRules) {
                    if ("published".equals(rule.getStatus())) {
                        rule.setStatus("modified");
                    }
                }
                daoService.updateSpellRules(Arrays.asList(spellRules), Arrays.asList(deleted));
                daoService.setMaxSuggest(store, maxSuggest);
                response.success(null);
            }
        } catch (DaoException e) {
            logger.error("Error occured in updateSpellRuleBatch()", e);
            response.error("Error occured in during spell rule update.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Integer> getMaxSuggest() {
        ServiceResponse<Integer> response = new ServiceResponse<Integer>();
        String store = UtilityService.getStoreId();

        try {
            response.success(daoService.getMaxSuggest(store));
        } catch (DaoException e) {
            logger.error("Error occured in getMaxSuggest.", e);
            response.error("Unable to retrieve maximum suggestions count.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<SpellRule> getRuleById(String ruleId) {
        ServiceResponse<SpellRule> response = new ServiceResponse<SpellRule>();

        try {
            SpellRule spellRule = daoService.getSpellRule(ruleId, UtilityService.getStoreId());

            if (spellRule != null) {
                response.success(spellRule);
            } else {
                response.error("Unable to find Did You Mean rule by id.");
            }
        } catch (DaoException e) {
            logger.error("Failed during getRuleById()", e);
            response.error("Failed to retrieve Did You Mean rules by id.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Map<String, Object>> getModifiedSpellRules(int pageNumber, int itemsPerPage) {
        ServiceResponse<Map<String, Object>> response = new ServiceResponse<Map<String, Object>>();
        try {
            SpellRule rule = new SpellRule();

            rule.setStoreId(UtilityService.getStoreId());
            rule.setStatus(modifiedStatusList);

            Map<String, Object> data = new HashMap<String, Object>();

            data.put("maxSuggest", getMaxSuggest().getData());
            data.put("spellRule", daoService
                    .getSpellRule(new SearchCriteria<SpellRule>(rule, pageNumber, itemsPerPage)).getList());

            response.success(data);
        } catch (DaoException e) {
            logger.error("Failed during getSpellRule()", e);
            response.error("Failed to retrieve Did You Mean rules.");
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private List<String> checkDuplicatedSearchTerms(String store, SpellRule[] spellRules, SpellRule[] deleted,
            boolean isUpdate) throws DaoException {
        List<String> searchTerms = new ArrayList<String>();
        List<String> duplicates = new ArrayList<String>();

        // Removed search terms from edited rules
        Set<String> deletedSearchTerms = new HashSet<String>();

        // On update, collect first the list of deleted keywords. This includes
        // keywords in deleted rules and keywords removed from updated rules.
        if (isUpdate) {
            for (SpellRule rule : deleted) {
                SpellRule old = daoService.getSpellRule(rule.getRuleId(), store);

                if (old != null) {
                    deletedSearchTerms.addAll(Arrays.asList(old.getSearchTerms()));
                }
            }

            for (SpellRule rule : spellRules) {
                SpellRule old = daoService.getSpellRule(rule.getRuleId(), store);
                List<String> newTerms = Lists.transform(Arrays.asList(rule.getSearchTerms()),
                        StringUtil.lowercaseTransformer);
                List<String> oldTerms = Arrays.asList(old.getSearchTerms());

                deletedSearchTerms.addAll(CollectionUtils.subtract(oldTerms, newTerms));
            }
        }

        // Check for duplicate search terms.
        for (SpellRule rule : spellRules) {
            List<String> curTerms = Lists.transform(Arrays.asList(rule.getSearchTerms()),
                    StringUtil.lowercaseTransformer);
            Collection<String> common = CollectionUtils.intersection(searchTerms, curTerms);

            if (common.size() == 0) {
                searchTerms.addAll(curTerms);

                List<String> dbSearchTerms = getDuplicatedSearchTerms(store, isUpdate ? rule.getRuleId() : null,
                        rule.getSearchTerms());

                dbSearchTerms.removeAll(deletedSearchTerms);
                duplicates.addAll(dbSearchTerms);
            } else {
                searchTerms.addAll(curTerms);
                duplicates.addAll(common);
            }
        }

        return duplicates;
    }

    // Duplicated search terms are those that exist in the database but for a
    // different rule.
    private List<String> getDuplicatedSearchTerms(String store, String ruleId, String[] searchTerms)
            throws DaoException {
        List<String> duplicates = new ArrayList<String>();

        for (String searchTerm : searchTerms) {
            SpellRule rule = daoService.getSpellRuleForSearchTerm(store, searchTerm);

            if (rule != null && (StringUtils.isBlank(ruleId) || !ruleId.equals(rule.getRuleId()))) {
                duplicates.add(searchTerm);
            }
        }

        return duplicates;
    }

    public SpellRules getSpellRules() {
        SpellRules rules = null;

        try {
            String store = UtilityService.getStoreId();
            List<SpellRule> spellRule = daoService.getSpellRules(store, null);
            Integer maxSuggest = daoService.getMaxSuggest(store);

            rules = new SpellRules();
            rules.setStore(store);
            rules.setMaxSuggest(maxSuggest);
            rules.setSpellRule(Lists.transform(spellRule, SpellRule.transformer));
        } catch (DaoException e) {
            logger.error("Error occurred on getSpellRules.", e);
        }

        return rules;
    }
}
