package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
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
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="ruleStatusDAO")
public class RuleStatusDAO {

	// needed by spring AOP
	public RuleStatusDAO(){}
	
	public static final String EXCLUDE  = "exclude";
	public static final String ELEVATE  = "elevate";
	public static final String REDIRECT  = "redirect";
	public static final String RELEVANCY  = "relevancy";
	
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
	        super(jdbcTemplate, DAOConstants.SP_GET_RULE_STATUS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_UPDATE_STATUS, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RuleStatus>() {
	        	public RuleStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new RuleStatus(
	                		rs.getString(DAOConstants.COLUMN_RULE_STATUS_ID), 
	                		rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID), 
	                		rs.getString(DAOConstants.COLUMN_REFERENCE_ID), 
	                		rs.getString(DAOConstants.COLUMN_DESCRIPTION),
	                		rs.getString(DAOConstants.COLUMN_APPROVED_STATUS),
	                		rs.getString(DAOConstants.COLUMN_UPDATE_STATUS),
	                		rs.getString(DAOConstants.COLUMN_PUBLISHED_STATUS), 
	                		rs.getDate(DAOConstants.COLUMN_LAST_PUBLISHED_DATE),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY), 
	                		rs.getDate(DAOConstants.COLUMN_CREATED_DATE),
	                		rs.getDate(DAOConstants.COLUMN_LAST_MODIFIED_DATE)	                		
	                		);
	        	}

	        }));
		}
	}

	private class AddRuleStatusStoredProcedure extends CUDStoredProcedure {
	    public AddRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RULE_STATUS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_UPDATE_STATUS, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}
	
	private class UpdateRuleStatusStoredProcedure extends CUDStoredProcedure {
	    public UpdateRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RULE_STATUS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_UPDATE_STATUS, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class DeleteRuleStatusStoredProcedure extends CUDStoredProcedure {
	    public DeleteRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RULE_STATUS);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_STATUS_ID, Types.INTEGER));
		}
	}
	
    @Audit(entity = Entity.queryCleaning, operation = Operation.delete)
    public int deleteRuleStatus(RuleStatus ruleStatus) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_RULE_STATUS_ID, ruleStatus.getRuleStatusId());
        return DAOUtils.getUpdateCount(deleteRuleStatusStoredProcedure.execute(inputs));
    }	

    public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria) throws DaoException {
		try {
			RuleStatus ruleStatus = searchCriteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, ruleStatus.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS, StringUtils.isNotBlank(ruleStatus.getApprovalStatus())?ruleStatus.getApprovalStatus():null);
			inputs.put(DAOConstants.PARAM_UPDATE_STATUS, StringUtils.isNotBlank(ruleStatus.getUpdateStatus())?ruleStatus.getUpdateStatus():null);
			inputs.put(DAOConstants.PARAM_START_DATE, searchCriteria.getStartDate());
			inputs.put(DAOConstants.PARAM_END_DATE, searchCriteria.getEndDate());
			inputs.put(DAOConstants.PARAM_START_ROW, searchCriteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, searchCriteria.getEndRow());
			return DAOUtils.getRecordSet(getRuleStatusStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getRuleStatus()", e);
		}
    }	

    @Audit(entity = Entity.queryCleaning, operation = Operation.add)
    public int addRuleStatus(RuleStatus ruleStatus) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, ruleStatus.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, ruleStatus.getRuleRefId());
			inputs.put(DAOConstants.PARAM_DESCRIPTION, ruleStatus.getDescription());
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS, ruleStatus.getPublishedStatus());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS, ruleStatus.getApprovalStatus());
			inputs.put(DAOConstants.PARAM_UPDATE_STATUS, ruleStatus.getUpdateStatus());
			inputs.put(DAOConstants.PARAM_CREATED_BY, ruleStatus.getCreatedBy());
			result = DAOUtils.getUpdateCount(addRuleStatusStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRuleStatus()", e);
    	}
    	return result;
    }

    @Audit(entity = Entity.queryCleaning, operation = Operation.update)
    public int updateRuleStatus(RuleStatus ruleStatus) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, ruleStatus.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, ruleStatus.getRuleRefId());
			inputs.put(DAOConstants.PARAM_DESCRIPTION, ruleStatus.getDescription());
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS, ruleStatus.getPublishedStatus());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS, ruleStatus.getApprovalStatus());
			inputs.put(DAOConstants.PARAM_UPDATE_STATUS, ruleStatus.getUpdateStatus());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, ruleStatus.getCreatedBy());
			result = DAOUtils.getUpdateCount(updateRuleStatusStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRuleStatus()", e);
    	}
    	return result;
    }

	public int updateRuleStatus(List<RuleStatus> ruleStatusList) throws DaoException {
		int result = -1;
		for (RuleStatus ruleStatus : ruleStatusList) {
			result = updateRuleStatus(ruleStatus);
			if (result < 1) {
				break;
			}
		}
		return result;
	}
}