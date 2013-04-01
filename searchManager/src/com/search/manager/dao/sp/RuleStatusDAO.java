package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.ExportType;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="ruleStatusDAO")
public class RuleStatusDAO {

	// needed by spring AOP
	public RuleStatusDAO(){}

	private static final String RS_SQL = "SELECT REFERENCE_ID FROM RULE_STATUS WHERE";

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

	public enum SortOrder {
		DESCRIPTION_ASCENDING,
		DESCRIPTION_DESCENDING,
		LAST_PUBLISHED_DATE_ASCENDING,
		LAST_PUBLISHED_DATE_DESCENDING,
		LAST_EXPORT_DATE_ASCENDING,
		LAST_EXPORT_DATE_DESCENDING;

		public int getIntValue() {
			switch (this) {
			case DESCRIPTION_ASCENDING:
			default:
				return 0;
			case DESCRIPTION_DESCENDING:
				return 1;
			case LAST_PUBLISHED_DATE_ASCENDING:
				return 2;
			case LAST_PUBLISHED_DATE_DESCENDING:
				return 3;
			case LAST_EXPORT_DATE_ASCENDING:
				return 4;
			case LAST_EXPORT_DATE_DESCENDING:
				return 5;
			}
		}
	}

	private class GetRuleStatusStoredProcedure extends GetStoredProcedure {
		public GetRuleStatusStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_RULE_STATUS);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EVENT_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RuleStatus>() {
				public RuleStatus mapRow(ResultSet rs, int rowNum) throws SQLException {

					String exportTypeVal = rs.getString(DAOConstants.COLUMN_EXPORT_TYPE);
					ExportType exportType = null;
					if (StringUtils.isNumeric(exportTypeVal)) {
						try {
							exportType = ExportType.get(Integer.valueOf(exportTypeVal));
						} catch (Exception e) {
						}
					}
					return new RuleStatus(
							rs.getString(DAOConstants.COLUMN_RULE_STATUS_ID), 
							rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID), 
							rs.getString(DAOConstants.COLUMN_REFERENCE_ID), 
							rs.getString(DAOConstants.COLUMN_PRODUCT_STORE_ID), 
							rs.getString(DAOConstants.COLUMN_DESCRIPTION),

							rs.getString(DAOConstants.COLUMN_EVENT_STATUS),
							rs.getString(DAOConstants.COLUMN_REQUEST_BY),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_REQUEST_DATE)),

							rs.getString(DAOConstants.COLUMN_APPROVED_STATUS),
							rs.getString(DAOConstants.COLUMN_APPROVAL_BY),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_APPROVAL_DATE)),

							rs.getString(DAOConstants.COLUMN_PUBLISHED_STATUS), 
							rs.getString(DAOConstants.COLUMN_PUBLISHED_BY),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_PUBLISHED_DATE)),

							exportType,
							rs.getString(DAOConstants.COLUMN_EXPORT_BY),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_EXPORT_DATE)),

							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY), 
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP))	                		
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_EVENT_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_EVENT_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REQUEST_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_APPROVAL_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PUBLISHED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPORT_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPORT_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_REQUEST_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_APPROVAL_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_EXPORT_DATE, Types.TIMESTAMP));
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

	public int deleteRuleStatus(RuleStatus ruleStatus) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_RULE_STATUS_ID, ruleStatus.getRuleStatusId());
		return DAOUtils.getUpdateCount(deleteRuleStatusStoredProcedure.execute(inputs));
	}	

	public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria, SortOrder sortOrder) throws DaoException {
		try {
			RuleStatus ruleStatus = searchCriteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, ruleStatus.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, ruleStatus.getRuleRefId());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS, StringUtils.isNotBlank(ruleStatus.getApprovalStatus())?ruleStatus.getApprovalStatus():null);
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS, StringUtils.isNotBlank(ruleStatus.getPublishedStatus())?ruleStatus.getPublishedStatus():null);
			inputs.put(DAOConstants.PARAM_EVENT_STATUS, StringUtils.isNotBlank(ruleStatus.getUpdateStatus())?ruleStatus.getUpdateStatus():null);
			inputs.put(DAOConstants.PARAM_START_DATE, searchCriteria.getStartDate());
			inputs.put(DAOConstants.PARAM_END_DATE, searchCriteria.getEndDate());
			inputs.put(DAOConstants.PARAM_START_ROW, searchCriteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, searchCriteria.getEndRow());
			inputs.put(DAOConstants.PARAM_STORE_ID, ruleStatus.getStoreId());
			inputs.put(DAOConstants.PARAM_SORT_BY, sortOrder != null ? sortOrder.getIntValue() : 0);

			return DAOUtils.getRecordSet(getRuleStatusStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getRuleStatus()", e);
		}
	}	

	@Audit(entity = Entity.ruleStatus, operation = Operation.add)
	public int addRuleStatus(RuleStatus ruleStatus) throws DaoException {
		int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, ruleStatus.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, ruleStatus.getRuleRefId());
			inputs.put(DAOConstants.PARAM_DESCRIPTION, ruleStatus.getDescription());
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS, ruleStatus.getPublishedStatus());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS, ruleStatus.getApprovalStatus());
			inputs.put(DAOConstants.PARAM_EVENT_STATUS, ruleStatus.getUpdateStatus());
			inputs.put(DAOConstants.PARAM_CREATED_BY, ruleStatus.getCreatedBy());
			inputs.put(DAOConstants.PARAM_STORE_ID, ruleStatus.getStoreId());
			inputs.put(DAOConstants.PARAM_REQUEST_BY, ruleStatus.getRequestBy());
			inputs.put(DAOConstants.PARAM_APPROVAL_BY, ruleStatus.getApprovalBy());
			inputs.put(DAOConstants.PARAM_PUBLISHED_BY, ruleStatus.getPublishedBy());
			inputs.put(DAOConstants.PARAM_LAST_REQUEST_DATE, JodaDateTimeUtil.toSqlDate(ruleStatus.getLastRequestDateTime()));
			inputs.put(DAOConstants.PARAM_LAST_APPROVAL_DATE, JodaDateTimeUtil.toSqlDate(ruleStatus.getLastApprovalDateTime()));
			result = DAOUtils.getUpdateCount(addRuleStatusStoredProcedure.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addRuleStatus()", e);
		}
		return result;
	}

	@Audit(entity = Entity.ruleStatus, operation = Operation.update)
	public int updateRuleStatus(RuleStatus ruleStatus) throws DaoException {
		DAOValidation.checkRuleStatusPK(ruleStatus);
		int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, ruleStatus.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, ruleStatus.getRuleRefId());
			inputs.put(DAOConstants.PARAM_DESCRIPTION, StringUtils.isNotBlank(ruleStatus.getDescription())?ruleStatus.getDescription():null);
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS, ruleStatus.getPublishedStatus());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS, ruleStatus.getApprovalStatus());
			inputs.put(DAOConstants.PARAM_EVENT_STATUS, ruleStatus.getUpdateStatus());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, ruleStatus.getLastModifiedBy());
			inputs.put(DAOConstants.PARAM_STORE_ID, ruleStatus.getStoreId());
			inputs.put(DAOConstants.PARAM_REQUEST_BY, ruleStatus.getRequestBy());
			inputs.put(DAOConstants.PARAM_APPROVAL_BY, ruleStatus.getApprovalBy());
			inputs.put(DAOConstants.PARAM_EXPORT_BY, ruleStatus.getExportBy());
			inputs.put(DAOConstants.PARAM_EXPORT_TYPE, ruleStatus.getExportType() != null ? ruleStatus.getExportType().toString() : null);
			inputs.put(DAOConstants.PARAM_LAST_EXPORT_DATE, JodaDateTimeUtil.toSqlDate(ruleStatus.getLastExportDateTime()));
			inputs.put(DAOConstants.PARAM_LAST_REQUEST_DATE, JodaDateTimeUtil.toSqlDate(ruleStatus.getLastRequestDateTime()));
			inputs.put(DAOConstants.PARAM_LAST_APPROVAL_DATE, JodaDateTimeUtil.toSqlDate(ruleStatus.getLastApprovalDateTime()));
			inputs.put(DAOConstants.PARAM_PUBLISHED_BY, ruleStatus.getPublishedBy());
			result = DAOUtils.getUpdateCount(updateRuleStatusStoredProcedure.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during updateRuleStatus()", e);
		}
		return result;
	}

	public RuleStatus getRuleStatus(RuleStatus ruleStatus) throws DaoException {
		RuleStatus result = null;
		RecordSet<RuleStatus> rSet = getRuleStatus(new SearchCriteria<RuleStatus>(ruleStatus, null, null, 1, 1), SortOrder.DESCRIPTION_ASCENDING);
		if (rSet.getList().size() > 0) {
			result = rSet.getList().get(0);
		}
		return result;
	}

	public List<String> getCleanList(List<String> ruleRefIds, Integer ruleTypeId, String pStatus, String aStatus) {
		StringBuilder sBuilder = new StringBuilder(RS_SQL);
		int size = ruleRefIds.size();
		boolean orFlag = size > 1;

		if(ruleTypeId != null){
			sBuilder.append(" RULE_TYPE_ID = ").append(ruleTypeId);
		}

		if(size > 0){
			sBuilder.append(" AND (");
			for (int i = 0; i < size; i++) {
				String ruleRefId = ruleRefIds.get(i);
				sBuilder.append("REFERENCE_ID = '").append(StringEscapeUtils.escapeSql(ruleRefId)).append("'");
				if (orFlag && i != size-1) {
					sBuilder.append(" OR ");
				}
			}
			sBuilder.append(") ");
		}
		if (!StringUtils.isBlank(pStatus)) {
			sBuilder.append("AND PUBLISHED_STATUS = '").append(pStatus).append("'");
		}
		if (!StringUtils.isBlank(aStatus)) {
			sBuilder.append("AND APPROVED_STATUS = '").append(aStatus).append("'");
		}

		return getRuleStatusStoredProcedure.getJdbcTemplate().query(sBuilder.toString(), new RowMapper<String>() {
			public String mapRow(ResultSet resultSet, int i) throws SQLException {
				return resultSet.getString(1);
			}
		});
	}

}