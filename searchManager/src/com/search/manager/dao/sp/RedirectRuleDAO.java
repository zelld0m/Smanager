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

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="redirectRuleDAO")
public class RedirectRuleDAO {

	// needed by spring AOP
	public RedirectRuleDAO(){}
	
	@Autowired
	public RedirectRuleDAO(JdbcTemplate jdbcTemplate) {
		addRedirectRuleStoredProcedure = new AddRedirectRuleStoredProcedure(jdbcTemplate);
		getRedirectRuleStoredProcedure = new GetRedirectRuleStoredProcedure(jdbcTemplate);
		deleteRedirectRuleStoredProcedure = new DeleteRedirectRuleStoredProcedure(jdbcTemplate);
		updateRedirectRuleStoredProcedure = new UpdateRedirectRuleStoredProcedure(jdbcTemplate);
	}

	private GetRedirectRuleStoredProcedure getRedirectRuleStoredProcedure;
	private AddRedirectRuleStoredProcedure addRedirectRuleStoredProcedure;
	private DeleteRedirectRuleStoredProcedure deleteRedirectRuleStoredProcedure;
	private UpdateRedirectRuleStoredProcedure updateRedirectRuleStoredProcedure;
	
	private class GetRedirectRuleStoredProcedure extends GetStoredProcedure {
	    public GetRedirectRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_REDIRECT);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RedirectRule>() {
	        	public RedirectRule mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new RedirectRule(
	                		rs.getString(DAOConstants.COLUMN_RULE_ID), 
	                		rs.getString(DAOConstants.COLUMN_REDIRECT_TYPE_ID), 
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CONDITION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ACTIVE_FLAG, Types.TINYINT));
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TERM, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CONDITION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ACTIVE_FLAG, Types.TINYINT));
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
		}
	}
	
    @Audit(entity = Entity.queryCleaning, operation = Operation.delete)
    public int deleteRedirectRule(RedirectRule rule) {
		// TODO: add validation
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
		inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
        return DAOUtils.getUpdateCount(deleteRedirectRuleStoredProcedure.execute(inputs));
    }	

    public RecordSet<RedirectRule> getRedirectRules(SearchCriteria<RedirectRule> criteria) throws DaoException {
		try {
			RedirectRule redirectRule = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_ID, StringUtils.trimToNull(redirectRule.getRuleId()));
	        inputs.put(DAOConstants.PARAM_STORE_ID, redirectRule.getStoreId());
			inputs.put(DAOConstants.PARAM_SEARCH_TERM, StringUtils.trimToNull(redirectRule.getSearchTerm()));	        
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getRedirectRuleStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getRedirectrule()", e);
		}
    }	

    public RedirectRule getRedirectRule(SearchCriteria<RedirectRule> criteria) throws DaoException {
    	RecordSet<RedirectRule> rules = getRedirectRules(criteria);
    	return (rules.getTotalSize() > 0 ? rules.getList().get(0): null);
    }
    
    @Audit(entity = Entity.queryCleaning, operation = Operation.add)
    public int addRedirectRule(RedirectRule rule) throws DaoException {
		// TODO: add validation
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			rule.setRuleId(DAOUtils.generateUniqueId());
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_REDIRECT_TYPE_ID, rule.getRedirectTypeId());
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
		// TODO: add validation
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_REDIRECT_TYPE_ID, rule.getRedirectTypeId());
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