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
        int errorLevel = 0;
        String store = UtilityService.getStoreId();
        ServiceResponse<Void> response = new ServiceResponse<Void>();

        try {
            // Check for duplicate search terms.
            List<String> duplicates = null;

            try {
                duplicates = checkDuplicatedSearchTerms(store, spellRules, false);
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
                    daoService.saveSpellRules(store);
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
                daoService.reloadSpellRules(store);
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
                    SpellRules rules = daoService.getSpellRules(store);

                    if (rules != null) {
                        rules.setMaxSuggest(maxSuggest);
                    }

                    daoService.saveSpellRules(UtilityService.getStoreId());
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
                daoService.reloadSpellRules(store);
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
            response.success(daoService.getMaxSuggest(UtilityService.getStoreId()));
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

        return daoService.addSpellRule(rule) > 0;
    }

    private boolean updateSpellRule(String ruleId, String[] searchTerms, String[] suggestions) throws DaoException {
        SpellRule rule = new SpellRule();

        rule.setRuleId(ruleId);
        rule.setStoreId(UtilityService.getStoreId());
        rule.setLastModifiedBy(UtilityService.getUsername());
        rule.setSearchTerms(searchTerms);
        rule.setSuggestions(suggestions);

        return daoService.updateSpellRule(rule) > 0;
    }

    private void deleteSpellRules(String storeId, SpellRule[] rules) throws DaoException {
        for (SpellRule rule : rules) {
            rule.setStoreId(storeId);
            daoService.deleteSpellRule(rule);
        }
    }

    private List<String> checkDuplicatedSearchTerms(String store, SpellRule[] spellRules, boolean isUpdate)
            throws DaoException {
        List<String> searchTerms = new ArrayList<String>();
        List<String> duplicates = new ArrayList<String>();

        // Removed search terms from edited rules
        List<String> deletedSearchTerms = new ArrayList<String>();

        if (isUpdate) {
            // For update operation, we have to get the list of search terms that were removed.
            for (SpellRule rule: spellRules) {
                SpellRule oldRule = daoService.getSpellRuleById(store, rule.getRuleId());
                List<String> newTerms = Lists.transform(Arrays.asList(rule.getSearchTerms()), StringUtil.lowercaseTransformer);
                List<String> oldTerms = new ArrayList<String>();
                
                oldTerms.addAll(Lists.transform(Arrays.asList(oldRule.getSearchTerms()), StringUtil.lowercaseTransformer));
                oldTerms.removeAll(newTerms);

                deletedSearchTerms.addAll(oldTerms);
            }
        }

        // Check for duplicate search terms.
        for (SpellRule rule : spellRules) {
            List<String> curTerms = Lists.transform(Arrays.asList(rule.getSearchTerms()), StringUtil.lowercaseTransformer);
            @SuppressWarnings("unchecked")
            Collection<String> inter = CollectionUtils.intersection(searchTerms, curTerms);

            if (inter.size() == 0) {
                searchTerms.addAll(curTerms);

                List<String> oldSearchTerms = checkDuplicatedSearchTerms(store, isUpdate ? rule.getRuleId() : null,
                        rule.getSearchTerms());
                oldSearchTerms.removeAll(deletedSearchTerms);
                duplicates.addAll(oldSearchTerms);
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
            if (daoService.isDuplicateSearchTerm(store, searchTerm, ruleId)) {
                duplicates.add(searchTerm);
            }
        }

        return duplicates;
    }

    @RemoteMethod
    public SpellRule getRuleById(String ruleId) {
    	ServiceResponse<RecordSet<SpellRule>> response = new ServiceResponse<RecordSet<SpellRule>>();
    	
        try {
            SpellRule spellRuleFilter = new SpellRule(ruleId, UtilityService.getStoreId());
            RecordSet<SpellRule> spellRules = daoService.getSpellRule(new SearchCriteria<SpellRule>(spellRuleFilter, 1, 1));
            if(spellRules != null && spellRules.getTotalSize() > 0) {
            	return spellRules.getList().get(0);
            }
        } catch (DaoException e) {
            logger.error("Failed during getRuleById()", e);
            response.error("Failed to retrieve Did You Mean rules by id.");
        }

        return null;
    }
    
    static List<String> modifiedStatusList;
    
    static {
    	modifiedStatusList = new ArrayList<String>();
    	modifiedStatusList.add("new");
    	modifiedStatusList.add("modified");
    	modifiedStatusList.add("deleted");
    }
    
    @RemoteMethod
    public ServiceResponse<SpellRules> getModifiedSpellRules(String ruleId, String searchTerm, String suggestion,
            int pageNumber, int itemsPerPage) {
        ServiceResponse<SpellRules> response = new ServiceResponse<SpellRules>();
        try {
            SpellRule rule = new SpellRule(ruleId, UtilityService.getStoreId());
            if (StringUtils.isNotEmpty(searchTerm))
                rule.setSearchTerms(new String[] { searchTerm });
            if (StringUtils.isNotEmpty(suggestion))
                rule.setSuggestions(new String[] { suggestion });
            SpellRules spellRule = new SpellRules();
            spellRule.setMaxSuggest(daoService.getMaxSuggest(UtilityService.getStoreId()));
            spellRule.setSpellRule(daoService.getSpellRuleXml(new SearchCriteria<SpellRule>(rule, pageNumber, itemsPerPage), modifiedStatusList).getList());
            response.success(spellRule);
        } catch (DaoException e) {
            logger.error("Failed during getSpellRule()", e);
            response.error("Failed to retrieve Did You Mean rules.");
        }
        return response;
    }
    
    public SpellRules getSpellRules() {
    	return daoService.getSpellRules(UtilityService.getStoreId());
    }
    
}
