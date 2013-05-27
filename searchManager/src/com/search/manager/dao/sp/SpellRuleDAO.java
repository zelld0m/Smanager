package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;
import com.search.manager.report.model.xml.RuleFileXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.service.UtilityService;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.ws.ConfigManager;

@Repository("spellRuleDAO")
public class SpellRuleDAO {

    private static Logger logger = Logger.getLogger(SpellRuleDAO.class);

    @Autowired
    private DaoService daoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        getSpellRuleProcedure = new GetSpellRuleProcedure(jdbcTemplate);
        addSpellRuleProcedure = new AddSpellRuleProcedure(jdbcTemplate);
        updateSpellRuleProcedure = new UpdateSpellRuleProcedure(jdbcTemplate);
        deleteSpellRuleProcedure = new DeleteSpellRuleProcedure(jdbcTemplate);
        getSpellRuleForSearchTermProcedure = new GetSpellRuleForSearchTermProcedure(jdbcTemplate);
    }

    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria) throws DaoException {
        SpellRule rule = criteria.getModel();

        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            params.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
            params.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, rule.getSearchTerms() != null
                    && rule.getSearchTerms().length > 0 ? rule.getSearchTerms()[0] : null);
            params.put(DAOConstants.PARAM_SUGGEST_LIKE, rule.getSuggestions() != null
                    && rule.getSuggestions().length > 0 ? rule.getSuggestions()[0] : null);
            params.put(DAOConstants.PARAM_STATUS, rule.getStatus());
            params.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            params.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());

            return DAOUtils.getRecordSet(getSpellRuleProcedure.execute(params));
        } catch (Exception e) {
            logger.error("Error occured on getSpellRule", e);
            throw new DaoException("Error occurred on getSpellRule.", e);
        }
    }

    public RecordSet<SpellRule> getSpellRules(String store, String status) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_RULE_ID, null);
            params.put(DAOConstants.PARAM_STORE_ID, store);
            params.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, null);
            params.put(DAOConstants.PARAM_SUGGEST_LIKE, null);
            params.put(DAOConstants.PARAM_STATUS, status);
            params.put(DAOConstants.PARAM_START_ROW, 0);
            params.put(DAOConstants.PARAM_END_ROW, Integer.MAX_VALUE);

            return DAOUtils.getRecordSet(getSpellRuleProcedure.execute(params));
        } catch (Exception e) {
            logger.error("Error occured on getSpellRule", e);
            throw new DaoException("Error occurred on getSpellRules.", e);
        }
    }

    public SpellRule getSpellRule(String ruleId, String store) throws DaoException {
        RecordSet<SpellRule> spellRules = getSpellRule(new SearchCriteria<SpellRule>(new SpellRule(ruleId, store), 1, 1));

        if (spellRules != null && spellRules.getTotalSize() > 0) {
            return spellRules.getList().get(0);
        } else {
            return null;
        }
    }

    @Transactional
    @Audit(entity = Entity.spell, operation = Operation.add)
    public int addSpellRules(List<SpellRule> spellRules) throws DaoException {
        int count = 0;
        String username = UtilityService.getUsername();
        String store = UtilityService.getStoreId();

        try {
            for (SpellRule rule : spellRules) {
                Map<String, Object> params = new HashMap<String, Object>();

                if (StringUtils.isBlank(rule.getRuleId())) {
                    rule.setRuleId(DAOUtils.generateUniqueId());
                }

                if (StringUtils.isBlank(rule.getStoreId())) {
                    rule.setStoreId(store);
                }

                params.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
                params.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
                params.put(DAOConstants.PARAM_SEARCH_TERM, rule.toTabbedSearchTerm());
                params.put(DAOConstants.PARAM_SUGGEST, rule.toTabbedSuggestions());
                params.put(DAOConstants.PARAM_STATUS, rule.getStatus());
                params.put(DAOConstants.PARAM_CREATED_BY, username);

                count += DAOUtils.getUpdateCount(addSpellRuleProcedure.execute(params));
            }
        } catch (Exception e) {
            throw new DaoException("Error occurred on addSpellRules.", e);
        }

        return count;
    }

    @Transactional
    @Audit(entity = Entity.spell, operation = Operation.update)
    public int updateSpellRules(List<SpellRule> spellRules, List<SpellRule> deleted) throws DaoException {
        int count = 0;
        String username = UtilityService.getUsername();

        try {
            for (SpellRule rule : deleted) {
                Map<String, Object> params = new HashMap<String, Object>();

                params.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
                params.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());

                if ("new".equals(rule.getStatus())) {
                    params.put(DAOConstants.PARAM_PHYSICAL, 1);
                } else {
                    params.put(DAOConstants.PARAM_PHYSICAL, 0);
                }

                params.put(DAOConstants.PARAM_MODIFIED_BY, username);
                count += DAOUtils.getUpdateCount(deleteSpellRuleProcedure.execute(params));
            }

            for (SpellRule rule : spellRules) {
                Map<String, Object> params = new HashMap<String, Object>();

                params.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
                params.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
                params.put(DAOConstants.PARAM_SEARCH_TERM, rule.toTabbedSearchTerm());
                params.put(DAOConstants.PARAM_SUGGEST, rule.toTabbedSuggestions());
                params.put(DAOConstants.PARAM_STATUS, rule.getStatus());
                params.put(DAOConstants.PARAM_MODIFIED_BY, username);

                count += DAOUtils.getUpdateCount(updateSpellRuleProcedure.execute(params));
            }
        } catch (Exception e) {
            throw new DaoException("Error occurred on updateSpellRules.", e);
        }

        return count;
    }

    public SpellRule getSpellRuleForSearchTerm(String store, String searchTerm) throws DaoException {
        SpellRule rule = null;

        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_SEARCH_TERM, searchTerm);
            params.put(DAOConstants.PARAM_STORE_ID, store);

            RecordSet<SpellRule> spellRules = DAOUtils.getRecordSet(getSpellRuleForSearchTermProcedure.execute(params));

            if (spellRules != null && spellRules.getTotalSize() > 0) {
                rule = spellRules.getList().get(0);
            }
        } catch (Exception e) {
            logger.error("Error occurred on getSpellRuleForSearchTerm.", e);
            throw new DaoException("Error occurred on getSpellRuleForSearchTerm.", e);
        }

        return rule;
    }

    public Integer getMaxSuggest(String store) {
        return Integer.parseInt(StringUtils.defaultIfBlank(ConfigManager.getInstance()
                .getStoreSetting(store, "maxSpellSuggestions"), "3"));
    }

    @Audit(entity = Entity.spell, operation = Operation.updateSetting)
    public boolean setMaxSuggest(String store, Integer maxSuggest) {
        return ConfigManager.getInstance().setStoreSetting(store, "maxSpellSuggestions",
                String.valueOf(maxSuggest));
    }

    @Transactional
    public boolean restoreSpellRules(String store, List<SpellRule> rules, int maxSuggest) throws DaoException {
        try {
            final Set<String> ruleIds = new HashSet<String>();
            String username = UtilityService.getUsername();

            List<SpellRule> currentRules = getSpellRules(store, null).getList();
            List<String> currentRuleIds = Lists.transform(currentRules, new Function<SpellRule, String>() {
                public String apply(SpellRule arg) {
                    ruleIds.add(arg.getRuleId());
                    return arg.getRuleId();
                }
            });
            List<String> versionRuleIds = Lists.transform(rules, new Function<SpellRule, String>() {
                public String apply(SpellRule arg) {
                    ruleIds.add(arg.getRuleId());
                    return arg.getRuleId();
                }
            });

            List<RuleXml> ruleXmls = daoService.getPublishedRuleVersions(store, "didyoumean", "spell_rule");
            List<SpellRule> publishedRules = Collections.emptyList();
            List<String> publishedRuleIds = Collections.emptyList();

            if (ruleXmls != null && ruleXmls.size() > 0) {
                SpellRules spellRules = (SpellRules) RuleXmlUtil.loadVersion((RuleFileXml) ruleXmls.get(0));

                publishedRules = Lists.transform(spellRules.getSpellRule(), SpellRuleXml.transformer);
                publishedRuleIds = Lists.transform(publishedRules, new Function<SpellRule, String>() {
                    public String apply(SpellRule arg) {
                        ruleIds.add(arg.getRuleId());
                        return arg.getRuleId();
                    }
                });
            }

            ruleIds.addAll(currentRuleIds);
            ruleIds.addAll(versionRuleIds);
            ruleIds.addAll(publishedRuleIds);

            for (String ruleId : ruleIds) {
                int idx;
                SpellRule published = (idx = publishedRuleIds.indexOf(ruleId)) >= 0 ? publishedRules.get(idx) : null;
                SpellRule version = (idx = versionRuleIds.indexOf(ruleId)) >= 0 ? rules.get(idx) : null;
                SpellRule current = (idx = currentRuleIds.indexOf(ruleId)) >= 0 ? currentRules.get(idx) : null;

                Map<String, Object> params = new HashMap<String, Object>();
                DBAction action = DBAction.getAction(published, version, current);

                params.put(DAOConstants.PARAM_STORE_ID, store);

                switch (action) {
                    case INSERT_N:
                        params.put(DAOConstants.PARAM_RULE_ID, version.getRuleId());
                        params.put(DAOConstants.PARAM_SEARCH_TERM, version.toTabbedSearchTerm());
                        params.put(DAOConstants.PARAM_SUGGEST, version.toTabbedSuggestions());
                        params.put(DAOConstants.PARAM_STATUS, action.getStatus());
                        params.put(DAOConstants.PARAM_CREATED_BY, username);

                        addSpellRuleProcedure.execute(params);
                        break;
                    case UPDATE_N:
                    case UPDATE_M:
                    case UPDATE_P:
                        params.put(DAOConstants.PARAM_RULE_ID, version.getRuleId());
                        params.put(DAOConstants.PARAM_SEARCH_TERM, version.toTabbedSearchTerm());
                        params.put(DAOConstants.PARAM_SUGGEST, version.toTabbedSuggestions());
                        params.put(DAOConstants.PARAM_STATUS, action.getStatus());
                        params.put(DAOConstants.PARAM_MODIFIED_BY, username);
                        updateSpellRuleProcedure.execute(params);
                        break;
                    case UPDATE_D:
                        params.put(DAOConstants.PARAM_RULE_ID, published.getRuleId());
                        params.put(DAOConstants.PARAM_SEARCH_TERM, published.toTabbedSearchTerm());
                        params.put(DAOConstants.PARAM_SUGGEST, published.toTabbedSuggestions());
                        params.put(DAOConstants.PARAM_STATUS, action.getStatus());
                        params.put(DAOConstants.PARAM_MODIFIED_BY, username);
                        updateSpellRuleProcedure.execute(params);
                        break;
                    case DELETE_P:
                        params.put(DAOConstants.PARAM_RULE_ID, current.getRuleId());
                        params.put(DAOConstants.PARAM_PHYSICAL, 1);
                        params.put(DAOConstants.PARAM_MODIFIED_BY, username);
                        deleteSpellRuleProcedure.execute(params);
                        break;
                    case DELETE_L:
                        params.put(DAOConstants.PARAM_RULE_ID, current.getRuleId());
                        params.put(DAOConstants.PARAM_PHYSICAL, 0);
                        params.put(DAOConstants.PARAM_MODIFIED_BY, username);
                        deleteSpellRuleProcedure.execute(params);
                        break;
                    default:
                        break;
                }
            }

            setMaxSuggest(store, maxSuggest);

            return true;
        } catch (Exception e) {
            logger.error("Error occurred on restoreSpellRules.", e);
            throw new DaoException("Error occurred on restoreSpellRules.", e);
        }
    }

    @Transactional
    public boolean publishSpellRules(String store) throws DaoException {
        String modifiedStatus = "new\tmodified\tdeleted";
        String username = UtilityService.getUsername();

        List<SpellRule> rules = getSpellRules(store, modifiedStatus).getList();
        Map<String, Object> params = new HashMap<String, Object>();

        for (SpellRule sr : rules) {
            params.clear();

            if ("deleted".equalsIgnoreCase(sr.getStatus())) {
                params.put(DAOConstants.PARAM_RULE_ID, sr.getRuleId());
                params.put(DAOConstants.PARAM_STORE_ID, store);
                params.put(DAOConstants.PARAM_PHYSICAL, 1);
                params.put(DAOConstants.PARAM_MODIFIED_BY, "");

                deleteSpellRuleProcedure.execute(params);
            } else {
                params.put(DAOConstants.PARAM_RULE_ID, sr.getRuleId());
                params.put(DAOConstants.PARAM_STORE_ID, store);
                params.put(DAOConstants.PARAM_SEARCH_TERM, sr.toTabbedSearchTerm());
                params.put(DAOConstants.PARAM_SUGGEST, sr.toTabbedSuggestions());
                params.put(DAOConstants.PARAM_STATUS, "published");
                params.put(DAOConstants.PARAM_MODIFIED_BY, username);

                updateSpellRuleProcedure.execute(params);
            }
        }

        return true;
    }

    /*
     * //////////////////////////////////////////////////////////////
     * /////////////////// STORED PROCEDURES ////////////////////////
     * //////////////////////////////////////////////////////////////
     */
    private GetSpellRuleProcedure getSpellRuleProcedure;
    private AddSpellRuleProcedure addSpellRuleProcedure;
    private UpdateSpellRuleProcedure updateSpellRuleProcedure;
    private DeleteSpellRuleProcedure deleteSpellRuleProcedure;
    private GetSpellRuleForSearchTermProcedure getSpellRuleForSearchTermProcedure;

    /*
     * //////////////////////////////////////////////////////////////
     * ///////////// STORED PROCEDURES IMPLEMENTATION ///////////////
     * //////////////////////////////////////////////////////////////
     */
    private class GetSpellRuleProcedure extends GetStoredProcedure {
        public GetSpellRuleProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SUGGEST_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STATUS, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<SpellRule>() {
                public SpellRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SpellRule rule = new SpellRule(rs.getString(DAOConstants.COLUMN_RULE_ID),
                            rs.getString(DAOConstants.COLUMN_STORE_ID), rs.getString(DAOConstants.COLUMN_STATUS), null,
                            null, rs.getString(DAOConstants.COLUMN_CREATED_BY),
                            rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
                            rs.getDate(DAOConstants.COLUMN_CREATED_STAMP),
                            rs.getDate(DAOConstants.COLUMN_LAST_UPDATED_STAMP));

                    rule.fromTabbedSearchTerms(rs.getString(DAOConstants.COLUMN_SEARCH_TERM));
                    rule.fromTabbedSuggestions(rs.getString(DAOConstants.COLUMN_SUGGEST));

                    return rule;
                }
            }));
        }
    }

    private class AddSpellRuleProcedure extends CUDStoredProcedure {
        public AddSpellRuleProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STATUS, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SUGGEST, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
        }
    }

    private class UpdateSpellRuleProcedure extends CUDStoredProcedure {
        public UpdateSpellRuleProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_UPDATE_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STATUS, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SUGGEST, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class DeleteSpellRuleProcedure extends CUDStoredProcedure {
        public DeleteSpellRuleProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_DELETE_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_PHYSICAL, Types.INTEGER));
        }
    }

    private class GetSpellRuleForSearchTermProcedure extends GetStoredProcedure {
        public GetSpellRuleForSearchTermProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_SPELL_RULE_FOR_SEARCH_TERM);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<SpellRule>() {
                public SpellRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SpellRule rule = new SpellRule(rs.getString(DAOConstants.COLUMN_RULE_ID),
                            rs.getString(DAOConstants.COLUMN_STORE_ID), rs.getString(DAOConstants.COLUMN_STATUS), null,
                            null, rs.getString(DAOConstants.COLUMN_CREATED_BY),
                            rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
                            rs.getDate(DAOConstants.COLUMN_CREATED_STAMP),
                            rs.getDate(DAOConstants.COLUMN_LAST_UPDATED_STAMP));

                    rule.fromTabbedSearchTerms(rs.getString(DAOConstants.COLUMN_SEARCH_TERM));
                    rule.fromTabbedSuggestions(rs.getString(DAOConstants.COLUMN_SUGGEST));

                    return rule;
                }
            }));
        }
    }

    private enum DBAction {
        INSERT_N, UPDATE_N, UPDATE_M, UPDATE_P, UPDATE_D, DELETE_P, DELETE_L, NONE;

        private String getStatus() {
            switch (this) {
                case INSERT_N:
                case UPDATE_N:
                    return "new";
                case UPDATE_M:
                    return "modified";
                case UPDATE_P:
                    return "published";
                case UPDATE_D:
                    return "deleted";
                default:
                    return null;
            }
        }

        private static DBAction getAction(SpellRule published, SpellRule version, SpellRule current) {
            if (published == null) {
                if (version != null && current == null) {
                    return INSERT_N;
                } else if (version == null && current != null) {
                    return DELETE_P;
                } else if (version != null && !version.sameTermsWith(current)) {
                    return UPDATE_N;
                }
            } else if (version != null && (current == null || !version.sameTermsWith(current))) {
                return version.sameTermsWith(published) ? UPDATE_P : UPDATE_M;
            } else if (version == null && current != null) {
                return current.sameTermsWith(published) ? DELETE_L : UPDATE_D;
            }

            return NONE;
        }
    }
}
