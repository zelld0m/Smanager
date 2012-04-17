package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

public abstract class GetStoredProcedure extends StoredProcedure {
	public GetStoredProcedure(JdbcTemplate jdbcTemplate, String storeProcedureName) {
        super(jdbcTemplate, storeProcedureName);
        declareParameters();
        declareSqlReturnResultSetParameters();
        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_TOTAL, new RowMapper<Integer>() {
        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
        	}
        }));
        compile();
	}
	
	protected abstract void declareParameters();
	protected abstract void declareSqlReturnResultSetParameters();

}
