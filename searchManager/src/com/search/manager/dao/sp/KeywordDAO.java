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
import com.search.manager.model.Keyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="keywordDAO")
public class KeywordDAO {

	private AddKeywordStoredProcedure addKeywordStoredProcedure;
	private GetKeywordStoredProcedure getKeywordStoredProcedure;
	
// TODO: implement
//	private UpdateKeywordStoredProcedure updateKeywordStoredProcedure;
//	private DeleteKeywordStoredProcedure deleteKeywordStoredProcedure;

	public KeywordDAO() {}
	
	@Autowired
	public KeywordDAO(JdbcTemplate jdbcTemplate) {
    	addKeywordStoredProcedure = new AddKeywordStoredProcedure(jdbcTemplate);
    	getKeywordStoredProcedure = new GetKeywordStoredProcedure(jdbcTemplate);
    }
	
	private class AddKeywordStoredProcedure extends CUDStoredProcedure {
	    public AddKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
		}
	}

	private class GetKeywordStoredProcedure extends GetStoredProcedure {
	    public GetKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXACT_MATCH, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Keyword>() {
	        	public Keyword mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new Keyword(rs.getString(DAOConstants.COLUMN_PROD_KEYWORD_ID), rs.getString(DAOConstants.COLUMN_KEYWORD));
	        	}
	        }));
		}
	}
	
	@Audit(entity = Entity.keyword, operation = Operation.add)
    public int addKeyword(Keyword keyword) throws DaoException {
		try {
			DAOValidation.checkKeywordId(keyword);
	    	Map<String, String> inputs = new HashMap<String, String>();
	        inputs.put(DAOConstants.PARAM_KEYWORD_ID, StringUtils.lowerCase(keyword.getKeywordId()));
	        inputs.put(DAOConstants.PARAM_KEYWORD, StringUtils.lowerCase(keyword.getKeyword()));
	        return DAOUtils.getUpdateCount(addKeywordStoredProcedure.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during addKeyword()", e);
    	}
    }
    
    public List<Keyword> getKeywords(Keyword keyword) {
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(keyword));
        inputs.put(DAOConstants.PARAM_START_ROW, 0);
        inputs.put(DAOConstants.PARAM_END_ROW, 0);
        // TODO: ask DBA to swap implementation for EXACT_MATCH 0 and 1
        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 0);
        return DAOUtils.getList(getKeywordStoredProcedure.execute(inputs));
    }
    
    public Keyword getKeyword(Keyword keyword) {
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_KEYWORD, DAOUtils.getKeywordId(keyword));
        inputs.put(DAOConstants.PARAM_START_ROW, 0);
        inputs.put(DAOConstants.PARAM_END_ROW, 0);
        // TODO: ask DBA to swap implementation for EXACT_MATCH 0 and 1
        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 1);
        return DAOUtils.getItem(getKeywordStoredProcedure.execute(inputs));
    }
}