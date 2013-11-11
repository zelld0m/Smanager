package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.BannerRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Filter.MatchType;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;

@Repository("bannerRuleDaoSp")
public class BannerRuleDaoSpImpl extends GenericDaoSpImpl<BannerRule> implements
		BannerRuleDao {

	private AddStoredProcedure addSp;
	@SuppressWarnings("unused")
	private UpdateStoredProcedure updateSp;
	private DeleteStoredProcedure deleteSp;
	private SearchStoredProcedure searchSp;

	@SuppressWarnings("unused")
	private BannerRuleDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public BannerRuleDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		deleteSp = new DeleteStoredProcedure(jdbcTemplate);
		searchSp = new SearchStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends CUDStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY,
					Types.VARCHAR));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		// TODO create update sp
		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, "");
		}

		@Override
		protected void declareParameters() {
			// TODO Auto-generated method stub

		}

	}

	private class DeleteStoredProcedure extends CUDStoredProcedure {

		public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_BANNER_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME,
					Types.VARCHAR));
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_BANNER_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TEXT,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<BannerRule>() {
						public BannerRule mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return new BannerRule(
									rs.getString(DAOConstants.COLUMN_STORE_ID),
									rs.getString(DAOConstants.COLUMN_RULE_ID),
									rs.getString(DAOConstants.COLUMN_RULE_NAME),
									rs.getString(DAOConstants.COLUMN_CREATED_BY),
									rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
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
		throw new CoreDaoException("Unimplemented method.");
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
	protected Map<String, Object> generateAddInput(BannerRule model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();
			String ruleId = model.getRuleId();

			if (StringUtils.isBlank(ruleId)) {
				model.setRuleId(DAOUtils.generateUniqueId());
			}

			inputs.put(DAOConstants.MODEL_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(BannerRule model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.MODEL_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(BannerRule model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();
		inParam.put(DAOConstants.PARAM_RULE_ID, null);
		inParam.put(DAOConstants.PARAM_STORE_ID, null);
		inParam.put(DAOConstants.PARAM_SEARCH_TEXT, null);
		inParam.put(DAOConstants.PARAM_MATCH_TYPE, null);
		inParam.put(DAOConstants.PARAM_IMAGE_PATH_ID, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);

		return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId) {
		Search search = new Search(BannerRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, id));
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE,
				MatchType.MATCH_ID.getIntValue()));
		return search;
	}

	// Add BannerRuleDao specific method here...

}
