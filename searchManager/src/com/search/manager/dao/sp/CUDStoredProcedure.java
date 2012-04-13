package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * Base class for Create, Update, Delete StoredProcedure classes
 */
public class CUDStoredProcedure extends StoredProcedure {
	public CUDStoredProcedure(JdbcTemplate jdbcTemplate, String storeProcedureName) {
        super(jdbcTemplate, storeProcedureName);
        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Integer>() {
        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(DAOConstants.COLUMN_RESULT);
        	}
        }));
	}
}
