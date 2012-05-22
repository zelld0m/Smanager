package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Group;
import com.search.manager.model.RecordSet;

@Repository(value="GroupsDAO")
public class GroupsDAO {

	// needed by spring AOP
	public GroupsDAO(){}

	private static final String GET_PERMISSIONS = "select DISTINCT PERMISSION_ID from security_group_permission";
	private static final String GET_GROUPS = "Select GROUP_ID from USER_GROUP";

	@Autowired
	public GroupsDAO(JdbcTemplate jdbcTemplate) {
		getGroupsStoredProcedure = new GetGroupsStoredProcedure(jdbcTemplate);
	}

	private GetGroupsStoredProcedure getGroupsStoredProcedure;

	private class GetGroupsStoredProcedure extends GetStoredProcedure {
	    public GetGroupsStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_GROUP_SECURITY);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PERMISSION_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Group>() {
	        	public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new Group(
	               		rs.getString(DAOConstants.COLUMN_GROUP_ID), 
	               		rs.getString(DAOConstants.COLUMN_PERMISSION_ID)
	          		);
	        	}

	        }));
		}
	}

    public RecordSet<Group> getGroupPermission(String groupId) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_GROUP_ID, groupId);
			inputs.put(DAOConstants.PARAM_PERMISSION_ID, null);
			inputs.put(DAOConstants.PARAM_START_ROW, 1);
			inputs.put(DAOConstants.PARAM_END_ROW, 10);
			return DAOUtils.getRecordSet(getGroupsStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getGroups()", e);
		}
    }	

	public List<String> getGroups() {
		return getGroupsStoredProcedure.getJdbcTemplate().query(
				GET_GROUPS, new RowMapper() {
					public Object mapRow(ResultSet resultSet, int i) throws SQLException {
						return resultSet.getString(1);
					}
				});
	}

	public List<String> getAllPermissions() {
		return getGroupsStoredProcedure.getJdbcTemplate().query(
				GET_PERMISSIONS, new RowMapper() {
					public Object mapRow(ResultSet resultSet, int i) throws SQLException {
						return resultSet.getString(1);
					}
				});
	}
}
