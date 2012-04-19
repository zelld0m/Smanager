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
import com.search.manager.model.RuleStatus;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="ruleStatusDAO")
public class RuleStatusDAO {

	// needed by spring AOP
	public RuleStatusDAO(){}
	
	@Autowired
	public RuleStatusDAO(JdbcTemplate jdbcTemplate) {
		addRuleStatusStoredProcedure = new AddRuleStatusStoredProcedure(jdbcTemplate);
		getRuleStatusStoredProcedure = new GetRuleStatusStoredProcedure(jdbcTemplate);
		deleteRuleStatusStoredProcedure = new DeleteRuleStatusStoredProcedure(jdbcTemplate);
		updateRuleStatusStoredProcedure = new UpdateRuleStatusStoredProcedure(jdbcTemplate);
	}

	private GetRuleStatusStoredProcedure getRuleStatusStoredProcedure;
	private AddRuleStatusStoredProcedure addRuleStatusStoredProcedure;
	private DeleteRuleStatusStoredProcedure deleteRuleStatusStoredProcedure;
	private UpdateRuleStatusStoredProcedure updateRuleStatusStoredProcedure;
	
	private class GetRuleStatusStoredProcedure extends GetStoredProcedure {
	    public GetRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
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
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RuleStatus>() {
	        	public RuleStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new RuleStatus(
//	                		rs.getString(DAOConstants.COLUMN_RULE_ID), 
//	                		rs.getString(DAOConstants.COLUMN_REDIRECT_TYPE_ID), 
//	                		rs.getString(DAOConstants.COLUMN_NAME), 
//	                		rs.getString(DAOConstants.COLUMN_STORE_ID),
//	                		rs.getInt(DAOConstants.COLUMN_PRIORITY), 
//	                		rs.getString(DAOConstants.COLUMN_SEARCH_TERM),
//	                		rs.getString(DAOConstants.COLUMN_CONDITION),
//	                		rs.getInt(DAOConstants.COLUMN_ACTIVE_FLAG), 
//	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
//	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY), 
//	                		rs.getDate(DAOConstants.COLUMN_CREATED_DATE),
//	                		rs.getDate(DAOConstants.COLUMN_LAST_MODIFIED_DATE)	                		
	                		);
	        	}
	        }));
		}
	}

	private class AddRuleStatusStoredProcedure extends CUDStoredProcedure {
	    public AddRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
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
	
	private class UpdateRuleStatusStoredProcedure extends CUDStoredProcedure {
	    public UpdateRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
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

	private class DeleteRuleStatusStoredProcedure extends CUDStoredProcedure {
	    public DeleteRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_REDIRECT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
		}
	}
	
    @Audit(entity = Entity.queryCleaning, operation = Operation.delete)
    public int deleteRuleStatus(RuleStatus rule) {
		Map<String, Object> inputs = new HashMap<String, Object>();
//		inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
//		inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
        return DAOUtils.getUpdateCount(deleteRuleStatusStoredProcedure.execute(inputs));
    }	

    public RecordSet<RuleStatus> getRuleStatus(String ruleTypeId, String publishedStatus, String approvedStatus, Integer startRow, Integer endRow) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
//			inputs.put(DAOConstants.PARAM_RULE_ID, StringUtils.isNotBlank(ruleId)?ruleId:null);
//			inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
//			inputs.put(DAOConstants.PARAM_SEARCH_TERM, StringUtils.isNotBlank(searchTerm)?searchTerm:null);
			inputs.put(DAOConstants.PARAM_START_ROW, startRow);
			inputs.put(DAOConstants.PARAM_END_ROW, endRow);
			return DAOUtils.getRecordSet(getRuleStatusStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getRuleStatus()", e);
		}
    }	

    @Audit(entity = Entity.queryCleaning, operation = Operation.add)
    public int addRuleStatus(RuleStatus rule) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
//			rule.setRuleId(DAOUtils.generateUniqueId());
//			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
//			inputs.put(DAOConstants.PARAM_REDIRECT_TYPE_ID, rule.getRedirectTypeId());
//			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
//			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
//			inputs.put(DAOConstants.PARAM_RULE_PRIORITY, rule.getPriority());
//			inputs.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerm());
//			inputs.put(DAOConstants.PARAM_CONDITION, rule.getCondition());
//			inputs.put(DAOConstants.PARAM_ACTIVE_FLAG, rule.getActiveFlag());
//			inputs.put(DAOConstants.PARAM_CREATED_BY, rule.getCreatedBy());
			result = DAOUtils.getUpdateCount(addRuleStatusStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRuleStatus()", e);
    	}
    	return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.update)
    public int updateRuleStatus(RuleStatus rule) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
//			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
//			inputs.put(DAOConstants.PARAM_REDIRECT_TYPE_ID, rule.getRedirectTypeId());
//			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
//			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
//			inputs.put(DAOConstants.PARAM_RULE_PRIORITY, rule.getPriority());
//			inputs.put(DAOConstants.PARAM_SEARCH_TERM, rule.getSearchTerm());
//			inputs.put(DAOConstants.PARAM_ACTIVE_FLAG, rule.getActiveFlag());
//			inputs.put(DAOConstants.PARAM_CONDITION, rule.getCondition());
//			inputs.put(DAOConstants.PARAM_MODIFIED_BY, rule.getModifiedBy());
			result = DAOUtils.getUpdateCount(updateRuleStatusStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRuleStatus()", e);
    	}
    	return result;
    }
}