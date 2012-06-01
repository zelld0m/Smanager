package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.User;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;

@Repository(value="usersDAO")
public class UsersDAO {

	// needed by spring AOP
	public UsersDAO(){}

	@Autowired
	public UsersDAO(JdbcTemplate jdbcTemplate) {
		addUserStoredProcedure = new AddUserStoredProcedure(jdbcTemplate);
		getUserStoredProcedure = new GetUserStoredProcedure(jdbcTemplate);
		deleteUserStoredProcedure = new DeleteUserStoredProcedure(jdbcTemplate);
		updateUserStoredProcedure = new UpdateUserStoredProcedure(jdbcTemplate);
	}

	private GetUserStoredProcedure getUserStoredProcedure;
	private AddUserStoredProcedure addUserStoredProcedure;
	private DeleteUserStoredProcedure deleteUserStoredProcedure;
	private UpdateUserStoredProcedure updateUserStoredProcedure;
	
	private class GetUserStoredProcedure extends GetStoredProcedure {
	    public GetUserStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_USERS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_GROUP_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAMELIKE, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_EMAIL, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_ACTIVE_USER, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE2, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE2, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_USER_LOCKED, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW2, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW2, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<User>() {
	        	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new User(
	                		rs.getString(DAOConstants.COLUMN_USER_NAME), 
	                		rs.getString(DAOConstants.COLUMN_FULL_NAME), 
	                		rs.getString(DAOConstants.COLUMN_CURRENT_PASSWORD),
	                		rs.getString(DAOConstants.COLUMN_EMAIL),
	                		rs.getString(DAOConstants.COLUMN_GROUP_ID),
	                		rs.getBoolean(DAOConstants.COLUMN_ACCT_NON_LOCKED),
	                		!rs.getBoolean(DAOConstants.COLUMN_CRED_NON_EXPIRED),
	                		rs.getTimestamp(DAOConstants.COLUMN_LAST_ACCESS_DATE),
	                		rs.getString(DAOConstants.COLUMN_IP),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP),
	                		rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP),
	                		rs.getTimestamp(DAOConstants.COLUMN_THRU_DATE),
	                		rs.getString(DAOConstants.COLUMN_STORE_ID));
	        	}

	        }));
		}
	}

    public RecordSet<User> getUsers(SearchCriteria<User> searchCriteria, MatchType matchTypeName) throws DaoException {
		try {
			User user = searchCriteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_GROUP_ID, user.getGroupId());
			inputs.put(DAOConstants.PARAM_USER_NAME, null);
			inputs.put(DAOConstants.PARAM_USER_NAMELIKE, null);
			switch (matchTypeName) {
				case MATCH_ID:
					inputs.put(DAOConstants.PARAM_USER_NAME, user.getUsername());
					break;
				default:
					inputs.put(DAOConstants.PARAM_USER_NAMELIKE, user.getFullName());
					break;
			}
			inputs.put(DAOConstants.PARAM_EMAIL, user.getEmail());
			inputs.put(DAOConstants.PARAM_STORE_ID, user.getStoreId());
			inputs.put(DAOConstants.PARAM_ACTIVE_USER, user.isAccountNonExpired()==null?null:user.isAccountNonExpired()?'Y':'N');
			inputs.put(DAOConstants.PARAM_START_DATE2, DateAndTimeUtils.convertToSqlTimestampStartOfDay(searchCriteria.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE2, DateAndTimeUtils.convertToSqlTimestampEndOfDay(searchCriteria.getEndDate()));
			inputs.put(DAOConstants.PARAM_USER_LOCKED, user.isAccountNonLocked()==null?null:user.isAccountNonLocked()?'1':'0');
			inputs.put(DAOConstants.PARAM_START_ROW2, searchCriteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW2, searchCriteria.getEndRow());
			return DAOUtils.getRecordSet(getUserStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getUser()", e);
		}
    }	


	private class AddUserStoredProcedure extends CUDStoredProcedure {
	    public AddUserStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_USERS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_SALUTATION, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_FIRST_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_MIDDLE_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PERSONAL_TITLE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EMAIL, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CURRENT_PASSWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PASSWORD_HINT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REQUIRE_PASSWORD_CHANGE, Types.CHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ACCT_NON_LOCKED, Types.CHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IP, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_THRU_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}
	
    public int addUser(User user) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_SALUTATION, null);
			inputs.put(DAOConstants.PARAM_FIRST_NAME, user.getFullName());
			inputs.put(DAOConstants.PARAM_MIDDLE_NAME, null);
			inputs.put(DAOConstants.PARAM_LAST_NAME, null);
			inputs.put(DAOConstants.PARAM_USER_NAME, user.getUsername());
			inputs.put(DAOConstants.PARAM_PERSONAL_TITLE, null);
			inputs.put(DAOConstants.PARAM_EMAIL, user.getEmail());
			inputs.put(DAOConstants.PARAM_CURRENT_PASSWORD, user.getPassword());
			inputs.put(DAOConstants.PARAM_PASSWORD_HINT, null);
			inputs.put(DAOConstants.PARAM_REQUIRE_PASSWORD_CHANGE, user.isCredentialsNonExpired()==null || user.isCredentialsNonExpired()?'0':1);
			inputs.put(DAOConstants.PARAM_ACCT_NON_LOCKED, user.isAccountNonLocked()==null || user.isAccountNonLocked()?'1':'0');
			inputs.put(DAOConstants.PARAM_IP, null);
			inputs.put(DAOConstants.PARAM_GROUP_ID, user.getGroupId());
			inputs.put(DAOConstants.PARAM_STORE, user.getStoreId());
			inputs.put(DAOConstants.PARAM_THRU_DATE, user.getThruDate()==null?DateAndTimeUtils.addYearToDate(5):user.getThruDate());
			inputs.put(DAOConstants.PARAM_CREATED_BY, UtilityService.getUsername());
			result = DAOUtils.getUpdateCount(addUserStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addUser()", e);
    	}
    	return result;
    }

	private class UpdateUserStoredProcedure extends CUDStoredProcedure {
	    public UpdateUserStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_USERS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EMAIL, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_SALUTATION, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_FIRST_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_MIDDLE_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_FULL_NAME, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_PERSONAL_TITLE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CURRENT_PASSWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PASSWORD_HINT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_HAS_LOGGED_OUT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_REQUIRE_PASSWORD_CHANGE, Types.CHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ACCT_NON_LOCKED, Types.CHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_ACCESS_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SUCCESSIVE_FAILED_LOGINS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IP, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_THRU_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

    public int updateUser(User user) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_USER_NAME, user.getUsername());
			inputs.put(DAOConstants.PARAM_EMAIL, user.getEmail());
			inputs.put(DAOConstants.PARAM_SALUTATION, null);
			inputs.put(DAOConstants.PARAM_FIRST_NAME, user.getFullName());
			inputs.put(DAOConstants.PARAM_MIDDLE_NAME, null);
			inputs.put(DAOConstants.PARAM_LAST_NAME, null);
			inputs.put(DAOConstants.PARAM_FULL_NAME, null);
			inputs.put(DAOConstants.PARAM_PERSONAL_TITLE, null);
			inputs.put(DAOConstants.PARAM_CURRENT_PASSWORD, user.getPassword());
			inputs.put(DAOConstants.PARAM_PASSWORD_HINT, null);
			inputs.put(DAOConstants.PARAM_HAS_LOGGED_OUT, null);
			inputs.put(DAOConstants.PARAM_REQUIRE_PASSWORD_CHANGE, user.isCredentialsNonExpired()==null?null:user.isCredentialsNonExpired()?'0':1);
			inputs.put(DAOConstants.PARAM_ACCT_NON_LOCKED, user.isAccountNonLocked()==null?null:user.isAccountNonLocked()?'1':'0');
			inputs.put(DAOConstants.PARAM_LAST_ACCESS_DATE, user.getLastAccessDate());
			inputs.put(DAOConstants.PARAM_SUCCESSIVE_FAILED_LOGINS, user.getSuccessiveFailedLogin());
			inputs.put(DAOConstants.PARAM_IP, user.getIp());
			inputs.put(DAOConstants.PARAM_GROUP_ID, user.getGroupId());
			inputs.put(DAOConstants.PARAM_THRU_DATE, user.getThruDate());
			String modifiedBy = null;
			try {
				modifiedBy = UtilityService.getUsername();
			} catch (NullPointerException npe) {
				modifiedBy = "SYSTEM";
			}
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, modifiedBy);
			result = DAOUtils.getUpdateCount(updateUserStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateUser()", e);
    	}
    	return result;
    }
	private class DeleteUserStoredProcedure extends CUDStoredProcedure {
	    public DeleteUserStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_USERS);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));

		}
	}
	
    public int deleteUser(String username) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_USER_NAME, username);
		inputs.put(DAOConstants.PARAM_MODIFIED_BY, UtilityService.getUsername());
        return DAOUtils.getUpdateCount(deleteUserStoredProcedure.execute(inputs));
    }	

}
