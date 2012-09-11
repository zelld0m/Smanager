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
import com.search.manager.model.DemoteResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="demoteDAO")
public class DemoteDAO {

	// needed by spring AOP
	public DemoteDAO(){}
	
	@Autowired
	public DemoteDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddDemoteStoredProcedure(jdbcTemplate);
    	getSP = new GetDemoteStoredProcedure(jdbcTemplate);
    	getItemSP = new GetDemoteItemStoredProcedure(jdbcTemplate);
    	getNoExpirySP = new GetNoExpiryDemoteStoredProcedure(jdbcTemplate);
    	updateSP = new UpdateDemoteStoredProcedure(jdbcTemplate);
    	updateExpiryDateSP = new UpdateDemoteExpiryDateStoredProcedure(jdbcTemplate);
    	updateCommentSP = new UpdateDemoteCommentStoredProcedure(jdbcTemplate);
    	appendCommentSP = new AppendDemoteCommentStoredProcedure(jdbcTemplate);
    	deleteSP = new DeleteDemoteStoredProcedure(jdbcTemplate);
    }
	
	private AddDemoteStoredProcedure addSP;
	private GetDemoteStoredProcedure getSP;
	private GetDemoteItemStoredProcedure getItemSP;
	private GetNoExpiryDemoteStoredProcedure getNoExpirySP;
	private UpdateDemoteStoredProcedure updateSP;
	private UpdateDemoteExpiryDateStoredProcedure updateExpiryDateSP;
	private UpdateDemoteCommentStoredProcedure updateCommentSP;
	private AppendDemoteCommentStoredProcedure appendCommentSP;
	private DeleteDemoteStoredProcedure deleteSP;

	private class AddDemoteStoredProcedure extends CUDStoredProcedure {
	    public AddDemoteStoredProcedure(JdbcTemplate jdbcTemplate) {
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_TYPE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FORCE_ADD, Types.VARCHAR));
		}
	}

	private class GetDemoteStoredProcedure extends GetStoredProcedure {
	    public GetDemoteStoredProcedure(JdbcTemplate jdbcTemplate) {
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FORCE_ADD, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<DemoteResult>() {
	            public DemoteResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new DemoteResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getInt(DAOConstants.COLUMN_SEQUENCE_NUM),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE),
                			rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
                			rs.getString(DAOConstants.COLUMN_MEMBER_ID),
                			rs.getInt(DAOConstants.COLUMN_FORCE_ADD) == 1);
	            }
	        }));
		}
	}

	private class GetDemoteItemStoredProcedure extends StoredProcedure {
	    public GetDemoteItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_ELEVATE_ITEM);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<DemoteResult>() {
	            public DemoteResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new DemoteResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getInt(DAOConstants.COLUMN_SEQUENCE_NUM),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE),
                			rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
                			rs.getString(DAOConstants.COLUMN_MEMBER_ID),
                			rs.getInt(DAOConstants.COLUMN_FORCE_ADD) == 1);
	            }
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class GetNoExpiryDemoteStoredProcedure extends GetStoredProcedure {
	    public GetNoExpiryDemoteStoredProcedure(JdbcTemplate jdbcTemplate) {
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
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<DemoteResult>() {
	            public DemoteResult mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	                return new DemoteResult(
	                		new StoreKeyword(new Store(rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                						 new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD))),
	                		rs.getString(DAOConstants.COLUMN_VALUE),
	                		rs.getInt(DAOConstants.COLUMN_ROW_NUMBER),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE),
                			rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
                			rs.getString(DAOConstants.COLUMN_MEMBER_ID),
                			rs.getInt(DAOConstants.COLUMN_FORCE_ADD) == 1);
	            }
	        }));
		}
	}
	
	private class UpdateDemoteStoredProcedure extends CUDStoredProcedure {
	    public UpdateDemoteStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}
	}
	
	private class UpdateDemoteExpiryDateStoredProcedure extends CUDStoredProcedure {
	    public UpdateDemoteExpiryDateStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE_EXPIRY_DATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));	
		}
	}
	
	private class UpdateDemoteCommentStoredProcedure extends CUDStoredProcedure {
	    public UpdateDemoteCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE_COMMENT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class AppendDemoteCommentStoredProcedure extends CUDStoredProcedure {
	    public AppendDemoteCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_ELEVATE_COMMENT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class DeleteDemoteStoredProcedure extends CUDStoredProcedure {
	    public DeleteDemoteStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_ELEVATE);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}
	}
	
	@Audit(entity = Entity.demote, operation = Operation.add)
    public int addDemote(DemoteResult demote) throws DaoException {
		try {
    		DAOValidation.checkDemotePK(demote);
    		String keyword = DAOUtils.getKeywordId(demote.getStoreKeyword());
	    	int count = -1;
			if (StringUtils.isNotEmpty(keyword)) {
	    		String storeId = StringUtils.lowerCase(StringUtils.trim(demote.getStoreKeyword().getStoreId()));
	    		String value = null;
	            if (demote.getDemoteEntity() == MemberTypeEntity.PART_NUMBER) {
	            	value = StringUtils.trim(demote.getEdp());
	            } else {
	            	value = demote.getCondition().getCondition();
	            }
	    		
	    		Integer sequence = demote.getLocation();
	    		String username = StringUtils.trim(demote.getCreatedBy());
	    		String comment = StringUtils.trim(demote.getComment());
	    		Date expiryDate = demote.getExpiryDate();
	    		demote.setMemberId(DAOUtils.generateUniqueId());
	        	Map<String, Object> inputs = new HashMap<String, Object>();
	            inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
	            inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
	            inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
	            inputs.put(DAOConstants.PARAM_VALUE, value);
	            inputs.put(DAOConstants.PARAM_COMMENT, comment);
	            inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, sequence);
	            inputs.put(DAOConstants.PARAM_EXPIRY_DATE, expiryDate);
	            inputs.put(DAOConstants.PARAM_CREATED_BY, username);
	            inputs.put(DAOConstants.PARAM_MEMBER_TYPE_ID, demote.getDemoteEntity());
	            inputs.put(DAOConstants.PARAM_FORCE_ADD, demote.isForceAdd()!=null && demote.isForceAdd()?1:0);
	            count  = DAOUtils.getUpdateCount(addSP.execute(inputs));
	    	}
	    	return count;
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addDemote()", e);
    	}
    }
    
	public RecordSet<DemoteResult> getDemote(SearchCriteria<DemoteResult> criteria) throws DaoException {
		try {
			DemoteResult demote = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, criteria.getModel().getMemberId());
	        inputs.put(DAOConstants.PARAM_FORCE_ADD, criteria.getModel().isForceAdd());
	        return DAOUtils.getRecordSet(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getDemote()", e);
    	}
    }

    public DemoteResult getDemoteItem(DemoteResult demote) throws DaoException {
    	try {
			Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
	    	return DAOUtils.getItem(getItemSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getDemoteItem()", e);
    	}
    }
    
    public RecordSet<DemoteResult> getDemoteNoExpiry(SearchCriteria<DemoteResult> criteria) throws DaoException {
		try {
			DemoteResult demote = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getNoExpirySP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getDemoteNoExpiry()", e);
    	}
    }
    
	@Audit(entity = Entity.demote, operation = Operation.update)
    public int updateDemote(DemoteResult demote) throws DaoException {
		try {
    		DAOValidation.checkDemotePK(demote);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        if (!StringUtils.isBlank(demote.getEdp())) {
		        inputs.put(DAOConstants.PARAM_VALUE, demote.getEdp());
	        } else if (demote.getCondition() != null && !StringUtils.isBlank(demote.getCondition().getCondition())) {
		        inputs.put(DAOConstants.PARAM_VALUE, demote.getCondition().getCondition());
	        } else {
	        	inputs.put(DAOConstants.PARAM_VALUE, null);
	        }
	        inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, demote.getLocation());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, demote.getLastModifiedBy());
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
	        return DAOUtils.getUpdateCount(updateSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateDemote()", e);
    	}
    }

	@Audit(entity = Entity.demote, operation = Operation.updateExpiryDate)
    public int updateDemoteExpiryDate(DemoteResult demote) throws DaoException {
		try {
    		DAOValidation.checkDemotePK(demote);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
	        inputs.put(DAOConstants.PARAM_EXPIRY_DATE, demote.getExpiryDate());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, demote.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateExpiryDateSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateDemoteExpiryDate()", e);
    	}
    }
    
	@Audit(entity = Entity.demote, operation = Operation.updateComment)
    public int updateDemoteComment(DemoteResult demote) throws DaoException {
		try {
    		DAOValidation.checkDemotePK(demote);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
	        inputs.put(DAOConstants.PARAM_COMMENT, demote.getComment());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, demote.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateDemoteComment()", e);
    	}
    }
    
	@Audit(entity = Entity.demote, operation = Operation.appendComment)
    public int appendDemoteComment(DemoteResult demote) throws DaoException {
		try {
    		DAOValidation.checkDemotePK(demote);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
	        inputs.put(DAOConstants.PARAM_COMMENT, demote.getComment());
	        inputs.put(DAOConstants.PARAM_MODIFIED_BY, demote.getLastModifiedBy());
	        return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during appendDemoteComment()", e);
		}
    }
    
	@Audit(entity = Entity.demote, operation = Operation.delete)
    public int removeDemote(DemoteResult demote) throws DaoException {
		try {
    		DAOValidation.checkDemotePK(demote);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
            return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during removeDemote()", e);
		}
    }

	@Audit(entity = Entity.demote, operation = Operation.clear)
    public int clearDemote(StoreKeyword storeKeyword) throws DaoException {
		try {
    		DAOValidation.checkStoreKeywordPK(storeKeyword);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(storeKeyword));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(storeKeyword));
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, null);
            return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during clearDemote()", e);
		}
    }
	
}
