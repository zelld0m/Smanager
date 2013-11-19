package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.AuditTrailDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.AuditTrail;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;

@Repository("auditTrailDaoSp")
public class AuditTrailDaoSpImpl extends GenericDaoSpImpl<AuditTrail> implements
		AuditTrailDao {

	private AddStoredProcedure addSp;
	private SearchStoredProcedure searchSp;

	private final static String GET_REFID_SQL = "select REFERENCE, USER_NAME from dbo.AUDIT_TRAIL where entity = ? AND operation = ? and store = ? ORDER BY REFERENCE";
	private final static String GET_USER_SQL = "select distinct(USER_NAME) from AUDIT_TRAIL WHERE STORE = ? ORDER BY USER_NAME";
	private final static String GET_ACTION_SQL = "select distinct(OPERATION) from AUDIT_TRAIL WHERE STORE = ? ORDER BY OPERATION";
	private final static String GET_ADMIN_ENTITY_SQL = "select distinct(ENTITY) from AUDIT_TRAIL WHERE STORE = ? ORDER BY ENTITY";
	private final static String GET_ENTITY_SQL = "select distinct(ENTITY) from AUDIT_TRAIL WHERE STORE = ? and ENTITY <> 'security' ORDER BY ENTITY";
	private final static String GET_REF_SQL = "select distinct(REFERENCE) from AUDIT_TRAIL WHERE STORE = ? ORDER BY REFERENCE";

	@SuppressWarnings("unused")
	private AuditTrailDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public AuditTrailDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		searchSp = new SearchStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends CUDStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_AUDIT_TRAIL);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DETAILS,
					Types.VARCHAR));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_AUDIT_TRAIL);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_OPERATION,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ENTITY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ADMIN,
					Types.CHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<AuditTrail>() {
						public AuditTrail mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return new AuditTrail(
									rs.getString(DAOConstants.COLUMN_USER_NAME),
									rs.getString(DAOConstants.COLUMN_ENTITY),
									rs.getString(DAOConstants.COLUMN_OPERATION),
									rs.getString(DAOConstants.COLUMN_STORE),
									rs.getString(DAOConstants.COLUMN_KEYWORD),
									rs.getString(DAOConstants.COLUMN_REFERENCE),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_DATE)),
									rs.getString(DAOConstants.COLUMN_DETAILS));
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
		throw new CoreDaoException("Unsupported Operation.");
	}

	@Override
	protected StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException {
		throw new CoreDaoException("Unsupported Operation.");
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchSp;
	}

	@Override
	protected Map<String, Object> generateAddInput(AuditTrail model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_USER_NAME, model.getUsername());
			inputs.put(DAOConstants.PARAM_OPERATION, model.getOperation());
			inputs.put(DAOConstants.PARAM_ENTITY, model.getEntity());
			inputs.put(DAOConstants.PARAM_STORE, model.getStoreId());
			inputs.put(DAOConstants.PARAM_KEYWORD, model.getKeyword());
			inputs.put(DAOConstants.PARAM_REFERENCE, model.getReferenceId());
			inputs.put(DAOConstants.PARAM_DATE,
					JodaDateTimeUtil.toSqlDate(model.getCreatedDate()));
			inputs.put(DAOConstants.PARAM_DETAILS, model.getDetails());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(AuditTrail model)
			throws CoreDaoException {
		throw new CoreDaoException("Unsupported Operation.");
	}

	@Override
	protected Map<String, Object> generateDeleteInput(AuditTrail model)
			throws CoreDaoException {
		throw new CoreDaoException("Unsupported Operation.");
	}

	@Override
	protected Search generateSearchInput(AuditTrail model)
			throws CoreDaoException {

		if (model != null) {
			Search search = new Search(AuditTrail.class);

			if (StringUtils.isNotBlank(model.getUsername())) {
				search.addFilter(new Filter(DAOConstants.PARAM_USER_NAME, model
						.getUsername()));
			}
			if (StringUtils.isNotBlank(model.getOperation())) {
				search.addFilter(new Filter(DAOConstants.PARAM_OPERATION, model
						.getOperation()));
			}
			if (StringUtils.isNotBlank(model.getEntity())) {
				search.addFilter(new Filter(DAOConstants.PARAM_ENTITY, model
						.getEntity()));
			}
			if (StringUtils.isNotBlank(model.getStoreId())) {
				search.addFilter(new Filter(DAOConstants.PARAM_STORE, model
						.getStoreId()));
			}
			if (StringUtils.isNotBlank(model.getKeyword())) {
				search.addFilter(new Filter(DAOConstants.PARAM_KEYWORD, model
						.getKeyword()));
			}
			if (StringUtils.isNotBlank(model.getReferenceId())) {
				search.addFilter(new Filter(DAOConstants.PARAM_REFERENCE, model
						.getReferenceId()));
			}
			if (model.getCreatedDate() != null) {
				search.addFilter(new Filter(DAOConstants.PARAM_DATE,
						JodaDateTimeUtil.toSqlDate(model.getCreatedDate())));
			}
			if (StringUtils.isNotBlank(model.getDetails())) {
				search.addFilter(new Filter(DAOConstants.PARAM_DETAILS, model
						.getDetails()));
			}
			return search;
		}

		return null;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();
		inParam = new HashMap<String, Object>();

		inParam.put(DAOConstants.PARAM_USER_NAME, null);
		inParam.put(DAOConstants.PARAM_OPERATION, null);
		inParam.put(DAOConstants.PARAM_ENTITY, null);
		inParam.put(DAOConstants.PARAM_STORE, null);
		inParam.put(DAOConstants.PARAM_KEYWORD, null);
		inParam.put(DAOConstants.PARAM_REFERENCE, null);
		inParam.put(DAOConstants.PARAM_ADMIN, null);
		inParam.put(DAOConstants.PARAM_START_DATE, null);
		inParam.put(DAOConstants.PARAM_END_DATE, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);

		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(storeId)) {
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setStoreId(storeId);
			// TODO add audit trail id
			// auditTrail.setId(id);
		}
		return null;
	}

	// TODO change RecordSet<AuditTrail> to SearchResult<AuditTrail>
	// can be deleted, replace with search(Search search)
	public RecordSet<AuditTrail> getAuditTrail(
			SearchCriteria<AuditTrail> auditDetail, boolean adminFlag)
			throws DataAccessException {

		Map<String, Object> inputs = new HashMap<String, Object>();
		AuditTrail auditTrail = auditDetail.getModel();
		inputs.put(DAOConstants.PARAM_USER_NAME, auditTrail.getUsername());
		inputs.put(DAOConstants.PARAM_OPERATION, auditTrail.getOperation());
		inputs.put(DAOConstants.PARAM_ENTITY, auditTrail.getEntity());
		inputs.put(DAOConstants.PARAM_STORE, auditTrail.getStoreId());
		inputs.put(DAOConstants.PARAM_KEYWORD, auditTrail.getKeyword());
		inputs.put(DAOConstants.PARAM_REFERENCE, auditTrail.getReferenceId());
		inputs.put(DAOConstants.PARAM_ADMIN, adminFlag ? 'Y' : 'N');
		inputs.put(DAOConstants.PARAM_START_DATE,
				JodaDateTimeUtil.toSqlDate(auditDetail.getStartDate()));
		inputs.put(DAOConstants.PARAM_END_DATE,
				JodaDateTimeUtil.toSqlDate(auditDetail.getEndDate()));
		inputs.put(DAOConstants.PARAM_START_ROW, auditDetail.getStartRow());
		inputs.put(DAOConstants.PARAM_END_ROW, auditDetail.getEndRow());

		return DAOUtils.getRecordSet(searchSp.execute(inputs));
	}

	@Override
	public List<String> getRefIDs(String ent, String opt, String storeId) {
		String sql = GET_REFID_SQL;

		return searchSp.getJdbcTemplate().query(sql,
				new String[] { ent, opt, storeId }, new RowMapper<String>() {
					public String mapRow(ResultSet resultSet, int i)
							throws SQLException {
						return resultSet.getString(1);
					}
				});
	}

	@Override
	public List<String> getDropdownValues(int type, String storeId,
			boolean adminFlag) {
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
		return searchSp.getJdbcTemplate().query(sql, new String[] { storeId },
				new RowMapper<String>() {
					public String mapRow(ResultSet resultSet, int i)
							throws SQLException {
						return resultSet.getString(1);
					}
				});
	}

}
