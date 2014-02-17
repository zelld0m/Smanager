package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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

import com.search.manager.core.annotation.Auditable;
import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.enums.RuleType;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

@Auditable(entity = Entity.typeaheadRule)
@Repository("typeaheadRuleDaoSp")
public class TypeaheadRuleDaoSpImpl extends GenericDaoSpImpl<TypeaheadRule> implements TypeaheadRuleDao{

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;

	private AddStoredProcedure addStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;

	@SuppressWarnings("unused")
	private TypeaheadRuleDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public TypeaheadRuleDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addStoredProcedure = new AddStoredProcedure(jdbcTemplate);
		updateStoredProcedure = new UpdateStoredProcedure(jdbcTemplate);
		searchStoredProcedure = new SearchStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends GetStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_ADD_TYPEAHEAD_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadRule>() {
				public TypeaheadRule mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		// TODO create update sp
		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_UPDATE_TYPEAHEAD_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_GET_TYPEAHEAD_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadRule>() {
				public TypeaheadRule mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private TypeaheadRule buildModel(ResultSet rs, int rowNum) throws SQLException {
		if(rs.getMetaData().getColumnCount() < 2)
			return null;
		
		TypeaheadRule typeaheadRule = new TypeaheadRule();

		typeaheadRule.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		typeaheadRule.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
		typeaheadRule.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		typeaheadRule.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs
				.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));

		typeaheadRule.setStoreId(rs.getString(DAOConstants.COLUMN_STORE_ID));
		typeaheadRule.setRuleId(rs.getString(DAOConstants.COLUMN_RULE_ID));
		typeaheadRule.setRuleName(rs.getString(DAOConstants.COLUMN_RULE_NAME));
		typeaheadRule.setRuleType(RuleType.values()[rs.getInt(DAOConstants.COLUMN_RULE_TYPE)]);
		return typeaheadRule;
	}

	@Override
	protected StoredProcedure getAddStoredProcedure() throws CoreDaoException {
		return addStoredProcedure;
	}

	@Override
	protected StoredProcedure getUpdateStoredProcedure()
			throws CoreDaoException {
		return updateStoredProcedure;
	}

	@Override
	protected StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchStoredProcedure;
	}

	@Override
	protected Map<String, Object> generateAddInput(TypeaheadRule model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, model.getRuleType() != null ? model.getRuleType().ordinal() : null);
			inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
			inputs.put(DAOConstants.PARAM_CREATED_STAMP, model.getCreatedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getCreatedDate()) : null);
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(TypeaheadRule model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, model.getRuleType() != null ? model.getRuleType().ordinal() : null);
			inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, model.getLastModifiedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(TypeaheadRule model)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Search generateSearchInput(TypeaheadRule model)
			throws CoreDaoException {

		Search search = new Search(TypeaheadRule.class);

		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, model.getRuleId()));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_TYPE_ID, model.getRuleType() != null ? model.getRuleType().ordinal() : null));
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, model.getStoreId()));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_NAME, model.getRuleName()));
		search.addFilter(new Filter(DAOConstants.PARAM_CREATED_STAMP, model.getCreatedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getCreatedDate()) : null));


		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();

		inParam.put(DAOConstants.PARAM_RULE_ID, null);
		inParam.put(DAOConstants.PARAM_RULE_TYPE_ID, null);
		inParam.put(DAOConstants.PARAM_STORE_ID, null);
		inParam.put(DAOConstants.PARAM_RULE_NAME, null);
		inParam.put(DAOConstants.PARAM_CREATED_STAMP, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);

		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		TypeaheadRule typeaheadRule = new TypeaheadRule();
		if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(storeId)) {
			typeaheadRule.setRuleId(id);
			typeaheadRule.setStoreId(storeId);
		}
		return generateSearchInput(typeaheadRule);
	}

}
