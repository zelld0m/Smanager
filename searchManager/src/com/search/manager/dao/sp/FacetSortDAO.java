package com.search.manager.dao.sp;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.model.FacetSort;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

@Repository(value="facetSortDAO")
public class FacetSortDAO {

	private AddFacetSortStoredProcedure addFacetSortSP;
	private DeleteFacetSortStoredProcedure deleteFacetSortSP;
	private GetFacetSortStoredProcedure getFacetSortSP;
	private UpdateFacetSortStoredProcedure updateFacetSortSP;
	
	private AddFacetGroupStoredProcedure addFacetGroupSP;
	private DeleteFacetGroupStoredProcedure deleteFacetGroupSP;
	private UpdateFacetGroupStoredProcedure updateFacetGroupSP;
	
	private AddFacetGroupItemStoredProcedure addFacetGroupItemSP;
	private DeleteFacetGroupItemStoredProcedure deleteFacetGroupItemSP;
	private UpdateFacetGroupItemStoredProcedure updateFacetGroupItemSP;
	private ClearFacetGroupItemStoredProcedure clearFacetGroupItemSP;

	public FacetSortDAO(){}

	@Autowired
	public FacetSortDAO(JdbcTemplate jdbcTemplate) {
		addFacetSortSP = new AddFacetSortStoredProcedure(jdbcTemplate);
		deleteFacetSortSP = new DeleteFacetSortStoredProcedure(jdbcTemplate);
		getFacetSortSP = new GetFacetSortStoredProcedure(jdbcTemplate);
	}

	private class AddFacetSortStoredProcedure extends CUDStoredProcedure {
	    public AddFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_FACET_SORT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
		}
	}
	
	private class DeleteFacetSortStoredProcedure extends CUDStoredProcedure {
	    public DeleteFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_SORT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class GetFacetSortStoredProcedure extends CUDStoredProcedure {
	    public GetFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_GET_FACET_SORT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class UpdateFacetSortStoredProcedure extends CUDStoredProcedure {
	    public UpdateFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_FACET_SORT);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_DESCRIPTION, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.DATE));
		}
	}
	
	private class AddFacetGroupStoredProcedure extends CUDStoredProcedure {
	    public AddFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_ADD_FACET_GROUP);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class DeleteFacetGroupStoredProcedure extends CUDStoredProcedure {
	    public DeleteFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class UpdateFacetGroupStoredProcedure extends CUDStoredProcedure {
	    public UpdateFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class AddFacetGroupItemStoredProcedure extends CUDStoredProcedure {
	    public AddFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class DeleteFacetGroupItemStoredProcedure extends CUDStoredProcedure {
	    public DeleteFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP_ITEM);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class UpdateFacetGroupItemStoredProcedure extends CUDStoredProcedure {
	    public UpdateFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_UPDATE_FACET_GROUP_ITEM);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	private class ClearFacetGroupItemStoredProcedure extends CUDStoredProcedure {
	    public ClearFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
	        super(jdbcTemplate, DAOConstants.SP_CLEAR_FACET_GROUP_ITEM);
	    }

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RELEVANCY_ID, Types.VARCHAR));
		}
	}
	
	@Audit(entity = Entity.facetSort, operation = Operation.add)
	public int addFacetSort(FacetSort facetSort) throws DaoException {
    	try {
    		DAOValidation.checkFacetSort(facetSort);
    		DAOValidation.checkStoreId(facetSort.getStore());
        	Map<String, Object> inputs = new HashMap<String, Object>();
        	String ruleId = facetSort.getRuleId();
        	
        	if (StringUtils.isEmpty(ruleId)) {
        		ruleId = DAOUtils.generateUniqueId();
        	}
            
        	inputs.put(DAOConstants.PARAM_RELEVANCY_ID, ruleId);
            inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(facetSort.getRuleName()));
            inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(facetSort.getStore()));
          
            return DAOUtils.getUpdateCount(addFacetSortSP.execute(inputs));
    	}
    	catch (Exception e) {
    		throw new DaoException("Failed during addFacetSort(): " + e.getMessage(), e);
    	}
    }
	
	@Audit(entity = Entity.facetSort, operation = Operation.delete)
    public int deleteFacetSort(FacetSort facetSort) throws DaoException {
		try {
    		DAOValidation.checkFacetSortPK(facetSort);
	    	Map<String, Object> inputs = new HashMap<String, Object>();
            inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetSort.getRuleId());
        	return DAOUtils.getUpdateCount(deleteFacetSortSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during deleteFacetSort(): " + e.getMessage(), e);
    	}
    }
}