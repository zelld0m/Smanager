package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.CampaignBanner;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.SearchCriteria.MatchType;

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
	private GetCampaignBannersStoredProcedure getBannerSP;
	private SearchCampaignBannersStoredProcedure searchBannerSP;
	private AddKeywordToCampaignBannerStoredProcedure addBannerKeywordSP;
	private DeleteCampaignBannerKeywordStoredProcedure deleteBannerKeywordSP;
	private GetCampaignBannerKeywordsStoredProcedure getBannerKeywordSP;
	private SearchCampaignBannerKeywordsStoredProcedure searchBannerKeywordSP;
	
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
    	getBannerSP = new GetCampaignBannersStoredProcedure(jdbcTemplate);
    	searchBannerSP = new SearchCampaignBannersStoredProcedure(jdbcTemplate);
    	addBannerKeywordSP = new AddKeywordToCampaignBannerStoredProcedure(jdbcTemplate);
    	deleteBannerKeywordSP = new DeleteCampaignBannerKeywordStoredProcedure(jdbcTemplate);
    	getBannerKeywordSP = new GetCampaignBannerKeywordsStoredProcedure(jdbcTemplate);
    	searchBannerKeywordSP = new SearchCampaignBannerKeywordsStoredProcedure(jdbcTemplate);
    }

	private class AddCampaignStoredProcedure extends StoredProcedure {
	    public AddCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_CAMPAIGN);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class GetCampaignStoredProcedure extends StoredProcedure {
	    public GetCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_CAMPAIGN);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Campaign>() {
	            public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Campaign(
	                		rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID),
	                		rs.getString(DAOConstants.COLUMN_NAME),
	                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
	                		rs.getDate(DAOConstants.COLUMN_START_DATE),
	                		rs.getDate(DAOConstants.COLUMN_END_DATE),
	                		rs.getString(DAOConstants.COLUMN_COMMENT),
	                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
	                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
                			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}
	
	private class UpdateCampaignStoredProcedure extends StoredProcedure {
	    public UpdateCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_CAMPAIGN);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class DeleteCampaignStoredProcedure extends StoredProcedure {
	    public DeleteCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_CAMPAIGN);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class UpdateCampaignCommentStoredProcedure extends StoredProcedure {
	    public UpdateCampaignCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_CAMPAIGN_COMMENT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class AppendCampaignCommentStoredProcedure extends StoredProcedure {
	    public AppendCampaignCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_CAMPAIGN_COMMENT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}

	private class SearchCampaignStoredProcedure extends StoredProcedure {
	    public SearchCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_SEARCH_CAMPAIGN);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Campaign>() {
	            public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Campaign(
                		rs.getString(DAOConstants.COLUMN_CAMPAIGN_ID),
                		rs.getString(DAOConstants.COLUMN_NAME),
                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
                		rs.getDate(DAOConstants.COLUMN_START_DATE),
                		rs.getDate(DAOConstants.COLUMN_END_DATE),
                		rs.getString(DAOConstants.COLUMN_COMMENT),
                		rs.getString(DAOConstants.COLUMN_CREATED_BY),
                		rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
                		rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
            			rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
	            }
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<Integer>() {
	        	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return rs.getInt(DAOConstants.COLUMN_TOTAL_NUMBER);
	        	}
	        }));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_CAMPAIGN, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}
	
	private class AddBannerToCampaignStoredProcedure extends StoredProcedure {
	    private AddBannerToCampaignStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_CAMPAIGN_BANNER);
			declareParameter(new SqlParameter(DAOConstants.PARAM_CAMPAIGN_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}

	public int addCampaignBanner(CampaignBanner campaignBanner) throws DaoException {
    	try {
    		// TODO: add checking for duplicates
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getCampaignId());
            inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getBannerId());
            inputs.put(DAOConstants.PARAM_START_DATE, campaignBanner.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, campaignBanner.getEndDate());
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
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getCampaignId());
            inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getBannerId());
            inputs.put(DAOConstants.PARAM_START_DATE, campaignBanner.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, campaignBanner.getEndDate());
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
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getCampaignId());
            inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getBannerId());
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
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getCampaignId());
            inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getBannerId());
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
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaignBanner.getCampaign().getCampaignId());
            inputs.put(DAOConstants.PARAM_BANNER_ID, campaignBanner.getBanner().getBannerId());
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
	            							  rs.getDate(DAOConstants.COLUMN_START_DATE),
	            							  rs.getDate(DAOConstants.COLUMN_END_DATE),
	            							  rs.getString(DAOConstants.COLUMN_CREATED_BY),
	            							  rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	            							  rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
	            							  rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
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
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, model.getCampaignId());
	        inputs.put(DAOConstants.PARAM_STORE_ID, model.getStore().getStoreId().toLowerCase().trim());
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getBannerSP.execute(inputs));
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
	            							  rs.getDate(DAOConstants.COLUMN_START_DATE),
	            							  rs.getDate(DAOConstants.COLUMN_END_DATE),
	            							  rs.getString(DAOConstants.COLUMN_CREATED_BY),
	            							  rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY),
	            							  rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE),
	            							  rs.getTimestamp(DAOConstants.COLUMN_LAST_MODIFIED_DATE));
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
	        inputs.put(DAOConstants.PARAM_CAMPAIGN, campaign.getCampaignId());
			inputs.put(DAOConstants.PARAM_CAMPAIGN, campaignMatchType.equals(MatchType.MATCH_ID) ? campaign.getCampaignId() : campaign.getCampaignName());
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE_CAMPAIGN, campaignMatchType.getIntValue());
			inputs.put(DAOConstants.PARAM_BANNER, bannerMatchType.equals(MatchType.MATCH_ID) ? banner.getBannerId() : banner.getBannerName());
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
	            	map.put(DAOConstants.COLUMN_CREATED_DATE, rs.getTimestamp(DAOConstants.COLUMN_CREATED_DATE));
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
	
	public RecordSet<CampaignBanner> getCampaignBannerKeywords(SearchCriteria<CampaignBanner> criteria) throws DaoException {
		try {
			CampaignBanner model = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, model.getCampaign().getCampaignId());
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(model.getCampaign().getStore()));
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getBannerKeywordSP.execute(inputs));
		} catch (Exception e) {
			throw (e instanceof DaoException) ? (DaoException)e : new DaoException("Failed during getCampaign()", e);
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
	            	map.put(DAOConstants.COLUMN_START_DATE, rs.getDate(DAOConstants.COLUMN_START_DATE));
	            	map.put(DAOConstants.COLUMN_END_DATE, rs.getDate(DAOConstants.COLUMN_END_DATE));
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
	
	public RecordSet<CampaignBanner> getCampaignBannerWithKeyword(SearchCriteria<CampaignBanner> criteria) throws DaoException {
		try {
			CampaignBanner model = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, model.getCampaign().getCampaignId());
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(model.getCampaign().getStore()));
	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(searchBannerKeywordSP.execute(inputs));
		} catch (Exception e) {
			throw (e instanceof DaoException) ? (DaoException)e : new DaoException("Failed during getCampaign()", e);
    	}
	}
	
	// banner keywords
//	private  getBannerSP;
//	private  searchBannerSP;
//	private  getBannerKeywordSP;
//	private SearchCampaignBannerKeywordsStoredProcedure searchBannerKeywordSP;

	
	public int addCampaign(Campaign campaign) throws DaoException {
    	try {
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, DAOUtils.generateUniqueId());
            inputs.put(DAOConstants.PARAM_CAMPAIGN_NAME, StringUtils.trimToEmpty(campaign.getCampaignName()));
            inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(campaign.getStore()));
            inputs.put(DAOConstants.PARAM_START_DATE, campaign.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, campaign.getEndDate());
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
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, model.getCampaignId());
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
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getCampaignId());
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
            inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getCampaignId());
            inputs.put(DAOConstants.PARAM_CAMPAIGN_NAME, StringUtils.trimToEmpty(campaign.getCampaignName()));
            inputs.put(DAOConstants.PARAM_START_DATE, campaign.getStartDate());
            inputs.put(DAOConstants.PARAM_END_DATE, campaign.getEndDate());
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
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getCampaignId());
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
	        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getCampaignId());
            inputs.put(DAOConstants.PARAM_COMMENT, campaign.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, campaign.getLastModifiedBy());
        	return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during appendCampaignComment()", e);
    	}
    }
	
    public int deleteCampaign(Campaign campaign) throws DaoException {
		try {
			if (campaign != null && campaign.getCampaignId() != null) {
		    	Map<String, Object> inputs = new HashMap<String, Object>();
		        inputs.put(DAOConstants.PARAM_CAMPAIGN_ID, campaign.getCampaignId());
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
			inputs.put(DAOConstants.PARAM_CAMPAIGN, campaignMatchType.equals(MatchType.MATCH_ID) ? model.getCampaignId() : model.getCampaignName());
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
    
}
