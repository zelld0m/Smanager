package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.annotation.Auditable;
import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.dao.TypeaheadSuggestionDao;
import com.search.manager.core.enums.MemberType;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

@Auditable(entity = Entity.typeaheadSuggestion)
@Repository("typeaheadSuggestionDaoSp")
public class TypeaheadSuggestionDaoSpImpl extends GenericDaoSpImpl<TypeaheadSuggestion> implements TypeaheadSuggestionDao{

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;

	private AddStoredProcedure addStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;

	public TypeaheadSuggestionDaoSpImpl() {};
	
	@Autowired(required = true)
	public TypeaheadSuggestionDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addStoredProcedure = new AddStoredProcedure(jdbcTemplate);
		updateStoredProcedure = new UpdateStoredProcedure(jdbcTemplate);
		searchStoredProcedure = new SearchStoredProcedure(jdbcTemplate);
	}
	
	private class AddStoredProcedure extends GetStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_ADD_TYPEAHEAD_SUGGESTION);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_SORT_ORDER, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSuggestion>() {
				public TypeaheadSuggestion mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_UPDATE_TYPEAHEAD_SUGGESTION);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_GET_TYPEAHEAD_SUGGESTION);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSuggestion>() {
				public TypeaheadSuggestion mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private TypeaheadSuggestion buildModel(ResultSet rs, int rowNum) throws SQLException {
		if(rs.getMetaData().getColumnCount() < 2)
			return null;
		
		TypeaheadSuggestion typeaheadSuggestion = new TypeaheadSuggestion();

		typeaheadSuggestion.setTypeaheadSuggestionId(rs.getString(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID));
		typeaheadSuggestion.setRuleId(rs.getString(DAOConstants.COLUMN_RULE_ID));
		typeaheadSuggestion.setMemberType(MemberType.values()[rs.getInt(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE)]);
		typeaheadSuggestion.setMemberValue(rs.getString(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE));
		typeaheadSuggestion.setSortOrder(rs.getInt(TypeaheadDaoConstant.COLUMN_SORT_ORDER));
		typeaheadSuggestion.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		typeaheadSuggestion.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
		typeaheadSuggestion.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		typeaheadSuggestion.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs
				.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));

		
		return typeaheadSuggestion;
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
		throw new CoreDaoException("Method has no implementation.");
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchStoredProcedure;
	}

	@Override
	protected Map<String, Object> generateAddInput(TypeaheadSuggestion model)
			throws CoreDaoException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		if(model.getTypeaheadSuggestionId() == null) {
			model.setTypeaheadSuggestionId(DAOUtils.generateUniqueId());
		}
		
		inputs.put(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, model.getTypeaheadSuggestionId());
		inputs.put(DAOConstants.COLUMN_RULE_ID, model.getRuleId());
		inputs.put(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, model.getMemberType() != null ? model.getMemberType().ordinal() : null);
		inputs.put(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, model.getMemberValue());
		inputs.put(TypeaheadDaoConstant.COLUMN_SORT_ORDER, model.getSortOrder());
		inputs.put(DAOConstants.COLUMN_CREATED_BY, model.getCreatedBy());
		inputs.put(DAOConstants.COLUMN_CREATED_STAMP, model.getCreatedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getCreatedDate()) : null);
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(TypeaheadSuggestion model)
			throws CoreDaoException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		inputs.put(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, model.getTypeaheadSuggestionId());
		inputs.put(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, model.getMemberType() != null ? model.getMemberType().ordinal() + 1 : null);
		inputs.put(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, model.getMemberValue());
		inputs.put(TypeaheadDaoConstant.COLUMN_SORT_ORDER, model.getSortOrder());
		inputs.put(DAOConstants.COLUMN_LAST_UPDATED_BY, model.getLastModifiedBy());
		inputs.put(DAOConstants.COLUMN_LAST_UPDATED_STAMP, model.getLastModifiedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getLastModifiedDate()) : null);
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(TypeaheadSuggestion model)
			throws CoreDaoException {
		throw new CoreDaoException("Method does not have an implementation.");
	}

	@Override
	protected Search generateSearchInput(TypeaheadSuggestion model)
			throws CoreDaoException {
		Search search = new Search(TypeaheadSuggestion.class);
		
		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, model.getTypeaheadSuggestionId()));
		search.addFilter(new Filter(DAOConstants.COLUMN_RULE_ID, model.getRuleId()));
		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, model.getMemberType() != null ? model.getMemberType().ordinal() + 1 : null));
		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, model.getMemberValue()));
		search.addFilter(new Filter(DAOConstants.COLUMN_CREATED_STAMP, model.getCreatedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getCreatedDate()) : null));
		search.addFilter(new Filter(DAOConstants.PARAM_START_ROW, 0));
		search.addFilter(new Filter(DAOConstants.PARAM_END_ROW, 0));
		
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		inputs.put(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_SUGGESTION_ID, null);
		inputs.put(DAOConstants.COLUMN_RULE_ID, null);
		inputs.put(TypeaheadDaoConstant.COLUMN_MEMBER_TYPE, null);
		inputs.put(TypeaheadDaoConstant.COLUMN_MEMBER_VALUE, null);
		inputs.put(DAOConstants.COLUMN_CREATED_STAMP, null);
		inputs.put(DAOConstants.PARAM_START_ROW, 0);
		inputs.put(DAOConstants.PARAM_END_ROW, 0);
		
		return inputs;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}


}
