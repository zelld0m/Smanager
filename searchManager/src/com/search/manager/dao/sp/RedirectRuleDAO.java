package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

public class RedirectRuleDAO {

	// needed by spring AOP
	public RedirectRuleDAO(){
	}
	
	private GetRedirectRuleStoredProcedure getRedirectRuleStoredProcedure;
	private AddRedirectRuleStoredProcedure addRedirectRuleStoredProcedure;
	private DeleteRedirectRuleStoredProcedure deleteRedirectRuleStoredProcedure;
	private UpdateRedirectRuleStoredProcedure updateRedirectRuleStoredProcedure;
	private static int maxId = 0;
	private final static String SQL_MAX_ID = "select max(rule_id) from redirect_rule";
	
	public RedirectRuleDAO(JdbcTemplate jdbcTemplate) {
		addRedirectRuleStoredProcedure = new AddRedirectRuleStoredProcedure(jdbcTemplate);
		getRedirectRuleStoredProcedure = new GetRedirectRuleStoredProcedure(jdbcTemplate);
		deleteRedirectRuleStoredProcedure = new DeleteRedirectRuleStoredProcedure(jdbcTemplate);
		updateRedirectRuleStoredProcedure = new UpdateRedirectRuleStoredProcedure(jdbcTemplate);
		new MaxId(jdbcTemplate);
    }

	private class MaxId extends JdbcDaoSupport {
		public MaxId(JdbcTemplate jdbcTemplate){
			maxId = jdbcTemplate.queryForInt(SQL_MAX_ID);
		}
	}
	
	private class GetRedirectRuleStoredProcedure extends StoredProcedure {
	    public GetRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_REDIRECT);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RedirectRule>() {
	        	public RedirectRule mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new RedirectRule(
	                		rs.getInt(DAOConstants.COLUMN_RULE_ID), 
	                		rs.getString(DAOConstants.COLUMN_NAME), 
	                		rs.getString(DAOConstants.COLUMN_STORE_ID),
	                		rs.getInt(DAOConstants.COLUMN_PRIORITY), 
	                		rs.getString(DAOConstants.COLUMN_SEARCH_TERM),
	                		rs.getString(DAOConstants.COLUMN_CONDITION),
	                		rs.getInt(DAOConstants.COLUMN_ACTIVE_FLAG), 
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY), 
	                		rs.getDate(DAOConstants.COLUMN_CREATED_DATE),
	                		rs.getDate(DAOConstants.COLUMN_LAST_MODIFIED_DATE)	                		
	                		);
	        	}
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}

	private class AddRedirectRuleStoredProcedure extends StoredProcedure {
	    public AddRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_REDIRECT);
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CONDITION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ACTIVE_FLAG, Types.TINYINT));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class UpdateRedirectRuleStoredProcedure extends StoredProcedure {
	    public UpdateRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_REDIRECT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CONDITION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ACTIVE_FLAG, Types.TINYINT));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}

	private class DeleteRedirectRuleStoredProcedure extends StoredProcedure {
	    public DeleteRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_REDIRECT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.INTEGER));
	        compile();
	    }
	}
	
    @Audit(entity = Entity.queryCleaning, operation = Operation.delete)
    public int deleteRedirectRule(RedirectRule rule) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
		inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
        return DAOUtils.getUpdateCount(deleteRedirectRuleStoredProcedure.execute(inputs));
    }	

    public RecordSet<RedirectRule> getRedirectrule(String searchTerm, Integer ruleId, String storeId, Integer startRow, Integer endRow) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_ID, ruleId);
			inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
			inputs.put(DAOConstants.PARAM_SEARCH_TERM, StringUtils.isNotBlank(searchTerm)?searchTerm:null);
			inputs.put(DAOConstants.PARAM_START_ROW, startRow);
			inputs.put(DAOConstants.PARAM_END_ROW, endRow);
			return DAOUtils.getRecordSet(getRedirectRuleStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getRedirectrule()", e);
		}
    }	

    @Audit(entity = Entity.queryCleaning, operation = Operation.add)
    public int addRedirectRule(RedirectRule rule) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			synchronized (this) {
				rule.setRuleId(++maxId);
			}
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
			inputs.put(DAOConstants.PARAM_RULE_PRIORITY, rule.getPriority());
			inputs.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerm());
			inputs.put(DAOConstants.PARAM_CONDITION, rule.getCondition());
			inputs.put(DAOConstants.PARAM_ACTIVE_FLAG, rule.getActiveFlag());
			inputs.put(DAOConstants.PARAM_CREATED_BY, rule.getCreatedBy());
			result = DAOUtils.getUpdateCount(addRedirectRuleStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRedirectRule()", e);
    	}
    	return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.update)
    public int updateRedirectRule(RedirectRule rule) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
			inputs.put(DAOConstants.PARAM_RULE_PRIORITY, rule.getPriority());
			inputs.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerm());
			inputs.put(DAOConstants.PARAM_ACTIVE_FLAG, rule.getActiveFlag());
			inputs.put(DAOConstants.PARAM_CONDITION, rule.getCondition());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getModifiedBy());
			result = DAOUtils.getUpdateCount(updateRedirectRuleStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRedirectRule()", e);
    	}
    	return result;
    }
}