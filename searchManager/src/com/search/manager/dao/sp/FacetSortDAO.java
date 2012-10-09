package com.search.manager.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
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
	private GetFacetGroupStoredProcedure getFacetGroupSP;
	private UpdateFacetGroupStoredProcedure updateFacetGroupSP;
	
	private AddFacetGroupItemStoredProcedure addFacetGroupItemSP;
	private DeleteFacetGroupItemStoredProcedure deleteFacetGroupItemSP;
	private GetFacetGroupItemStoredProcedure getFacetGroupItemSP;
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
		getFacetGroupSP = new GetFacetGroupStoredProcedure(jdbcTemplate);
		updateFacetGroupSP = new UpdateFacetGroupStoredProcedure(jdbcTemplate);
		
		addFacetGroupItemSP = new AddFacetGroupItemStoredProcedure(jdbcTemplate);
		deleteFacetGroupItemSP = new DeleteFacetGroupItemStoredProcedure(jdbcTemplate);
		getFacetGroupItemSP = new GetFacetGroupItemStoredProcedure(jdbcTemplate);
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
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW2, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW2, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MATCH_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SORT_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RETURN_OPTION, Types.INTEGER));
		}

		private Map<String, List<String>> getItems(String facetGroup, String facetGroupItems){
			Map<String, List<String>> items = new HashMap<String, List<String>>();
			
			String[] arrGroupName = StringUtils.split(facetGroup, ',');
			String[] arrGroupItem = StringUtils.split(facetGroupItems, "&&");
			
			for (int i=0; i< ArrayUtils.getLength(arrGroupName); i++){
				items.put(arrGroupName[i], (i+1)> ArrayUtils.getLength(arrGroupItem)? new ArrayList<String>(): Arrays.asList(ArrayUtils.nullToEmpty(StringUtils.split(arrGroupItem[i], "||"))));
			}
			
			return items;
		}
		
		private Map<String, SortType> getGroupSortType(String facetGroup, String groupSortType){
			Map<String, SortType> sortTypes = new HashMap<String, SortType>();
			
			String[] arrGroupName = StringUtils.split(facetGroup, ',');
			String[] arrGroupSortType = StringUtils.split(groupSortType, ',');
			
			for (int i=0; i< ArrayUtils.getLength(arrGroupName); i++){
				sortTypes.put(arrGroupName[i], (i+1)> ArrayUtils.getLength(arrGroupSortType) || "NULL".equalsIgnoreCase(arrGroupSortType[i])? null: SortType.get(Integer.valueOf(arrGroupSortType[i])));
			}
			
			return sortTypes;
		}
		
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<FacetSort>() {
				public FacetSort mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					
					FacetSort facetSort = new FacetSort(
							rs.getString(DAOConstants.COLUMN_RULE_ID),
							rs.getString(DAOConstants.COLUMN_RULE_NAME),
							RuleType.get(rs.getInt(DAOConstants.COLUMN_RULE_TYPE)),
							SortType.get(rs.getInt(DAOConstants.COLUMN_SORT_TYPE)),
							new Store(rs.getString(DAOConstants.COLUMN_STORE_ID)),
							getItems(rs.getString(DAOConstants.COLUMN_GROUP_NAME_LIST), rs.getString(DAOConstants.COLUMN_ITEM_NAME_LIST)),
							getGroupSortType(rs.getString(DAOConstants.COLUMN_GROUP_NAME_LIST), rs.getString(DAOConstants.COLUMN_SORT_ID_LIST))
					);
					
					facetSort.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
					facetSort.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY));
					facetSort.setCreatedDate(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP));
					facetSort.setLastModifiedDate(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP));
			
					return facetSort;
				}
			}));
		}
	}
	
	private class GetFacetGroupStoredProcedure extends GetFacetSortStoredProcedure {
		public GetFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate);
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<FacetGroup>() {
				public FacetGroup mapRow(ResultSet rs, int rowNum) throws SQLException
				{	
					FacetGroup facetGroup = new FacetGroup(
							rs.getString(DAOConstants.COLUMN_RULE_ID),
							rs.getString(DAOConstants.COLUMN_FACET_GROUP_ID),
							rs.getString(DAOConstants.COLUMN_FACET_GROUP_NAME),
							FacetGroupType.get(rs.getInt(DAOConstants.COLUMN_FACET_GROUP_TYPE)),
							StringUtils.isBlank(rs.getString(DAOConstants.COLUMN_SORT_TYPE))? null: SortType.get(rs.getInt(DAOConstants.COLUMN_SORT_TYPE)) ,
							rs.getInt(DAOConstants.COLUMN_FACET_GROUP_SEQUENCE)
					);
					
					facetGroup.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
					facetGroup.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY));
					facetGroup.setCreatedDate(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP));
					facetGroup.setLastModifiedDate(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP));
					
					return facetGroup;
				}
			}));
		}
	}
	
	private class GetFacetGroupItemStoredProcedure extends GetFacetSortStoredProcedure {
		public GetFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate);
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<FacetGroupItem>() {
				public FacetGroupItem mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					FacetGroupItem facetGroupItem = new FacetGroupItem(
							rs.getString(DAOConstants.COLUMN_FACET_GROUP_ID),
							rs.getString(DAOConstants.COLUMN_MEMBER_ID),
							rs.getString(DAOConstants.COLUMN_FACET_GROUP_ITEM_NAME),
							rs.getInt(DAOConstants.COLUMN_FACET_GROUP_ITEM_SEQUENCE)
					);
					
					facetGroupItem.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
					facetGroupItem.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_MODIFIED_BY));
					facetGroupItem.setCreatedDate(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP));
					facetGroupItem.setLastModifiedDate(rs.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP));
					
					return facetGroupItem;
				}
			}));
		}
	}

	private class UpdateFacetSortStoredProcedure extends CUDStoredProcedure {
		public UpdateFacetSortStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_FACET_SORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SORT_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class AddFacetGroupStoredProcedure extends CUDStoredProcedure {
		public AddFacetGroupStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_FACET_GROUP);
		}
		
		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SORT_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_SEQUENCE, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
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
			super(jdbcTemplate, DAOConstants.SP_UPDATE_FACET_GROUP);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_SEQUENCE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SORT_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class AddFacetGroupItemStoredProcedure extends CUDStoredProcedure {
		public AddFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_FACET_GROUP_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ITEM_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ITEM_SEQUENCE, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class DeleteFacetGroupItemStoredProcedure extends CUDStoredProcedure {
		public DeleteFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_FACET_GROUP_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
		}
	}

	private class UpdateFacetGroupItemStoredProcedure extends CUDStoredProcedure {
		public UpdateFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_FACET_GROUP_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ITEM_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ITEM_SEQUENCE, Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_MODIFIED_BY, Types.VARCHAR));
		}
	}

	private class ClearFacetGroupItemStoredProcedure extends CUDStoredProcedure {
		public ClearFacetGroupItemStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_CLEAR_FACET_GROUP_ITEM);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_FACET_GROUP_ID, Types.VARCHAR));
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
			RuleType ruleType = facetSort.getRuleType();
			SortType sortType = facetSort.getSortType();

			if (StringUtils.isEmpty(ruleId)) {
				ruleId = DAOUtils.generateUniqueId();
			}

			inputs.put(DAOConstants.PARAM_RULE_ID, ruleId);
			inputs.put(DAOConstants.PARAM_RULE_NAME, StringUtils.trimToEmpty(facetSort.getRuleName()));
			inputs.put(DAOConstants.PARAM_RULE_TYPE,  (ruleType==null)? sortType : ruleType.toString());
			inputs.put(DAOConstants.PARAM_STORE_ID, facetSort.getStore().getStoreId());
			inputs.put(DAOConstants.PARAM_SORT_TYPE, (sortType==null)? sortType : sortType.toString());
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
			inputs.put(DAOConstants.PARAM_RULE_ID, facetSort.getRuleId());
			return DAOUtils.getUpdateCount(deleteFacetSortSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during deleteFacetSort(): " + e.getMessage(), e);
		}
	}
	
	public FacetSort searchFacetSort(FacetSort facetSort) throws DaoException {
    	RecordSet<FacetSort> rules = searchFacetSort(new SearchCriteria<FacetSort>(facetSort, 1, 1), StringUtils.isBlank(facetSort.getRuleId())? MatchType.MATCH_NAME : MatchType.MATCH_ID);
    	return (rules.getTotalSize() > 0 ? rules.getList().get(0): null);
    }
	
	public RecordSet<FacetSort> searchFacetSort(SearchCriteria<FacetSort> criteria, MatchType matchType) throws DaoException {
		try {
			DAOValidation.checkSearchCriteria(criteria);
			FacetSort model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
	    	RuleType ruleType = model.getRuleType();
	    	SortType sortType = model.getSortType();
			
	    	inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
	    	inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, "");
	    	inputs.put(DAOConstants.PARAM_RULE_NAME, model.getRuleName());
	        inputs.put(DAOConstants.PARAM_RULE_TYPE, (ruleType==null)? ruleType: ruleType.toString());
	        inputs.put(DAOConstants.PARAM_STORE_ID, DAOUtils.getStoreId(model.getStore()));
	        inputs.put(DAOConstants.PARAM_START_ROW2, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW2, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE, (matchType == null) ? null : matchType.getIntValue());
	        inputs.put(DAOConstants.PARAM_SORT_TYPE, (sortType==null)? sortType: sortType.toString());
	        inputs.put(DAOConstants.PARAM_RETURN_OPTION, 0);
	        
	        return DAOUtils.getRecordSet(getFacetSortSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during searchFacetSort(): " + e.getMessage(), e);
    	}
	}

	public RecordSet<FacetGroup> searchFacetGroup(SearchCriteria<FacetGroup> criteria, MatchType matchType) throws DaoException {
		try {
			DAOValidation.checkSearchCriteria(criteria);
			FacetGroup model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
	    	
	    	SortType sortType = model.getSortType();
			
	    	inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
	    	inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, "");
	    	inputs.put(DAOConstants.PARAM_RULE_NAME, "");
	        inputs.put(DAOConstants.PARAM_RULE_TYPE, "");
	        inputs.put(DAOConstants.PARAM_STORE_ID, "");
	        inputs.put(DAOConstants.PARAM_START_ROW2, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW2, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE, (matchType == null) ? matchType : matchType.getIntValue());
	        inputs.put(DAOConstants.PARAM_SORT_TYPE, (sortType==null)? sortType: sortType.toString());
	        inputs.put(DAOConstants.PARAM_RETURN_OPTION, 1);
	        
	        return DAOUtils.getRecordSet(getFacetGroupSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during searchFacetSort(): " + e.getMessage(), e);
    	}
	}

	public RecordSet<FacetGroupItem> searchFacetGroupItem(SearchCriteria<FacetGroupItem> criteria, MatchType matchType) throws DaoException {
		try {
			DAOValidation.checkSearchCriteria(criteria);
			FacetGroupItem model = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
	    	
	    	inputs.put(DAOConstants.PARAM_RULE_ID, model.getRuleId());
	    	inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, model.getFacetGroupId());
	    	inputs.put(DAOConstants.PARAM_RULE_NAME, "");
	        inputs.put(DAOConstants.PARAM_RULE_TYPE, "");
	        inputs.put(DAOConstants.PARAM_STORE_ID, "");
	        inputs.put(DAOConstants.PARAM_START_ROW2, criteria.getStartRow());
	        inputs.put(DAOConstants.PARAM_END_ROW2, criteria.getEndRow());
	        inputs.put(DAOConstants.PARAM_MATCH_TYPE, (matchType == null) ? matchType : matchType.getIntValue());
	        inputs.put(DAOConstants.PARAM_SORT_TYPE, "");
	        inputs.put(DAOConstants.PARAM_RETURN_OPTION, 2);
	        
	        return DAOUtils.getRecordSet(getFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
    		throw new DaoException("Failed during searchFacetGroupItem(): " + e.getMessage(), e);
    	}
	}

	@Audit(entity = Entity.facetSort, operation = Operation.update)
	public int updateFacetSort(FacetSort facetSort) throws DaoException {
		try {
			DAOValidation.checkFacetSortPK(facetSort);
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(DAOConstants.PARAM_RULE_ID, facetSort.getRuleId());
			inputs.put(DAOConstants.PARAM_RULE_NAME, facetSort.getName());
			inputs.put(DAOConstants.PARAM_SORT_TYPE, SortType.get(facetSort.getSortType().toString()));
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, facetSort.getLastModifiedBy());
			
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
			SortType sortType = facetGroup.getSortType();

			if (StringUtils.isBlank(facetGroupId)) {
				facetGroupId = DAOUtils.generateUniqueId();
			}

			inputs.put(DAOConstants.PARAM_RULE_ID, StringUtils.trimToEmpty(facetGroup.getRuleId()));
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, facetGroupId);
			inputs.put(DAOConstants.PARAM_FACET_GROUP_NAME, StringUtils.trimToEmpty(facetGroup.getName()));
			inputs.put(DAOConstants.PARAM_FACET_GROUP_TYPE, facetGroup.getFacetGroupType().toString());
			inputs.put(DAOConstants.PARAM_SORT_TYPE, sortType==null ? sortType: sortType.toString());
			inputs.put(DAOConstants.PARAM_FACET_GROUP_SEQUENCE, facetGroup.getSequence());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, StringUtils.trimToEmpty(facetGroup.getCreatedBy()));

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
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, facetGroup.getId());
			return DAOUtils.getUpdateCount(deleteFacetGroupSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during deleteFacetGroup(): " + e.getMessage(), e);
		}
	}

	public int updateFacetGroup(FacetGroup facetGroup) throws DaoException {
		try {
			DAOValidation.checkFacetGroupPK(facetGroup);
			Map<String, Object> inputs = new HashMap<String, Object>();
		
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, facetGroup.getId());
			inputs.put(DAOConstants.PARAM_FACET_GROUP_SEQUENCE, facetGroup.getSequence()!=null ? facetGroup.getSequence(): "");
			inputs.put(DAOConstants.PARAM_SORT_TYPE, facetGroup.getSortType());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, facetGroup.getLastModifiedBy());
			
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

			inputs.put(DAOConstants.PARAM_MEMBER_ID, facetGroupItemId);
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, facetGroupItem.getFacetGroupId());
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ITEM_NAME, StringUtils.trimToEmpty(facetGroupItem.getName()));
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ITEM_SEQUENCE, facetGroupItem.getSequence());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, facetGroupItem.getCreatedBy());

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
			inputs.put(DAOConstants.PARAM_MEMBER_ID, facetGroupItem.getId());
			return DAOUtils.getUpdateCount(deleteFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during deleteFacetGroupItem(): " + e.getMessage(), e);
		}
	}

	public int updateFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException {
		try {
			DAOValidation.checkFacetGroupItemPK(facetGroupItem);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_MEMBER_ID, facetGroupItem.getId());
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ITEM_NAME, facetGroupItem.getId());
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ITEM_SEQUENCE, facetGroupItem.getId());
			inputs.put(DAOConstants.PARAM_MODIFIED_BY, facetGroupItem.getId());
			return DAOUtils.getUpdateCount(updateFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateFacetGroupItem(): " + e.getMessage(), e);
		}
	}	

	public int clearFacetGroupItem(FacetGroup facetGroup) throws DaoException {
		try {
			DAOValidation.checkFacetGroupPK(facetGroup);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_FACET_GROUP_ID, facetGroup.getId());
			return DAOUtils.getUpdateCount(clearFacetGroupItemSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during clearFacetGroupItem(): " + e.getMessage(), e);
		}
	}	
}
