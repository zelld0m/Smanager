package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;

@Repository(value="auditTrailDAO")
public class AuditTrailDAO {
	
	public AuditTrailDAO() {}
	
	@Autowired
	public AuditTrailDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddAuditTrailStoredProcedure(jdbcTemplate);
    	getSP = new GetAuditTrailStoredProcedure(jdbcTemplate);
//    	getRefSP = new GetAuditTrailReferenceStoredProcedure(jdbcTemplate);
    }
	private final static String GET_REFID_SQL = "select REFERENCE, USER_NAME from dbo.AUDIT_TRAIL where entity = ? AND operation = ? and store = ? ORDER BY REFERENCE";
	private final static String GET_USER_SQL = "select distinct(USER_NAME) from AUDIT_TRAIL WHERE STORE = ? ORDER BY USER_NAME";
	private final static String GET_ACTION_SQL = "select distinct(OPERATION) from AUDIT_TRAIL WHERE STORE = ? ORDER BY OPERATION";
	private final static String GET_ADMIN_ENTITY_SQL = "select distinct(ENTITY) from AUDIT_TRAIL WHERE STORE = ? ORDER BY ENTITY";
	private final static String GET_ENTITY_SQL = "select distinct(ENTITY) from AUDIT_TRAIL WHERE STORE = ? and ENTITY <> 'security' ORDER BY ENTITY";
	private final static String GET_REF_SQL = "select distinct(REFERENCE) from AUDIT_TRAIL WHERE STORE = ? ORDER BY REFERENCE";

	private AddAuditTrailStoredProcedure addSP;
	private GetAuditTrailStoredProcedure getSP;
//	private GetAuditTrailReferenceStoredProcedure getRefSP;

	private class AddAuditTrailStoredProcedure extends CUDStoredProcedure {
	    public AddAuditTrailStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_AUDIT_TRAIL);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DETAILS, Types.VARCHAR));
		}
	}

	private class GetAuditTrailStoredProcedure extends GetStoredProcedure {
	    public GetAuditTrailStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_AUDIT_TRAIL);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ADMIN, Types.CHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
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
		                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_DATE)),
		                		rs.getString(DAOConstants.COLUMN_DETAILS)
		                		);
		            }
		        }));
		}
	}
	
//	private class GetAuditTrailReferenceStoredProcedure extends GetStoredProcedure {
//	    public GetAuditTrailReferenceStoredProcedure(JdbcTemplate jdbcTemplate) {
//	        super(jdbcTemplate, DAOConstants.SP_GET_AUDIT_TRAIL);
//	    }
//
//		@Override
//		protected void declareParameters() {
//			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION, Types.VARCHAR));
//			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY, Types.VARCHAR));
//			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE, Types.VARCHAR));
//		}
//
//		@Override
//		protected void declareSqlReturnResultSetParameters() {
//			 declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<AuditTrail>() {
//		            public AuditTrail mapRow(ResultSet rs, int rowNum) throws SQLException
//		            {
//		                return new AuditTrail(
//		                		rs.getString(DAOConstants.COLUMN_REFERENCE)
//		                		);
//		            }
//		        }));
//		}
//	}

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
            inputs.put(DAOConstants.PARAM_DATE, JodaDateTimeUtil.toSqlDate(auditTrail.getDateTime()));
            inputs.put(DAOConstants.PARAM_DETAILS, auditTrail.getDetails());
            			
           	i = DAOUtils.getUpdateCount(addSP.execute(inputs));
    	}
    	return i;
    }
    
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
    public RecordSet<AuditTrail> getAuditTrail(SearchCriteria<AuditTrail> auditDetail, boolean adminFlag) throws DataAccessException {

    	Map<String, Object> inputs = new HashMap<String, Object>();
		AuditTrail auditTrail = auditDetail.getModel();
        inputs.put(DAOConstants.PARAM_USER_NAME, auditTrail.getUsername());
        inputs.put(DAOConstants.PARAM_OPERATION, auditTrail.getOperation());
        inputs.put(DAOConstants.PARAM_ENTITY, auditTrail.getEntity());
        inputs.put(DAOConstants.PARAM_STORE, auditTrail.getStoreId());
        inputs.put(DAOConstants.PARAM_KEYWORD, auditTrail.getKeyword());
        inputs.put(DAOConstants.PARAM_REFERENCE, auditTrail.getReferenceId());
        inputs.put(DAOConstants.PARAM_ADMIN, adminFlag?'Y':'N');
        inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(auditDetail.getStartDate()));
        inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(auditDetail.getEndDate()));
        inputs.put(DAOConstants.PARAM_START_ROW, auditDetail.getStartRow());
        inputs.put(DAOConstants.PARAM_END_ROW, auditDetail.getEndRow());

        return DAOUtils.getRecordSet(getSP.execute(inputs));
    }
    
    public List<String> getRefIDs(String ent, String opt, String storeId) {
		String sql = GET_REFID_SQL;
		
		return getSP.getJdbcTemplate().query(
				sql, new String[] {ent,opt,storeId}, new RowMapper<String>() {
					public String mapRow(ResultSet resultSet, int i) throws SQLException {
						return resultSet.getString(1);
					}
				});
	}
	public List<String> getDropdownValues(int type, String storeId, boolean adminFlag) {
		String sql = null;
		switch (type) {
			case 1: 
				sql = GET_USER_SQL;
				break;
			case 2: 
				sql = GET_ACTION_SQL;
				break;
			case 3: 
				if (adminFlag) {
					sql = GET_ADMIN_ENTITY_SQL;
				} else {
					sql = GET_ENTITY_SQL;
				}
				break;
			case 4: 
				sql = GET_REF_SQL;
				break;
		}
		return getSP.getJdbcTemplate().query(
				sql, new String[] {storeId}, new RowMapper<String>() {
					public String mapRow(ResultSet resultSet, int i) throws SQLException {
						return resultSet.getString(1);
					}
				});
	}
 }