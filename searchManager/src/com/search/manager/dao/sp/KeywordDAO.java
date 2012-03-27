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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.aop.Audit;
import com.search.manager.model.Keyword;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

public class KeywordDAO {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private AddKeywordStoredProcedure addKeywordStoredProcedure;
	private GetKeywordStoredProcedure getKeywordStoredProcedure;
	
// TODO: implement
//	private UpdateKeywordStoredProcedure updateKeywordStoredProcedure;
//	private DeleteKeywordStoredProcedure deleteKeywordStoredProcedure;

	public KeywordDAO() {
	}
	
	private class AddKeywordStoredProcedure extends StoredProcedure {
	    public AddKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
	        compile();
	    }
	}

	private class GetKeywordStoredProcedure extends StoredProcedure {
	    public GetKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_KEYWORD);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Keyword>() {
	        	public Keyword mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new Keyword(rs.getString(DAOConstants.COLUMN_PROD_KEYWORD_ID), rs.getString(DAOConstants.COLUMN_KEYWORD));
	        	}
	        }));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXACT_MATCH, Types.INTEGER));
	        compile();
	    }
	}

	public KeywordDAO(JdbcTemplate jdbcTemplate) {
    	addKeywordStoredProcedure = new AddKeywordStoredProcedure(jdbcTemplate);
    	getKeywordStoredProcedure = new GetKeywordStoredProcedure(jdbcTemplate);
    }
	
	@Audit(entity = Entity.keyword, operation = Operation.add)
    public int addKeyword(String keyword) {
    	if (!StringUtils.isEmpty(keyword)) {
    		keyword = keyword.toLowerCase().trim();
        	Map<String, String> inputs = new HashMap<String, String>();
            inputs.put(DAOConstants.PARAM_KEYWORD_ID, keyword);
            inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
            return DAOUtils.getUpdateCount(addKeywordStoredProcedure.execute(inputs));
    	}
    	return -1;
    }
    
    public List<Keyword> getKeywords(String keyword) {
		if (keyword == null) {
			keyword = "";
		}
		keyword = StringUtils.lowerCase(StringUtils.trimToEmpty(keyword));
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
        inputs.put(DAOConstants.PARAM_START_ROW, 0);
        inputs.put(DAOConstants.PARAM_END_ROW, 0);
        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 0);
        return DAOUtils.getList(getKeywordStoredProcedure.execute(inputs));
    }
    
    public Keyword getKeyword(String keyword) {
		keyword = StringUtils.lowerCase(StringUtils.trimToEmpty(keyword));
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_KEYWORD, keyword);
        inputs.put(DAOConstants.PARAM_START_ROW, 0);
        inputs.put(DAOConstants.PARAM_END_ROW, 0);
        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 1);
        return DAOUtils.getItem(getKeywordStoredProcedure.execute(inputs));
    }
    
}
