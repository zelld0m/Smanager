package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.ReplaceKeywordMessageType;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value = "redirectRuleDAO")
public class RedirectRuleDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(RedirectRuleDAO.class);

    @Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;
    
    // needed by spring AOP
    public RedirectRuleDAO() {
    }

    @Autowired
    public RedirectRuleDAO(JdbcTemplate jdbcTemplate) {
        addRedirectRuleStoredProcedure = new AddRedirectRuleStoredProcedure(jdbcTemplate);
        getRedirectRuleStoredProcedure = new GetRedirectRuleStoredProcedure(jdbcTemplate);
        deleteRedirectRuleStoredProcedure = new DeleteRedirectRuleStoredProcedure(jdbcTemplate);
        updateRedirectRuleStoredProcedure = new UpdateRedirectRuleStoredProcedure(jdbcTemplate);
        getRedirectRuleKeywordStoredProcedure = new GetRedirectRuleKeywordStoredProcedure(jdbcTemplate);
        addRedirectRuleKeywordStoredProcedure = new AddRedirectRuleKeywordStoredProcedure(jdbcTemplate);
        deleteRedirectRuleKeywordStoredProcedure = new DeleteRedirectRuleKeywordStoredProcedure(jdbcTemplate);
        addRedirectRuleConditionStoredProcedure = new AddRedirectRuleConditionStoredProcedure(jdbcTemplate);
        updateRedirectRuleConditionStoredProcedure = new UpdateRedirectRuleConditionStoredProcedure(jdbcTemplate);
        deleteRedirectRuleConditionStoredProcedure = new DeleteRedirectRuleConditionStoredProcedure(jdbcTemplate);
        getRedirectRuleConditionStoredProcedure = new GetRedirectRuleConditionStoredProcedure(jdbcTemplate);
    }
    private GetRedirectRuleStoredProcedure getRedirectRuleStoredProcedure;
    private AddRedirectRuleStoredProcedure addRedirectRuleStoredProcedure;
    private DeleteRedirectRuleStoredProcedure deleteRedirectRuleStoredProcedure;
    private UpdateRedirectRuleStoredProcedure updateRedirectRuleStoredProcedure;
    private GetRedirectRuleKeywordStoredProcedure getRedirectRuleKeywordStoredProcedure;
    private AddRedirectRuleKeywordStoredProcedure addRedirectRuleKeywordStoredProcedure;
    private DeleteRedirectRuleKeywordStoredProcedure deleteRedirectRuleKeywordStoredProcedure;
    // returns the sequence number generated
    private AddRedirectRuleConditionStoredProcedure addRedirectRuleConditionStoredProcedure;
    // returns the sequence number passed
    private UpdateRedirectRuleConditionStoredProcedure updateRedirectRuleConditionStoredProcedure;
    private DeleteRedirectRuleConditionStoredProcedure deleteRedirectRuleConditionStoredProcedure;
    private GetRedirectRuleConditionStoredProcedure getRedirectRuleConditionStoredProcedure;

    private class GetRedirectRuleStoredProcedure extends GetStoredProcedure {

        public GetRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_REDIRECT);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RedirectRule>() {
                public RedirectRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String includeKeyword = rs.getString(DAOConstants.COLUMN_INCLUDE_KEYWORD);
                    Boolean isIncludeKeyword = StringUtils.isBlank(includeKeyword) ? null
                            : StringUtils.equalsIgnoreCase(includeKeyword, "Y") ? true : false;
                    return new RedirectRule(
                            rs.getString(DAOConstants.COLUMN_RULE_ID),
                            rs.getString(DAOConstants.COLUMN_REDIRECT_TYPE_ID),
                            rs.getString(DAOConstants.COLUMN_NAME),
                            rs.getString(DAOConstants.COLUMN_DESCRIPTION),
                            rs.getString(DAOConstants.COLUMN_STORE_ID),
                            rs.getInt(DAOConstants.COLUMN_PRIORITY),
                            rs.getString(DAOConstants.COLUMN_SEARCH_TERM),
                            rs.getString(DAOConstants.COLUMN_CONDITION),
                            rs.getString(DAOConstants.COLUMN_CREATED_BY),
                            rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
                            jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
                            jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
                            rs.getString(DAOConstants.COLUMN_CHANGE_KEYWORD),
                            rs.getString(DAOConstants.COLUMN_REDIRECT_URL),
                            isIncludeKeyword,
                            ReplaceKeywordMessageType.getByName(rs.getString(DAOConstants.COLUMN_REPLACE_KEYWORD_MESSAGE_TYPE)),
                            rs.getString(DAOConstants.COLUMN_REPLACE_KEYWORD_MESSAGE_CUSTOM_TEXT));
                }
            }));
        }
    }

    private class GetRedirectRuleKeywordStoredProcedure extends GetStoredProcedure {

        public GetRedirectRuleKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_REDIRECT);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RESULT, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RedirectRule>() {
                public RedirectRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new RedirectRule(
                            rs.getString(DAOConstants.COLUMN_RULE_ID),
                            rs.getString(DAOConstants.COLUMN_STORE_ID),
                            rs.getString(DAOConstants.COLUMN_NAME),
                            rs.getString(DAOConstants.COLUMN_SEARCH_TERM),
                            null);
                }
            }));
        }
    }

    private class AddRedirectRuleStoredProcedure extends CUDStoredProcedure {

        public AddRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_REDIRECT);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_REDIRECT_TYPE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_PRIORITY, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_REDIRECT_URL, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_INCLUDE_KEYWORD, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CHANGE_KEYWORD, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RK_MSG_TYPE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RK_MSG_CUSTOM_TEXT, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
        }
    }

    private class UpdateRedirectRuleStoredProcedure extends CUDStoredProcedure {

        public UpdateRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_UPDATE_REDIRECT);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_REDIRECT_TYPE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_NAME, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_REDIRECT_URL, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_INCLUDE_KEYWORD, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CHANGE_KEYWORD, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RK_MSG_TYPE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RK_MSG_CUSTOM_TEXT, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_PRIORITY, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_ACTIVE_FLAG, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class DeleteRedirectRuleStoredProcedure extends CUDStoredProcedure {

        public DeleteRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_DELETE_REDIRECT);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class AddRedirectRuleKeywordStoredProcedure extends CUDStoredProcedure {

        public AddRedirectRuleKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_REDIRECT_KEYWORD);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class AddRedirectRuleConditionStoredProcedure extends LoggerStoredProcedure {

        public AddRedirectRuleConditionStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_REDIRECT_CONDITION);
            declareParameters();
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_RESULT, new RowMapper<List<Integer>>() {
                public List<Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(rs.getInt(DAOConstants.COLUMN_RESULT));
                    list.add(rs.getInt(DAOConstants.COLUMN_SEQUENCE_NUMBER));
                    return list;
                }
            }));
            compile();
        }

        private void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CONDITION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class UpdateRedirectRuleConditionStoredProcedure extends CUDStoredProcedure {

        public UpdateRedirectRuleConditionStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_UPDATE_REDIRECT_CONDITION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CONDITION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class GetRedirectRuleConditionStoredProcedure extends GetStoredProcedure {

        public GetRedirectRuleConditionStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_REDIRECT);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM_LIKE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RESULT, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RedirectRuleCondition>() {
                public RedirectRuleCondition mapRow(ResultSet rs, int rowNum) throws SQLException {
                    RedirectRuleCondition rr = new RedirectRuleCondition(
                            rs.getString(DAOConstants.COLUMN_RULE_ID),
                            rs.getInt(DAOConstants.COLUMN_SEQUENCE_NUMBER),
                            rs.getString(DAOConstants.COLUMN_CONDITION));
                    rr.setStoreId(rs.getString(DAOConstants.COLUMN_STORE_ID));
                    return rr;
                }
            }));
        }
    }

    private class DeleteRedirectRuleKeywordStoredProcedure extends CUDStoredProcedure {

        public DeleteRedirectRuleKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_DELETE_REDIRECT_KEYWORD);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    private class DeleteRedirectRuleConditionStoredProcedure extends CUDStoredProcedure {

        public DeleteRedirectRuleConditionStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_DELETE_REDIRECT_CONDITION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
        }
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.delete)
    public int deleteRedirectRule(RedirectRule rule) {
        // TODO: add validation
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
        inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());
        return DAOUtils.getUpdateCount(deleteRedirectRuleStoredProcedure.execute(inputs));
    }

    public RecordSet<RedirectRule> getRedirectRules(SearchCriteria<RedirectRule> criteria) throws DaoException {
        try {
            RedirectRule redirectRule = criteria.getModel();
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, redirectRule.getRuleId());
            inputs.put(DAOConstants.PARAM_RULE_NAME, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, null);
            inputs.put(DAOConstants.PARAM_STORE_ID, redirectRule.getStoreId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, redirectRule.getSearchTerm());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, null);
            inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
            return DAOUtils.getRecordSet(getRedirectRuleStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during getRedirectrule()", e);
        }
    }

    public RedirectRule getRedirectRule(RedirectRule redirectRule) throws DaoException {
        RecordSet<RedirectRule> rules = getRedirectRules(new SearchCriteria<RedirectRule>(redirectRule, null, null, 1, 1));
        return (rules.getTotalSize() > 0 ? rules.getList().get(0) : null);
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.add)
    public int addRedirectRule(RedirectRule rule) throws DaoException {
        // TODO: add validation
        int result = -1;
        try {
            result = (StringUtils.isEmpty(addRedirectRuleAndGetId(rule))) ? 0 : 1;
        } catch (Exception e) {
            throw new DaoException("Failed during addRedirectRule()", e);
        }
        return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.add)
    public String addRedirectRuleAndGetId(RedirectRule rule) throws DaoException {
        // TODO: add validation
        try {
            String id = rule.getRuleId();
            if (StringUtils.isEmpty(id)) {
                id = DAOUtils.generateUniqueId();
            }
            Map<String, Object> inputs = new HashMap<String, Object>();
            rule.setRuleId(id);
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_REDIRECT_TYPE_ID, rule.getRedirectTypeId());
            inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
            inputs.put(DAOConstants.PARAM_DESCRIPTION, rule.getDescription());
            inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
            inputs.put(DAOConstants.PARAM_RULE_PRIORITY, rule.getPriority());
            inputs.put(DAOConstants.PARAM_REDIRECT_URL, rule.getRedirectUrl());
            inputs.put(DAOConstants.PARAM_INCLUDE_KEYWORD, rule.getIncludeKeyword() == null ? null : rule.getIncludeKeyword() ? "Y" : "N");
            inputs.put(DAOConstants.PARAM_CHANGE_KEYWORD, rule.getChangeKeyword());
            inputs.put(DAOConstants.PARAM_RK_MSG_TYPE, rule.getReplaceKeywordMessageType() != null ? rule.getReplaceKeywordMessageType().name() : ReplaceKeywordMessageType.DEFAULT.name());
            inputs.put(DAOConstants.PARAM_RK_MSG_CUSTOM_TEXT, rule.getReplaceKeywordMessageCustomText());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getCreatedBy());
            inputs.put(DAOConstants.PARAM_CREATED_BY, rule.getCreatedBy());
            if (DAOUtils.getUpdateCount(addRedirectRuleStoredProcedure.execute(inputs)) > 0) {
                return id;
            }
        } catch (Exception e) {
            throw new DaoException("Failed during addRedirectRuleAndGetId()", e);
        }
        return null;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.update)
    public int updateRedirectRule(RedirectRule rule) throws DaoException {
        // TODO: add validation
        int result = -1;
        try {
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_REDIRECT_TYPE_ID, rule.getRedirectTypeId());
            inputs.put(DAOConstants.PARAM_NAME, rule.getRuleName());
            inputs.put(DAOConstants.PARAM_DESCRIPTION, rule.getDescription());
            inputs.put(DAOConstants.PARAM_REDIRECT_URL, rule.getRedirectUrl());
            inputs.put(DAOConstants.PARAM_INCLUDE_KEYWORD, rule.getIncludeKeyword() == null ? null : rule.getIncludeKeyword() ? "Y" : "N");
            inputs.put(DAOConstants.PARAM_CHANGE_KEYWORD, rule.getChangeKeyword());
            inputs.put(DAOConstants.PARAM_RK_MSG_TYPE, rule.getReplaceKeywordMessageType() != null ? rule.getReplaceKeywordMessageType().name() : null);
            inputs.put(DAOConstants.PARAM_RK_MSG_CUSTOM_TEXT, rule.getReplaceKeywordMessageCustomText());
            inputs.put(DAOConstants.PARAM_RULE_PRIORITY, rule.getPriority());
            inputs.put(DAOConstants.PARAM_ACTIVE_FLAG, "ENABLED");
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());
            result = DAOUtils.getUpdateCount(updateRedirectRuleStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during updateRedirectRule()", e);
        }
        return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.mapKeyword)
    public int addRedirectKeyword(RedirectRule rule) throws DaoException {
        int result = -1;
        try {
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerm());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());
            result = DAOUtils.getUpdateCount(addRedirectRuleKeywordStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during addRedirectKeyword()", e);
        }
        return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.unmapKeyword)
    public int removeRedirectKeyword(RedirectRule rule) throws DaoException {
        int result = -1;
        try {
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerm());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());
            result = DAOUtils.getUpdateCount(deleteRedirectRuleKeywordStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during removeRedirectKeyword()", e);
        }
        return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.addCondition)
    public int addRedirectCondition(RedirectRuleCondition rule) throws DaoException {
        try {
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, rule.getSequenceNumber());
            inputs.put(DAOConstants.PARAM_CONDITION, rule.getCondition());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());

            int i = -1;
            Map<String, Object> resultMap = addRedirectRuleConditionStoredProcedure.execute(inputs);
            if (resultMap != null) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Integer> resultList = ((List<List<Integer>>) resultMap.get(DAOConstants.RESULT_SET_RESULT)).get(0);
                    if (resultList != null) {
                        i = resultList.get(0);
                        if (i > 0) {
                            return resultList.get(1);
                        }
                    }
                } catch (Exception e) {
                    logger.error("failed to get update count", e);
                }
            }
            return i;
        } catch (Exception e) {
            throw new DaoException("Failed during addRedirectCondition()", e);
        }
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.updateCondition)
    public int updateRedirectCondition(RedirectRuleCondition rule) throws DaoException {
        int result = -1;
        try {
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, rule.getSequenceNumber());
            inputs.put(DAOConstants.PARAM_CONDITION, rule.getCondition());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());
            result = DAOUtils.getUpdateCount(updateRedirectRuleConditionStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during updateRedirectCondition()", e);
        }
        return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.removeCondition)
    public int removeRedirectCondition(RedirectRuleCondition rule) throws DaoException {
        int result = -1;
        try {
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
            inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, rule.getSequenceNumber());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getLastModifiedBy());
            result = DAOUtils.getUpdateCount(deleteRedirectRuleConditionStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during removeRedirectCondition()", e);
        }
        return result;
    }

    public RecordSet<StoreKeyword> getRedirectKeywords(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType, ExactMatch keywordExactMatch) throws DaoException {
        try {
            RedirectRule redirectRule = criteria.getModel();
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, null);
            inputs.put(DAOConstants.PARAM_STORE_ID, redirectRule.getStoreId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, null);
            inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, null);
            inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
            inputs.put(DAOConstants.PARAM_RESULT, 1); // return keywords in separate records
            switch (redirectMatchType) {
                case MATCH_NAME:
                    inputs.put(DAOConstants.PARAM_RULE_NAME, redirectRule.getRuleName());
                    break;
                case LIKE_NAME:
                    inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, redirectRule.getRuleName());
                    break;
                case MATCH_ID:
                    inputs.put(DAOConstants.PARAM_RULE_ID, redirectRule.getRuleId());
                    break;
            }
            switch (keywordExactMatch) {
                case MATCH:
                    inputs.put(DAOConstants.PARAM_SEARCH_TERM, redirectRule.getSearchTerm());
                    break;
                case SIMILAR:
                    inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, redirectRule.getSearchTerm());
                    break;
            }
            RecordSet<RedirectRule> ruleSet = DAOUtils.getRecordSet(getRedirectRuleKeywordStoredProcedure.execute(inputs));
            List<StoreKeyword> list = new ArrayList<StoreKeyword>();
            if (ruleSet.getTotalSize() > 0) {
                for (RedirectRule rule : ruleSet.getList()) {
                    // TODO: temp fix until DBA fixes SP to no return null search terms
                    if (StringUtils.isNotBlank(rule.getSearchTerm())) {
                        list.add(new StoreKeyword(rule.getStoreId(), rule.getSearchTerm()));
                    }
                }
            }
            return new RecordSet<StoreKeyword>(list, ruleSet.getTotalSize());
        } catch (Exception e) {
            throw new DaoException("Failed during getRedirectKeywords()", e);
        }
    }

    public RecordSet<RedirectRuleCondition> getRedirectConditions(SearchCriteria<RedirectRule> criteria) throws DaoException {
        try {
            RedirectRule redirectRule = criteria.getModel();
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, StringUtils.trimToNull(redirectRule.getRuleId()));
            inputs.put(DAOConstants.PARAM_RULE_NAME, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, null);
            inputs.put(DAOConstants.PARAM_STORE_ID, redirectRule.getStoreId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, null);
            inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, null);
            inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
            inputs.put(DAOConstants.PARAM_RESULT, 2); // return conditions in separate records
            return DAOUtils.getRecordSet(getRedirectRuleConditionStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during getRedirectConditions()", e);
        }
    }

    public RecordSet<RedirectRule> getRedirectForKeywords(SearchCriteria<StoreKeyword> criteria) throws DaoException {
        try {
            StoreKeyword storeKeyword = criteria.getModel();
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, null);
            inputs.put(DAOConstants.PARAM_STORE_ID, storeKeyword.getStoreId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, storeKeyword.getKeywordId());
            inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
            inputs.put(DAOConstants.PARAM_RESULT, 1);
            return DAOUtils.getRecordSet(getRedirectRuleKeywordStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during getRedirectForKeywords()", e);
        }
    }

    public RecordSet<RedirectRule> searchRedirectRules(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType) throws DaoException {
        try {
            RedirectRule model = criteria.getModel();
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, null);
            inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, null);
            inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, null);
            inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
            switch (redirectMatchType) {
                case MATCH_NAME:
                    inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
                    break;
                case LIKE_NAME:
                    inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, model.getRuleName());
                    break;
                case MATCH_ID:
                    inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
                    break;
            }
            return DAOUtils.getRecordSet(getRedirectRuleStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during searchRedirectRules()", e);
        }
    }

    public RecordSet<RedirectRule> searchRedirectRuleKeywords(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType, ExactMatch keywordExactMatch) throws DaoException {
        try {
            RedirectRule model = criteria.getModel();
            Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RULE_ID, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME, null);
            inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, null);
            inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
            inputs.put(DAOConstants.PARAM_SEARCH_TERM, null);
            inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, null);
            inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
            inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
            inputs.put(DAOConstants.PARAM_RESULT, 1);
            switch (redirectMatchType) {
                case MATCH_NAME:
                    inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
                    break;
                case LIKE_NAME:
                    inputs.put(DAOConstants.PARAM_RULE_NAME_LIKE, model.getRuleName());
                    break;
                case MATCH_ID:
                    inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
                    break;
            }
            switch (keywordExactMatch) {
                case MATCH:
                    inputs.put(DAOConstants.PARAM_SEARCH_TERM, model.getSearchTerm());
                    break;
                case SIMILAR:
                    inputs.put(DAOConstants.PARAM_SEARCH_TERM_LIKE, model.getSearchTerm());
                    break;
            }
            return DAOUtils.getRecordSet(getRedirectRuleKeywordStoredProcedure.execute(inputs));
        } catch (Exception e) {
            throw new DaoException("Failed during searchRedirectRuleKeywords()", e);
        }
    }
}