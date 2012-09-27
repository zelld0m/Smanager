package com.search.manager.dao.sp;

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
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.SearchCriteria.MatchType;
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
		updateFacetSortSP = new UpdateFacetSortStoredProcedure(jdbcTemplate);
		addFacetGroupSP = new AddFacetGroupStoredProcedure(jdbcTemplate);
		deleteFacetGroupSP = new DeleteFacetGroupStoredProcedure(jdbcTemplate);
		updateFacetGroupSP = new UpdateFacetGroupStoredProcedure(jdbcTemplate);
		addFacetGroupItemSP = new AddFacetGroupItemStoredProcedure(jdbcTemplate);
		deleteFacetGroupItemSP = new DeleteFacetGroupItemStoredProcedure(jdbcTemplate);
		updateFacetGroupItemSP = new UpdateFacetGroupItemStoredProcedure(jdbcTemplate);
		clearFacetGroupItemSP = new ClearFacetGroupItemStoredProcedure(jdbcTemplate); ;
	}

	private class AddFacetSortStoredProcedure extends CUDStoredProcedure {
		public AddFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_FACET_SORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SORT_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class DeleteFacetSortStoredProcedure extends CUDStoredProcedure {
		public DeleteFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_SORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
		}
	}

	private class GetFacetSortStoredProcedure extends GetStoredProcedure {
		public GetFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_FACET_SORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<Relevancy>() {
				public Relevancy mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new Relevancy(
							rs.getString(DAOConstants.COLUMN_RELEVANCY_ID),
							rs.getString(DAOConstants.COLUMN_NAME),
							rs.getString(DAOConstants.COLUMN_DESCRIPTION),
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
		}
	}

	private class UpdateFacetSortStoredProcedure extends CUDStoredProcedure {
		public UpdateFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_FACET_SORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
		}
	}

	private class AddFacetGroupStoredProcedure extends CUDStoredProcedure {
		public AddFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_FACET_GROUP);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
		}
	}

	private class DeleteFacetGroupStoredProcedure extends CUDStoredProcedure {
		public DeleteFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
		}
	}

	private class UpdateFacetGroupStoredProcedure extends CUDStoredProcedure {
		public UpdateFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
		}
	}

	private class AddFacetGroupItemStoredProcedure extends CUDStoredProcedure {
		public AddFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
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
	public String addFacetSortAndGetId(FacetSort facetSort) throws DaoException {
		String id = DAOUtils.generateUniqueId();
		facetSort.setId(id);
		return (addFacetSort(facetSort) > 0) ?  id : null;
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

			inputs.put(DAOConstants.PARAM_RULE_ID, ruleId);
			inputs.put(DAOConstants.PARAM_RULE_NAME, StringUtils.trimToEmpty(facetSort.getRuleName()));
			inputs.put(DAOConstants.PARAM_RULE_TYPE, facetSort.getRuleType());
			inputs.put(DAOConstants.PARAM_STORE_ID, facetSort.getStore().getStoreId());
			inputs.put(DAOConstants.PARAM_SORT_TYPE, facetSort.getSortType());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, facetSort.getCreatedBy());

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

	@SuppressWarnings("rawtypes")
	public Object searchFacet(SearchCriteria criteria, MatchType matchType) throws DaoException {
		try {
			DAOValidation.checkSearchCriteria(criteria);
			Map<String, Object> inputs = new HashMap<String, Object>();
			//			Object model = criteria.getModel();
			//			
			//			if(model instanceof FacetSort){
			//				model = (FacetSort) model;
			//				inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(((FacetSort) model).getStore()));
			//				inputs.put(DAOConstants.PARAM_RELEVANCY, (matchType == null) ?
			//						null : (matchType.equals(MatchType.MATCH_ID) ?
			//								model.getRuleId() : model.getRuleName()));
			//				inputs.put(DAOConstants.PARAM_MATCH_TYPE_RELEVANCY, (matchType == null) ?
			//			}else if(model instanceof FacetGroup){
			//				model = (FacetGroup) model;
			//			}else if(model instanceof FacetGroupItem){
			//				model = (FacetGroupItem) model;
			//			}
			//			
			//	        		null : matchType.getIntValue());
			//	        inputs.put(DAOConstants.PARAM_START_DATE, criteria.getStartDate());
			//	        inputs.put(DAOConstants.PARAM_END_DATE, criteria.getEndDate());
			//	        inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			//	        inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			return DAOUtils.getRecordSet(getFacetSortSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during searchFacet(): " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public RecordSet<FacetSort> searchFacetSort(SearchCriteria<FacetSort> criteria, MatchType matchType) throws DaoException {
		return (RecordSet<FacetSort>) searchFacet(criteria, matchType);
	}

	@SuppressWarnings("unchecked")
	public RecordSet<FacetGroup> searchFacetGroup(SearchCriteria<FacetGroup> criteria, MatchType matchType) throws DaoException {
		return (RecordSet<FacetGroup>) searchFacet(criteria, matchType);
	}

	@SuppressWarnings("unchecked")
	public RecordSet<FacetGroupItem> searchFacetGroupItem(SearchCriteria<FacetGroupItem> criteria, MatchType matchType) throws DaoException {
		return (RecordSet<FacetGroupItem>) searchFacet(criteria, matchType);
	}

	@Audit(entity = Entity.facetSort, operation = Operation.update)
	public int updateFacetSort(FacetSort facetSort) throws DaoException {
		try {
			DAOValidation.checkFacetSortPK(facetSort);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetSort.getRuleId());
			return DAOUtils.getUpdateCount(updateFacetSortSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateFacetSort(): " + e.getMessage(), e);
		}
	}

	public int addFacetGroup(FacetGroup facetGroup) throws DaoException {
		try {
			DAOValidation.checkFacetGroup(facetGroup);
			Map<String, Object> inputs = new HashMap<String, Object>();
			String facetGroupId = facetGroup.getId();

			if (StringUtils.isEmpty(facetGroupId)) {
				facetGroupId = DAOUtils.generateUniqueId();
			}

			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroupId);
			inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(facetGroup.getName()));

			return DAOUtils.getUpdateCount(addFacetGroupSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addFacetGroup(): " + e.getMessage(), e);
		}
	}

	public int deleteFacetGroup(FacetGroup facetGroup) throws DaoException {
		try {
			DAOValidation.checkFacetGroupPK(facetGroup);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroup.getId());
			return DAOUtils.getUpdateCount(deleteFacetGroupSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during addFacetGroup(): " + e.getMessage(), e);
		}
	}

	public int updateFacetGroup(FacetGroup facetGroup) throws DaoException {
		try {
			DAOValidation.checkFacetGroupPK(facetGroup);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroup.getId());
			return DAOUtils.getUpdateCount(updateFacetGroupSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateFacetGroup(): " + e.getMessage(), e);
		}
	}

	public int addFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException {
		try {
			DAOValidation.checkFacetGroupItem(facetGroupItem);
			Map<String, Object> inputs = new HashMap<String, Object>();
			String facetGroupItemId = facetGroupItem.getId();

			if (StringUtils.isEmpty(facetGroupItemId)) {
				facetGroupItemId = DAOUtils.generateUniqueId();
			}

			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroupItemId);
			inputs.put(DAOConstants.PARAM_RELEVANCY_NAME, StringUtils.trimToEmpty(facetGroupItem.getName()));

			return DAOUtils.getUpdateCount(addFacetGroupItemSP.execute(inputs));
		}
		catch (Exception e) {
			throw new DaoException("Failed during addFacetGroupItem(): " + e.getMessage(), e);
		}
	}

	public int deleteFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException {
		try {
			DAOValidation.checkFacetGroupItemPK(facetGroupItem);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroupItem.getId());
			return DAOUtils.getUpdateCount(deleteFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during deleteFacetGroupItem(): " + e.getMessage(), e);
		}
	}

	public int updateFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException {
		try {
			DAOValidation.checkFacetGroupItemPK(facetGroupItem);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroupItem.getId());
			return DAOUtils.getUpdateCount(updateFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateFacetGroupItem(): " + e.getMessage(), e);
		}
	}	

	public int clearFacetGroupItem(FacetGroup facetGroup) throws DaoException {
		try {
			DAOValidation.checkFacetGroupPK(facetGroup);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_RELEVANCY_ID, facetGroup.getId());
			return DAOUtils.getUpdateCount(clearFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during clearFacetGroupItem(): " + e.getMessage(), e);
		}
	}	
}