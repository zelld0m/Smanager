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

import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.constant.TypeaheadSectionDaoConstant;
import com.search.manager.core.dao.TypeaheadSectionDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadSection;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;

public class TypeaheadSectionDaoSpImpl extends GenericDaoSpImpl<TypeaheadSection> implements TypeaheadSectionDao{

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;

	private AddStoredProcedure addStoredProcedure;
	private DeleteStoredProcedure deleteStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;
	
	private class AddStoredProcedure extends GetStoredProcedure {
		
		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadSectionDaoConstant.SP_ADD_TYPEAGEAD_SECTION);
		}


		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_DISABLED, Types.BOOLEAN));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSection>() {
				public TypeaheadSection mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends GetStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadSectionDaoConstant.SP_UPDATE_TYPEAHEAD_SECTION);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_DISABLED, Types.BIT));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}
		
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSection>() {
				public TypeaheadSection mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private class DeleteStoredProcedure extends CUDStoredProcedure {

        public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, TypeaheadSectionDaoConstant.SP_DELETE_TYPEAHEAD_SECTION);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, Types.VARCHAR));
        }
    }

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadSectionDaoConstant.SP_GET_TYPEAHEAD_SECTION);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionDaoConstant.COLUMN_DISABLED, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSection>() {
				public TypeaheadSection mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private TypeaheadSection buildModel(ResultSet rs, int rowNum) throws SQLException {
		if (rs.getMetaData().getColumnCount() < 2) {
			return null;
		}

		TypeaheadSection section = new TypeaheadSection();


		section.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		section.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
		section.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		section.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));
		section.setName(rs.getString(TypeaheadSectionDaoConstant.COLUMN_NAME));
		section.setType(rs.getInt(TypeaheadSectionDaoConstant.COLUMN_TYPE));
		section.setDisabled(rs.getBoolean(TypeaheadSectionDaoConstant.COLUMN_DISABLED));
		section.setRuleId(rs.getString(DAOConstants.COLUMN_RULE_ID));
		section.setTypeaheadSectionId(rs.getString(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID));

		return section;
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
		return deleteStoredProcedure;
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchStoredProcedure;
	}

	@Override
	protected Map<String, Object> generateAddInput(TypeaheadSection model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if (model != null) {
			String sectionId = model.getTypeaheadSectionId();

			if (StringUtils.isBlank(sectionId)) {
				model.setTypeaheadSectionId(DAOUtils.generateUniqueId());
			}
			
			inputs = new HashMap<String, Object>();
			
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, model.getTypeaheadSectionId());
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_RULE_ID, model.getRuleId());
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_NAME, model.getName());
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_TYPE, model.getType());
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_DISABLED, model.isDisabled());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
			inputs.put(DAOConstants.PARAM_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getCreatedDate()));
			
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(TypeaheadSection model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, model.getTypeaheadSectionId());
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_NAME, model.getName());
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_DISABLED, model.isDisabled());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, model.getLastModifiedBy());
			inputs.put(DAOConstants.COLUMN_LAST_UPDATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getLastModifiedDate()));
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(TypeaheadSection model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, model.getTypeaheadSectionId());
		}
		
		return inputs;
	}

	@Override
	protected Search generateSearchInput(TypeaheadSection model)
			throws CoreDaoException {
		Search search = new Search(TypeaheadSection.class);
		
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, model.getRuleId()));
		search.addFilter(new Filter(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, model.getTypeaheadSectionId()));
		search.addFilter(new Filter(TypeaheadSectionDaoConstant.COLUMN_NAME, model.getName()));
		search.addFilter(new Filter(TypeaheadSectionDaoConstant.COLUMN_TYPE, model.getType()));
		search.addFilter(new Filter(TypeaheadSectionDaoConstant.COLUMN_DISABLED, model.isDisabled()));

		search.addFilter(new Filter(DAOConstants.PARAM_CREATED_STAMP,
				jodaDateTimeUtil.toSqlDate(model.getCreatedDate())));
		
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();
		
		inParam.put(TypeaheadSectionDaoConstant.COLUMN_TYPEAHEAD_SECTION_ID, null);
		inParam.put(TypeaheadSectionDaoConstant.COLUMN_RULE_ID, null);
		inParam.put(TypeaheadSectionDaoConstant.COLUMN_NAME, null);
		inParam.put(TypeaheadSectionDaoConstant.COLUMN_TYPE, null);
		inParam.put(TypeaheadSectionDaoConstant.COLUMN_DISABLED, null);
		inParam.put(DAOConstants.PARAM_CREATED_STAMP, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);
		
		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		TypeaheadSection typeaheadSection = new TypeaheadSection();
		
		if (StringUtils.isNotBlank(id)) {
			typeaheadSection.setTypeaheadSectionId(id);
		}

		return generateSearchInput(typeaheadSection);
	}

}
