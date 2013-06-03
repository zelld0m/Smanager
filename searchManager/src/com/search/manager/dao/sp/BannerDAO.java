package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.ImagePath;
import com.search.manager.model.ImagePathType;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="bannerDAO")
public class BannerDAO {

	public BannerDAO(){
		// for AOP use	
	}

	private AddRuleStoredProcedure addRuleSP;
	private DeleteRuleStoredProcedure deleteRuleSP;
	private GetRuleStoredProcedure getRuleSP;
	private AddRuleItemStoredProcedure addRuleItemSP;
	private UpdateRuleItemStoredProcedure updateRuleItemSP;
	private DeleteRuleItemStoredProcedure deleteRuleItemSP;
	private GetRuleItemStoredProcedure getRuleItemSP;
	private AddRuleItemImagePathStoredProcedure addRuleItemImagePathSP;
	private UpdateRuleItemImagePathStoredProcedure updateRuleItemImagePathSP;
	private GetRuleItemImagePathStoredProcedure getRuleItemImagePathSP;

	@Autowired
	public BannerDAO(JdbcTemplate jdbcTemplate) {
		addRuleSP = new AddRuleStoredProcedure(jdbcTemplate);
		deleteRuleSP = new DeleteRuleStoredProcedure(jdbcTemplate);
		getRuleSP =  new GetRuleStoredProcedure(jdbcTemplate);
		addRuleItemSP =  new AddRuleItemStoredProcedure(jdbcTemplate);
		updateRuleItemSP =  new UpdateRuleItemStoredProcedure(jdbcTemplate);
		deleteRuleItemSP =  new DeleteRuleItemStoredProcedure(jdbcTemplate);
		getRuleItemSP =  new GetRuleItemStoredProcedure(jdbcTemplate);
		addRuleItemImagePathSP =  new AddRuleItemImagePathStoredProcedure(jdbcTemplate);
		updateRuleItemImagePathSP =  new UpdateRuleItemImagePathStoredProcedure(jdbcTemplate);
		getRuleItemImagePathSP = new GetRuleItemImagePathStoredProcedure(jdbcTemplate);
	}

	private class AddRuleStoredProcedure extends CUDStoredProcedure {
		public AddRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class DeleteRuleStoredProcedure extends CUDStoredProcedure {
		public DeleteRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_BANNER_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
		}
	}

	private class GetRuleStoredProcedure extends GetStoredProcedure {
		public GetRuleStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_BANNER_RULE);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SEARCH_TEXT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<BannerRule>() {
				public BannerRule mapRow(ResultSet rs, int rowNum) throws SQLException{
					return new BannerRule(
							rs.getString(DAOConstants.COLUMN_STORE_ID),
							rs.getString(DAOConstants.COLUMN_RULE_ID),
							rs.getString(DAOConstants.COLUMN_RULE_NAME),
							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY)
					);
				}
			}));			
		}
	}	

	private class AddRuleItemStoredProcedure extends CUDStoredProcedure {
		public AddRuleItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_ALT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_PATH, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_NEW_WINDOW, Types.BOOLEAN));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class UpdateRuleItemStoredProcedure extends CUDStoredProcedure {
		public UpdateRuleItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_ALT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_PATH, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_NEW_WINDOW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_UPDATED_BY, Types.VARCHAR));		
		}
	}

	private class DeleteRuleItemStoredProcedure extends CUDStoredProcedure {
		public DeleteRuleItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
		}
	}

	private class GetRuleItemStoredProcedure extends GetStoredProcedure {
		public GetRuleItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_BANNER_RULE_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
			declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<BannerRuleItem>() {
				public BannerRuleItem mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new BannerRuleItem(
							new BannerRule(
									rs.getString(DAOConstants.COLUMN_STORE_ID),
									rs.getString(DAOConstants.COLUMN_RULE_ID),
									rs.getString(DAOConstants.COLUMN_RULE_NAME),
									null
							),
							rs.getString(DAOConstants.COLUMN_MEMBER_ID),
							rs.getInt(DAOConstants.COLUMN_PRIORITY),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_START_DATE)),
							JodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_END_DATE)),
							rs.getString(DAOConstants.COLUMN_IMAGE_ALT),
							rs.getString(DAOConstants.COLUMN_LINK_PATH),
							rs.getString(DAOConstants.COLUMN_DESCRIPTION),
							new ImagePath(
									rs.getString(DAOConstants.COLUMN_STORE_ID),
									rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ID),
									rs.getString(DAOConstants.COLUMN_IMAGE_PATH),
									ImagePathType.get(rs.getString(DAOConstants.COLUMN_IMAGE_PATH_TYPE)),
									rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ALIAS)
							),
							BooleanUtils.toBoolean(rs.getInt(DAOConstants.COLUMN_DISABLED)),
							BooleanUtils.toBoolean(rs.getInt(DAOConstants.COLUMN_OPEN_NEW_WINDOW))
					);
				}
			}));			
		}
	}	

	private class AddRuleItemImagePathStoredProcedure extends CUDStoredProcedure {
		public AddRuleItemImagePathStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_IMAGE_PATH);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ALIAS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class UpdateRuleItemImagePathStoredProcedure extends CUDStoredProcedure {
		public UpdateRuleItemImagePathStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER_IMAGE_PATH);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ALIAS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_UPDATED_BY, Types.VARCHAR));
		}
	}

	private class GetRuleItemImagePathStoredProcedure extends GetStoredProcedure {
		public GetRuleItemImagePathStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_BANNER_IMAGE_PATH);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ALIAS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ImagePath>() {
				public ImagePath mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new ImagePath(
							rs.getString(DAOConstants.COLUMN_STORE_ID),
							rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ID),
							rs.getString(DAOConstants.COLUMN_IMAGE_PATH),
							ImagePathType.get(rs.getString(DAOConstants.COLUMN_IMAGE_PATH_TYPE)),
							rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ALIAS),
							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY)
					);
				}
			}));			
		}
	}	
	
	@Audit(entity = Entity.banner, operation = Operation.add)
	public int addRule(BannerRule rule) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			String ruleId = rule.getRuleId();
			
			if (StringUtils.isBlank(ruleId)) {
				rule.setRuleId(DAOUtils.generateUniqueId());
			}
			
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
			inputs.put(DAOConstants.PARAM_CREATED_BY, rule.getCreatedBy());
			return DAOUtils.getUpdateCount(addRuleSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addRule()", e);
		}
	}
	
	@Audit(entity = Entity.banner, operation = Operation.delete)
	public int deleteRule(BannerRule rule) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
			
			return DAOUtils.getUpdateCount(deleteRuleSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during deleteRule()", e);
		}
	}
	
	public BannerRule getRuleById(String storeId, String ruleId) throws DaoException {
        try {
            BannerRule rule = null;
            Map<String, Object> inputs = new HashMap<String, Object>();

            inputs.put(DAOConstants.PARAM_RULE_ID, ruleId);
            inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
            inputs.put(DAOConstants.PARAM_MATCH_TYPE, MatchType.MATCH_ID.getIntValue());
            inputs.put(DAOConstants.PARAM_SEARCH_TEXT, null);
            inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, null);
            inputs.put(DAOConstants.PARAM_START_ROW, 1);
            inputs.put(DAOConstants.PARAM_END_ROW, 1);

            RecordSet<BannerRule> rules = DAOUtils.getRecordSet(getRuleSP.execute(inputs));

            if (rules != null && rules.getList().size() > 0) {
                rule = rules.getList().get(0);
            }

            return rule;
        } catch (Exception e) {
            throw new DaoException("Failed during searchRule()", e);
        }
	}

	public RecordSet<BannerRule> searchRule(SearchCriteria<BannerRule> criteria) throws DaoException {
		return searchRule(criteria, null, null);
	}
	
	public RecordSet<BannerRule> searchRule(SearchCriteria<BannerRule> criteria, String imagePathId) throws DaoException {
		return searchRule(criteria, imagePathId, null);
	}
	
	public RecordSet<BannerRule> searchRule(SearchCriteria<BannerRule> criteria, String imagePathId, MatchType matchType) throws DaoException {
		try {
			BannerRule model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_SEARCH_TEXT, model.getRuleName());
			inputs.put(DAOConstants.PARAM_MATCH_TYPE, matchType!=null? matchType.getIntValue(): matchType);
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePathId);
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());

			return DAOUtils.getRecordSet(getRuleSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during searchRule()", e);
		}
	}
	
	@Audit(entity = Entity.banner, operation = Operation.addBanner)
	public int addRuleItem(BannerRuleItem ruleItem) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			BannerRule rule = ruleItem.getRule();
			String memberId = ruleItem.getMemberId();
			
			if (StringUtils.isBlank(memberId)) {
				ruleItem.setMemberId(DAOUtils.generateUniqueId());
			}
			
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, ruleItem.getMemberId());
			inputs.put(DAOConstants.PARAM_PRIORITY, ruleItem.getPriority());
			inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(ruleItem.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(ruleItem.getEndDate()));
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, ruleItem.getImagePath().getId());
			inputs.put(DAOConstants.PARAM_IMAGE_ALT, ruleItem.getImageAlt());
			inputs.put(DAOConstants.PARAM_LINK_PATH, ruleItem.getLinkPath());
			inputs.put(DAOConstants.PARAM_NEW_WINDOW, ruleItem.getOpenNewWindow());
			inputs.put(DAOConstants.PARAM_DESCRIPTION, ruleItem.getDescription());
			inputs.put(DAOConstants.PARAM_CREATED_BY, ruleItem.getCreatedBy());
			
			return DAOUtils.getUpdateCount(addRuleItemSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addRuleItem()", e);
		}
	}
	
	@Audit(entity = Entity.banner, operation = Operation.updateBanner)
	public int updateRuleItem(BannerRuleItem ruleItem) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			BannerRule rule = ruleItem.getRule();
			String memberId = ruleItem.getMemberId();
			
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, memberId);
			inputs.put(DAOConstants.PARAM_PRIORITY, ruleItem.getPriority());
			inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(ruleItem.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(ruleItem.getEndDate()));
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, ruleItem.getImagePath().getId());
			inputs.put(DAOConstants.PARAM_IMAGE_ALT, ruleItem.getImageAlt());
			inputs.put(DAOConstants.PARAM_LINK_PATH, ruleItem.getLinkPath());
			inputs.put(DAOConstants.PARAM_NEW_WINDOW, BooleanUtils.toIntegerObject(ruleItem.getOpenNewWindow()));
			inputs.put(DAOConstants.PARAM_DESCRIPTION, ruleItem.getDescription());
			inputs.put(DAOConstants.PARAM_DISABLED, BooleanUtils.toIntegerObject(ruleItem.getDisabled()));
			inputs.put(DAOConstants.PARAM_LAST_UPDATED_BY, ruleItem.getLastModifiedBy());
			
			return DAOUtils.getUpdateCount(updateRuleItemSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during updateRuleItem()", e);
		}
	}
	
	@Audit(entity = Entity.banner, operation = Operation.deleteBanner)
	public int deleteRuleItem(BannerRuleItem ruleItem) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			BannerRule rule = ruleItem.getRule();
			ImagePath imagePath = ruleItem.getImagePath();
			
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, ruleItem.getMemberId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePath!=null? imagePath.getId() : imagePath);
			
			return DAOUtils.getUpdateCount(deleteRuleItemSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during deleteRuleItem()", e);
		}
	}
	
	public RecordSet<BannerRuleItem> searchRuleItem(SearchCriteria<BannerRuleItem> criteria) throws DaoException {
		return searchRuleItem(criteria, null);
	}
	
	public RecordSet<BannerRuleItem> searchRuleItem(SearchCriteria<BannerRuleItem> criteria, Boolean disabledOnly) throws DaoException {
		try {
			BannerRuleItem model = criteria.getModel();
			BannerRule rule = model.getRule();
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(DAOConstants.PARAM_RULE_ID, rule.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, rule.getRuleName());
			inputs.put(DAOConstants.PARAM_STORE_ID, rule.getStoreId());
			inputs.put(DAOConstants.PARAM_MEMBER_ID, model.getMemberId());
			inputs.put(DAOConstants.PARAM_START_DATE, JodaDateTimeUtil.toSqlDate(criteria.getStartDate()));
			inputs.put(DAOConstants.PARAM_END_DATE, JodaDateTimeUtil.toSqlDate(criteria.getEndDate()));
			inputs.put(DAOConstants.PARAM_DISABLED, disabledOnly);
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			
			return DAOUtils.getRecordSet(getRuleItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during searchRuleItem()", e);
		}
	}
	
	public RecordSet<ImagePath> searchImagePath(SearchCriteria<ImagePath> criteria) throws DaoException {
		try {
			ImagePath model = criteria.getModel();
			
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, model.getId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH, model.getPath());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_TYPE, model.getPathType());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ALIAS, model.getAlias());
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getStartRow());
			
			return DAOUtils.getRecordSet(getRuleItemImagePathSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during searchRuleItemImagePath()", e);
		}
	}
	
	@Audit(entity = Entity.banner, operation = Operation.addImagePath)
	public int addImagePath(ImagePath imagePath) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			String imagePathId = imagePath.getId();
			
			if (StringUtils.isBlank(imagePathId)) {
				imagePathId = DAOUtils.generateUniqueId();
			}
			
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePathId);
			inputs.put(DAOConstants.PARAM_STORE_ID, imagePath.getStoreId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH, imagePath.getPath());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_TYPE, imagePath.getPathType());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ALIAS, imagePath.getAlias());
			inputs.put(DAOConstants.PARAM_CREATED_BY, imagePath.getCreatedBy());
			
			return DAOUtils.getUpdateCount(addRuleItemImagePathSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addRuleItemImagePath()", e);
		}
	}
	
	@Audit(entity = Entity.banner, operation = Operation.updateImagePath)
	public int updateImagePath(ImagePath imagePath) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePath.getId());
			inputs.put(DAOConstants.PARAM_STORE_ID, imagePath.getStoreId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ALIAS, imagePath.getAlias());
			inputs.put(DAOConstants.PARAM_LAST_UPDATED_BY, imagePath.getLastModifiedBy());
			
			return DAOUtils.getUpdateCount(updateRuleItemImagePathSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during updateRuleItemImagePath()", e);
		}
	}
}
