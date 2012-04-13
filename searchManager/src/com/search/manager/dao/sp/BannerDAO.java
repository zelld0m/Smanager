package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Banner;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.SearchCriteria.MatchType;

@Repository(value="bannerDAO")
public class BannerDAO {

	// for AOP use
	public BannerDAO(){
	}
	
	private AddBannerStoredProcedure addSP;
	private UpdateBannerStoredProcedure updateSP;
	private DeleteBannerStoredProcedure deleteSP;
	private GetBannerStoredProcedure getSP;
	private SearchBannerStoredProcedure searchSP;
	private UpdateBannerCommentStoredProcedure updateCommentSP;
	private AppendBannerCommentStoredProcedure appendCommentSP;
	
	public BannerDAO(JdbcTemplate jdbcTemplate) {
    	addSP = new AddBannerStoredProcedure(jdbcTemplate);
    	updateSP = new UpdateBannerStoredProcedure(jdbcTemplate) ;
    	deleteSP = new DeleteBannerStoredProcedure(jdbcTemplate);
    	getSP = new GetBannerStoredProcedure(jdbcTemplate);
    	searchSP = new SearchBannerStoredProcedure(jdbcTemplate);
    	updateCommentSP = new UpdateBannerCommentStoredProcedure(jdbcTemplate);
    	appendCommentSP = new AppendBannerCommentStoredProcedure(jdbcTemplate);
    }

	private class AddBannerStoredProcedure extends StoredProcedure {
	    public AddBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_BANNER);
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_URL, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_URL, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class GetBannerStoredProcedure extends StoredProcedure {
	    public GetBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_BANNER);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Banner>() {
	            public Banner mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Banner(
                		rs.getString(DAOConstants.COLUMN_BANNER_ID),
                		rs.getString(DAOConstants.COLUMN_NAME),
                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
                		rs.getString(DAOConstants.COLUMN_IMAGE_URL),
                		rs.getString(DAOConstants.COLUMN_LINK_URL),
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
	        declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	    }
	}
	
	private class UpdateBannerStoredProcedure extends StoredProcedure {
	    public UpdateBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER);
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_URL, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_URL, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class DeleteBannerStoredProcedure extends StoredProcedure {
	    public DeleteBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_BANNER);
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class UpdateBannerCommentStoredProcedure extends StoredProcedure {
	    public UpdateBannerCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER_COMMENT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}
	
	private class AppendBannerCommentStoredProcedure extends StoredProcedure {
	    public AppendBannerCommentStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_APPEND_BANNER_COMMENT);
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_COMMENT, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
	        compile();
	    }
	}

	private class SearchBannerStoredProcedure extends StoredProcedure {
	    public SearchBannerStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_SEARCH_BANNER);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Banner>() {
	            public Banner mapRow(ResultSet rs, int rowNum) throws SQLException
	            {
	            	return new Banner(
                		rs.getString(DAOConstants.COLUMN_BANNER_ID),
                		rs.getString(DAOConstants.COLUMN_NAME),
                		new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
                		rs.getString(DAOConstants.COLUMN_IMAGE_URL),
                		rs.getString(DAOConstants.COLUMN_LINK_URL),
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_BANNER, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE_BANNER, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
	        compile();
	   }
	}
	
	
	public int addBanner(Banner banner) throws DaoException {
    	try {
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_BANNER_ID, DAOUtils.generateUniqueId());
            inputs.put(DAOConstants.PARAM_BANNER_NAME, StringUtils.trimToEmpty(banner.getBannerName()));
            inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(banner.getStore()));
            inputs.put(DAOConstants.PARAM_IMAGE_URL, StringUtils.trimToEmpty(banner.getImagePath()));
            inputs.put(DAOConstants.PARAM_LINK_URL, StringUtils.trimToEmpty(banner.getLinkPath()));
            inputs.put(DAOConstants.PARAM_COMMENT, banner.getComment());
            inputs.put(DAOConstants.PARAM_CREATED_BY, StringUtils.trimToEmpty(banner.getCreatedBy()));
            return DAOUtils.getUpdateCount(addSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addBanner()", e);
    	}
    }
	
	public Banner getBanner(Banner banner) throws DaoException {
		try {
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_BANNER_ID, banner.getBannerId());
	        String storeId = null;
	        if (banner.getStore() != null) {
	        	storeId = StringUtils.lowerCase(StringUtils.trim(banner.getStore().getStoreId()));
	        }
	        inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
	        inputs.put(DAOConstants.PARAM_START_ROW, 0);
	        inputs.put(DAOConstants.PARAM_END_ROW, 0);
	        return DAOUtils.getItem(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getBanner()", e);
    	}
	}
	
	public RecordSet<Banner> getBanners(SearchCriteria<Banner> criteria) throws DaoException {
		try {
			Banner model = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        String storeId = null;
	        if (model.getStore() != null) {
	        	storeId = StringUtils.lowerCase(StringUtils.trim(model.getStore().getStoreId()));
	        }
	        inputs.put(DAOConstants.PARAM_BANNER_ID, model.getBannerId());
	        inputs.put(DAOConstants.PARAM_STORE_ID, storeId);
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(getSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during getBanner()", e);
    	}
	}

	public int updateBanner(Banner banner) throws DaoException {
    	try {
        	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_BANNER_ID, banner.getBannerId());
            inputs.put(DAOConstants.PARAM_BANNER_NAME, StringUtils.trimToEmpty(banner.getBannerName()));
            inputs.put(DAOConstants.PARAM_IMAGE_URL, banner.getImagePath());
            inputs.put(DAOConstants.PARAM_LINK_URL, banner.getLinkPath());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, banner.getLastModifiedBy());
            return DAOUtils.getUpdateCount(updateSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during updateBanner()", e);
    	}
    }
	
    public int updateBannerComment(Banner banner) throws DaoException {
		try {
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_BANNER_ID, banner.getBannerId());
            inputs.put(DAOConstants.PARAM_COMMENT, banner.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, banner.getLastModifiedBy());
        	return DAOUtils.getUpdateCount(updateCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during updateBannerComment()", e);
    	}
    }
    
    public int appendBannerComment(Banner banner) throws DaoException {
		try {
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_BANNER_ID, banner.getBannerId());
            inputs.put(DAOConstants.PARAM_COMMENT, banner.getComment());
            inputs.put(DAOConstants.PARAM_MODIFIED_BY, banner.getLastModifiedBy());
        	return DAOUtils.getUpdateCount(appendCommentSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during appendBannerComment()", e);
    	}
    }
	
    public int deleteBanner(Banner banner) throws DaoException {
		try {
			if (banner != null && banner.getBannerId() != null) {
		    	Map<String, Object> inputs = new HashMap<String, Object>();
		        inputs.put(DAOConstants.PARAM_BANNER_ID, banner.getBannerId());
	        	return DAOUtils.getUpdateCount(deleteSP.execute(inputs));
			}
			return -1;
		} catch (Exception e) {
    		throw new DaoException("Failed during deleteBanner()", e);
    	}
    }

	public RecordSet<Banner> searchBanner(SearchCriteria<Banner> criteria, MatchType bannerMatchType) throws DaoException {
		try {
			Banner model = criteria.getModel();
	    	Map<String, Object> inputs = new HashMap<String, Object>();
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(model.getStore()));
			inputs.put(DAOConstants.PARAM_BANNER, bannerMatchType.equals(MatchType.MATCH_ID) ? model.getBannerId() : model.getBannerName());
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE_BANNER, String.valueOf(bannerMatchType.getIntValue()));
	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
	        return DAOUtils.getRecordSet(searchSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during searchBanner()", e);
    	}
	}
    
}
