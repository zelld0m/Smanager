package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

public class ElevateDAO {

	// needed by spring AOP
	public ElevateDAO(){
	}
	
	private AddElevateStoredProcedure addSP;
	private GetElevateStoredProcedure getSP;
	private GetElevateItemStoredProcedure getItemSP;
	private GetNoExpiryElevateStoredProcedure getNoExpirySP;
	private UpdateElevateStoredProcedure updateSP;
	private UpdateElevateExpiryDateStoredProcedure updateExpiryDateSP;
	private UpdateElevateCommentStoredProcedure updateCommentSP;
	private AppendElevateCommentStoredProcedure appendCommentSP;
	private DeleteElevateStoredProcedure deleteSP;

	private class AddElevateStoredProcedure extends CUDStoredProcedure {
	    public AddElevateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class GetElevateStoredProcedure extends GetStoredProcedure {
	    public GetElevateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ElevateResult>() {
	            public ElevateResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new ElevateResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getInt(DAOConstants.COLUMN_SEQUENCE_NUM),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
		}
	}

	private class GetElevateItemStoredProcedure extends StoredProcedure {
	    public GetElevateItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_ELEVATE_ITEM);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ElevateResult>() {
	            public ElevateResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new ElevateResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getInt(DAOConstants.COLUMN_SEQUENCE_NUM),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class GetNoExpiryElevateStoredProcedure extends GetStoredProcedure {
	    public GetNoExpiryElevateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ElevateResult>() {
	            public ElevateResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new ElevateResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getInt(DAOConstants.COLUMN_ROW_NUMBER),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
		}
	}
	
	private class UpdateElevateStoredProcedure extends CUDStoredProcedure {
	    public UpdateElevateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class UpdateElevateExpiryDateStoredProcedure extends CUDStoredProcedure {
	    public UpdateElevateExpiryDateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE_EXPIRY_DATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));	
		}
	}
	
	private class UpdateElevateCommentStoredProcedure extends CUDStoredProcedure {
	    public UpdateElevateCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE_COMMENT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class AppendElevateCommentStoredProcedure extends CUDStoredProcedure {
	    public AppendElevateCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_ELEVATE_COMMENT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class DeleteElevateStoredProcedure extends CUDStoredProcedure {
	    public DeleteElevateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
		}
	}
	
	public ElevateDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddElevateStoredProcedure(jdbcTemplate);
    	getSP = new GetElevateStoredProcedure(jdbcTemplate);
    	getItemSP = new GetElevateItemStoredProcedure(jdbcTemplate);
    	getNoExpirySP = new GetNoExpiryElevateStoredProcedure(jdbcTemplate);
    	updateSP = new UpdateElevateStoredProcedure(jdbcTemplate);
    	updateExpiryDateSP = new UpdateElevateExpiryDateStoredProcedure(jdbcTemplate);
    	updateCommentSP = new UpdateElevateCommentStoredProcedure(jdbcTemplate);
    	appendCommentSP = new AppendElevateCommentStoredProcedure(jdbcTemplate);
    	deleteSP = new DeleteElevateStoredProcedure(jdbcTemplate);
    }

	@Audit(entity = Entity.elevate, operation = Operation.add)
    public int addElevate(ElevateResult elevate) throws DaoException {
		try {
    		DAOValidation.checkElevatePK(elevate);
    		String keyword = DAOUtils.getKeywordId(elevate.getStoreKeyword());
	    	if (StringUtils.isNotEmpty(keyword)) {
	    		String storeId = StringUtils.lowerCase(StringUtils.trim(elevate.getStoreKeyword().getStoreId()));
	    		String productId = StringUtils.trim(elevate.getEdp());
	    		Integer sequence = elevate.getLocation();
	    		String username = StringUtils.trim(elevate.getCreatedBy());
	    		String comment = StringUtils.trim(elevate.getComment());
	    		Date expiryDate = elevate.getExpiryDate();
	    		
	    		// check for duplicates
	    		ElevateResult match = getElevateItem(elevate);
	    		if (match == null) {
		        	Map<String, Object> inputs = new HashMap<String, Object>();
		            inputs.put(DAOConstants.PARAM_MEMBER_ID, DAOUtils.generateUniqueId());
		            inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
		            inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
		            inputs.put(DAOConstants.PARAM_VALUE, productId);
		            inputs.put(DAOConstants.PARAM_COMMENT, comment);
		            inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, sequence);
		            inputs.put(DAOConstants.PARAM_EXPIRY_DATE, expiryDate);
		            inputs.put(DAOConstants.PARAM_CREATED_BY, username);
		            return DAOUtils.getUpdateCount(addSP.execute(inputs));
	    		}
	    	}
	    	return -1;
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addElevate()", e);
    	}
    }
    
	public RecordSet<ElevateResult> getElevate(SearchCriteria<ElevateResult> criteria) throws DaoException {
		try {
			ElevateResult elevate = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getElevate()", e);
    	}
    }
    
    public ElevateResult getElevateItem(ElevateResult elevate) throws DaoException {
    	try {
			Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
	    	return DAOUtils.getItem(getItemSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getElevateItem()", e);
    	}
    }
    
    public RecordSet<ElevateResult> getElevateNoExpiry(SearchCriteria<ElevateResult> criteria) throws DaoException {
		try {
			ElevateResult elevate = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getNoExpirySP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getElevateNoExpiry()", e);
    	}
    }
    
	@Audit(entity = Entity.elevate, operation = Operation.update)
    public int updateElevate(ElevateResult elevate) throws DaoException {
		try {
    		DAOValidation.checkElevatePK(elevate);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
	        inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, elevate.getLocation());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, elevate.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateElevate()", e);
    	}
    }

	@Audit(entity = Entity.elevate, operation = Operation.updateExpiryDate)
    public int updateElevateExpiryDate(ElevateResult elevate) throws DaoException {
		try {
    		DAOValidation.checkElevatePK(elevate);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
	        inputs.put(DAOConstants.PARAM_EXPIRY_DATE, elevate.getExpiryDate());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, elevate.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateExpiryDateSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateElevateExpiryDate()", e);
    	}
    }
    
	@Audit(entity = Entity.elevate, operation = Operation.updateComment)
    public int updateElevateComment(ElevateResult elevate) throws DaoException {
		try {
    		DAOValidation.checkElevatePK(elevate);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
	        inputs.put(DAOConstants.PARAM_COMMENT, elevate.getComment());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, elevate.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateElevateComment()", e);
    	}
    }
    
	@Audit(entity = Entity.elevate, operation = Operation.appendComment)
    public int appendElevateComment(ElevateResult elevate) throws DaoException {
		try {
    		DAOValidation.checkElevatePK(elevate);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
	        inputs.put(DAOConstants.PARAM_COMMENT, elevate.getComment());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, elevate.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during appendElevateComment()", e);
		}
    }
    
	@Audit(entity = Entity.elevate, operation = Operation.delete)
    public int removeElevate(ElevateResult elevate) throws DaoException {
		try {
    		DAOValidation.checkElevatePK(elevate);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
            return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during removeElevate()", e);
		}
    }

	@Audit(entity = Entity.elevate, operation = Operation.clear)
    public int clearElevate(StoreKeyword storeKeyword) throws DaoException {
		try {
    		DAOValidation.checkStoreKeywordPK(storeKeyword);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(storeKeyword));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(storeKeyword));
	        inputs.put(DAOConstants.PARAM_VALUE, null);
            return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during clearElevate()", e);
		}
    }
	
}