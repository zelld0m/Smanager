package com.search.manager.core.dao.sp;

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
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.RuleStatusDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.enums.ExportType;
import com.search.manager.jodatime.JodaDateTimeUtil;

@Repository("ruleStatusDaoSp")
public class RuleStatusDaoSpImpl extends GenericDaoSpImpl<RuleStatus> implements
		RuleStatusDao {

	private AddStoredProcedure addSp;
	private UpdateStoredProcedure updateSp;
	private DeleteStoredProcedure deleteSp;
	private SearchStoredProcedure searchSp;

	private static final String RS_SQL = "SELECT REFERENCE_ID FROM RULE_STATUS WHERE";

	@SuppressWarnings("unused")
	private RuleStatusDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public RuleStatusDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		updateSp = new UpdateStoredProcedure(jdbcTemplate);
		deleteSp = new DeleteStoredProcedure(jdbcTemplate);
		searchSp = new SearchStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends CUDStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_RULE_STATUS);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EVENT_STATUS,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REQUESTED_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_RULE_STATUS);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EVENT_STATUS,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REQUEST_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_APPROVAL_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PUBLISHED_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPORT_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPORT_TYPE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_LAST_REQUEST_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_LAST_APPROVAL_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_LAST_PUBLISHED_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_LAST_EXPORT_DATE, Types.TIMESTAMP));
		}
	}

	private class DeleteStoredProcedure extends CUDStoredProcedure {

		public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_RULE_STATUS);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_RULE_STATUS_ID, Types.INTEGER));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_RULE_STATUS);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_PUBLISHED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_APPROVED_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EVENT_STATUS,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SORT_BY,
					Types.VARCHAR));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<RuleStatus>() {
						public RuleStatus mapRow(ResultSet rs, int rowNum)
								throws SQLException {

							String exportTypeVal = rs
									.getString(DAOConstants.COLUMN_EXPORT_TYPE);
							ExportType exportType = null;
							if (StringUtils.isNumeric(exportTypeVal)) {
								try {
									exportType = ExportType.get(Integer
											.valueOf(exportTypeVal));
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
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_REQUEST_DATE)),

									rs.getString(DAOConstants.COLUMN_APPROVED_STATUS),
									rs.getString(DAOConstants.COLUMN_APPROVAL_BY),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_APPROVAL_DATE)),

									rs.getString(DAOConstants.COLUMN_PUBLISHED_STATUS),
									rs.getString(DAOConstants.COLUMN_PUBLISHED_BY),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_PUBLISHED_DATE)),

									exportType,
									rs.getString(DAOConstants.COLUMN_EXPORT_BY),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_EXPORT_DATE)),

									rs.getString(DAOConstants.COLUMN_CREATED_BY),
									rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));
						}

					}));
		}
	}

	@Override
	protected StoredProcedure getAddStoredProcedure() throws CoreDaoException {
		return addSp;
	}

	@Override
	protected StoredProcedure getUpdateStoredProcedure()
			throws CoreDaoException {
		return updateSp;
	}

	@Override
	protected StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException {
		return deleteSp;
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchSp;
	}

	@Override
	protected Map<String, Object> generateAddInput(RuleStatus model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();

			// TODO generated id?
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, model.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, model.getRuleRefId());
			inputs.put(DAOConstants.PARAM_DESCRIPTION, model.getDescription());
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS,
					model.getPublishedStatus());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS,
					model.getApprovalStatus());
			inputs.put(DAOConstants.PARAM_EVENT_STATUS, model.getUpdateStatus());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_REQUESTED_BY, model.getRequestBy());
			inputs.put(DAOConstants.PARAM_APPROVAL_BY, model.getApprovalBy());
			inputs.put(DAOConstants.PARAM_PUBLISHED_BY, model.getPublishedBy());
			inputs.put(DAOConstants.PARAM_LAST_REQUEST_DATE,
					JodaDateTimeUtil.toSqlDate(model.getLastRequestDate()));
			inputs.put(DAOConstants.PARAM_LAST_APPROVAL_DATE,
					JodaDateTimeUtil.toSqlDate(model.getLastApprovalDate()));
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(RuleStatus model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		// TODO validation
		if (model == null) {
			throw new CoreDaoException("No rule status provided");
		}
		if (StringUtils.isBlank(model.getStoreId())) {
			throw new CoreDaoException("No store id provided");
		}
		if (StringUtils.isBlank(model.getRuleRefId())) {
			throw new CoreDaoException("No rule status reference id provided");
		}
		if (model.getRuleTypeId() == null) {
			throw new CoreDaoException("No rule status type id provided");
		}

		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, model.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, model.getRuleRefId());
			inputs.put(
					DAOConstants.PARAM_DESCRIPTION,
					StringUtils.isNotBlank(model.getDescription()) ? model
							.getDescription() : null);
			inputs.put(DAOConstants.PARAM_PUBLISHED_STATUS,
					model.getPublishedStatus());
			inputs.put(DAOConstants.PARAM_APPROVED_STATUS,
					model.getApprovalStatus());
			inputs.put(DAOConstants.PARAM_EVENT_STATUS, model.getUpdateStatus());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY,
					model.getLastModifiedBy());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_REQUEST_BY, model.getRequestBy());
			inputs.put(DAOConstants.PARAM_APPROVAL_BY, model.getApprovalBy());
			inputs.put(DAOConstants.PARAM_PUBLISHED_BY, model.getPublishedBy());
			inputs.put(DAOConstants.PARAM_EXPORT_BY, model.getExportBy());
			inputs.put(DAOConstants.PARAM_EXPORT_TYPE,
					model.getExportType() != null ? model.getExportType()
							.toString() : null);
			inputs.put(DAOConstants.PARAM_LAST_REQUEST_DATE,
					JodaDateTimeUtil.toSqlDate(model.getLastRequestDate()));
			inputs.put(DAOConstants.PARAM_LAST_APPROVAL_DATE,
					JodaDateTimeUtil.toSqlDate(model.getLastApprovalDate()));
			inputs.put(DAOConstants.PARAM_LAST_PUBLISHED_DATE,
					JodaDateTimeUtil.toSqlDate(model.getLastPublishedDate()));
			inputs.put(DAOConstants.PARAM_LAST_EXPORT_DATE,
					JodaDateTimeUtil.toSqlDate(model.getLastExportDate()));
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(RuleStatus model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		// TODO do we delete?
		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_STATUS_ID,
					model.getRuleStatusId());
		}

		return inputs;
	}

	@Override
	protected Search generateSearchInput(RuleStatus model)
			throws CoreDaoException {
		if (model != null) {
			Search search = new Search(RuleStatus.class);
			if (StringUtils.isNotBlank(model.getStoreId())) {
				search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, model
						.getStoreId()));
			}
			if (model.getRuleTypeId() != null) {
				search.addFilter(new Filter(DAOConstants.PARAM_RULE_TYPE_ID,
						model.getRuleTypeId()));
			}
			if (StringUtils.isNotBlank(model.getRuleRefId())) {
				search.addFilter(new Filter(DAOConstants.PARAM_REFERENCE_ID,
						model.getRuleRefId()));
			}
			if (StringUtils.isNotBlank(model.getPublishedStatus())) {
				search.addFilter(new Filter(
						DAOConstants.PARAM_PUBLISHED_STATUS, model
								.getPublishedStatus()));
			}
			if (StringUtils.isNotBlank(model.getApprovalStatus())) {
				search.addFilter(new Filter(DAOConstants.PARAM_APPROVED_STATUS,
						model.getApprovalStatus()));
			}
			if (StringUtils.isNotBlank(model.getUpdateStatus())) {
				search.addFilter(new Filter(DAOConstants.PARAM_EVENT_STATUS,
						model.getUpdateStatus()));
			}

			return search;
		}
		return null;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();
		inParam.put(DAOConstants.PARAM_RULE_TYPE_ID, null);
		inParam.put(DAOConstants.PARAM_REFERENCE_ID, null);
		inParam.put(DAOConstants.PARAM_PUBLISHED_STATUS, null);
		inParam.put(DAOConstants.PARAM_APPROVED_STATUS, null);
		inParam.put(DAOConstants.PARAM_EVENT_STATUS, null);
		inParam.put(DAOConstants.PARAM_START_DATE, null);
		inParam.put(DAOConstants.PARAM_END_DATE, null);
		inParam.put(DAOConstants.PARAM_STORE_ID, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);
		inParam.put(DAOConstants.PARAM_SORT_BY, 0);

		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(storeId)) {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleStatusId(id);
			ruleStatus.setStoreId(storeId);
			return generateSearchInput(ruleStatus);
		}

		return null;
	}

	// Add RuleStatusDao specific method here...

	@Override
	public List<String> getCleanList(List<String> ruleRefIds,
			Integer ruleTypeId, String pStatus, String aStatus)
			throws CoreDaoException {
		StringBuilder sBuilder = new StringBuilder(RS_SQL);
		int size = ruleRefIds.size();
		boolean orFlag = size > 1;

		if (ruleTypeId != null) {
			sBuilder.append(" RULE_TYPE_ID = ").append(ruleTypeId);
		}

		if (size > 0) {
			sBuilder.append(" AND (");
			for (int i = 0; i < size; i++) {
				String ruleRefId = ruleRefIds.get(i);
				sBuilder.append("REFERENCE_ID = '")
						.append(StringEscapeUtils.escapeSql(ruleRefId))
						.append("'");
				if (orFlag && i != size - 1) {
					sBuilder.append(" OR ");
				}
			}
			sBuilder.append(") ");
		}
		if (!StringUtils.isBlank(pStatus)) {
			sBuilder.append("AND PUBLISHED_STATUS = '").append(pStatus)
					.append("'");
		}
		if (!StringUtils.isBlank(aStatus)) {
			sBuilder.append("AND APPROVED_STATUS = '").append(aStatus)
					.append("'");
		}

		return searchSp.getJdbcTemplate().query(sBuilder.toString(),
				new RowMapper<String>() {
					public String mapRow(ResultSet resultSet, int i)
							throws SQLException {
						return resultSet.getString(1);
					}
				});
	}

}
