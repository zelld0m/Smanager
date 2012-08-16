package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="excludeDAO")
public class ExcludeDAO {

	public ExcludeDAO() {}
	
	@Autowired
	public ExcludeDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddExcludeStoredProcedure(jdbcTemplate);
    	getSP = new GetExcludeStoredProcedure(jdbcTemplate);
    	getItemSP = new GetExcludeItemStoredProcedure(jdbcTemplate);
    	updateSP = new UpdateExcludeStoredProcedure(jdbcTemplate);
    	deleteSP = new DeleteExcludeStoredProcedure(jdbcTemplate);
    	updateExpiryDateSP = new UpdateExcludeExpiryDateStoredProcedure(jdbcTemplate);
    	updateCommentSP = new UpdateExcludeCommentStoredProcedure(jdbcTemplate);
    	appendCommentSP = new AppendExcludeCommentStoredProcedure(jdbcTemplate);
    }
	
	private AddExcludeStoredProcedure addSP;
	private GetExcludeStoredProcedure getSP;
	private GetExcludeItemStoredProcedure getItemSP;
	private UpdateExcludeStoredProcedure updateSP;
	private DeleteExcludeStoredProcedure deleteSP;
	private UpdateExcludeCommentStoredProcedure updateCommentSP;
	private AppendExcludeCommentStoredProcedure appendCommentSP;
	private UpdateExcludeExpiryDateStoredProcedure updateExpiryDateSP;

	private class AddExcludeStoredProcedure extends CUDStoredProcedure {
	    public AddExcludeStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_EXCLUDE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class GetExcludeStoredProcedure extends GetStoredProcedure {
	    public GetExcludeStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_EXCLUDE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ExcludeResult>() {
	            public ExcludeResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new ExcludeResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE),
                			rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
                			rs.getString(DAOConstants.COLUMN_MEMBER_ID));
	            }
	        }));
	    }
	}

	private class GetExcludeItemStoredProcedure extends StoredProcedure {
	    public GetExcludeItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_EXCLUDE_ITEM);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ExcludeResult>() {
	            public ExcludeResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new ExcludeResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE),
                			rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
                			rs.getString(DAOConstants.COLUMN_MEMBER_ID));
	            }
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class UpdateExcludeStoredProcedure extends CUDStoredProcedure {
	    public UpdateExcludeStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_EXCLUDE);
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
	
	private class DeleteExcludeStoredProcedure extends CUDStoredProcedure {
	    public DeleteExcludeStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_EXCLUDE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}
	}
	
	private class UpdateExcludeCommentStoredProcedure extends CUDStoredProcedure {
	    public UpdateExcludeCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_EXCLUDE_COMMENT);
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

	private class AppendExcludeCommentStoredProcedure extends CUDStoredProcedure {
	    public AppendExcludeCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_EXCLUDE_COMMENT);
	        compile();
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
	
	private class UpdateExcludeExpiryDateStoredProcedure extends CUDStoredProcedure {
	    public UpdateExcludeExpiryDateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_EXCLUDE_EXPIRY_DATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	@Audit(entity = Entity.exclude, operation = Operation.add)
    public int addExclude(ExcludeResult exclude) throws DaoException {
		try {
			DAOValidation.checkExcludePK(exclude);
    		String keyword = DAOUtils.getKeywordId(exclude.getStoreKeyword());
	    	if (StringUtils.isNotEmpty(keyword)) {
	    		String storeId = DAOUtils.getStoreId(exclude.getStoreKeyword());
	    		String username = StringUtils.trim(exclude.getCreatedBy());
	    		String comment = StringUtils.trim(exclude.getComment());
	    		Date expiryDate = exclude.getExpiryDate();
	    		String value = null;
	            if (exclude.getExcludeEntity() == MemberTypeEntity.PART_NUMBER) {
	            	value = StringUtils.trim(exclude.getEdp());
	            } else {
	            	value = exclude.getCondition();
	            }
		        Map<String, Object> inputs = new HashMap<String, Object>();
		        inputs.put(DAOConstants.PARAM_MEMBER_ID, DAOUtils.generateUniqueId());
	            inputs.put(DAOConstants.PARAM_MEMBER_TYPE_ID, exclude.getExcludeEntity());
	            inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
	            inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
	            inputs.put(DAOConstants.PARAM_VALUE, value);
	            inputs.put(DAOConstants.PARAM_COMMENT, comment);
	            inputs.put(DAOConstants.PARAM_EXPIRY_DATE, expiryDate);
	            inputs.put(DAOConstants.PARAM_CREATED_BY, username);
	            return DAOUtils.getUpdateCount(addSP.execute(inputs));
	    	}
	    	return -1;
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addExclude()", e);
    	}
    }
    
    public RecordSet<ExcludeResult> getExclude(SearchCriteria<ExcludeResult> criteria) throws DaoException {
		try {
			ExcludeResult exclude = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, exclude.getMemberId());
	        return DAOUtils.getRecordSet(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getExclude()", e);
    	}
    }
    
    public ExcludeResult getExcludeItem(ExcludeResult exclude) throws DaoException {
    	try {
			Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, exclude.getEdp());
	    	return DAOUtils.getItem(getItemSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getExcludeItem()", e);
    	}
    }
    
	@Audit(entity = Entity.exclude, operation = Operation.update)
    public int updateExclude(ExcludeResult exclude) throws DaoException {
		try {
			DAOValidation.checkExcludePK(exclude);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, exclude.getEdp());
	        inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, 1);
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, exclude.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateExclude()", e);
    	}
    }
    
	@Audit(entity = Entity.exclude, operation = Operation.updateComment)
    public int updateExcludeComment(ExcludeResult exclude) throws DaoException {
		try {
			DAOValidation.checkExcludePK(exclude);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, exclude.getEdp());
	        inputs.put(DAOConstants.PARAM_COMMENT, exclude.getComment());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, exclude.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateExcludeComment()", e);
    	}
    }
    
	@Audit(entity = Entity.exclude, operation = Operation.appendComment)
    public int appendExcludeComment(ExcludeResult exclude) throws DaoException {
		try {
			DAOValidation.checkExcludePK(exclude);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_VALUE, exclude.getEdp());
	        inputs.put(DAOConstants.PARAM_COMMENT, exclude.getComment());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, exclude.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during appendExcludeComment()", e);
		}
    }
	
	@Audit(entity = Entity.exclude, operation = Operation.delete)
    public int removeExclude(ExcludeResult exclude) throws DaoException {
		try {
			DAOValidation.checkExcludePK(exclude);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(exclude.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, exclude.getMemberId());
            return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during removeExclude()", e);
		}
    }

	
	@Audit(entity = Entity.exclude, operation = Operation.clear)
    public int clearExclude(StoreKeyword storeKeyword) throws DaoException {
		try {
    		DAOValidation.checkStoreKeywordPK(storeKeyword);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(storeKeyword));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(storeKeyword));
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, null);
            return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during clearExclude()", e);
		}
    }
	
	@Audit(entity = Entity.exclude, operation = Operation.updateExpiryDate)
    public int updateExcludeExpiryDate(ExcludeResult exclude) throws DaoException {
		try {
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, exclude.getMemberId());
	        inputs.put(DAOConstants.PARAM_EXPIRY_DATE, exclude.getExpiryDate());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, exclude.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateExpiryDateSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateExcludeExpiryDate()", e);
    	}
    }
}
