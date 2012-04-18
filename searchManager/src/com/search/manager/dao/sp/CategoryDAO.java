package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.model.Category;
import com.search.manager.model.CategoryList;
import com.search.manager.service.UtilityService;
import com.search.ws.SearchHelper;

@Repository(value="categoryDAO")
public class CategoryDAO {

	// needed by spring AOP
	public CategoryDAO(){
	}
	
	@Autowired
	public CategoryDAO(JdbcTemplate jdbcTemplate) {
		getCategoriesStoredProcedure = new GetCategoriesStoredProcedure(jdbcTemplate);
    }
	
	private Logger logger = Logger.getLogger(CategoryDAO.class);
	private final static String MANUFACTURER = "Manufacturer"; 
	private final static String CAT_CODE = "CatCode:";
	private final static String STAR = "*";
	
	private GetCategoriesStoredProcedure getCategoriesStoredProcedure;
	
	private class GetCategoriesStoredProcedure extends StoredProcedure {
	    public GetCategoriesStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_CATEGORY_BY_FILTER);
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Category>() {
	        	public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new Category(rs.getString(DAOConstants.COLUMN_PRODUCT_CATEGORY_ID), rs.getString(DAOConstants.COLUMN_CATEGORY_NAME));
	        	}
	        }));
	        declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<String>() {
	        	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
	                return new String(rs.getString(DAOConstants.COLUMN_MANUFACTURER));
	        	}
	        }));
	        declareParameter(new SqlParameter(DAOConstants.PARAM_CAT_CODE, Types.VARCHAR));
	        compile();
	    }
	}

    public CategoryList getCategories(String categoryCode) {
    	logger.info("Category code is " + categoryCode);
    	categoryCode = StringUtils.upperCase(StringUtils.trimToEmpty(categoryCode));
		Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(DAOConstants.PARAM_CAT_CODE, StringUtils.isBlank(categoryCode)?null:categoryCode);
        Map<String,Object> result = getCategoriesStoredProcedure.execute(inputs);
        CategoryList categoryList = null;
        if (result != null) {
        	List<Category> categories = (List<Category>) result.get(DAOConstants.RESULT_SET_1);
        	List<String> filters = new ArrayList<String>();
        	filters.add(CAT_CODE + categoryCode + STAR);
        	List<String> manufacturers = SearchHelper.getFacetValues(UtilityService.getServerName(), UtilityService.getStoreLabel(), MANUFACTURER, filters);
        	categoryList = new CategoryList(categories, manufacturers);
        }
        return categoryList;
    }	
}