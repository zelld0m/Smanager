package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.JodaDateTimeFormatAnnotationFormatterFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.jodatime.JodaTimeUtil;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.CampaignBanner;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;

@Repository(value="campaignDAO")
public class CampaignDAO {

	// for AOP use
	public CampaignDAO(){
	}

	private AddCampaignStoredProcedure addSP;
	private UpdateCampaignStoredProcedure updateSP;
	private DeleteCampaignStoredProcedure deleteSP;
	private GetCampaignStoredProcedure getSP;
	private SearchCampaignStoredProcedure searchSP;
	private UpdateCampaignCommentStoredProcedure updateCommentSP;
	private AppendCampaignCommentStoredProcedure appendCommentSP;

	// banner keywords
	private AddBannerToCampaignStoredProcedure addBannerSP;
	private UpdateCampaignBannerStoredProcedure updateBannerSP;
	private DeleteCampaignBannerStoredProcedure deleteBannerSP;
	private GetCampaignBannersStoredProcedure getCampaignBannerSP;
	private SearchCampaignBannersStoredProcedure searchBannerSP;
	private AddKeywordToCampaignBannerStoredProcedure addBannerKeywordSP;
	private DeleteCampaignBannerKeywordStoredProcedure deleteBannerKeywordSP;
	private GetCampaignBannerKeywordsStoredProcedure getBannerKeywordSP;
	private SearchCampaignBannerKeywordsStoredProcedure searchBannerKeywordSP;

	@Autowired
	public CampaignDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddCampaignStoredProcedure(jdbcTemplate);
    	updateSP = new UpdateCampaignStoredProcedure(jdbcTemplate) ;
    	deleteSP = new DeleteCampaignStoredProcedure(jdbcTemplate);
    	getSP = new GetCampaignStoredProcedure(jdbcTemplate);
    	searchSP = new SearchCampaignStoredProcedure(jdbcTemplate);
    	updateCommentSP = new UpdateCampaignCommentStoredProcedure(jdbcTemplate);
    	appendCommentSP = new AppendCampaignCommentStoredProcedure(jdbcTemplate);
    	
    	addBannerSP = new AddBannerToCampaignStoredProcedure(jdbcTemplate);
    	updateBannerSP = new UpdateCampaignBannerStoredProcedure(jdbcTemplate);
    	deleteBannerSP = new DeleteCampaignBannerStoredProcedure(jdbcTemplate);
    	getCampaignBannerSP = new GetCampaignBannersStoredProcedure(jdbcTemplate);
    	searchBannerSP = new SearchCampaignBannersStoredProcedure(jdbcTemplate);
    	addBannerKeywordSP = new AddKeywordToCampaignBannerStoredProcedure(jdbcTemplate);
    	deleteBannerKeywordSP = new DeleteCampaignBannerKeywordStoredProcedure(jdbcTemplate);
    	getBannerKeywordSP = new GetCampaignBannerKeywordsStoredProcedure(jdbcTemplate);
    	searchBannerKeywordSP = new SearchCampaignBannerKeywordsStoredProcedure(jdbcTemplate);
    }

	private class AddCampaignStoredProcedure extends CUDStoredProcedure {
		public AddCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_CAMPAIGN);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	private class GetCampaignStoredProcedure extends GetStoredProcedure {
		public GetCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_CAMPAIGN);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Campaign>() {
				public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new Campaign(
							rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID),
							rs.getString(DAOConstants.COLUMN_NAME),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_START_DATE)),
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_END_DATE)),
							rs.getString(DAOConstants.COLUMN_COMMENT),
							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
				}
			}));
		}
	}

	private class UpdateCampaignStoredProcedure extends CUDStoredProcedure {
		public UpdateCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_CAMPAIGN);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class DeleteCampaignStoredProcedure extends CUDStoredProcedure {
		public DeleteCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_CAMPAIGN);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
		}
	}

	private class UpdateCampaignCommentStoredProcedure extends CUDStoredProcedure {
		public UpdateCampaignCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_CAMPAIGN_COMMENT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class AppendCampaignCommentStoredProcedure extends CUDStoredProcedure {
		public AppendCampaignCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_APPEND_CAMPAIGN_COMMENT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class SearchCampaignStoredProcedure extends GetStoredProcedure {
		public SearchCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_SEARCH_CAMPAIGN);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_CAMPAIGN, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Campaign>() {
				public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new Campaign(
							rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID),
							rs.getString(DAOConstants.COLUMN_NAME),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_START_DATE)),
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_END_DATE)),
							rs.getString(DAOConstants.COLUMN_COMMENT),
							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
				}
			}));
		}
	}

	private class AddBannerToCampaignStoredProcedure extends CUDStoredProcedure {
		private AddBannerToCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_CAMPAIGN_BANNER);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
		}
	}

	public int addCampaignBanner(CampaignBanner campaignBanner) throws DaoException {
		try {
			// TODO: add checking for duplicates
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getRuleId());
			inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getRuleId());
			inputs.put(DAOConstants.PARAM_START_DATE, campaignBanner.getStartDateTime());
			inputs.put(DAOConstants.PARAM_END_DATE, campaignBanner.getEndDateTime());
			inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(campaignBanner.getCreatedBy()));
			return DAOUtils.getUpdateCount(addBannerSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addCampaignBanner()", e);
		}
	}

	private class UpdateCampaignBannerStoredProcedure extends StoredProcedure {
		private UpdateCampaignBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_CAMPAIGN_BANNER);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
			compile();
		}
	}

	public int updateCampaignBanner(CampaignBanner campaignBanner) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getRuleId());
			inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getRuleId());
			inputs.put(DAOConstants.PARAM_START_DATE, campaignBanner.getStartDateTime());
			inputs.put(DAOConstants.PARAM_END_DATE, campaignBanner.getEndDateTime());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(campaignBanner.getLastModifiedBy()));
			return DAOUtils.getUpdateCount(updateBannerSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during updateCampaignBanner()", e);
		}
	}

	private class DeleteCampaignBannerStoredProcedure extends StoredProcedure {
		private DeleteCampaignBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_CAMPAIGN_BANNER);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			compile();
		}
	}

	public int deleteCampaignBanner(CampaignBanner campaignBanner) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getRuleId());
			inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getRuleId());
			return DAOUtils.getUpdateCount(deleteBannerSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during deleteCampaignBanner()", e);
		}
	}

	private class AddKeywordToCampaignBannerStoredProcedure extends StoredProcedure {
		private AddKeywordToCampaignBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_CAMPAIGN_BANNER_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
			compile();
		}
	}

	public int addCampaignBannerKeyword(CampaignBanner campaignBanner) throws DaoException {
		try {
			// TODO: add checking for duplicates
			int i = 0;
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getRuleId());
			inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getRuleId());
			inputs.put(DAOConstants.PARAM_CREATED_BY, campaignBanner.getBanner().getCreatedBy());
			for (Keyword keyword: campaignBanner.getKeywords()) {
				inputs.put(DAOConstants.PARAM_KEYWORD, keyword.getKeywordId());
				i += DAOUtils.getUpdateCount(addBannerKeywordSP.execute(inputs));
			}
			return i;
		}
		catch (Exception e) {
			throw new DaoException("Failed during addCampaignBannerKeyword()", e);
		}
	}

	private class DeleteCampaignBannerKeywordStoredProcedure extends StoredProcedure {
		private DeleteCampaignBannerKeywordStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_CAMPAIGN_BANNER_KEYWORD);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD, Types.VARCHAR));
			compile();
		}
	}

	public int deleteCampaignBannerKeyword(CampaignBanner campaignBanner) throws DaoException {
		try {
			// TODO: add checking for duplicates
			int i = 0;
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getRuleId());
			inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getRuleId());
			for (Keyword keyword: campaignBanner.getKeywords()) {
				inputs.put(DAOConstants.PARAM_KEYWORD, keyword.getKeywordId());
				i += DAOUtils.getUpdateCount(deleteBannerKeywordSP.execute(inputs));
			}
			return i;
		}
		catch (Exception e) {
			throw new DaoException("Failed during deleteCampaignBannerKeyword()", e);
		}
	}

	private class GetCampaignBannersStoredProcedure extends StoredProcedure {
		private GetCampaignBannersStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_CAMPAIGN_BANNER);
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<CampaignBanner>() {
				public CampaignBanner mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					Campaign campaign = new Campaign(rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID),
							rs.getString(DAOConstants.COLUMN_CAMPAIGN_NAME),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)));
					Banner banner = new Banner(rs.getString(DAOConstants.COLUMN_BANNER_ID),
							rs.getString(DAOConstants.COLUMN_BANNER_NAME),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
							rs.getString(DAOConstants.COLUMN_IMAGE_URL),
							rs.getString(DAOConstants.COLUMN_LINK_URL));
					return new CampaignBanner(campaign, banner, (List<Keyword>)null,
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_START_DATE)),
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_END_DATE)),
							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
				}
			}));
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
				}
			}));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			compile();
		}
	}

	public RecordSet<CampaignBanner> getCampaignBanners(SearchCriteria<Campaign> criteria) throws DaoException {
		try {
			Campaign model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStore().getStoreId().toLowerCase().trim());
			inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
			inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			return DAOUtils.getRecordSet(getCampaignBannerSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getCampaign()", e);
		}
	}

	private class SearchCampaignBannersStoredProcedure extends StoredProcedure {
		private SearchCampaignBannersStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_SEARCH_CAMPAIGN_BANNER);
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<CampaignBanner>() {
				public CampaignBanner mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					Campaign campaign = new Campaign(rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID),
							rs.getString(DAOConstants.COLUMN_CAMPAIGN_NAME),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)));
					Banner banner = new Banner(rs.getString(DAOConstants.COLUMN_BANNER_ID),
							rs.getString(DAOConstants.COLUMN_BANNER_NAME),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
							rs.getString(DAOConstants.COLUMN_IMAGE_URL),
							rs.getString(DAOConstants.COLUMN_LINK_URL));
					return new CampaignBanner(campaign, banner, (List<Keyword>)null,
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_START_DATE)),
							JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_END_DATE)),
							rs.getString(DAOConstants.COLUMN_CREATED_BY),
							rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)),
							JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE)));
				}
			}));
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
				}
			}));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_CAMPAIGN, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_BANNER, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			compile();
		}
	}

	public RecordSet<CampaignBanner> searchCampaignBanners(SearchCriteria<CampaignBanner> criteria, MatchType campaignMatchType, MatchType bannerMatchType) throws DaoException {
		try {
			CampaignBanner model = criteria.getModel();
			Campaign campaign = model.getCampaign();
			Banner banner = model.getBanner();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(campaign.getStore()));
			inputs.put(DAOConstants.PARAM_CAMPAIGN, campaign.getRuleId());
			inputs.put(DAOConstants.PARAM_CAMPAIGN, campaignMatchType.equals(MatchType.MATCH_ID) ? campaign.getRuleId() : campaign.getRuleName());
			inputs.put(DAOConstants.PARAM_MATCH_TYPE_CAMPAIGN, campaignMatchType.getIntValue());
			inputs.put(DAOConstants.PARAM_BANNER, bannerMatchType.equals(MatchType.MATCH_ID) ? banner.getRuleId() : banner.getRuleName());
			inputs.put(DAOConstants.PARAM_MATCH_TYPE_BANNER, bannerMatchType.getIntValue());
			inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
			inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			return DAOUtils.getRecordSet(searchBannerSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getCampaign()", e);
    	}
	}


	private class GetCampaignBannerKeywordsStoredProcedure extends StoredProcedure {
		private GetCampaignBannerKeywordsStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_CAMPAIGN_BANNER_KEYWORD);
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Map<String, Object>>() {
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(DAOConstants.COLUMN_CAMPAIGN_ID, rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID));
					map.put(DAOConstants.COLUMN_CAMPAIGN_NAME, rs.getString(DAOConstants.COLUMN_CAMPAIGN_NAME));
					map.put(DAOConstants.COLUMN_BANNER_ID, rs.getString(DAOConstants.COLUMN_BANNER_ID));
					map.put(DAOConstants.COLUMN_BANNER_NAME, rs.getString(DAOConstants.COLUMN_BANNER_NAME));
					map.put(DAOConstants.COLUMN_CREATED_BY, rs.getString(DAOConstants.COLUMN_CREATED_BY));
					map.put(DAOConstants.COLUMN_CREATED_DATE, JodaTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE)));
					return map;
				}
			}));
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
				}
			}));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			compile();
		}
	}

	private class SearchCampaignBannerKeywordsStoredProcedure extends StoredProcedure {
		private SearchCampaignBannerKeywordsStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_CAMPAIGN_BANNER_WITH_KEYWORD);
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Map<String, Object>>() {
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(DAOConstants.COLUMN_CAMPAIGN_ID, rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID));
					map.put(DAOConstants.COLUMN_STORE_ID, rs.getString(DAOConstants.COLUMN_STORE_ID));
					map.put(DAOConstants.COLUMN_CAMPAIGN_NAME, rs.getString(DAOConstants.COLUMN_CAMPAIGN_NAME));
					map.put(DAOConstants.COLUMN_BANNER_ID, rs.getString(DAOConstants.COLUMN_BANNER_ID));
					map.put(DAOConstants.COLUMN_BANNER_NAME, rs.getString(DAOConstants.COLUMN_BANNER_NAME));
					map.put(DAOConstants.COLUMN_START_DATE, JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_START_DATE)));
					map.put(DAOConstants.COLUMN_END_DATE, JodaTimeUtil.toDateTime(rs.getDate(DAOConstants.COLUMN_END_DATE)));
					//	            	map.put(DAOConstants.COLUMN_CREATED_BY, rs.getString(DAOConstants.COLUMN_CREATED_BY));
					//	            	map.put(DAOConstants.COLUMN_CREATED_DATE, rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE));
					return map;
				}
			}));
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
				}
			}));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
			compile();
		}
	}

	public int addCampaign(Campaign campaign) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			String campaignId = campaign.getRuleId();
			if (StringUtils.isEmpty(campaignId)) {
				campaignId = DAOUtils.generateUniqueId();
			}
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, DAOUtils.generateUniqueId());
			inputs.put(DAOConstants.PARAM_CAMPAIGN_NAME, StringUtils.trimToEmpty(campaign.getRuleName()));
			inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(campaign.getStore()));
			inputs.put(DAOConstants.PARAM_START_DATE, campaign.getStartDateTime());
			inputs.put(DAOConstants.PARAM_END_DATE, campaign.getEndDateTime());
			inputs.put(DAOConstants.PARAM_COMMENT, campaign.getComment());
			inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(campaign.getCreatedBy()));
			return DAOUtils.getUpdateCount(addSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addCampaign()", e);
		}
	}

	public RecordSet<Campaign> getCampaigns(SearchCriteria<Campaign> criteria) throws DaoException {
		try {
			Campaign model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, model.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStore().getStoreId().toLowerCase().trim());
			inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
			inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			return DAOUtils.getRecordSet(getSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getCampaign()", e);
		}
	}

	public Campaign getCampaign(Campaign campaign) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getRuleId());
			inputs.put(DAOConstants.PARAM_STORE_ID, campaign.getStore().getStoreId().toLowerCase().trim());
			inputs.put(DAOConstants.PARAM_START_DATE, null);
			inputs.put(DAOConstants.PARAM_END_DATE, null);
			inputs.put(DAOConstants.PARAM_START_ROW, 0);
			inputs.put(DAOConstants.PARAM_END_ROW, 0);
			return DAOUtils.getItem(getSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getCampaign()", e);
		}
	}

	public int updateCampaign(Campaign campaign) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getRuleId());
			inputs.put(DAOConstants.PARAM_CAMPAIGN_NAME, StringUtils.trimToEmpty(campaign.getRuleName()));
			inputs.put(DAOConstants.PARAM_START_DATE, campaign.getStartDateTime());
			inputs.put(DAOConstants.PARAM_END_DATE, campaign.getEndDateTime());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(campaign.getLastModifiedBy()));
			return DAOUtils.getUpdateCount(updateSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during updateCampaign()", e);
		}
	}

	public int updateCampaignComment(Campaign campaign) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getRuleId());
			inputs.put(DAOConstants.PARAM_COMMENT, campaign.getComment());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, campaign.getLastModifiedBy());
			return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateCampaignComment()", e);
		}
	}

	public int appendCampaignComment(Campaign campaign) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getRuleId());
			inputs.put(DAOConstants.PARAM_COMMENT, campaign.getComment());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, campaign.getLastModifiedBy());
			return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during appendCampaignComment()", e);
		}
	}

	public int deleteCampaign(Campaign campaign) throws DaoException {
		try {
			if (campaign != null && campaign.getRuleId() != null) {
				Map<String, Object> inputs = new HashMap<String, Object>();
				inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getRuleId());
				return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
			}
			return -1;
		} catch (Exception e) {
			throw new DaoException("Failed during deleteCampaign()", e);
		}
	}

	public RecordSet<Campaign> searchCampaign(SearchCriteria<Campaign> criteria, MatchType campaignMatchType) throws DaoException {
		try {
			Campaign model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStore().getStoreId().toLowerCase().trim());
			inputs.put(DAOConstants.PARAM_CAMPAIGN, campaignMatchType.equals(MatchType.MATCH_ID) ? model.getRuleId() : model.getRuleName());
			inputs.put(DAOConstants.PARAM_MATCH_TYPE_CAMPAIGN, campaignMatchType.getIntValue());
			inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
			inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			return DAOUtils.getRecordSet(searchSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during searchCampaign()", e);
		}
	}

	public String addCampaignAndGetId(Campaign campaign) throws DaoException {
		String ruleId = campaign.getRuleId();
    	if (StringUtils.isEmpty(ruleId)) {
    		ruleId = DAOUtils.generateUniqueId();
    	}
		
    	campaign.setRuleId(ruleId);
		return (addCampaign(campaign) > 0) ?  ruleId : null;
	}

}
