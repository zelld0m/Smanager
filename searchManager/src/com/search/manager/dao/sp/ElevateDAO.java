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
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="elevateDAO")
public class ElevateDAO {

	// needed by spring AOP
	public ElevateDAO(){}

	@Autowired
	public ElevateDAO(JdbcTemplate jdbcTemplate) {
		addSP = new AddElevateStoredProcedure(jdbcTemplate);
		getSP = new GetElevateStoredProcedure(jdbcTemplate);
		getItemSP = new GetElevateItemStoredProcedure(jdbcTemplate);
		getNoExpirySP = new GetNoExpiryElevateStoredProcedure(jdbcTemplate);
		updateSP = new UpdateElevateStoredProcedure(jdbcTemplate);
		updateExpiryDateSP = new UpdateElevateExpiryDateStoredProcedure(jdbcTemplate);
		deleteSP = new DeleteElevateStoredProcedure(jdbcTemplate);
	}

	private AddElevateStoredProcedure addSP;
	private GetElevateStoredProcedure getSP;
	private GetElevateItemStoredProcedure getItemSP;
	private GetNoExpiryElevateStoredProcedure getNoExpirySP;
	private UpdateElevateStoredProcedure updateSP;
	private UpdateElevateExpiryDateStoredProcedure updateExpiryDateSP;
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_TYPE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FORCE_ADD, Types.VARCHAR));
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FORCE_ADD, Types.INTEGER));
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
									JodaDateTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE)),
									JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
									JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
									rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
									rs.getString(DAOConstants.COLUMN_MEMBER_ID),
									rs.getInt(DAOConstants.COLUMN_FORCE_ADD) == 1);
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
									JodaDateTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE)),
									JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
									JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
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
									JodaDateTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_EXPIRY_DATE)),
									JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
									JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)),
									rs.getString(DAOConstants.COLUMN_MEMBER_TYPE_ID),
									rs.getString(DAOConstants.COLUMN_MEMBER_ID),
									rs.getInt(DAOConstants.COLUMN_FORCE_ADD) == 1);
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FORCE_ADD, Types.VARCHAR));
		}
	}

	private class UpdateElevateExpiryDateStoredProcedure extends CUDStoredProcedure {
		public UpdateElevateExpiryDateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_ELEVATE_EXPIRY_DATE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRY_DATE, Types.DATE));
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}
	}

	@Audit(entity = Entity.elevate, operation = Operation.add)
	public int addElevate(ElevateResult elevate) throws DaoException {
		try {
			DAOValidation.checkElevatePK(elevate);
			String keyword = DAOUtils.getKeywordId(elevate.getStoreKeyword());
			int count = -1;
			if (StringUtils.isNotEmpty(keyword)) {
				String storeId = StringUtils.lowerCase(StringUtils.trim(elevate.getStoreKeyword().getStoreId()));
				String value = null;
				if (elevate.getElevateEntity() == MemberTypeEntity.PART_NUMBER) {
					value = StringUtils.trim(elevate.getEdp());
				} else {
					value = elevate.getCondition().getCondition();
				}

				Integer sequence = elevate.getLocation();
				String username = StringUtils.trim(elevate.getCreatedBy());
				String comment = StringUtils.trim(elevate.getComment());
				DateTime expiryDateTime = elevate.getExpiryDateTime();
				elevate.setMemberId(DAOUtils.generateUniqueId());
				Map<String, Object> inputs = new HashMap<String, Object>();
				inputs.put(DAOConstants.PARAM_MEMBER_ID, elevate.getMemberId());
				inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
				inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
				inputs.put(DAOConstants.PARAM_VALUE, value);
				inputs.put(DAOConstants.PARAM_COMMENT, comment);
				inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, sequence);
				inputs.put(DAOConstants.PARAM_EXPIRY_DATE, JodaDateTimeUtil.toSqlDate(expiryDateTime));
				inputs.put(DAOConstants.PARAM_CREATED_BY, username);
				inputs.put(DAOConstants.PARAM_MEMBER_TYPE_ID, elevate.getElevateEntity());
				inputs.put(DAOConstants.PARAM_FORCE_ADD, elevate.isForceAdd()!=null && elevate.isForceAdd()?1:0);
				count  = DAOUtils.getUpdateCount(addSP.execute(inputs));
			}
			return count;
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
			inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(criteria.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(criteria.getEndDate()));
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, criteria.getModel().getMemberId());
			inputs.put(DAOConstants.PARAM_FORCE_ADD, criteria.getModel().isForceAdd());
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
			inputs.put(DAOConstants.PARAM_MEMBER_ID, elevate.getMemberId());
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
			if (!StringUtils.isBlank(elevate.getEdp())) {
				inputs.put(DAOConstants.PARAM_VALUE, elevate.getEdp());
			} else if (elevate.getCondition() != null && !StringUtils.isBlank(elevate.getCondition().getCondition())) {
				inputs.put(DAOConstants.PARAM_VALUE, elevate.getCondition().getCondition());
			} else {
				inputs.put(DAOConstants.PARAM_VALUE, null);
			}
			inputs.put(DAOConstants.PARAM_SEQUENCE_NUM, elevate.getLocation());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, elevate.getLastModifiedBy());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, elevate.getMemberId());
			inputs.put(DAOConstants.PARAM_FORCE_ADD, elevate.isForceAdd()!=null && elevate.isForceAdd()?1:0);
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
			inputs.put(DAOConstants.PARAM_MEMBER_ID, elevate.getMemberId());
			inputs.put(DAOConstants.PARAM_EXPIRY_DATE, JodaDateTimeUtil.toSqlDate(elevate.getExpiryDateTime()));
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, elevate.getLastModifiedBy());
			int af = DAOUtils.getUpdateCount(updateExpiryDateSP.execute(inputs));
			return af;
		} catch (Exception e) {
			throw new DaoException("Failed during updateElevateExpiryDate()", e);
		}
	}

	@Audit(entity = Entity.elevate, operation = Operation.updateComment)
	public int updateElevateComment(ElevateResult elevate) throws DaoException {
		return 1;
	}

	@Audit(entity = Entity.elevate, operation = Operation.appendComment)
	public int appendElevateComment(ElevateResult elevate) throws DaoException {
		return 1;
	}

	@Audit(entity = Entity.elevate, operation = Operation.delete)
	public int removeElevate(ElevateResult elevate) throws DaoException {
		try {
			DAOValidation.checkElevatePK(elevate);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(elevate.getStoreKeyword()));
			inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(elevate.getStoreKeyword()));
			inputs.put(DAOConstants.PARAM_MEMBER_ID, elevate.getMemberId());
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
			inputs.put(DAOConstants.PARAM_MEMBER_ID, null);
			return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during clearElevate()", e);
		}
	}

}
