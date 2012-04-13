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
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;

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
	
	private class AddRelevancyStoredProcedure extends StoredProcedure {
	    public AddRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RELEVANCY);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}

	private class GetRelevancyStoredProcedure extends StoredProcedure {
	    public GetRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_RELEVANCY);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Relevancy>() {
	            public Relevancy mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Relevancy(
	                		rs.getString(DAOConstants.COLUMN_RELEVANCY_ID),
	                		rs.getString(DAOConstants.COLUMN_NAME),
	                		rs.getString(DAOConstants.COLUMN_DESCRIPTION),
	                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
	                		rs.getDate(DAOConstants.COLUMN_START_DATE),
	                		rs.getDate(DAOConstants.COLUMN_END_DATE),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}
	
	private class UpdateRelevancyStoredProcedure extends StoredProcedure {
	    public UpdateRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class DeleteRelevancyStoredProcedure extends StoredProcedure {
	    public DeleteRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RELEVANCY);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class UpdateRelevancyCommentStoredProcedure extends StoredProcedure {
	    public UpdateRelevancyCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY_COMMENT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class AppendRelevancyCommentStoredProcedure extends StoredProcedure {
	    public AppendRelevancyCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_RELEVANCY_COMMENT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}

	private class SearchRelevancyStoredProcedure extends StoredProcedure {
	    public SearchRelevancyStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_SEARCH_RELEVANCY);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Relevancy>() {
	            public Relevancy mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Relevancy(
	                		rs.getString(DAOConstants.COLUMN_RELEVANCY_ID),
	                		rs.getString(DAOConstants.COLUMN_NAME),
	                		rs.getString(DAOConstants.COLUMN_DESCRIPTION),
	                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
	                		rs.getDate(DAOConstants.COLUMN_START_DATE),
	                		rs.getDate(DAOConstants.COLUMN_END_DATE),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}

	private class AddRelevancyFieldStoredProcedure extends StoredProcedure {
	    public AddRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RELEVANCY_FIELD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class GetRelevancyFieldStoredProcedure extends StoredProcedure {
	    public GetRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_RELEVANCY_FIELD);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RelevancyField>() {
	            public RelevancyField mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new RelevancyField(
	                		new Relevancy(rs.getString(DAOConstants.COLUMN_RELEVANCY_ID)),
	                		rs.getString(DAOConstants.COLUMN_FIELD_NAME),
	                		rs.getString(DAOConstants.COLUMN_FIELD_VALUE),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}
	
	private class UpdateRelevancyFieldStoredProcedure extends StoredProcedure {
	    public UpdateRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY_FIELD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_VALUE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class DeleteRelevancyFieldStoredProcedure extends StoredProcedure {
	    public DeleteRelevancyFieldStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RELEVANCY_FIELD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FIELD_NAME, Types.VARCHAR));
	        compile();
	    }
	}

	private class AddRelevancyKeywordStoredProcedure extends StoredProcedure {
	    public AddRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_RELEVANCY_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class GetRelevancyKeywordStoredProcedure extends StoredProcedure {
	    public GetRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_RELEVANCY_KEYWORD);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RelevancyKeyword>() {
	            public RelevancyKeyword mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new RelevancyKeyword(
	            			new Keyword(rs.getString(DAOConstants.COLUMN_PROD_KEYWORD_ID)),
	                		new Relevancy(rs.getString(DAOConstants.COLUMN_RELEVANCY_ID)),
	                		rs.getInt(DAOConstants.COLUMN_PRIORITY),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}
	
	private class UpdateRelevancyKeywordStoredProcedure extends StoredProcedure {
	    public UpdateRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_RELEVANCY_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class DeleteRelevancyKeywordStoredProcedure extends StoredProcedure {
	    public DeleteRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_RELEVANCY_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
	        compile();
	    }
	}

	private class SearchRelevancyKeywordStoredProcedure extends StoredProcedure {
	    public SearchRelevancyKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_SEARCH_RELEVANCY_KEYWORD);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<RelevancyKeyword>() {
	            public RelevancyKeyword mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new RelevancyKeyword(
	            			new Keyword(rs.getString(DAOConstants.COLUMN_KEYWORD)),
	                		new Relevancy(rs.getString(DAOConstants.COLUMN_RELEVANCY_ID), rs.getString(DAOConstants.COLUMN_RELEVANCY_NAME)),
	                		rs.getInt(DAOConstants.COLUMN_PRIORITY),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXACT_MATCH, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}

    public int saveRelevancy(Relevancy relevancy) throws DaoException {
    	return (getRelevancy(relevancy) == null) ? addRelevancy(relevancy)
    			: updateRelevancy(relevancy);
    	// TODO: save parameters??
    }

	public String addRelevancyAndGetId(Relevancy relevancy) throws DaoException {
    	try {
    		DAOValidation.checkStoreId(relevancy.getStore());
        	Map<String, Object> inputs = new HashMap<String, Object>();
        	String id = DAOUtils.generateUniqueId();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, id);
            inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(relevancy.getRelevancyName()));
            inputs.put(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, StringUtils.trimToEmpty(relevancy.getDescription()));
            inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(relevancy.getStore()));
            inputs.put(DAOConstants.PARAM_START_DATE, relevancy.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, relevancy.getEndDate());
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(relevancy.getCreatedBy()));
            addSP.execute(inputs);
            return id;
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addAndGetRelevancy(): " + e.getMessage(), e);
    	}
    }
	
	public int addRelevancy(Relevancy relevancy) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyId(relevancy);
    		DAOValidation.checkStoreId(relevancy.getStore());
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, DAOUtils.generateUniqueId());
            inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(relevancy.getRelevancyName()));
            inputs.put(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, StringUtils.trimToEmpty(relevancy.getDescription()));
            inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(relevancy.getStore()));
            inputs.put(DAOConstants.PARAM_START_DATE, relevancy.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, relevancy.getEndDate());
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(relevancy.getCreatedBy()));
            return DAOUtils.getUpdateCount(addSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addRelevancy(): " + e.getMessage(), e);
    	}
    }
	
	public int updateRelevancy(Relevancy relevancy) throws DaoException {
    	try {
    		DAOValidation.checkRelevancyId(relevancy);
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(relevancy.getRelevancyName()));
            inputs.put(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, StringUtils.trimToEmpty(relevancy.getDescription()));
            inputs.put(DAOConstants.PARAM_START_DATE, relevancy.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, relevancy.getEndDate());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancy.getLastModifiedBy()));
            return DAOUtils.getUpdateCount(updateSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateRelevancy(): " + e.getMessage(), e);
    	}
    }

    public int deleteRelevancy(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyId(relevancy);
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

	public boolean saveRelevancyDetails(Relevancy relevancy) throws DaoException {
		try {
			// save relevancy info
    		saveRelevancy(relevancy);
    		// save relevancy fields info
	    	Map<String, Object> inputs = new HashMap<String, Object>();
			try {
				Map<String, String> map = relevancy.getParameters();
	            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
				for (String fieldName: map.keySet()) {
					String fieldValue = map.get(fieldName);
		            inputs.put(DAOConstants.PARAM_FIELD_NAME, fieldName);
		            inputs.put(DAOConstants.PARAM_FIELD_VALUE, fieldValue);
		            if (DAOUtils.getItem(getRelevancyFieldSP.execute(inputs)) != null) {
			            if (StringUtils.isEmpty(fieldValue)) {
			            	deleteRelevancyFieldSP.execute(inputs);
			            }
			            else {
			            	updateRelevancyFieldSP.execute(inputs);
			            }
		            }
		            else {
		            	addRelevancyFieldSP.execute(inputs);
		            }
				}
				return true;
			} catch (Exception e) {
	    		throw new DaoException("Failed during saveRelevancyDetails(): " + e.getMessage(), e);
	    	}
		} catch (Exception e) {
    		throw new DaoException("Failed during saveRelevancyDetails()", e);
    	}
	}
	
	public Relevancy getRelevancyDetails(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyId(relevancy);
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
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(searchSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during searchRelevancy(): " + e.getMessage(), e);
    	}
	}
	
    public int updateRelevancyComment(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyId(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancy.getLastModifiedBy()));
        	return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateRelevancyComment(): " + e.getMessage(), e);
    	}
    }
    
    public int appendRelevancyComment(Relevancy relevancy) throws DaoException {
		try {
    		DAOValidation.checkRelevancyId(relevancy);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, relevancy.getRelevancyId());
            inputs.put(DAOConstants.PARAM_COMMENT, relevancy.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(relevancy.getLastModifiedBy()));
        	return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during appendRelevancyComment(): " + e.getMessage(), e);
    	}
    }
	
    public int saveRelevancyField(RelevancyField relevancyField) throws DaoException {
    	return (getRelevancyField(relevancyField) == null) ? addRelevancyField(relevancyField)
    			: updateRelevancyField(relevancyField);
    }
    
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
			DAOValidation.checkRelevancyId(relevancy);
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

    public int saveRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
    	return (getRelevancyKeyword(relevancyKeyword) == null) ? addRelevancyKeyword(relevancyKeyword)
    			: saveRelevancyKeyword(relevancyKeyword);
    }
    
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
			DAOValidation.checkRelevancyId(relevancy);
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
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        RecordSet<RelevancyKeyword>  rs = DAOUtils.getRecordSet(searchRelevancyKeywordSP.execute(inputs));
	        return rs;
		} catch (Exception e) {
    		throw new DaoException("Failed during searchRelevancyKeywords(): " + e.getMessage(), e);
    	}
	}
}