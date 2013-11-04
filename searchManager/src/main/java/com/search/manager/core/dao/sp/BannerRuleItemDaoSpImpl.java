package com.search.manager.core.dao.sp;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.jodatime.JodaDateTimeUtil;

@Repository("bannerRuleItemDaoSp")
public class BannerRuleItemDaoSpImpl extends GenericDaoSpImpl<BannerRuleItem>
		implements BannerRuleItemDao {

	private AddStoredProcedure addSp;
	private UpdateStoredProcedure updateSp;
	private DeleteStoredProcedure deleteSp;

	@SuppressWarnings("unused")
	private BannerRuleItemDaoSpImpl() {
		// do nothing...
	}

	public BannerRuleItemDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		updateSp = new UpdateStoredProcedure(jdbcTemplate);
		deleteSp = new DeleteStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends CUDStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_ALT,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_PATH,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_NEW_WINDOW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY,
					Types.VARCHAR));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE,
					Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_ALT,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_PATH,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_NEW_WINDOW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_LAST_UPDATED_BY, Types.VARCHAR));
		}
	}

	private class DeleteStoredProcedure extends CUDStoredProcedure {

		public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE,
					Types.VARCHAR));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> generateAddInput(BannerRuleItem model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();
			BannerRule bannerRule = model.getRule();
			ImagePath imagePath = model.getImagePath();
			String memberId = model.getMemberId();

			if (StringUtils.isBlank(memberId)) {
				model.setMemberId(DAOUtils.generateUniqueId());
			}

			inputs.put(DAOConstants.PARAM_STORE_ID, bannerRule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, bannerRule.getRuleId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, model.getMemberId());
			inputs.put(DAOConstants.PARAM_PRIORITY, model.getPriority());
			inputs.put(DAOConstants.PARAM_START_DATE,
					JodaDateTimeUtil.toSqlDate(model.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE,
					JodaDateTimeUtil.toSqlDate(model.getEndDate()));
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePath.getId());
			inputs.put(DAOConstants.PARAM_IMAGE_ALT, model.getImageAlt());
			inputs.put(DAOConstants.PARAM_LINK_PATH, model.getLinkPath());
			inputs.put(DAOConstants.PARAM_NEW_WINDOW,
					BooleanUtils.toIntegerObject(model.getOpenNewWindow()));
			inputs.put(DAOConstants.PARAM_DESCRIPTION, model.getDescription());
			inputs.put(DAOConstants.PARAM_DISABLED,
					BooleanUtils.toIntegerObject(model.getDisabled()));
			inputs.put(DAOConstants.PARAM_IMAGE_SIZE, imagePath.getSize());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(BannerRuleItem model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();

			BannerRule bannerRule = model.getRule();
			ImagePath imagePath = model.getImagePath();
			String memberId = model.getMemberId();

			inputs.put(DAOConstants.PARAM_STORE_ID, bannerRule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, bannerRule.getRuleId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, memberId);
			inputs.put(DAOConstants.PARAM_PRIORITY, model.getPriority());
			inputs.put(DAOConstants.PARAM_START_DATE,
					JodaDateTimeUtil.toSqlDate(model.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE,
					JodaDateTimeUtil.toSqlDate(model.getEndDate()));
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID,
					imagePath != null ? imagePath.getId() : null);
			inputs.put(DAOConstants.PARAM_IMAGE_ALT, model.getImageAlt());
			inputs.put(DAOConstants.PARAM_LINK_PATH, model.getLinkPath());
			inputs.put(DAOConstants.PARAM_NEW_WINDOW,
					BooleanUtils.toIntegerObject(model.getOpenNewWindow()));
			inputs.put(DAOConstants.PARAM_DESCRIPTION, model.getDescription());
			inputs.put(DAOConstants.PARAM_DISABLED,
					BooleanUtils.toIntegerObject(model.getDisabled()));
			inputs.put(DAOConstants.PARAM_IMAGE_SIZE,
					imagePath != null ? imagePath.getSize() : null);
			inputs.put(DAOConstants.PARAM_LAST_UPDATED_BY,
					model.getLastModifiedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(BannerRuleItem model)
			throws CoreDaoException {
		Map<String, Object> inputs = new HashMap<String, Object>();

		if (model != null) {
			inputs = new HashMap<String, Object>();
			BannerRule bannerRule = model.getRule();
			ImagePath imagePath = model.getImagePath();

			inputs.put(DAOConstants.PARAM_STORE_ID, bannerRule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, bannerRule.getRuleId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, model.getMemberId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID,
					imagePath != null ? imagePath.getId() : null);
			inputs.put(DAOConstants.PARAM_IMAGE_SIZE,
					imagePath != null ? imagePath.getSize() : null);
		}

		return inputs;
	}

	@Override
	protected Search generateSearchById(String id) {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter(DAOConstants.PARAM_MEMBER_ID, id));
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

}
