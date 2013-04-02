package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.report.model.xml.RuleKeywordXml;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.report.model.xml.SuggestKeywordXml;
import com.search.manager.xml.file.SpellIndex;

@Component("spellRuleDAO")
public class SpellRuleDAO {

    @Autowired
    private SpellIndex spellIndex;

    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria) throws DaoException {
        try {
            SpellRule rule = criteria.getModel();
            SpellRules spellRules = spellIndex.get(rule.getStoreId());

            List<SpellRule> retList = new ArrayList<SpellRule>();
            int total = 0;

            if (rule.getRuleId() != null) {
                SpellRuleXml xml = spellRules.getSpellRule(rule.getRuleId());

                if (xml != null) {
                    retList.add(new SpellRule(xml));
                    total = 1;
                }
            } else {
                List<SpellRuleXml> searchCollection = spellRules.getSpellRule();

                boolean hasSearchTerm = rule.getSearchTerms() != null
                        && StringUtils.isNotEmpty(rule.getSearchTerms()[0]);
                boolean hasSuggestTerm = rule.getSuggestions() != null
                        && StringUtils.isNotEmpty(rule.getSuggestions()[0]);
                boolean hasStatus = StringUtils.isNotEmpty(rule.getStatus());

                if (hasSearchTerm) {
                    List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();
                    for (SpellRuleXml xml : searchCollection) {
                        for (String kw : xml.getRuleKeyword().getKeyword()) {
                            if (kw.contains(rule.getSearchTerms()[0])) {
                                rules.add(xml);
                                break;
                            }
                        }
                    }

                    searchCollection = rules;
                }

                if (hasSuggestTerm) {
                    List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();
                    for (SpellRuleXml xml : searchCollection) {
                        for (String kw : xml.getSuggestKeyword().getSuggest()) {
                            if (kw.contains(rule.getSuggestions()[0])) {
                                rules.add(xml);
                                break;
                            }
                        }
                    }

                    searchCollection = rules;
                }

                if (hasStatus) {
                    List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();
                    for (SpellRuleXml xml : searchCollection) {
                        if (xml.getStatus().equals(rule.getStatus())) {
                            rules.add(xml);
                        }
                    }

                    searchCollection = rules;
                }

                for (SpellRuleXml xml : searchCollection) {
                    if (!xml.isDeleted()) {
                        retList.add(new SpellRule(xml));
                    }
                }

                total = retList.size();
            }

            return new RecordSet<SpellRule>(retList.subList(Math.max(0, criteria.getStartRow() - 1),
                    Math.min(retList.size(), criteria.getEndRow())), total);
        } catch (Exception e) {
            throw new DaoException("Failed during getSpellRule()", e);
        }
    }

    public SpellRule getSpellRuleForSearchTerm(String store, String searchTerm) {
        SpellRules rules = spellIndex.get(store);
        SpellRule rule = null;

        if (rules != null) {
            SpellRuleXml ruleXml = rules.checkSearchTerm(searchTerm);

            if (ruleXml != null) {
                rule = new SpellRule(ruleXml);
            }
        }

        return rule;
    }

    public int addSpellRule(SpellRule rule) throws DaoException {
        try {
            Date now = new Date();
            String ruleId = DAOUtils.generateUniqueId();
            SpellRuleXml ruleXml = new SpellRuleXml();

            ruleXml.setRuleId(ruleId);
            ruleXml.setStore(rule.getStoreId());
            ruleXml.setRuleKeyword(new RuleKeywordXml(Arrays.asList(rule.getSearchTerms())));
            ruleXml.setSuggestKeyword(new SuggestKeywordXml(Arrays.asList(rule.getSuggestions())));
            ruleXml.setCreatedBy(rule.getCreatedBy());
            ruleXml.setCreatedDate(now);
            ruleXml.setLastModifiedBy(rule.getCreatedBy());
            ruleXml.setLastModifiedDate(now);
            ruleXml.setStatus(rule.getStatus());

            spellIndex.get(rule.getStoreId()).addRule(ruleXml);
            return 1;
        } catch (Exception e) {
            throw new DaoException("Failed during addSpellRuleAndGetId()", e);
        }
    }

    public int updateSpellRule(SpellRule rule) throws DaoException {
        try {
            SpellRules rules = spellIndex.get(rule.getStoreId());
            SpellRuleXml xml = rules.getSpellRule(rule.getRuleId());

            if (xml != null) {
                xml.getRuleKeyword().setKeyword(Arrays.asList(rule.getSearchTerms()));
                xml.getSuggestKeyword().setSuggest(Arrays.asList(rule.getSuggestions()));
                xml.setLastModifiedBy(rule.getLastModifiedBy());
                xml.setLastModifiedDate(new Date());

                if (xml.getStatus() == "published") {
                    xml.setStatus("modified");
                }

                return 1;
            }

            return 0;
        } catch (Exception e) {
            throw new DaoException("Failed during addSpellRuleAndGetId()", e);
        }
    }

    public int deleteSpellRule(SpellRule rule) throws DaoException {
        try {
            SpellRules rules = spellIndex.get(rule.getStoreId());
            SpellRuleXml xml = rules.getSpellRule(rule.getRuleId());

            if (xml != null) {
                xml.setDeleted(true);

                if (xml.getStatus() == "published") {
                    xml.setStatus("modified");
                }

                rules.deleteFromSecondaryIndex(xml);
                return 1;
            }

            return 0;
        } catch (Exception e) {
            throw new DaoException("Failed during addSpellRuleAndGetId()", e);
        }
    }

    public boolean isDuplicateSearchTerm(String storeId, String searchTerm, String ruleId) throws DaoException {
        try {
            SpellRules rules = spellIndex.get(storeId);
            SpellRuleXml xml = rules.checkSearchTerm(searchTerm);

            return xml != null && !StringUtils.equals(xml.getRuleId(), ruleId);
        } catch (Exception e) {
            throw new DaoException("Faild during checkDuplicateSearchTerm()", e);
        }
    }

    public Integer getMaxSuggest(String storeId) throws DaoException {
        try {
            return spellIndex.get(storeId).getMaxSuggest();
        } catch (Exception e) {
            throw new DaoException("Faild during getMaxSuggest()", e);
        }
    }
}
