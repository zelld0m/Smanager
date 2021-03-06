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
import com.search.manager.core.dao.TypeaheadCategoryDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadCategory;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

@Auditable(entity = Entity.typeaheadCategory)
@Repository("typeaheadCategoryDaoSp")
public class TypeaheadCategoryDaoSpImpl extends GenericDaoSpImpl<TypeaheadCategory> implements TypeaheadCategoryDao{

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;

	private AddStoredProcedure addStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;
	
	@SuppressWarnings("unused")
	private TypeaheadCategoryDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public TypeaheadCategoryDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addStoredProcedure = new AddStoredProcedure(jdbcTemplate);
		updateStoredProcedure = new UpdateStoredProcedure(jdbcTemplate);
		searchStoredProcedure = new SearchStoredProcedure(jdbcTemplate);
	}
	
	private class AddStoredProcedure extends GetStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_ADD_TYPEAHEAD_CATEGORY);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_CATEGORY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadCategory>() {
				public TypeaheadCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_UPDATE_TYPEAHEAD_CATEGORY);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_CATEGORY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_GET_TYPEAHEAD_CATEGORY);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_CATEGORY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadCategory>() {
				public TypeaheadCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private TypeaheadCategory buildModel(ResultSet rs, int rowNum) throws SQLException {
		if(rs.getMetaData().getColumnCount() < 2)
			return null;

		TypeaheadCategory typeaheadCategory = new TypeaheadCategory();

		typeaheadCategory.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		typeaheadCategory.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
		typeaheadCategory.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		typeaheadCategory.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs
				.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));

		
		typeaheadCategory.setRuleId(rs.getString(DAOConstants.COLUMN_RULE_ID));
		typeaheadCategory.setCategory(rs.getString(TypeaheadDaoConstant.COLUMN_CATEGORY));
		
		return typeaheadCategory;
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
		return null;
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchStoredProcedure;
	}

	@Override
	protected Map<String, Object> generateAddInput(TypeaheadCategory model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(TypeaheadCategory model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(TypeaheadCategory model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
		}
		
		return inputs;
	}

	@Override
	protected Search generateSearchInput(TypeaheadCategory model)
			throws CoreDaoException {
		Search search = new Search(TypeaheadCategory.class);
		
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		return inputs;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

}
