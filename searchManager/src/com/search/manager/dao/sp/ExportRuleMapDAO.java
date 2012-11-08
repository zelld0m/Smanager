package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="exportRuleMapDAO")
public class ExportRuleMapDAO {

	// needed by spring AOP
	public ExportRuleMapDAO(){}
	
	@Autowired
	public ExportRuleMapDAO(JdbcTemplate jdbcTemplate) {
		addExportRuleMapStoredProcedure = new AddExportRuleMapStoredProcedure(jdbcTemplate);
		getExportRuleMapStoredProcedure = new GetExportRuleMapStoredProcedure(jdbcTemplate);
		deleteExportRuleMapStoredProcedure = new DeleteExportRuleMapStoredProcedure(jdbcTemplate);
		updateExportRuleMapStoredProcedure = new UpdateExportRuleMapStoredProcedure(jdbcTemplate);
	}

	private GetExportRuleMapStoredProcedure getExportRuleMapStoredProcedure;
	private AddExportRuleMapStoredProcedure addExportRuleMapStoredProcedure;
	private DeleteExportRuleMapStoredProcedure deleteExportRuleMapStoredProcedure;
	private UpdateExportRuleMapStoredProcedure updateExportRuleMapStoredProcedure;
	
	private class GetExportRuleMapStoredProcedure extends GetStoredProcedure {
	    public GetExportRuleMapStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_EXPORT_RULE_MAP);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_TARGET, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_TARGET, Types.VARCHAR));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ExportRuleMap>() {
	        	public ExportRuleMap mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new ExportRuleMap(
	                		rs.getString(DAOConstants.COLUMN_PROD_STORE_ID_ORIGIN), 
	                		rs.getString(DAOConstants.COLUMN_RULE_ID_ORIGIN), 
	                		rs.getString(DAOConstants.COLUMN_RULE_NAME_ORIGIN), 
	                		rs.getString(DAOConstants.COLUMN_PROD_STORE_ID_TARGET), 
	                		rs.getString(DAOConstants.COLUMN_RULE_ID_TARGET), 
	                		rs.getString(DAOConstants.COLUMN_RULE_NAME_TARGET), 
	                		rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID));
	        	}
	        }));
		}
	}

	private class AddExportRuleMapStoredProcedure extends CUDStoredProcedure {
	    public AddExportRuleMapStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_EXPORT_RULE_MAP);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_TARGET, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_TARGET, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_TARGET, Types.VARCHAR));
	   }
	}
	
	private class UpdateExportRuleMapStoredProcedure extends CUDStoredProcedure {
	    public UpdateExportRuleMapStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_EXPORT_RULE_MAP);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_TARGET, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_TARGET, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME_TARGET, Types.VARCHAR));
		}
	}

	private class DeleteExportRuleMapStoredProcedure extends CUDStoredProcedure {
	    public DeleteExportRuleMapStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_EXPORT_RULE_MAP);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_ORIGIN, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PROD_STORE_ID_TARGET, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID_TARGET, Types.VARCHAR));
		}
	}
	
    public int deleteExportRuleMap(ExportRuleMap exportRuleMap) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, exportRuleMap.getRuleType().ordinal());
		inputs.put(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, exportRuleMap.getStoreIdOrigin());
		inputs.put(DAOConstants.PARAM_RULE_ID_ORIGIN, exportRuleMap.getRuleIdOrigin());
		inputs.put(DAOConstants.PARAM_PROD_STORE_ID_TARGET, exportRuleMap.getStoreIdTarget());
		inputs.put(DAOConstants.PARAM_RULE_ID_TARGET, exportRuleMap.getStoreIdTarget());
        return DAOUtils.getUpdateCount(deleteExportRuleMapStoredProcedure.execute(inputs));
    }	

    public RecordSet<ExportRuleMap> getExportRuleMap(SearchCriteria<ExportRuleMap> searchCriteria) throws DaoException {
		try {
			ExportRuleMap exportRuleMap = searchCriteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, exportRuleMap.getRuleType().ordinal());
			inputs.put(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, exportRuleMap.getStoreIdOrigin());
			inputs.put(DAOConstants.PARAM_RULE_ID_ORIGIN, exportRuleMap.getRuleIdOrigin());
			inputs.put(DAOConstants.PARAM_PROD_STORE_ID_TARGET, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_RULE_ID_TARGET, exportRuleMap.getStoreIdTarget());
			return DAOUtils.getRecordSet(getExportRuleMapStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getExportRuleMap()", e);
		}
    }	

    @Audit(entity = Entity.ruleStatus, operation = Operation.add)
    public int addExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, exportRuleMap.getRuleType().ordinal());
			inputs.put(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, exportRuleMap.getStoreIdOrigin());
			inputs.put(DAOConstants.PARAM_RULE_ID_ORIGIN, exportRuleMap.getRuleIdOrigin());
			inputs.put(DAOConstants.PARAM_RULE_NAME_ORIGIN, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_PROD_STORE_ID_TARGET, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_RULE_ID_TARGET, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_RULE_NAME_TARGET, exportRuleMap.getStoreIdTarget());
			result = DAOUtils.getUpdateCount(addExportRuleMapStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addExportRuleMap()", e);
    	}
    	return result;
    }

    @Audit(entity = Entity.ruleStatus, operation = Operation.update)
    public int updateExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, exportRuleMap.getRuleType().ordinal());
			inputs.put(DAOConstants.PARAM_PROD_STORE_ID_ORIGIN, exportRuleMap.getStoreIdOrigin());
			inputs.put(DAOConstants.PARAM_RULE_ID_ORIGIN, exportRuleMap.getRuleIdOrigin());
			inputs.put(DAOConstants.PARAM_RULE_NAME_ORIGIN, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_PROD_STORE_ID_TARGET, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_RULE_ID_TARGET, exportRuleMap.getStoreIdTarget());
			inputs.put(DAOConstants.PARAM_RULE_NAME_TARGET, exportRuleMap.getStoreIdTarget());
			result = DAOUtils.getUpdateCount(updateExportRuleMapStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateExportRuleMap()", e);
    	}
    	return result;
    }

	public ExportRuleMap getExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException {
		ExportRuleMap result = null;
		RecordSet<ExportRuleMap> rSet = getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap, null, null, 1, 1));
		if (rSet.getList().size() > 0) {
			result = rSet.getList().get(0);
		}
		return result;
	}

}