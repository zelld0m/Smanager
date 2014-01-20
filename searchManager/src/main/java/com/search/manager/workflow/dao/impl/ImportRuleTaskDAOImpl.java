package com.search.manager.workflow.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.ImportRuleTaskDao;
import com.search.manager.core.dao.sp.GenericDaoSpImpl;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.util.IdGenerator;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.workflow.constant.WorkflowConstants;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.model.TaskExecutionResult;
import com.search.manager.workflow.model.TaskStatus;

@Repository(value="importRuleTaskDAO")
public class ImportRuleTaskDAOImpl 
	extends GenericDaoSpImpl<ImportRuleTask>
	implements ImportRuleTaskDao{

	private GetImportRuleTaskStoredProcedure getImportRuleTaskStoredProcedure;
	private AddImportRuleTaskStoredProcedure addImportRuleTaskStoredProcedure;
	private UpdateImportRuleTaskStoredProcedure updateImportRuleTaskStoredProcedure;
	
	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;
	
	public ImportRuleTaskDAOImpl(){}
	
	@Autowired
	public ImportRuleTaskDAOImpl(JdbcTemplate jdbcTemplate) {
		addImportRuleTaskStoredProcedure = new AddImportRuleTaskStoredProcedure(jdbcTemplate);
		getImportRuleTaskStoredProcedure = new GetImportRuleTaskStoredProcedure(jdbcTemplate);
		updateImportRuleTaskStoredProcedure = new UpdateImportRuleTaskStoredProcedure(jdbcTemplate);
	}
	
	private class GetImportRuleTaskStoredProcedure extends GetStoredProcedure {
		public GetImportRuleTaskStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, WorkflowConstants.SP_GET_IMPORT_RULE_TASK);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_RULE_TYPE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_IMPORT_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_STATUS, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_START_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_END_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_CREATED_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ImportRuleTask>() {
				public ImportRuleTask mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}

			}));
		}
	}
	
	private class AddImportRuleTaskStoredProcedure extends GetStoredProcedure {
		public AddImportRuleTaskStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, WorkflowConstants.SP_ADD_IMPORT_RULE_TASK);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_RULE_TYPE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_IMPORT_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_CREATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ImportRuleTask>() {
				public ImportRuleTask mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}
	
	private ImportRuleTask buildModel(ResultSet rs, int rowNum) throws SQLException {
			
			int taskStatusVal = rs.getInt(WorkflowConstants.COLUMN_TASK_STATUS);
			String importTypeVal = rs.getString(WorkflowConstants.COLUMN_IMPORT_TYPE);
			String ruleTypeVal = rs.getString(WorkflowConstants.COLUMN_RULE_TYPE_ID);
			
			TaskStatus taskStatus = null;
			ImportType importType = null;
			RuleEntity ruleEntity = null;
			
			try {
				taskStatus = TaskStatus.get(taskStatusVal);
			} catch (Exception e) {
			}
			
			if (StringUtils.isNumeric(importTypeVal)) {
				try {
					importType = ImportType.get(Integer.valueOf(importTypeVal));
				} catch (Exception e) {
				}
			}
			if (StringUtils.isNumeric(ruleTypeVal)) {
				try {
					ruleEntity = RuleEntity.get(Integer.valueOf(ruleTypeVal));
				} catch (Exception e) {
				}
			}
			
			ImportRuleTask result =  new ImportRuleTask(rs.getString(WorkflowConstants.COLUMN_TASK_ID), 
					ruleEntity, 
					rs.getString(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID), 
					rs.getString(WorkflowConstants.COLUMN_SOURCE_RULE_ID), 
					rs.getString(WorkflowConstants.COLUMN_SOURCE_RULE_NAME), 
					rs.getString(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID), 
					rs.getString(WorkflowConstants.COLUMN_TARGET_RULE_ID), 
					rs.getString(WorkflowConstants.COLUMN_TARGET_RULE_NAME), 
					importType, 
					new TaskExecutionResult(taskStatus, 
							rs.getString(WorkflowConstants.COLUMN_TASK_ERROR_MESSAGE),
							rs.getInt(WorkflowConstants.COLUMN_RUN_ATTEMPT),
							rs.getInt(WorkflowConstants.COLUMN_STATE_COMPLETED) != 0 ? ImportType.get(rs.getInt(WorkflowConstants.COLUMN_STATE_COMPLETED)) : null,
							jodaDateTimeUtil.toDateTime(rs.getTimestamp(WorkflowConstants.COLUMN_TASK_START_STAMP)), 
							jodaDateTimeUtil.toDateTime(rs.getTimestamp(WorkflowConstants.COLUMN_TASK_END_STAMP))));
			
			result.setCreatedBy(rs.getString(WorkflowConstants.COLUMN_CREATED_BY));
			result.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(WorkflowConstants.COLUMN_CREATED_STAMP)));
			result.setLastModifiedBy(rs.getString(WorkflowConstants.COLUMN_LAST_UPDATED_BY));
			result.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(WorkflowConstants.COLUMN_LAST_UPDATED_STAMP)));
			
			return result;
		
	}
	
	private class UpdateImportRuleTaskStoredProcedure extends GetStoredProcedure {
		public UpdateImportRuleTaskStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, WorkflowConstants.SP_UPDATE_IMPORT_RULE_TASK);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_STATUS, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_ERROR_MESSAGE, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_RUN_ATTEMPT, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_STATE_COMPLETED, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_START_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_END_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_LAST_UPDATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<ImportRuleTask>() {
				public ImportRuleTask mapRow(ResultSet rs, int rowNum) throws SQLException {
					return buildModel(rs, rowNum);
				}
			}));
		}
	}

	@Override
	protected StoredProcedure getAddStoredProcedure() throws CoreDaoException {
		return addImportRuleTaskStoredProcedure;
	}

	@Override
	protected StoredProcedure getUpdateStoredProcedure()
			throws CoreDaoException {
		return updateImportRuleTaskStoredProcedure;
	}

	@Override
	protected StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException {
		return null;
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return getImportRuleTaskStoredProcedure;
	}

	@Override
	protected Map<String, Object> generateAddInput(ImportRuleTask model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
			inputs.put(WorkflowConstants.COLUMN_TASK_ID, IdGenerator.generateUniqueId());
			inputs.put(WorkflowConstants.COLUMN_RULE_TYPE_ID, model.getRuleEntity().getCode());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, model.getSourceStoreId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_ID, model.getSourceRuleId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, model.getSourceRuleName());
			
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, model.getTargetStoreId());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_ID, model.getTargetRuleId());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_NAME, model.getTargetRuleName());
			inputs.put(WorkflowConstants.COLUMN_IMPORT_TYPE, model.getImportType().ordinal());
			inputs.put(WorkflowConstants.COLUMN_CREATED_BY, model.getCreatedBy());
			inputs.put(WorkflowConstants.COLUMN_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getCreatedDate()));
		}
		
		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(ImportRuleTask model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;
		
		if(model != null) {
			inputs = new HashMap<String, Object>();
			
			inputs.put(WorkflowConstants.COLUMN_TASK_ID, model.getTaskId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, model.getSourceRuleName());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_NAME, model.getTargetRuleName());
			
			TaskExecutionResult taskExecutionResult = model.getTaskExecutionResult();
			
			inputs.put(WorkflowConstants.COLUMN_TASK_STATUS, taskExecutionResult.getTaskStatus().ordinal() + 1);
			inputs.put(WorkflowConstants.COLUMN_TASK_ERROR_MESSAGE, taskExecutionResult.getTaskErrorMessage());
			inputs.put(WorkflowConstants.COLUMN_RUN_ATTEMPT, taskExecutionResult.getRunAttempt());
			inputs.put(WorkflowConstants.COLUMN_STATE_COMPLETED, taskExecutionResult.getStateCompleted() != null ? taskExecutionResult.getStateCompleted().ordinal() + 1 : 0);
			inputs.put(WorkflowConstants.COLUMN_TASK_START_STAMP, jodaDateTimeUtil.toSqlDate(taskExecutionResult.getTaskStartDateTime()));
			inputs.put(WorkflowConstants.COLUMN_TASK_END_STAMP, jodaDateTimeUtil.toSqlDate(taskExecutionResult.getTaskEndDateTime()));
			inputs.put(WorkflowConstants.COLUMN_LAST_UPDATED_BY, model.getLastModifiedBy());
			inputs.put(WorkflowConstants.COLUMN_LAST_UPDATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getLastModifiedDate()));
		}
		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(ImportRuleTask model)
			throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Search generateSearchInput(ImportRuleTask model)
			throws CoreDaoException {
		Search search = new Search(model.getClass());
		List<Filter> filters = new ArrayList<Filter>();
		if(model != null) {
			TaskExecutionResult taskExecutionResult = model.getTaskExecutionResult();
			
			filters.add(new Filter(WorkflowConstants.COLUMN_RULE_TYPE_ID, model.getRuleEntity() != null ? model.getRuleEntity().getCode() : null));
			filters.add(new Filter(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, model.getSourceStoreId()));
			filters.add(new Filter(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, model.getSourceRuleName()));
			filters.add(new Filter(WorkflowConstants.COLUMN_SOURCE_RULE_ID, model.getSourceRuleId()));
			filters.add(new Filter(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, model.getTargetStoreId()));
			filters.add(new Filter(WorkflowConstants.COLUMN_TARGET_RULE_NAME, model.getTargetRuleName()));
			filters.add(new Filter(WorkflowConstants.COLUMN_TARGET_RULE_ID, model.getTargetRuleId()));
			filters.add(new Filter(WorkflowConstants.COLUMN_IMPORT_TYPE, model.getImportType() != null ? model.getImportType().ordinal() + 1 : null));
			filters.add(new Filter(WorkflowConstants.COLUMN_TASK_STATUS, taskExecutionResult != null ? taskExecutionResult.getTaskStatus().ordinal() + 1 : null));
			filters.add(new Filter(WorkflowConstants.COLUMN_TASK_START_STAMP, taskExecutionResult != null ? jodaDateTimeUtil.toSqlDate(taskExecutionResult.getTaskStartDateTime()) : null));
			filters.add(new Filter(WorkflowConstants.COLUMN_TASK_END_STAMP, taskExecutionResult != null ? jodaDateTimeUtil.toSqlDate(taskExecutionResult.getTaskEndDateTime()) : null));
			filters.add(new Filter(WorkflowConstants.COLUMN_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(model.getCreatedDate())));

		}
		search.addFilters(filters);
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();

        inParam.put(WorkflowConstants.COLUMN_RULE_TYPE_ID, null);
        inParam.put(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, null);
        inParam.put(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, null);
        inParam.put(WorkflowConstants.COLUMN_SOURCE_RULE_ID, null);
        inParam.put(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, null);
        inParam.put(WorkflowConstants.COLUMN_TARGET_RULE_NAME, null);
        inParam.put(WorkflowConstants.COLUMN_TARGET_RULE_ID, null);
        inParam.put(WorkflowConstants.COLUMN_IMPORT_TYPE, null);
        inParam.put(WorkflowConstants.COLUMN_TASK_ID, null);
        inParam.put(WorkflowConstants.COLUMN_TASK_STATUS, null);
        inParam.put(WorkflowConstants.COLUMN_TASK_START_STAMP, null);
        inParam.put(WorkflowConstants.COLUMN_TASK_END_STAMP, null);
        inParam.put(WorkflowConstants.COLUMN_CREATED_STAMP, null);
        inParam.put(WorkflowConstants.PARAM_START_ROW, 0);
        inParam.put(WorkflowConstants.PARAM_END_ROW, 0);

        return inParam;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		ImportRuleTask importRuleTask = new ImportRuleTask();
		
		if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(storeId)) {
			importRuleTask.setTargetStoreId(storeId);
			importRuleTask.setTaskId(id);
		}
		
		return generateSearchInput(importRuleTask);
	}
}
