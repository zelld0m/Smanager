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
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="storeKeywordDAO")
public class StoreKeywordDAO {
	
	public StoreKeywordDAO() {}

	@Autowired
	public StoreKeywordDAO(JdbcTemplate jdbcTemplate) {
    	addSp = new AddStoreKeywordStoredProcedure(jdbcTemplate);
    	getSp = new GetStoreKeywordStoredProcedure(jdbcTemplate);
    }

	private AddStoreKeywordStoredProcedure addSp;
	private GetStoreKeywordStoredProcedure getSp;
	
	// TODO: implement
	// update
	// delete
	
	private class AddStoreKeywordStoredProcedure extends CUDStoredProcedure {
	    public AddStoreKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_STORE_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
		}
	}

	private class GetStoreKeywordStoredProcedure extends GetStoredProcedure {
	    public GetStoreKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_STORE_KEYWORD);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXACT_MATCH, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<StoreKeyword>() {
	        	public StoreKeyword mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new StoreKeyword(
	                		new Store(rs.getString(DAOConstants.COLUMN_PRODUCT_STORE_ID)),
	                		new Keyword(rs.getString(DAOConstants.COLUMN_PROD_KEYWORD_ID),rs.getString(DAOConstants.COLUMN_KEYWORD)));
	        	}
	        }));
		}
	}
	
	@Audit(entity = Entity.storeKeyword, operation = Operation.add)
    public int addStoreKeyword(StoreKeyword storeKeyword) throws DaoException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
	    	Map<String, String> inputs = new HashMap<String, String>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, storeKeyword.getStoreId());
	        inputs.put(DAOConstants.PARAM_KEYWORD, storeKeyword.getKeywordId());
	        return DAOUtils.getUpdateCount(addSp.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during addStoreKeyword()", e);
    	}
    }
    
    public RecordSet<StoreKeyword> getStoreKeywords(SearchCriteria<StoreKeyword> criteria) throws DaoException {
		try {
			StoreKeyword sk = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(sk));
	        inputs.put(DAOConstants.PARAM_KEYWORD, StringUtils.trimToEmpty(DAOUtils.getKeywordId(sk)));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_EXACT_MATCH, 0);
	        return DAOUtils.getRecordSet(getSp.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getStoreKeywords()", e);
    	}
    }

    public StoreKeyword getStoreKeyword(String storeId, String keyword) throws DaoException {
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
        return DAOUtils.getItem(getSp.execute(inputs));
    }
    
	private final static String GET_ELEVATE_KEYWORDS_SQL = "select DISTINCT(PROD_KEYWORD_ID), * from PROD_KEYWORD_MEMBER where STATUS_ID = 'enabled' AND PARENT_MEMBER_ID = ?";
	private final static String GET_EXCLUDE_KEYWORDS_SQL = "select DISTINCT(PROD_KEYWORD_ID), * from PROD_KEYWORD_MEMBER where STATUS_ID = 'disabled' AND PARENT_MEMBER_ID = ?";
	private final static String GET_DEMOTE_KEYWORDS_SQL = "select DISTINCT(PROD_KEYWORD_ID), * from PROD_KEYWORD_MEMBER where STATUS_ID = 'demoted' AND PARENT_MEMBER_ID= ?";
    
    public List<Keyword> getAllKeywords(String storeId, RuleEntity ruleEntity) {
		String sql = "";
    	switch (ruleEntity) {
	    	case ELEVATE:
	    		sql = GET_ELEVATE_KEYWORDS_SQL;
	    		break;
	    	case EXCLUDE:
	    		sql = GET_EXCLUDE_KEYWORDS_SQL;
	    		break;
	    	case DEMOTE:
	    		sql = GET_DEMOTE_KEYWORDS_SQL;
	    		break;
	    	default: return null;
    	}
		
		return getSp.getJdbcTemplate().query(
				sql, new String[] {storeId}, new RowMapper<Keyword>() {
					public Keyword mapRow(ResultSet resultSet, int i) throws SQLException {
						return new Keyword(resultSet.getString(1));
					}
				});
    }
}
