package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.google.common.collect.Lists;
import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.StringUtil;
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

        getSpellRuleVersionProcedure = new GetSpellRuleVersionProcedure(jdbcTemplate);
        addSpellRuleVersionProcedure = new AddSpellRuleVersionProcedure(jdbcTemplate);
        deleteSpellRuleVersionProcedure = new DeleteSpellRuleVersionProcedure(jdbcTemplate);
        restoreSpellRuleVersionProcedure = new RestoreSpellRuleVersionProcedure(jdbcTemplate);
        publishSpellRuleProcedure = new PublishSpellRuleProcedure(jdbcTemplate);
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
        return Integer.parseInt(StringUtils.defaultIfBlank(
                ConfigManager.getInstance().getStoreSetting(store, "maxSpellSuggestions"), "3"));
    }

    @Audit(entity = Entity.spell, operation = Operation.updateSetting)
    public boolean setMaxSuggest(String store, Integer maxSuggest) {
        return ConfigManager.getInstance().setStoreSetting(store, "maxSpellSuggestions", String.valueOf(maxSuggest));
    }

    @Transactional
    public boolean restoreSpellRules(String store, int version, String username) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_STORE_ID, store);
            params.put(DAOConstants.PARAM_VERSION_NO, version);
            params.put(DAOConstants.PARAM_MODIFIED_BY, username);
            params.put(DAOConstants.PARAM_STORE_DST, null);

            //return DAOUtils.getUpdateCount(restoreSpellRuleVersionProcedure.execute(params)) > 0;
            restoreSpellRuleVersionProcedure.execute(params);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred on restoreSpellRules.", e);
            throw new DaoException("Error occurred on restoreSpellRules.", e);
        }
    }

    /*
     * This method includes the creation of published version in DB and the creation of rule xml file that will be
     * used by WS when updating rules in SOLR. If any of these steps should fail, the whole process should rollback.
     * Hence, we mark this method transactional. All steps needed to publish spell rules should be included here.
     */
    @Transactional
    public boolean publishSpellRules(String store) throws DaoException {
        try {
            // Publish spell rule in DB
            Map<String, Object> publishParams = new HashMap<String, Object>();
            publishParams.put(DAOConstants.PARAM_STORE_ID, store);
            //result = DAOUtils.getUpdateCount(publishSpellRuleProcedure.execute(publishParams));
            publishSpellRuleProcedure.execute(publishParams);

            // Get published spell rule and create xml file to be transfered to WS.
            Map<String, Object> getParams = new HashMap<String, Object>();
            getParams.put(DAOConstants.PARAM_STORE_ID, store);
            getParams.put(DAOConstants.PARAM_VERSION_NO, 0);
            RecordSet<SpellRule> records = DAOUtils.getRecordSet(getSpellRuleVersionProcedure.execute(getParams));
            List<SpellRule> spellRules = records.getList();
            int maxSuggest = getMaxSuggest(store);

            SpellRules spellRulesXml = new SpellRules(store, 0, "Did You Mean", "", UtilityService.getUsername(),
                    new Date(), "spell_rule", maxSuggest, Lists.transform(spellRules, SpellRule.transformer));

            RuleXmlUtil.ruleXmlToFile(store, RuleEntity.SPELL, "spell_rule_" + StringUtil.dateToStr(new Date(), "yyyyMMdd_hhmmss"), spellRulesXml, RuleVersionUtil.PUBLISH_PATH);
            ConfigManager.getInstance().setPublishedStoreLinguisticSetting(store, "maxSpellSuggestions",
                    String.valueOf(daoService.getMaxSuggest(store)));
            return true;
        } catch (Exception e) {
            logger.error("Error in publishing spell rules.", e);
            throw new DaoException("Error in publishing spell rules.", e);
        }
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

    // Versioning related procedures
    private GetSpellRuleVersionProcedure getSpellRuleVersionProcedure;
    private AddSpellRuleVersionProcedure addSpellRuleVersionProcedure;
    private DeleteSpellRuleVersionProcedure deleteSpellRuleVersionProcedure;
    private RestoreSpellRuleVersionProcedure restoreSpellRuleVersionProcedure;
    private PublishSpellRuleProcedure publishSpellRuleProcedure;

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
                    return transform(rs);
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
                    return transform(rs);
                }
            }));
        }
    }

    private class GetSpellRuleVersionProcedure extends GetStoredProcedure {
        public GetSpellRuleVersionProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_SPELL_RULE_VERSION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_VERSION_NO, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<SpellRule>() {
                public SpellRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return transform(rs);
                }
            }));
        }
    }

    private class AddSpellRuleVersionProcedure extends CUDStoredProcedure {
        public AddSpellRuleVersionProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_SPELL_RULE_VERSION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_VERSION_NO, Types.INTEGER));
        }
    }

    private class DeleteSpellRuleVersionProcedure extends CUDStoredProcedure {
        public DeleteSpellRuleVersionProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_DELETE_SPELL_RULE_VERSION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_VERSION_NO, Types.INTEGER));
        }
    }

    private class RestoreSpellRuleVersionProcedure extends CUDStoredProcedure {
        public RestoreSpellRuleVersionProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_RESTORE_SPELL_RULE_VERSION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_VERSION_NO, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_DST, Types.VARCHAR));
        }
    }

    private class PublishSpellRuleProcedure extends CUDStoredProcedure {
        public PublishSpellRuleProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_PUBLISH_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
        }
    }

    private static final SpellRule transform(ResultSet rs) throws SQLException {
        SpellRule rule = new SpellRule(rs.getString(DAOConstants.COLUMN_RULE_ID),
                rs.getString(DAOConstants.COLUMN_STORE_ID), rs.getString(DAOConstants.COLUMN_STATUS), null, null,
                rs.getString(DAOConstants.COLUMN_CREATED_BY), rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
                rs.getDate(DAOConstants.COLUMN_CREATED_STAMP), rs.getDate(DAOConstants.COLUMN_LAST_UPDATED_STAMP));

        rule.fromTabbedSearchTerms(rs.getString(DAOConstants.COLUMN_SEARCH_TERM));
        rule.fromTabbedSuggestions(rs.getString(DAOConstants.COLUMN_SUGGEST));

        return rule;
    }

    public boolean addSpellRuleVersion(String store, int versionNo) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_STORE_ID, store);
            params.put(DAOConstants.PARAM_VERSION_NO, versionNo);

            //return DAOUtils.getUpdateCount(addSpellRuleVersionProcedure.execute(params)) > 0;
            addSpellRuleVersionProcedure.execute(params);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred in addSpellRuleVersion.", e);
            throw new DaoException("Error occurred in addSpellRuleVersion.", e);
        }
    }

    public boolean deleteSpellRuleVersions(String store, int versionNo) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_STORE_ID, store);
            params.put(DAOConstants.PARAM_VERSION_NO, versionNo);

            //return DAOUtils.getUpdateCount(deleteSpellRuleVersionProcedure.execute(params)) > 0;
            deleteSpellRuleVersionProcedure.execute(params);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred in deleteSpellRuleVersions.", e);
            throw new DaoException("Error occurred in deleteSpellRuleVersions.", e);
        }
    }

    public List<SpellRule> getSpellRuleVersion(String store, int versionNo) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_STORE_ID, store);
            params.put(DAOConstants.PARAM_VERSION_NO, versionNo);

            RecordSet<SpellRule> records = DAOUtils.getRecordSet(getSpellRuleVersionProcedure.execute(params));
            List<SpellRule> spellRules = Collections.emptyList();

            if (records != null) {
                spellRules = records.getList();
            }

            return spellRules;
        } catch (Exception e) {
            logger.error("Error occurred in getSpellRuleVersion.", e);
            throw new DaoException("Error occurred in getSpellRuleVersion.", e);
        }
    }

	public boolean importRule(String dest, String origin, String username) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(DAOConstants.PARAM_STORE_ID, origin);
            params.put(DAOConstants.PARAM_VERSION_NO, 0);
            params.put(DAOConstants.PARAM_MODIFIED_BY, username);
            params.put(DAOConstants.PARAM_STORE_DST, dest);

            //return DAOUtils.getUpdateCount(restoreSpellRuleVersionProcedure.execute(params)) > 0;
            restoreSpellRuleVersionProcedure.execute(params);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred on importRule.", e);
            throw new DaoException("Error occurred on importRule.", e);
        }
    }
}
