package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.model.AuditTrail;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;

public class AuditTrailDAO {
	
	public AuditTrailDAO() {
	}

	private AddAuditTrailStoredProcedure addSP;
	private GetAuditTrailStoredProcedure getSP;

	private class AddAuditTrailStoredProcedure extends StoredProcedure {
	    public AddAuditTrailStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_AUDIT_TRAIL);
			declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DETAILS, Types.VARCHAR));
	        compile();
	    }
	}

	private class GetAuditTrailStoredProcedure extends StoredProcedure {
	    public GetAuditTrailStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_AUDIT_TRAIL);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<AuditTrail>() {
	            public AuditTrail mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new AuditTrail(
	                		rs.getString(DAOConstants.COLUMN_USER_NAME),
	                		rs.getString(DAOConstants.COLUMN_ENTITY),
	                		rs.getString(DAOConstants.COLUMN_OPERATION),
	                		rs.getString(DAOConstants.COLUMN_STORE),
	                		rs.getString(DAOConstants.COLUMN_KEYWORD),
	                		rs.getString(DAOConstants.COLUMN_REFERENCE),
	                		rs.getTimestamp(DAOConstants.COLUMN_DATE),
	                		rs.getString(DAOConstants.COLUMN_DETAILS)
	                		);
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
			
			declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}

	public AuditTrailDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddAuditTrailStoredProcedure(jdbcTemplate);
    	getSP = new GetAuditTrailStoredProcedure(jdbcTemplate);
    }

    public int addAuditTrail(AuditTrail auditTrail) throws DataAccessException {
    	int i = -1;
    	if (auditTrail != null) {
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_USER_NAME, auditTrail.getUsername());
            inputs.put(DAOConstants.PARAM_OPERATION, auditTrail.getOperation());
            inputs.put(DAOConstants.PARAM_ENTITY, auditTrail.getEntity());
            inputs.put(DAOConstants.PARAM_STORE, auditTrail.getStoreId());
            inputs.put(DAOConstants.PARAM_KEYWORD, auditTrail.getKeyword());
            inputs.put(DAOConstants.PARAM_REFERENCE, auditTrail.getReferenceId());
            inputs.put(DAOConstants.PARAM_DATE, auditTrail.getDate());
            inputs.put(DAOConstants.PARAM_DETAILS, auditTrail.getDetails());
            			
            Map<String,Object> result = addSP.execute(inputs);
            if (result != null) {
            	i = DAOUtils.getResult(result.get(DAOConstants.UPDATE_COUNT_1));
            }
    	}
    	return i;
    }
    
    @SuppressWarnings("unchecked")
    /**
     * Sample:
     	AuditTrail auditTest = new AuditTrail();
		auditTest.setUsername(auditTrail.getUsername());
		TransferObject<AuditTrail> to = new TransferObject<AuditTrail>(auditTest);
		to.setStartRow(0);
		to.setEndRow(0);
		RecordSet<AuditTrail> set = auditTrailDAO.getAuditTrail(to);
		logger.debug("***************8log size:" + set.getTotalSize());
		for (AuditTrail au: set.getList()) {
			logger.debug(au.getDate() + " " + au.getDetails() + " " + au.getStoreId() + " " + au.getKeyword() + " " + au.getEntity() + " " + au.getOperation() + " " + au.getReferenceId() + " " + au.getDetails());
		}
     */
    public RecordSet<AuditTrail> getAuditTrail(SearchCriteria<AuditTrail> auditDetail) throws DataAccessException {
    	List<AuditTrail> auditList = new ArrayList<AuditTrail>();
    	int size = 0;
		Map<String, Object> inputs = new HashMap<String, Object>();
		AuditTrail auditTrail = auditDetail.getModel();
        inputs.put(DAOConstants.PARAM_USER_NAME, auditTrail.getUsername());
        inputs.put(DAOConstants.PARAM_OPERATION, auditTrail.getOperation());
        inputs.put(DAOConstants.PARAM_ENTITY, auditTrail.getEntity());
        inputs.put(DAOConstants.PARAM_STORE, auditTrail.getStoreId());
        inputs.put(DAOConstants.PARAM_KEYWORD, auditTrail.getKeyword());
        inputs.put(DAOConstants.PARAM_REFERENCE, auditTrail.getReferenceId());
        inputs.put(DAOConstants.PARAM_START_DATE, auditDetail.getStartDate());
        inputs.put(DAOConstants.PARAM_END_DATE, auditDetail.getEndDate());
        
        // TODO: temp fix for sorting of audit trail, while SP not fixed yet
        
        // get total size
        inputs.put(DAOConstants.PARAM_START_ROW, null);
        inputs.put(DAOConstants.PARAM_END_ROW, null);
        Map<String,Object> result = getSP.execute(inputs);
        int totalSize = 0;
        if (result != null) {
        	totalSize = ((List<Integer>)result.get(DAOConstants.RESULT_SET_2)).get(0);
        }
        
        // adjust start row and end row accordingly
        int startRow = totalSize - auditDetail.getEndRow() + 1;
        int endRow = totalSize - auditDetail.getStartRow() + 1;
        
        inputs.put(DAOConstants.PARAM_START_ROW, startRow);
        inputs.put(DAOConstants.PARAM_END_ROW, endRow);
        result = getSP.execute(inputs);
        if (result != null) {
        	auditList.addAll((List<AuditTrail>)result.get(DAOConstants.RESULT_SET_1));
        	size = ((List<Integer>)result.get(DAOConstants.RESULT_SET_2)).get(0);
        	Collections.sort(auditList, new Comparator<AuditTrail>() {
				@Override
				public int compare(AuditTrail paramT1, AuditTrail paramT2) {
					return paramT1.getDate().before(paramT2.getDate()) ? 1 : -1;
				}
        	});
        }
    	return new RecordSet<AuditTrail>(auditList, size);
    }
    
 }
