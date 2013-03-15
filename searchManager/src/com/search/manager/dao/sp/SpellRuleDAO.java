package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;

@Repository("spellRuleDAO")
public class SpellRuleDAO {

    private static final char DELIMITER = '|';
    private GetSpellRuleStoredProcedure getSpellRuleStoredProcedure;
    private CreateSpellRuleStoredProcedure createSpellRuleStoredProcedure;

    @Autowired
    public SpellRuleDAO(JdbcTemplate jdbcTemplate) {
        getSpellRuleStoredProcedure = new GetSpellRuleStoredProcedure(jdbcTemplate);
        createSpellRuleStoredProcedure = new CreateSpellRuleStoredProcedure(jdbcTemplate);
    }

    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria) throws DaoException {
        try {
            SpellRule rule = criteria.getModel();
            Map<String, Object> input = new HashMap<String, Object>();

            input.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            input.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
            input.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerms() != null ? rule.getSearchTerms()[0] : null);
            input.put(DAOConstants.PARAM_SUGGESTION, rule.getSuggestions() != null ? rule.getSuggestions()[0] : null);
            input.put(DAOConstants.PARAM_STATUS, rule.getStatus());
            input.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            input.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());

            return DAOUtils.getRecordSet(getSpellRuleStoredProcedure.execute(input));
        } catch (Exception e) {
            throw new DaoException("Failed during getSpellRule()", e);
        }
    }

    public String addSpellRuleAndGetId(SpellRule rule) throws DaoException {
        String retVal = null;

        try {
            String ruleId = DAOUtils.generateUniqueId();
            Map<String, Object> input = new HashMap<String, Object>();

            input.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            input.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
            input.put(DAOConstants.PARAM_SEARCH_TERM, StringUtils.join(rule.getSearchTerms(), DELIMITER));
            input.put(DAOConstants.PARAM_SUGGESTION, StringUtils.join(rule.getSuggestions(), DELIMITER));
            input.put(DAOConstants.PARAM_CREATED_BY, rule.getCreatedBy());
            input.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());

            if (DAOUtils.getUpdateCount(createSpellRuleStoredProcedure.execute(input)) > 0) {
                retVal = ruleId;
            }
        } catch (Exception e) {
            throw new DaoException("Failed during addSpellRuleAndGetId()", e);
        }

        return retVal;
    }

    public void updateSpellRule(SpellRule rule) throws DaoException {

    }

    public boolean checkDuplicateSearchTerm(String ruleId, String storeId, String searchTerm) throws DaoException {
        return false;
    }

    // @formatter:off
    //=======================================================//
    //                STORED PROCEDURE CLASSES               //
    //=======================================================//
    // @formatter:on

    private class GetSpellRuleStoredProcedure extends GetStoredProcedure {
        public GetSpellRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SUGGESTION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STATUS, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<SpellRule>() {
                @Override
                public SpellRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new SpellRule(rs.getString(DAOConstants.COLUMN_RULE_ID),
                            rs.getString(DAOConstants.COLUMN_STORE_ID), rs.getString(DAOConstants.COLUMN_STATUS),
                            StringUtils.split(rs.getString(DAOConstants.COLUMN_SEARCH_TERM), DELIMITER),
                            StringUtils.split(rs.getString(DAOConstants.COLUMN_SUGGESTION), DELIMITER));
                }
            }));
        }
    }

    private class CreateSpellRuleStoredProcedure extends CUDStoredProcedure {

        public CreateSpellRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_SPELL_RULE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SUGGESTION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }
}
