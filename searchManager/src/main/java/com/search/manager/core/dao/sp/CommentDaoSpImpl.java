package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.CommentDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.Comment;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;

@Repository("commentDaoSp")
public class CommentDaoSpImpl extends GenericDaoSpImpl<Comment> implements
		CommentDao {

	private AddStoredProcedure addSp;
	private UpdateStoredProcedure updateSp;
	private DeleteStoredProcedure deleteSp;
	private SearchStoredProcedure searchSp;

	@SuppressWarnings("unused")
	private CommentDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public CommentDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		updateSp = new UpdateStoredProcedure(jdbcTemplate);
		deleteSp = new DeleteStoredProcedure(jdbcTemplate);
		searchSp = new SearchStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends CUDStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_COMMENT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_COMMENT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT,
					Types.VARCHAR));
		}
	}

	private class DeleteStoredProcedure extends CUDStoredProcedure {

		public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_COMMENT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT_ID,
					Types.INTEGER));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_COMMENT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_REFERENCE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<Comment>() {
						public Comment mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return new Comment(
									rs.getInt(DAOConstants.COLUMN_COMMENT_ID),
									rs.getString(DAOConstants.COLUMN_REFERENCE_ID),
									rs.getString(DAOConstants.COLUMN_COMMENT),
									rs.getString(DAOConstants.COLUMN_CREATED_BY),
									JodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)),
									rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID));
						}

					}));
		}
	}

	@Override
	protected StoredProcedure getAddStoredProcedure() throws CoreDaoException {
		return addSp;
	}

	@Override
	protected StoredProcedure getUpdateStoredProcedure()
			throws CoreDaoException {
		return updateSp;
	}

	@Override
	protected StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException {
		return deleteSp;
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchSp;
	}

	@Override
	protected Map<String, Object> generateAddInput(Comment model)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(Comment model)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(Comment model)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Search generateSearchInput(Comment model) throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

}
