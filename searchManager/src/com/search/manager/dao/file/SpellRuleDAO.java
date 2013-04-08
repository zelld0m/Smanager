package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.xml.file.SpellIndex;

@Component("spellRuleDAO")
public class SpellRuleDAO {

    @Autowired
    private SpellIndex spellIndex;

    private static Logger logger = Logger.getLogger(SpellRuleDAO.class);
    
    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria) throws DaoException {
    	List<String> statusList = new ArrayList<String>();
    	String status = criteria.getModel().getStatus();
    	if (StringUtils.isNotBlank(status)) {
        	statusList.add(status);
    	}
    	return getSpellRule(criteria, statusList);
    }

    public SpellRules getSpellRules(String storeId) throws DaoException {
    	return spellIndex.get(storeId);
    }
    
    public RecordSet<SpellRuleXml> getSpellRuleXml(SearchCriteria<SpellRule> criteria, List<String> statusList) throws DaoException {
        try {
            SpellRule rule = criteria.getModel();
            SpellRules spellRules = spellIndex.get(rule.getStoreId());

            List<SpellRuleXml> retList = new ArrayList<SpellRuleXml>();
            int total = 0;

            if (rule.getRuleId() != null) {
                SpellRuleXml xml = spellRules.getSpellRule(rule.getRuleId());
                if (xml != null) {
                    retList.add(xml);
                    total = 1;
                }
            } else {
                retList = spellRules.selectActiveRules();

                boolean hasSearchTerm = rule.getSearchTerms() != null
                        && StringUtils.isNotEmpty(rule.getSearchTerms()[0]);
                boolean hasSuggestTerm = rule.getSuggestions() != null
                        && StringUtils.isNotEmpty(rule.getSuggestions()[0]);
                boolean hasStatus = CollectionUtils.isNotEmpty(statusList);

                if (hasSearchTerm) {
                    List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();
                    for (SpellRuleXml xml : retList) {
                        for (String kw : xml.getRuleKeyword().getKeyword()) {
                            if (kw.contains(rule.getSearchTerms()[0])) {
                                rules.add(xml);
                                break;
                            }
                        }
                    }

                    retList = rules;
                }

                if (hasSuggestTerm) {
                    List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();
                    for (SpellRuleXml xml : retList) {
                        for (String kw : xml.getSuggestKeyword().getSuggest()) {
                            if (kw.contains(rule.getSuggestions()[0])) {
                                rules.add(xml);
                                break;
                            }
                        }
                    }

                    retList = rules;
                }

                if (hasStatus) {
                	// TODO: check if need to call String.intern()
                    List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();
                    for (SpellRuleXml xml : retList) {
                        if (xml.getStatus() != null && statusList.contains(xml.getStatus().intern())) {
                            rules.add(xml);
                        }
                    }
                    retList = rules;
                }
                total = retList.size();
            }

            int startRow = criteria.getStartRow() == null ? 0 : criteria.getStartRow();
            int endRow = criteria.getEndRow() == null ? 0 : criteria.getEndRow();

            logger.debug("start row: " + criteria.getStartRow());
            logger.debug("end row: " + criteria.getEndRow());

            if (startRow == 0 || endRow == 0) {
            	return new RecordSet<SpellRuleXml>(retList, total);
            }
            return new RecordSet<SpellRuleXml>(retList.subList(Math.max(0, criteria.getStartRow() - 1),
                    Math.min(retList.size(), criteria.getEndRow())), total);
            
        } catch (Exception e) {
            throw new DaoException("Failed during getSpellRuleXml()", e);
        }
    }
    
    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria, List<String> statusList) throws DaoException {
        try {
        	RecordSet<SpellRuleXml> resultSet = getSpellRuleXml(criteria, statusList);
            List<SpellRule> retList = Lists.transform(resultSet.getList(), SpellRuleXml.transformer);
        	return new RecordSet<SpellRule>(retList, resultSet.getTotalSize());	
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

    public List<SpellRule> getActiveRules(String store) {
        SpellRules rules = spellIndex.get(store);

        if (rules != null) {
            return Lists.transform(rules.selectActiveRules(), SpellRuleXml.transformer);
        }

        return Collections.emptyList();
    }

    @Audit(entity = Entity.spell, operation = Operation.add)
    public int addSpellRule(SpellRule rule) throws DaoException {
        try {
            Date now = new Date();
            String ruleId = DAOUtils.generateUniqueId();

            rule.setRuleId(ruleId);
            rule.setCreatedDate(now);
            rule.setLastModifiedDate(now);

            spellIndex.get(rule.getStoreId()).addRule(new SpellRuleXml(rule));
            return 1;
        } catch (Exception e) {
            throw new DaoException("Failed during addSpellRuleAndGetId()", e);
        }
    }

    @Audit(entity = Entity.spell, operation = Operation.update)
    public int updateSpellRule(SpellRule rule) throws DaoException {
        try {
            SpellRules rules = spellIndex.get(rule.getStoreId());
            SpellRuleXml xml = rules.getSpellRule(rule.getRuleId());

            if (xml != null) {
                String oldStatus = xml.getStatus();
                rule.setLastModifiedDate(new Date());
                xml.update(rule);
                rules.updateStatusIndex(oldStatus, xml);
                return 1;
            }

            return 0;
        } catch (Exception e) {
            throw new DaoException("Failed during addSpellRuleAndGetId()", e);
        }
    }

    @Audit(entity = Entity.spell, operation = Operation.delete)
    public int deleteSpellRule(SpellRule rule) throws DaoException {
        try {
            SpellRules rules = spellIndex.get(rule.getStoreId());
            SpellRuleXml xml = rules.getSpellRule(rule.getRuleId());

            if (xml != null) {
                String oldStatus = xml.getStatus();

                if ("published".equalsIgnoreCase(oldStatus) || "modified".equalsIgnoreCase(oldStatus)) {
                    xml.setStatus("deleted");
                    rules.deleteFromSearchTermIndex(xml);
                    rules.updateStatusIndex(oldStatus, xml);

                    return 1;
                } else {
                    return deleteRulePhysically(rule);
                }
            }

            return 0;
        } catch (Exception e) {
            throw new DaoException("Failed during deleteSpellRule()", e);
        }
    }

    public int deleteRulePhysically(SpellRule rule) throws DaoException {
        try {
            SpellRules rules = spellIndex.get(rule.getStoreId());
            SpellRuleXml xml = rules.getSpellRule(rule.getRuleId());

            if (xml != null) {
                rules.deletePhysically(xml);
                return 1;
            }
        } catch (Exception e) {
            throw new DaoException("Failed during deleteRulePhysically()", e);
        }

        return 0;
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
            throw new DaoException("Failed during getMaxSuggest()", e);
        }
    }
    
    public boolean save(String storeId) throws DaoException {
        return spellIndex.save(storeId);
    }
    
    public void rollback(String storeId) throws DaoException {
        try {
			spellIndex.rollback(storeId);
		} catch (Exception e) {
            throw new DaoException("Failed during rollback()", e);
		}
    }
    
}
