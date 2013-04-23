package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="relevancyDAO")
public class RelevancyDAO {

	// for AOP use
	public RelevancyDAO(){}
	
	@Autowired
	public RelevancyDAO(JdbcTemplate jdbcTemplate) {
		addSP = new AddRelevancyStoredProcedure(jdbcTemplate);
		updateSP = new UpdateRelevancyStoredProcedure(jdbcTemplate) ;
		deleteSP = new DeleteRelevancyStoredProcedure(jdbcTemplate);
		getSP = new GetRelevancyStoredProcedure(jdbcTemplate);
		searchSP = new SearchRelevancyStoredProcedure(jdbcTemplate);
		updateCommentSP = new UpdateRelevancyCommentStoredProcedure(jdbcTemplate);
		appendCommentSP = new AppendRelevancyCommentStoredProcedure(jdbcTemplate);
		
		addRelevancyFieldSP = new AddRelevancyFieldStoredProcedure(jdbcTemplate);
		getRelevancyFieldSP = new GetRelevancyFieldStoredProcedure(jdbcTemplate);
		updateRelevancyFieldSP = new UpdateRelevancyFieldStoredProcedure(jdbcTemplate);
		deleteRelevancyFieldSP = new DeleteRelevancyFieldStoredProcedure(jdbcTemplate);
		
		addRelevancyKeywordSP = new AddRelevancyKeywordStoredProcedure(jdbcTemplate);
		getRelevancyKeywordSP = new GetRelevancyKeywordStoredProcedure(jdbcTemplate);
		updateRelevancyKeywordSP = new UpdateRelevancyKeywordStoredProcedure(jdbcTemplate);
		deleteRelevancyKeywordSP = new DeleteRelevancyKeywordStoredProcedure(jdbcTemplate);
		searchRelevancyKeywordSP = new SearchRelevancyKeywordStoredProcedure(jdbcTemplate);
	}
	
	private AddRelevancyStoredProcedure addSP;
	private GetRelevancyStoredProcedure getSP;
	private UpdateRelevancyStoredProcedure updateSP;
	private DeleteRelevancyStoredProcedure deleteSP;
	private SearchRelevancyStoredProcedure searchSP;
	private UpdateRelevancyCommentStoredProcedure updateCommentSP;
	private AppendRelevancyCommentStoredProcedure appendCommentSP;
	
	private AddRelevancyFieldStoredProcedure addRelevancyFieldSP;
	private GetRelevancyFieldStoredProcedure getRelevancyFieldSP;
	private UpdateRelevancyFieldStoredProcedure updateRelevancyFieldSP;
	private DeleteRelevancyFieldStoredProcedure deleteRelevancyFieldSP;
	
	private AddRelevancyKeywordStoredProcedure addRelevancyKeywordSP;
	private GetRelevancyKeywordStoredProcedure getRelevancyKeywordSP;
	private UpdateRelevancyKeywordStoredProcedure updateRelevancyKeywordSP;
	private DeleteRelevancyKeywordStoredProcedure deleteRelevancyKeywordSP;
	private SearchRelevancyKeywordStoredProcedure searchRelevancyKeywordSP;
	
	private class AddRelevancyStoredProcedure extends CUDStoredProcedure {
	    public AddRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RELEVANCY);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class GetRelevancyStoredProcedure extends GetStoredProcedure {
	    public GetRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_RELEVANCY);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Relevancy>() {
	            public Relevancy mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Relevancy(
	                		rs.getString(DAOConstants.COLUMN_RELEVANCY_ID),
	                		rs.getString(DAOConstants.COLUMN_NAME),
	                		rs.getString(DAOConstants.COLUMN_DESCRIPTION),
	                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_START_DATE)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_END_DATE)),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
	            }
	        }));
		}
	}
	
	private class UpdateRelevancyStoredProcedure extends CUDStoredProcedure {
	    public UpdateRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class DeleteRelevancyStoredProcedure extends CUDStoredProcedure {
	    public DeleteRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RELEVANCY);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class UpdateRelevancyCommentStoredProcedure extends CUDStoredProcedure {
	    public UpdateRelevancyCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY_COMMENT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class AppendRelevancyCommentStoredProcedure extends CUDStoredProcedure {
	    public AppendRelevancyCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_RELEVANCY_COMMENT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class SearchRelevancyStoredProcedure extends GetStoredProcedure {
	    public SearchRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_SEARCH_RELEVANCY);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Relevancy>() {
	            public Relevancy mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Relevancy(
	                		rs.getString(DAOConstants.COLUMN_RELEVANCY_ID),
	                		rs.getString(DAOConstants.COLUMN_NAME),
	                		rs.getString(DAOConstants.COLUMN_DESCRIPTION),
	                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_START_DATE)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_END_DATE)),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
	            }
	        }));
		}
	}

	private class AddRelevancyFieldStoredProcedure extends CUDStoredProcedure {
	    public AddRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RELEVANCY_FIELD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}
	
	private class GetRelevancyFieldStoredProcedure extends GetStoredProcedure {
	    public GetRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_RELEVANCY_FIELD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RelevancyField>() {
	            public RelevancyField mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new RelevancyField(
	                		new Relevancy(rs.getString(DAOConstants.COLUMN_RELEVANCY_ID)),
	                		rs.getString(DAOConstants.COLUMN_FIELD_NAME),
	                		rs.getString(DAOConstants.COLUMN_FIELD_VALUE),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
	                				JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
	            }
	        }));
		}
	}
	
	private class UpdateRelevancyFieldStoredProcedure extends CUDStoredProcedure {
	    public UpdateRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY_FIELD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class DeleteRelevancyFieldStoredProcedure extends CUDStoredProcedure {
	    public DeleteRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RELEVANCY_FIELD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
		}
	}

	private class AddRelevancyKeywordStoredProcedure extends CUDStoredProcedure {
	    public AddRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RELEVANCY_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}
	
	private class GetRelevancyKeywordStoredProcedure extends GetStoredProcedure {
	    public GetRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_RELEVANCY_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RelevancyKeyword>() {
	            public RelevancyKeyword mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new RelevancyKeyword(
	            			new Keyword(rs.getString(DAOConstants.COLUMN_PROD_KEYWORD_ID)),
	                		new Relevancy(rs.getString(DAOConstants.COLUMN_RELEVANCY_ID)),
	                		rs.getInt(DAOConstants.COLUMN_PRIORITY),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
	            }
	        }));
		}
	}
	
	private class UpdateRelevancyKeywordStoredProcedure extends CUDStoredProcedure {
	    public UpdateRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}
	
	private class DeleteRelevancyKeywordStoredProcedure extends CUDStoredProcedure {
	    public DeleteRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RELEVANCY_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
		}
	}

	private class SearchRelevancyKeywordStoredProcedure extends GetStoredProcedure {
	    public SearchRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_SEARCH_RELEVANCY_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXACT_MATCH, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RelevancyKeyword>() {
	            public RelevancyKeyword mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new RelevancyKeyword(
	            			new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD)),
	                		new Relevancy(rs.getString(DAOConstants.COLUMN_RELEVANCY_ID), rs.getString(DAOConstants.COLUMN_RELEVANCY_NAME)),
	                		rs.getInt(DAOConstants.COLUMN_PRIORITY),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
	                				JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
	            }
	        }));
		}
	}

	@Audit(entity = Entity.relevancy, operation = Operation.add)
	public String addRelevancyAndGetId(Relevancy relevancy) throws DaoException {
		String id = DAOUtils.generateUniqueId();
		relevancy.setRelevancyId(id);
		return (addRelevancy(relevancy) > 0) ?  id : null;
    }
	
	@Audit(entity = Entity.relevancy, operation = Operation.add)
	public int addRelevancy(Relevancy relevancy) throws DaoException {
    	try {
    		DAOValidation.checkRelevancy(relevancy);
    		DAOValidation.checkStoreId(relevancy.getStore());
        	Map<String, Object> inputs = new HashMap<String, Object>();
        	String relevancyId = relevancy.getRelevancyId();
        	if (StringUtils.isEmpty(relevancyId)) {
        		relevancyId = DAOUtils.generateUniqueId();
        	}
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyId);
            inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(relevancy.getRelevancyName()));
            inputs.put(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, StringUtils.trimToEmpty(relevancy.getDescription()));
            inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(relevancy.getStore()));
            inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(relevancy.getStartDateTime()));
            inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(relevancy.getEndDateTime()));
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(relevancy.getCreatedBy()));
            return DAOUtils.getUpdateCount(addSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRelevancy(): " + e.getMessage(), e);
    	}
    }
	
	@Audit(entity = Entity.relevancy, operation = Operation.update)
	public int updateRelevancy(Relevancy relevancy) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyPK(relevancy);
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(relevancy.getRelevancyName()));
            inputs.put(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, StringUtils.trimToEmpty(relevancy.getDescription()));
            inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(relevancy.getStartDateTime()));
            inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(relevancy.getEndDateTime()));
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancy.getLastModifiedBy()));
            return DAOUtils.getUpdateCount(updateSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRelevancy(): " + e.getMessage(), e);
    	}
    }

	@Audit(entity = Entity.relevancy, operation = Operation.delete)
    public int deleteRelevancy(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyPK(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
        	return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during deleteRelevancy(): " + e.getMessage(), e);
    	}
    }

	public Relevancy getRelevancy(Relevancy relevancy) throws DaoException {
		if (relevancy == null || relevancy.getRelevancyId() == null) {
			return null;
		}
		try {
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_STORE_ID, null);
	        inputs.put(DAOConstants.PARAM_START_DATE, null);
	        inputs.put(DAOConstants.PARAM_END_DATE, null);
	        inputs.put(DAOConstants.PARAM_START_ROW, 1);
	        inputs.put(DAOConstants.PARAM_END_ROW, 1);
	        return DAOUtils.getItem(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getRelevancy()", e);
    	}
	}
	
	public Relevancy getRelevancyDetails(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyPK(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_STORE_ID, null);
	        inputs.put(DAOConstants.PARAM_START_DATE, null);
	        inputs.put(DAOConstants.PARAM_END_DATE, null);
	        inputs.put(DAOConstants.PARAM_START_ROW, 1);
	        inputs.put(DAOConstants.PARAM_END_ROW, 1);
	        Relevancy result = DAOUtils.getItem(getSP.execute(inputs));
	        List<RelevancyField> fields = getRelevancyFields(relevancy).getList();
	        for (RelevancyField field: fields) {
	        	result.setParameter(field.getFieldName(), field.getFieldValue());
	        }
	        return result;
		} catch (Exception e) {
    		throw new DaoException("Failed during getRelevancy()", e);
    	}
	}
	
	public RecordSet<Relevancy> searchRelevancy(SearchCriteria<Relevancy> criteria, MatchType relevancyMatchType) throws DaoException {
		try {
			DAOValidation.checkSearchCriteria(criteria);
			Relevancy model = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(model.getStore()));
			inputs.put(DAOConstants.PARAM_RELEVANCY, (relevancyMatchType == null) ?
					null : (relevancyMatchType.equals(MatchType.MATCH_ID) ?
							model.getRelevancyId() : model.getRelevancyName()));
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, (relevancyMatchType == null) ?
	        		null : relevancyMatchType.getIntValue());
	        inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(criteria.getStartDate()));
	        inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(criteria.getEndDate()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(searchSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during searchRelevancy(): " + e.getMessage(), e);
    	}
	}
	
	@Audit(entity = Entity.relevancy, operation = Operation.updateComment)
    public int updateRelevancyComment(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyPK(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancy.getLastModifiedBy()));
        	return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateRelevancyComment(): " + e.getMessage(), e);
    	}
    }
    
	@Audit(entity = Entity.relevancy, operation = Operation.appendComment)
    public int appendRelevancyComment(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyPK(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancy.getLastModifiedBy()));
        	return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during appendRelevancyComment(): " + e.getMessage(), e);
    	}
    }
	
	// For some reason, only the first method called is being monitored by AOP
	@Audit(entity = Entity.relevancy, operation = Operation.saveRelevancyField)
    public int saveRelevancyField(RelevancyField relevancyField) throws DaoException {
    	return (getRelevancyField(relevancyField) == null) ? addRelevancyField(relevancyField)
    			: updateRelevancyField(relevancyField);
    }
    
	@Audit(entity = Entity.relevancy, operation = Operation.addRelevancyField)
	public int addRelevancyField(RelevancyField relevancyField) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyFieldPK(relevancyField);
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyField.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_FIELD_NAME, relevancyField.getFieldName());
            inputs.put(DAOConstants.PARAM_FIELD_VALUE, relevancyField.getFieldValue());
            inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(relevancyField.getCreatedBy()));            
            return DAOUtils.getUpdateCount(addRelevancyFieldSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRelevancyField(): " + e.getMessage(), e);
    	}
    }
	
	@Audit(entity = Entity.relevancy, operation = Operation.updateRelevancyField)
	public int updateRelevancyField(RelevancyField relevancyField) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyFieldPK(relevancyField);
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyField.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_FIELD_NAME, relevancyField.getFieldName());
            inputs.put(DAOConstants.PARAM_FIELD_VALUE, relevancyField.getFieldValue());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancyField.getLastModifiedBy()));
            return DAOUtils.getUpdateCount(updateRelevancyFieldSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRelevancyField(): " + e.getMessage(), e);
    	}
    }

	@Audit(entity = Entity.relevancy, operation = Operation.deleteRelevancyField)
    public int deleteRelevancyField(RelevancyField relevancyField) throws DaoException {
		try {
			DAOValidation.checkRelevancyFieldPK(relevancyField);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyField.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_FIELD_NAME, relevancyField.getFieldName());
        	return DAOUtils.getUpdateCount(deleteRelevancyFieldSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during deleteRelevancyField(): " + e.getMessage(), e);
    	}
    }

	public RelevancyField getRelevancyField(RelevancyField relevancyField) throws DaoException {
		try {
			DAOValidation.checkRelevancyFieldPK(relevancyField);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyField.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_FIELD_NAME, relevancyField.getFieldName());
	        inputs.put(DAOConstants.PARAM_START_ROW, 1);
	        inputs.put(DAOConstants.PARAM_END_ROW, 1);
	        return DAOUtils.getItem(getRelevancyFieldSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getRelevancyField(): " + e.getMessage(), e);
    	}
	}
	
	public RecordSet<RelevancyField> getRelevancyFields(Relevancy relevancy) throws DaoException {
		try {
			DAOValidation.checkRelevancyPK(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_FIELD_NAME, null);
	        inputs.put(DAOConstants.PARAM_START_ROW, 0);
	        inputs.put(DAOConstants.PARAM_END_ROW, 0);
	        return DAOUtils.getRecordSet(getRelevancyFieldSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getRelevancyFields(): " + e.getMessage(), e);
    	}
	}
    
	@Audit(entity = Entity.relevancy, operation = Operation.mapKeyword)
	public int addRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyKeywordPK(relevancyKeyword);
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyKeyword.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(relevancyKeyword.getKeyword()));
            inputs.put(DAOConstants.PARAM_PRIORITY, relevancyKeyword.getPriority());
            inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(relevancyKeyword.getCreatedBy()));
            return DAOUtils.getUpdateCount(addRelevancyKeywordSP.execute(inputs));
		}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRelevancyKeyword(): " + e.getMessage(), e);
    	}
    }
	
	@Audit(entity = Entity.relevancy, operation = Operation.updateKeywordMapping)
	public int updateRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyKeywordPK(relevancyKeyword);
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyKeyword.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(relevancyKeyword.getKeyword()));
            inputs.put(DAOConstants.PARAM_PRIORITY, relevancyKeyword.getPriority());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancyKeyword.getLastModifiedBy()));
            return DAOUtils.getUpdateCount(updateRelevancyKeywordSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRelevancyKeyword(): " + e.getMessage(), e);
    	}
    }

	@Audit(entity = Entity.relevancy, operation = Operation.unmapKeyword)
    public int deleteRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
		try {
    		DAOValidation.checkRelevancyKeywordPK(relevancyKeyword);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyKeyword.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(relevancyKeyword.getKeyword()));
        	return DAOUtils.getUpdateCount(deleteRelevancyKeywordSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during deleteRelevancyKeyword(): " + e.getMessage(), e);
    	}
    }

	public RelevancyKeyword getRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
		try {
    		DAOValidation.checkRelevancyKeywordPK(relevancyKeyword);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancyKeyword.getRelevancy().getRelevancyId());
            inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(relevancyKeyword.getKeyword()));
	        inputs.put(DAOConstants.PARAM_START_ROW, 1);
	        inputs.put(DAOConstants.PARAM_END_ROW, 1);
	        return DAOUtils.getItem(getRelevancyKeywordSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getRelevancyKeyword(): " + e.getMessage(), e);
    	}
	}
	
	public RecordSet<RelevancyKeyword> getRelevancyKeywords(Relevancy relevancy) throws DaoException {
		try {
			DAOValidation.checkRelevancyPK(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_KEYWORD, null);
	        inputs.put(DAOConstants.PARAM_START_ROW, 0);
	        inputs.put(DAOConstants.PARAM_END_ROW, 0);
	        return DAOUtils.getRecordSet(getRelevancyKeywordSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getRelevancyKeywords(): " + e.getMessage(), e);
    	}
	}

	public RecordSet<RelevancyKeyword> searchRelevancyKeywords(SearchCriteria<RelevancyKeyword> criteria, MatchType relevancyMatchType,
			ExactMatch keywordExactMatch) throws DaoException {
		try {
			// TODO: add validation
			DAOValidation.checkSearchCriteria(criteria);
			RelevancyKeyword model = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	    	if (model.getRelevancy() != null) {
		        inputs.put(DAOConstants.PARAM_STORE_ID, 
		        		model.getRelevancy().getStore() == null ? null : DAOUtils.getStoreId(model.getRelevancy().getStore()));
				inputs.put(DAOConstants.PARAM_RELEVANCY, (relevancyMatchType == null) ?
						null : (relevancyMatchType.equals(MatchType.MATCH_ID) ?
								model.getRelevancy().getRelevancyId() : model.getRelevancy().getRelevancyName()));
	    	}
	    	else {
		        inputs.put(DAOConstants.PARAM_STORE_ID, null);
				inputs.put(DAOConstants.PARAM_RELEVANCY, null);
	    	}
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, (relevancyMatchType == null) ?
	        		null : relevancyMatchType.getIntValue());
	        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(model.getKeyword()));
	        inputs.put(DAOConstants.PARAM_EXACT_MATCH, keywordExactMatch == null ? null : keywordExactMatch.getIntValue());
	        inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(criteria.getStartDate()));
	        inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(criteria.getEndDate()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        RecordSet<RelevancyKeyword>  rs = DAOUtils.getRecordSet(searchRelevancyKeywordSP.execute(inputs));
	        return rs;
		} catch (Exception e) {
    		throw new DaoException("Failed during searchRelevancyKeywords(): " + e.getMessage(), e);
    	}
	}
}