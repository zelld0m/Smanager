package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.search.manager.core.constant.KeywordAttributeDaoConstant;
import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.dao.KeywordAttributeDao;
import com.search.manager.core.enums.KeywordAttributeType;
import com.search.manager.core.enums.Status;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.KeywordAttribute;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;

@Component("keywordAttributeDaoSp")
public class KeywordAttributeDAOSpImpl extends GenericDaoSpImpl<KeywordAttribute> implements KeywordAttributeDao{

	@Autowired
	@Qualifier("jodaDateTimeUtil")
	private JodaDateTimeUtil jodaDateTimeUtil;
	
	private AddStoredProcedure addStoredProcedure;
	private DeleteStoredProcedure deleteStoredProcedure;
	private UpdateStoredProcedure updateStoredProcedure;
	private SearchStoredProcedure searchStoredProcedure;
	
	@SuppressWarnings("unused")
	private KeywordAttributeDAOSpImpl() {}
	
	@Autowired(required = true)
	public KeywordAttributeDAOSpImpl(JdbcTemplate jdbcTemplate) {
		addStoredProcedure = new AddStoredProcedure(jdbcTemplate);
		deleteStoredProcedure = new DeleteStoredProcedure(jdbcTemplate);
		updateStoredProcedure = new UpdateStoredProcedure(jdbcTemplate);
		searchStoredProcedure = new SearchStoredProcedure(jdbcTemplate);
	}
	
	private class AddStoredProcedure extends GetStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, KeywordAttributeDaoConstant.SP_ADD_KEYWORD_ATTRIBUTE);
		}


		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_INPUT_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_PRIORITY, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<KeywordAttribute>() {
				public KeywordAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	private class UpdateStoredProcedure extends GetStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, KeywordAttributeDaoConstant.SP_UPDATE_KEYWORD_ATTRIBUTE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_INPUT_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_PRIORITY, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}
		
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<KeywordAttribute>() {
				public KeywordAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private class DeleteStoredProcedure extends CUDStoredProcedure {

        public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, KeywordAttributeDaoConstant.SP_DELETE_KEYWORD_ATTRIBUTE);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, Types.VARCHAR));
        }
    }

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, KeywordAttributeDaoConstant.SP_GET_KEYWORD_ATTRIBUTE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(TypeaheadDaoConstant.COLUMN_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<KeywordAttribute>() {
				public KeywordAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private KeywordAttribute buildModel(ResultSet rs, int rowNum) throws SQLException {
		if (rs.getMetaData().getColumnCount() < 2) {
			return null;
		}

		KeywordAttribute attribute = new KeywordAttribute();


		attribute.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
		attribute.setLastModifiedBy(rs.getString("updated_by"));
		attribute.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
		attribute.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));
		attribute.setPriority(rs.getInt(KeywordAttributeDaoConstant.COLUMN_PRIORITY));
		attribute.setDisabled(Status.DISABLED.getName().equals(rs.getString(KeywordAttributeDaoConstant.COLUMN_STATUS)));
		attribute.setKeywordAttributeId(rs.getString(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID));
		attribute.setInputParamEnumId(rs.getString(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID));
		attribute.setParentAttributeId(rs.getString(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID));
		attribute.setKeywordAttributeType(KeywordAttributeType.findbyCode(attribute.getInputParamEnumId()));
		
		attribute.setInputValue(rs.getString(KeywordAttributeDaoConstant.COLUMN_INPUT_VALUE));

		return attribute;
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
	protected Map<String, Object> generateAddInput(KeywordAttribute model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;


		if (model != null) {
			String attributeId = model.getKeywordAttributeId();

			if (StringUtils.isBlank(attributeId)) {
				model.setKeywordAttributeId(DAOUtils.generateUniqueId());
			}

			inputs = new HashMap<String, Object>();
			inputs.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, model.getKeywordAttributeId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, model.getParentAttributeId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ID, model.getKeywordId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, model.getInputParamEnumId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_PRIORITY, model.getPriority() != null ? model.getPriority() : 1);
			inputs.put(KeywordAttributeDaoConstant.COLUMN_INPUT_VALUE, model.getInputValue());
			String status = getStatus(model);
			inputs.put(KeywordAttributeDaoConstant.COLUMN_STATUS, status != null ? status : Status.ENABLED.getName());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
			inputs.put(DAOConstants.PARAM_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getCreatedDate()));
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(KeywordAttribute model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;


		if (model != null) {
			
			inputs = new HashMap<String, Object>();
			inputs.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, model.getKeywordAttributeId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, model.getParentAttributeId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ID, model.getKeywordId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, model.getInputParamEnumId());
			inputs.put(KeywordAttributeDaoConstant.COLUMN_PRIORITY, model.getPriority() != null ? model.getPriority() : 1);
			inputs.put(KeywordAttributeDaoConstant.COLUMN_INPUT_VALUE, model.getInputValue());
			String status = getStatus(model);
			inputs.put(KeywordAttributeDaoConstant.COLUMN_STATUS, status != null ? status : Status.ENABLED.getName());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, model.getLastModifiedBy());
			inputs.put(DAOConstants.COLUMN_LAST_UPDATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getLastModifiedDate()));
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(KeywordAttribute model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

        if (model != null) {
            inputs = new HashMap<String, Object>();
            inputs.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, model.getKeywordAttributeId());
        }

        return inputs;
	}

	@Override
	protected Search generateSearchInput(KeywordAttribute model)
			throws CoreDaoException {

		Search search = new Search(KeywordAttribute.class);
		
		search.addFilter(new Filter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, model.getKeywordAttributeId()));
		search.addFilter(new Filter(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ID, model.getKeywordId()));
		search.addFilter(new Filter(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, model.getInputParamEnumId()));
		search.addFilter(new Filter(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, model.getParentAttributeId()));
		search.addFilter(new Filter(KeywordAttributeDaoConstant.COLUMN_STATUS, model.getDisabled() != null ? getStatus(model) : null));
		search.addFilter(new Filter(DAOConstants.PARAM_CREATED_STAMP,
				jodaDateTimeUtil.toSqlDate(model.getCreatedDate())));
		
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();
		
		inParam.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ATTRIBUTE_ID, null);
		inParam.put(KeywordAttributeDaoConstant.COLUMN_KEYWORD_ID, null);
		inParam.put(KeywordAttributeDaoConstant.COLUMN_INPUT_PARAM_ENUM_ID, null);
		inParam.put(KeywordAttributeDaoConstant.COLUMN_PARENT_ATTRIBUTE_ID, null);
		inParam.put(KeywordAttributeDaoConstant.COLUMN_STATUS, null);
		inParam.put(DAOConstants.PARAM_CREATED_STAMP, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);
		
		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getStatus(KeywordAttribute attribute) {
		
		if(attribute.getDisabled() != null && attribute.getDisabled()) {
			return Status.DISABLED.getName();
		} else {
			return Status.ENABLED.getName();
		}
	}

}
