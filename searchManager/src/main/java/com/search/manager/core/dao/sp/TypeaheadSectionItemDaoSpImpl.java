package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.constant.TypeaheadSectionDaoConstant;
import com.search.manager.core.constant.TypeaheadSectionItemDaoConstant;
import com.search.manager.core.dao.TypeaheadSectionItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadSectionItem;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;

public class TypeaheadSectionItemDaoSpImpl extends GenericDaoSpImpl<TypeaheadSectionItem> implements TypeaheadSectionItemDao{

	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;
	
	private AddStoredProcedure addStoredProcedure;
	private DeleteStoredProcedure deleteStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;
	
	private class AddStoredProcedure extends GetStoredProcedure {
		
		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadSectionItemDaoConstant.SP_TYPEAHEAD_SECTION_ITEM_ADD);
		}


		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_SECTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_VALUE, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSectionItem>() {
				public TypeaheadSectionItem mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends GetStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, TypeaheadSectionItemDaoConstant.SP_TYPEAHEAD_SECTION_ITEM_UPDATE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}
		
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSectionItem>() {
				public TypeaheadSectionItem mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private class DeleteStoredProcedure extends CUDStoredProcedure {

        public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, TypeaheadSectionItemDaoConstant.SP_TYPEAHEAD_SECTION_ITEM_DELETE);
        }

        @Override
        protected void declareParameters() {
        	declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, Types.VARCHAR));
        }
    }

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate,TypeaheadSectionItemDaoConstant.SP_TYPEAHEAD_SECTION_ITEM_GET);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_SECTION_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadSectionItemDaoConstant.COLUMN_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<TypeaheadSectionItem>() {
				public TypeaheadSectionItem mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private TypeaheadSectionItem buildModel(ResultSet rs, int rowNum) throws SQLException {
		if (rs.getMetaData().getColumnCount() < 2) {
			return null;
		}

		TypeaheadSectionItem sectionItem = new TypeaheadSectionItem();


		sectionItem.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		sectionItem.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
		sectionItem.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		sectionItem.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));
		sectionItem.setRuleId(rs.getString(DAOConstants.COLUMN_RULE_ID));
		sectionItem.setSectionId(rs.getString(TypeaheadSectionItemDaoConstant.COLUMN_SECTION_ID));
		sectionItem.setValue(rs.getString(TypeaheadSectionItemDaoConstant.COLUMN_VALUE));
		sectionItem.setTypeaheadSectionItemId(rs.getString(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID));

		return sectionItem;
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
	protected Map<String, Object> generateAddInput(TypeaheadSectionItem model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			
			String sectionItemId = model.getTypeaheadSectionItemId();
			
			inputs = new HashMap<String, Object>();
			
			if(StringUtils.isEmpty(sectionItemId)) {
				model.setTypeaheadSectionItemId(DAOUtils.generateUniqueId());
			}
			
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, model.getTypeaheadSectionItemId());
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_SECTION_ID, model.getSectionId());
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_RULE_ID, model.getRuleId());
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_VALUE, model.getValue());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
			inputs.put(DAOConstants.PARAM_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getCreatedDate()));
			
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(TypeaheadSectionItem model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			
			inputs = new HashMap<String, Object>();
			
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, model.getTypeaheadSectionItemId());
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_VALUE, model.getValue());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, model.getLastModifiedBy());
			inputs.put(DAOConstants.COLUMN_LAST_UPDATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getLastModifiedDate()));
			
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(TypeaheadSectionItem model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, model.getTypeaheadSectionItemId());
		}
		
		return inputs;
	}

	@Override
	protected Search generateSearchInput(TypeaheadSectionItem model)
			throws CoreDaoException {
		Search search = new Search(TypeaheadSectionItem.class);
		
		search.addFilter(new Filter(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, model.getTypeaheadSectionItemId()));
		search.addFilter(new Filter(TypeaheadSectionItemDaoConstant.COLUMN_SECTION_ID, model.getSectionId()));
		search.addFilter(new Filter(TypeaheadSectionItemDaoConstant.COLUMN_RULE_ID, model.getRuleId()));
		search.addFilter(new Filter(DAOConstants.PARAM_CREATED_STAMP,
				jodaDateTimeUtil.toSqlDate(model.getCreatedDate())));
		
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();
		
		inParam.put(TypeaheadSectionItemDaoConstant.COLUMN_TYPEAHEAD_SECTION_ITEM_ID, null);
		inParam.put(TypeaheadSectionItemDaoConstant.COLUMN_SECTION_ID, null);
		inParam.put(TypeaheadSectionItemDaoConstant.COLUMN_RULE_ID, null);
		inParam.put(DAOConstants.PARAM_CREATED_STAMP, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);
		
		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		Search search = new Search(TypeaheadSectionItem.class);
		return search;
	}

}
