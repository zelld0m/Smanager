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
import com.search.manager.model.Comment;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;

@Repository(value="commentDAO")
public class CommentDAO {

	// needed by spring AOP
	public CommentDAO(){}
	
	@Autowired
	public CommentDAO(JdbcTemplate jdbcTemplate) {
		addCommentStoredProcedure = new AddCommentStoredProcedure(jdbcTemplate);
		getCommentStoredProcedure = new GetCommentStoredProcedure(jdbcTemplate);
		deleteCommentStoredProcedure = new DeleteCommentStoredProcedure(jdbcTemplate);
		updateCommentStoredProcedure = new UpdateCommentStoredProcedure(jdbcTemplate);
	}

	private GetCommentStoredProcedure getCommentStoredProcedure;
	private AddCommentStoredProcedure addCommentStoredProcedure;
	private DeleteCommentStoredProcedure deleteCommentStoredProcedure;
	private UpdateCommentStoredProcedure updateCommentStoredProcedure;
	
	private class GetCommentStoredProcedure extends GetStoredProcedure {
	    public GetCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_COMMENT);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT_ID, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Comment>() {
	        	public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new Comment(
	                		rs.getInt(DAOConstants.COLUMN_COMMENT_ID), 
	                		rs.getString(DAOConstants.COLUMN_REFERENCE_ID), 
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP),
	                		rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID)
	                		);
	        	}

	        }));
		}
	}

	private class AddCommentStoredProcedure extends CUDStoredProcedure {
	    public AddCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_COMMENT);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}
	
	private class UpdateCommentStoredProcedure extends CUDStoredProcedure {
	    public UpdateCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_COMMENT);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
		}
	}

	private class DeleteCommentStoredProcedure extends CUDStoredProcedure {
	    public DeleteCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_COMMENT);
	    }

		@Override
		protected void declareParameters() {
	        declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID, Types.VARCHAR));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT_ID, Types.INTEGER));
		}
	}
	
    public int deleteComment(Integer commentId) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(DAOConstants.PARAM_COMMENT_ID, commentId);
		inputs.put(DAOConstants.PARAM_REFERENCE_ID, null);
        return DAOUtils.getUpdateCount(deleteCommentStoredProcedure.execute(inputs));
    }	

    public RecordSet<Comment> getComment(SearchCriteria<Comment> searchCriteria) throws DaoException {
		try {
			Comment comment = searchCriteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, comment.getReferenceId());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, comment.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_COMMENT_ID, null);
			inputs.put(DAOConstants.PARAM_START_ROW, searchCriteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, searchCriteria.getEndRow());
			return DAOUtils.getRecordSet(getCommentStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getComment()", e);
		}
    }	

    public int addComment(Comment comment) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, comment.getReferenceId());
			inputs.put(DAOConstants.PARAM_COMMENT, comment.getComment());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID, comment.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_CREATED_BY, comment.getUsername());
			result = DAOUtils.getUpdateCount(addCommentStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addComment()", e);
    	}
    	return result;
    }

    public int updateComment(Comment comment) throws DaoException {
    	int result = -1;
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_REFERENCE_ID, comment.getReferenceId());
			inputs.put(DAOConstants.PARAM_COMMENT, comment.getComment());
			result = DAOUtils.getUpdateCount(updateCommentStoredProcedure.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateComment()", e);
    	}
    	return result;
    }

}
