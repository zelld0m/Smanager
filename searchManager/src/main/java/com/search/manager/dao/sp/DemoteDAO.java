package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.core.model.Store;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="demoteDAO")
public class DemoteDAO {

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;
	
	// needed by spring AOP
	public DemoteDAO(){}

	@Autowired
	public DemoteDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddStoredProcedure(jdbcTemplate);
    	getSP = new GetDemoteStoredProcedure(jdbcTemplate);
    	getSPNew = new GetDemoteNewStoredProcedure(jdbcTemplate);
    	getNoExpirySP = new GetNoExpiryStoredProcedure(jdbcTemplate);
    	updateSP = new UpdateStoredProcedure(jdbcTemplate);
    	updateExpiryDateSP = new UpdateExpiryDateStoredProcedure(jdbcTemplate);
    	deleteSP = new DeleteStoredProcedure(jdbcTemplate);
    }

	private AddStoredProcedure addSP;
	private GetDemoteStoredProcedure getSP;
	private GetDemoteNewStoredProcedure getSPNew;
	private GetNoExpiryStoredProcedure getNoExpirySP;
	private UpdateStoredProcedure updateSP;
	private UpdateExpiryDateStoredProcedure updateExpiryDateSP;
	private DeleteStoredProcedure deleteSP;

	private class AddStoredProcedure extends CUDStoredProcedure {
		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_DEMOTE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEQUENCE_NUM, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_TYPE_ID, Types.VARCHAR));
		}
	}

	private class GetDemoteStoredProcedure extends GetStoredProcedure {
		public GetDemoteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_DEMOTE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
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
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_EXPIRY_DATE)),
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
									rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
									rs.getString(DAOConstants.COLUMN_MEMBER_ID));
				}
			}));
		}
	}

	// TODO dbo.usp_Get_Demote_New
	private class GetDemoteNewStoredProcedure extends GetStoredProcedure {
	    public GetDemoteNewStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_DEMOTE_NEW);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
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
	                		jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_EXPIRY_DATE)),
	                		jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
	                		jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
                			rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
                			rs.getString(DAOConstants.COLUMN_MEMBER_ID));
	            }
	        }));
		}
	}
	
	@SuppressWarnings("unused")
	private class GetItemStoredProcedure extends LoggerStoredProcedure {
		public GetItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_DEMOTE);
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
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_EXPIRY_DATE)),
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
									rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
									rs.getString(DAOConstants.COLUMN_MEMBER_ID));
				}
			}));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			compile();
		}
	}

	private class GetNoExpiryStoredProcedure extends GetStoredProcedure {
		public GetNoExpiryStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_DEMOTE);
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
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_EXPIRY_DATE)),
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
									jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
									rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
									rs.getString(DAOConstants.COLUMN_MEMBER_ID));
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {
		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_DEMOTE);
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

	private class UpdateExpiryDateStoredProcedure extends CUDStoredProcedure {
		public UpdateExpiryDateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_DEMOTE_EXPIRY_DATE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));	
		}
	}




	private class DeleteStoredProcedure extends CUDStoredProcedure {
		public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_DEMOTE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}
	}

	@Audit(entity = Entity.demote, operation = Operation.add)
	public int add(DemoteResult demote) throws DaoException {
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
				DateTime expiryDateTime = demote.getExpiryDate();
				demote.setMemberId(DAOUtils.generateUniqueId());
				Map<String, Object> inputs = new HashMap<String, Object>();
				inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
				inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
				inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
				inputs.put(DAOConstants.PARAM_VALUE, value);
				inputs.put(DAOConstants.PARAM_COMMENT, comment);
				inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, sequence);
				inputs.put(DAOConstants.PARAM_EXPIRY_DATE, jodaDateTimeUtil.toSqlDate(expiryDateTime));
				inputs.put(DAOConstants.PARAM_CREATED_BY, username);
				inputs.put(DAOConstants.PARAM_MEMBER_TYPE_ID, demote.getDemoteEntity());

				count  = DAOUtils.getUpdateCount(addSP.execute(inputs));
			}
			return count;
		}
		catch (Exception e) {
			throw new DaoException("Failed during addDemote()", e);
		}
	}

	public RecordSet<DemoteResult> getResultList(SearchCriteria<DemoteResult> criteria) throws DaoException {
		try {
			DemoteResult demote = criteria.getModel();

	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_DATE, jodaDateTimeUtil.toSqlDate(criteria.getStartDate()));
	        inputs.put(DAOConstants.PARAM_END_DATE, jodaDateTimeUtil.toSqlDate(criteria.getEndDate()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, criteria.getModel().getMemberId());
	        return DAOUtils.getRecordSet(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getDemote()", e);
    	}
    }

	// TODO using dbo.usp_Get_Demote_New
	public RecordSet<DemoteResult> getResultListNew(SearchCriteria<DemoteResult> criteria) throws DaoException {
		try {
			DemoteResult demote = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
	        inputs.put(DAOConstants.PARAM_START_DATE, jodaDateTimeUtil.toSqlDate(criteria.getStartDate()));
	        inputs.put(DAOConstants.PARAM_END_DATE, jodaDateTimeUtil.toSqlDate(criteria.getEndDate()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MEMBER_ID, criteria.getModel().getMemberId());
	        return DAOUtils.getRecordSet(getSPNew.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getResultListNew()", e);
    	}
    }
	
    public DemoteResult getItem(DemoteResult demote) throws DaoException {
    	try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(demote.getStoreKeyword()));
			inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(demote.getStoreKeyword()));
			inputs.put(DAOConstants.PARAM_START_DATE, null);
			inputs.put(DAOConstants.PARAM_END_DATE, null);
			inputs.put(DAOConstants.PARAM_START_ROW, null);
			inputs.put(DAOConstants.PARAM_END_ROW, null);
			inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
			return DAOUtils.getItem(getSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getDemoteItem()", e);
		}
	}

	public RecordSet<DemoteResult> getNoExpiry(SearchCriteria<DemoteResult> criteria) throws DaoException {
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
	public int update(DemoteResult demote) throws DaoException {
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
	public int updateExpiryDate(DemoteResult demote) throws DaoException {
		try {
			DAOValidation.checkDemotePK(demote);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_MEMBER_ID, demote.getMemberId());
			inputs.put(DAOConstants.PARAM_EXPIRY_DATE, jodaDateTimeUtil.toSqlDate(demote.getExpiryDate()));
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, demote.getLastModifiedBy());
			return DAOUtils.getUpdateCount(updateExpiryDateSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateDemoteExpiryDate()", e);
		}
	}

	@Audit(entity = Entity.demote, operation = Operation.updateComment)
	public int updateComment(DemoteResult demote) throws DaoException {
		return 1;
	}

	@Audit(entity = Entity.demote, operation = Operation.appendComment)
	public int appendComment(DemoteResult demote) throws DaoException {
		return 1;
	}

	@Audit(entity = Entity.demote, operation = Operation.delete)
	public int remove(DemoteResult demote) throws DaoException {
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
	public int clear(StoreKeyword storeKeyword) throws DaoException {
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
