package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.aop.Audit;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

public class StoreKeywordDAO {
	
	public StoreKeywordDAO() {
	}
	
	private Logger logger = Logger.getLogger(this.getClass());

	private AddStoreKeywordStoredProcedure addSp;
	private GetStoreKeywordStoredProcedure getSp;
	
	// TODO: implement
	// update
	// delete
	
	private class AddStoreKeywordStoredProcedure extends StoredProcedure {
	    public AddStoreKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_STORE_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
	        compile();
	    }
	}

	private class GetStoreKeywordStoredProcedure extends StoredProcedure {
	    public GetStoreKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_STORE_KEYWORD);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<StoreKeyword>() {
	        	public StoreKeyword mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new StoreKeyword(
	                		new Store(rs.getString(DAOConstants.COLUMN_PRODUCT_STORE_ID), rs.getString(DAOConstants.COLUMN_STORE_NAME)),
	                		new Keyword(rs.getString(DAOConstants.COLUMN_PROD_KEYWORD_ID),rs.getString(DAOConstants.COLUMN_KEYWORD)));
	        	}
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXACT_MATCH, Types.INTEGER));
	        compile();
	    }
	}
	
	public StoreKeywordDAO(JdbcTemplate jdbcTemplate) {
    	addSp = new AddStoreKeywordStoredProcedure(jdbcTemplate);
    	getSp = new GetStoreKeywordStoredProcedure(jdbcTemplate);
    }

	@Audit(entity = Entity.storeKeyword, operation = Operation.add)
    public int addStoreKeyword(String storeId, String keyword) throws DataAccessException {
    	int i = -1;
		if (!StringUtils.isEmpty(keyword)) {
			storeId= storeId.toLowerCase().trim();
			keyword = keyword.toLowerCase().trim();
	    	Map<String, String> inputs = new HashMap<String, String>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
	        inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
	        Map<String,Object> result = addSp.execute(inputs);
	        if (result != null) {
	        	i = Integer.parseInt(String.valueOf(result.get(DAOConstants.UPDATE_COUNT_1)));
	        }
    	}
    	return i;
    }
    
    @SuppressWarnings("unchecked")
    public RecordSet<StoreKeyword> getStoreKeywords(String storeId, String keyword, Integer startRow, Integer numRows) throws DataAccessException {
    	List<StoreKeyword> keywords = new ArrayList<StoreKeyword>();
    	int size = 0;
    	if (keyword == null) {
			keyword = "";
		}
		storeId = storeId.toLowerCase().trim();
		keyword = keyword.toLowerCase().trim();

		if (startRow == null) {
			startRow = 0;
		}
		if (numRows == null) {
			numRows = 0;
		}
		
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
        inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
        inputs.put(DAOConstants.PARAM_START_ROW, startRow);
        inputs.put(DAOConstants.PARAM_END_ROW, numRows);
        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 0);
        Map<String,Object> result = getSp.execute(inputs);
        if (result != null) {
        	keywords.addAll((List<StoreKeyword>)result.get(DAOConstants.RESULT_SET_1));
        	size = ((List<Integer>)result.get(DAOConstants.RESULT_SET_2)).get(0);
        }
    	return new RecordSet<StoreKeyword>(keywords, size);
    }

    @SuppressWarnings("unchecked")
    public StoreKeyword getStoreKeyword(String storeId, String keyword) throws DataAccessException {
    	StoreKeyword sk = null;
		if (keyword == null) {
			keyword = "";
		}
		storeId= storeId.toLowerCase().trim();
		keyword = keyword.toLowerCase().trim();
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
        inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
        inputs.put(DAOConstants.PARAM_START_ROW, 0);
        inputs.put(DAOConstants.PARAM_END_ROW, 0);
        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 1);
        Map<String,Object> result = getSp.execute(inputs);
        if (result != null) {
        	List<StoreKeyword> list = (List<StoreKeyword>)result.get(DAOConstants.RESULT_SET_1);
        	if (list != null && !list.isEmpty()) {
            	sk = list.get(0);
        	}
        }
    	return sk;
    }
    
}
