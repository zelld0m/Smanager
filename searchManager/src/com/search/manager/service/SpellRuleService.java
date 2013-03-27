package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.file.SpellRuleDAO;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.response.ServiceResponse;
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
        ServiceResponse<Void> response = new ServiceResponse<Void>();
        List<String> duplicates = new ArrayList<String>();

        for (SpellRule rule : spellRules) {
            ServiceResponse<SpellRule> internalResponse = addSpellRule(rule.getSearchTerms(), rule.getSuggestions());

            if (internalResponse.getErrorMessage() != null && internalResponse.getErrorMessage().getData() != null) {
                duplicates.addAll(Arrays.asList(((String[]) internalResponse.getErrorMessage().getData())));
            } else if (internalResponse.getStatus() != 0) {
                response.error("Error occured during batch addition.");
            }
        }

        if (duplicates.size() > 0 && response.getStatus() == 0) {
            response.error("Duplicate search terms exist.", duplicates);
        } else {
            response.success(null);
        }

        spellIndex.save(UtilityService.getStoreId());

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> updateSpellRuleBatch(SpellRule[] spellRules) {
        ServiceResponse<Void> response = new ServiceResponse<Void>();
        List<String> duplicates = new ArrayList<String>();

        for (SpellRule rule : spellRules) {
            ServiceResponse<Void> internalResponse = updateSpellRule(rule.getRuleId(), rule.getSearchTerms(),
                    rule.getSuggestions());

            if (internalResponse.getErrorMessage() != null && internalResponse.getErrorMessage().getData() != null) {
                duplicates.addAll(Arrays.asList(((String[]) internalResponse.getErrorMessage().getData())));
            } else if (internalResponse.getStatus() != 0) {
                response.error("Error occured during batch addition.");
            }
        }

        if (duplicates.size() > 0 && response.getStatus() == 0) {
            response.error("Duplicate search terms exist.", duplicates);
        } else {
            response.success(null);
        }

        spellIndex.save(UtilityService.getStoreId());
        return response;
    }

    @RemoteMethod
    public ServiceResponse<SpellRule> addSpellRule(String[] searchTerms, String[] suggestions) {
        ServiceResponse<SpellRule> response = new ServiceResponse<SpellRule>();

        try {
            String[] duplicates = checkDuplicatedSearchTerms(null, searchTerms);
            String username = UtilityService.getUsername();
            String storeId = UtilityService.getStoreId();

            if (duplicates.length > 0) {
                response.error("Duplicate search terms exist.", duplicates);
            } else {
                SpellRule rule = new SpellRule();
                Date now = new Date();

                rule.setStoreId(storeId);
                rule.setCreatedBy(username);
                rule.setCreatedDate(now);
                rule.setLastModifiedBy(username);
                rule.setLastModifiedDate(now);
                rule.setSearchTerms(searchTerms);
                rule.setSuggestions(suggestions);
                rule.setStatus("new");
                rule.setRuleId(spellRuleDAO.addSpellRuleAndGetId(rule));

                // try {
                // daoService.addRuleStatus(new RuleStatus(RuleEntity.SPELL,
                // storeId, rule.getRuleId(), rule
                // .getRuleId(), username, username, RuleStatusEntity.ADD,
                // RuleStatusEntity.UNPUBLISHED));
                // } catch (DaoException e) {
                // logger.error("Failed to create rule status for did you mean:)"
                // + rule.getRuleId(), e);
                // }

                response.success(rule);
            }
        } catch (DaoException ex) {
            logger.error("Error occured in addSpellRule()", ex);
            response.error("Error occured during spell rule creation.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> updateSpellRule(String ruleId, String[] searchTerms, String[] suggestions) {
        ServiceResponse<Void> response = new ServiceResponse<Void>();

        try {
            String[] duplicates = checkDuplicatedSearchTerms(ruleId, searchTerms);

            if (duplicates.length > 0) {
                response.error("Duplicate search terms exist.", duplicates);
            } else {
                SpellRule rule = new SpellRule();

                rule.setRuleId(ruleId);
                rule.setStoreId(UtilityService.getStoreId());
                rule.setLastModifiedBy(UtilityService.getUsername());
                rule.setSearchTerms(searchTerms);
                rule.setSuggestions(suggestions);

                spellRuleDAO.updateSpellRule(rule);
            }
        } catch (DaoException e) {
            logger.error("Error occured in updateSpellRule()", e);
            response.error("Error occured during spell rule update.");
        }

        return response;
    }

    @RemoteMethod
    public ServiceResponse<Void> deleteSpellRule(String ruleId) {
        ServiceResponse<Void> response = new ServiceResponse<Void>();

        try {
            SpellRule rule = new SpellRule(ruleId, UtilityService.getStoreId());
            spellRuleDAO.deleteSpellRule(rule);
            response.success(null);
        } catch (DaoException e) {
            logger.error("Error occured in deleteSpellRule()", e);
            response.error("Unable to delete rule.");
        }

        spellIndex.save(UtilityService.getStoreId());

        return response;
    }

    private String[] checkDuplicatedSearchTerms(final String ruleId, final String[] searchTerms) throws DaoException {
        List<String> duplicates = new ArrayList<String>();

        for (String searchTerm : searchTerms) {
            if (isDuplicateSearchTerm(ruleId, searchTerm)) {
                duplicates.add(searchTerm);
            }
        }

        return duplicates.toArray(new String[duplicates.size()]);
    }

    private boolean isDuplicateSearchTerm(String ruleId, String searchTerm) throws DaoException {
        return spellRuleDAO.checkDuplicateSearchTerm(UtilityService.getStoreId(), searchTerm, ruleId);
    }
}
