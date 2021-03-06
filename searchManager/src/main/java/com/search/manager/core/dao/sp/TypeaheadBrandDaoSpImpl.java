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
import com.search.manager.core.dao.TypeaheadBrandDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

@Auditable(entity = Entity.typeaheadBrand)
@Repository("typeaheadBrandDaoSp")
public class TypeaheadBrandDaoSpImpl extends GenericDaoSpImpl<TypeaheadBrand> implements TypeaheadBrandDao{

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;

	private AddStoredProcedure addStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;

	public TypeaheadBrandDaoSpImpl() {};
	
	@Autowired(required = true)
	public TypeaheadBrandDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addStoredProcedure = new AddStoredProcedure(jdbcTemplate);
		updateStoredProcedure = new UpdateStoredProcedure(jdbcTemplate);
		searchStoredProcedure = new SearchStoredProcedure(jdbcTemplate);
	}
	
	private class AddStoredProcedure extends GetStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_ADD_TYPEAHEAD_BRAND);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_BRAND_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_VENDOR_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_PRODUCT_COUNT, Types.INTEGER));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadBrand>() {
				public TypeaheadBrand mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		// TODO create update sp
		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadDaoConstant.SP_UPDATE_TYPEAHEAD_BRAND);
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
			super(jdbcTemplate, TypeaheadDaoConstant.SP_GET_TYPEAHEAD_BRAND);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_BRAND_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_VENDOR_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadBrand>() {
				public TypeaheadBrand mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private TypeaheadBrand buildModel(ResultSet rs, int rowNum) throws SQLException {
		if(rs.getMetaData().getColumnCount() < 2)
			return null;
		
		TypeaheadBrand typeaheadBrand = new TypeaheadBrand();
		
		typeaheadBrand.setTypeaheadBrandId(rs.getString(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID));
		typeaheadBrand.setRuleId(rs.getString(DAOConstants.COLUMN_RULE_ID));
		typeaheadBrand.setBrandName(rs.getString(TypeaheadDaoConstant.COLUMN_BRAND_NAME));
		typeaheadBrand.setVendorId(rs.getString(TypeaheadDaoConstant.COLUMN_VENDOR_ID));
		typeaheadBrand.setProductCount(rs.getInt(TypeaheadDaoConstant.COLUMN_PRODUCT_COUNT));
		typeaheadBrand.setSortOrder(rs.getInt(TypeaheadDaoConstant.COLUMN_PRIORITY));
		typeaheadBrand.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		typeaheadBrand.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
		typeaheadBrand.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		typeaheadBrand.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs
				.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));

		
		return typeaheadBrand;
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
	protected Map<String, Object> generateAddInput(TypeaheadBrand model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if(model != null) {

			if(model.getTypeaheadBrandId() == null) {
				model.setTypeaheadBrandId(DAOUtils.generateUniqueId());
			}
			inputs = new HashMap<String, Object>();
			inputs.put(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, model.getTypeaheadBrandId());
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(TypeaheadDaoConstant.COLUMN_BRAND_NAME, model.getBrandName());
			inputs.put(TypeaheadDaoConstant.COLUMN_VENDOR_ID, model.getVendorId());
			inputs.put(TypeaheadDaoConstant.COLUMN_PRODUCT_COUNT, model.getProductCount());
			inputs.put(TypeaheadDaoConstant.COLUMN_PRIORITY, model.getSortOrder());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
			inputs.put(DAOConstants.PARAM_CREATED_STAMP, model.getCreatedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getCreatedDate()) : null);
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(TypeaheadBrand model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if(model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, model.getTypeaheadBrandId());
			inputs.put(TypeaheadDaoConstant.COLUMN_BRAND_NAME, model.getBrandName());
			inputs.put(TypeaheadDaoConstant.COLUMN_VENDOR_ID, model.getVendorId());
			inputs.put(TypeaheadDaoConstant.COLUMN_PRODUCT_COUNT, model.getProductCount());
			inputs.put(TypeaheadDaoConstant.COLUMN_PRIORITY, model.getSortOrder());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, model.getLastModifiedBy());
			inputs.put(DAOConstants.COLUMN_LAST_MODIFIED_DATE, model.getLastModifiedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getLastModifiedDate()) : null);
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(TypeaheadBrand model)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Search generateSearchInput(TypeaheadBrand model)
			throws CoreDaoException {
		Search search = new Search(TypeaheadBrand.class);

		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, model.getTypeaheadBrandId()));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, model.getRuleId()));
		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_BRAND_NAME, model.getBrandName()));
		search.addFilter(new Filter(TypeaheadDaoConstant.COLUMN_VENDOR_ID, model.getVendorId()));
		search.addFilter(new Filter(DAOConstants.PARAM_CREATED_STAMP, model.getCreatedDate() != null ? jodaDateTimeUtil.toSqlDate(model.getCreatedDate()) : null));


		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		inputs.put(TypeaheadDaoConstant.COLUMN_TYPEAHEAD_BRAND_ID, null);
		inputs.put(DAOConstants.PARAM_RULE_ID, null);
		inputs.put(TypeaheadDaoConstant.COLUMN_BRAND_NAME, null);
		inputs.put(TypeaheadDaoConstant.COLUMN_VENDOR_ID, null);
		inputs.put(DAOConstants.PARAM_CREATED_STAMP, null);
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
