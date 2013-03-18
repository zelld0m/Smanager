package com.search.manager.service;

import java.util.ArrayList;
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
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.response.ServiceResponse;

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

            rule.setStatus(status);

            response.success(daoService.getSpellRule(new SearchCriteria<SpellRule>(rule, pageNumber, itemsPerPage)));
        } catch (DaoException e) {
            logger.error("Failed during getSpellRule()", e);
            response.error("Failed to retrieve Did You Mean rules.");
        }

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

                rule.setStoreId(storeId);
                rule.setCreatedBy(username);
                rule.setLastModifiedBy(username);
                rule.setSearchTerms(searchTerms);
                rule.setSuggestions(suggestions);
                rule.setRuleId(daoService.addSpellRuleAndGetId(rule));

                try {
                    daoService.addRuleStatus(new RuleStatus(RuleEntity.SPELL, storeId, rule.getRuleId(), rule
                            .getRuleId(), username, username, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
                } catch (DaoException e) {
                    logger.error("Failed to create rule status for did you mean:)" + rule.getRuleId(), e);
                }

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

                daoService.updateSpellRule(rule);
            }
        } catch (DaoException e) {
            logger.error("Error occured in updateSpellRule()", e);
            response.error("Error occured during spell rule update.");
        }

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
        boolean retVal = false;
        RecordSet<SpellRule> spellRules = daoService
                .checkDuplicateSearchTerm(UtilityService.getStoreId(), searchTerm);

        if (spellRules.getTotalSize() > 0 && StringUtils.isNotEmpty(ruleId)) {
            retVal = !ruleId.equals(spellRules.getList().get(0).getRuleId());
        } else {
            retVal = spellRules.getTotalSize() > 0 && StringUtils.isEmpty(ruleId);
        }

        return retVal;
    }
}
