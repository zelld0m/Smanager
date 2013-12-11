package com.search.manager.workflow.dao.impl;

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

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.dao.sp.RuleStatusDAO.SortOrder;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.workflow.constant.WorkflowConstants;
import com.search.manager.workflow.dao.ImportRuleTaskDAO;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.model.TaskExecutionResult;
import com.search.manager.workflow.model.TaskStatus;

@Repository(value="importRuleTaskDAO")
public class ImportRuleTaskDAOImpl 
	implements ImportRuleTaskDAO{

	private GetImportRuleTaskStoredProcedure getImportRuleTaskStoredProcedure;
	private AddImportRuleTaskStoredProcedure addImportRuleTaskStoredProcedure;
	private UpdateImportRuleTaskStoredProcedure updateImportRuleTaskStoredProcedure;
	
	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;
	
	// needed by spring AOP
	public ImportRuleTaskDAOImpl(){}
	
	@Autowired
	public ImportRuleTaskDAOImpl(JdbcTemplate jdbcTemplate) {
		addImportRuleTaskStoredProcedure = new AddImportRuleTaskStoredProcedure(jdbcTemplate);
		getImportRuleTaskStoredProcedure = new GetImportRuleTaskStoredProcedure(jdbcTemplate);
		updateImportRuleTaskStoredProcedure = new UpdateImportRuleTaskStoredProcedure(jdbcTemplate);
	}
	
	public ImportRuleTask getImportRuleTask(ImportRuleTask importRuleTask) throws DaoException {
		ImportRuleTask result = null;
		
		RecordSet<ImportRuleTask> rSet = getImportRuleTask(new SearchCriteria<ImportRuleTask>(importRuleTask, null, null, 1, 1), SortOrder.DESCRIPTION_ASCENDING);
		
		if (rSet.getList().size() > 0) {
			result = rSet.getList().get(0);
		}
		
		return result;
	}
		
	public RecordSet<ImportRuleTask> getImportRuleTask(SearchCriteria<ImportRuleTask> searchCriteria, SortOrder sortOrder) throws DaoException {
		
		try{
			ImportRuleTask importRuleTask = searchCriteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(WorkflowConstants.COLUMN_RULE_TYPE_ID, importRuleTask.getRuleEntity().ordinal());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, importRuleTask.getSourceStoreId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, importRuleTask.getSourceRuleName());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, importRuleTask.getTargetStoreId());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_NAME, importRuleTask.getTargetRuleName());
			inputs.put(WorkflowConstants.COLUMN_IMPORT_TYPE, importRuleTask.getImportType());
			inputs.put(WorkflowConstants.COLUMN_TASK_STATUS, importRuleTask.getTaskExecutionResult().getTaskStatus());
			inputs.put(WorkflowConstants.COLUMN_TASK_START_STAMP, jodaDateTimeUtil.toSqlDate(importRuleTask.getTaskExecutionResult().getTaskStartDateTime()));
			inputs.put(WorkflowConstants.COLUMN_TASK_END_STAMP, jodaDateTimeUtil.toSqlDate(importRuleTask.getTaskExecutionResult().getTaskEndDateTime()));
			inputs.put(WorkflowConstants.COLUMN_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(importRuleTask.getCreatedDate()));
			inputs.put(WorkflowConstants.PARAM_START_ROW, searchCriteria.getStartRow());
			inputs.put(WorkflowConstants.PARAM_END_ROW, searchCriteria.getEndRow());
			inputs.put(DAOConstants.PARAM_SORT_BY, sortOrder != null ? sortOrder.getIntValue() : 0);
			
			return DAOUtils.getRecordSet(getImportRuleTaskStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getImportRuleTask()", e);
		}
		
	}
	
	public int updateImportRuleTask(ImportRuleTask importRuleTask) throws DaoException {
		int result = -1;
		
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(WorkflowConstants.COLUMN_TASK_ID, importRuleTask.getTaskId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, importRuleTask.getSourceRuleName());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_NAME, importRuleTask.getTargetRuleName());
			
			TaskExecutionResult taskExecutionResult = importRuleTask.getTaskExecutionResult();
			
			inputs.put(WorkflowConstants.COLUMN_TASK_STATUS, taskExecutionResult.getTaskStatus());
			inputs.put(WorkflowConstants.COLUMN_TASK_ERROR_MESSAGE, taskExecutionResult.getTaskErrorMessage());
			inputs.put(WorkflowConstants.COLUMN_TASK_START_STAMP, jodaDateTimeUtil.toSqlDate(taskExecutionResult.getTaskStartDateTime()));
			inputs.put(WorkflowConstants.COLUMN_TASK_END_STAMP, jodaDateTimeUtil.toSqlDate(taskExecutionResult.getTaskEndDateTime()));
			inputs.put(WorkflowConstants.COLUMN_LAST_UPDATED_BY, importRuleTask.getLastModifiedBy());
			inputs.put(WorkflowConstants.COLUMN_LAST_UPDATED_STAMP, jodaDateTimeUtil.toSqlDate(importRuleTask.getLastModifiedDate()));
			
			result = DAOUtils.getUpdateCount(updateImportRuleTaskStoredProcedure.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateImportRuleTask()", e);
		}
		
		return result;
	}
	
	public ImportRuleTask addImportRuleTask(ImportRuleTask importRuleTask) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			
			inputs.put(WorkflowConstants.COLUMN_TASK_ID, DAOUtils.generateUniqueId());
			inputs.put(WorkflowConstants.COLUMN_RULE_TYPE_ID, importRuleTask.getRuleEntity().getCode());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, importRuleTask.getSourceStoreId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_ID, importRuleTask.getSourceRuleId());
			inputs.put(WorkflowConstants.COLUMN_SOURCE_RULE_NAME, importRuleTask.getSourceRuleName());
			
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, importRuleTask.getTargetStoreId());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_ID, importRuleTask.getTargetRuleId());
			inputs.put(WorkflowConstants.COLUMN_TARGET_RULE_NAME, importRuleTask.getTargetRuleName());
			inputs.put(WorkflowConstants.COLUMN_IMPORT_TYPE, importRuleTask.getImportType().ordinal());
			inputs.put(WorkflowConstants.COLUMN_CREATED_BY, importRuleTask.getCreatedBy());
			inputs.put(WorkflowConstants.COLUMN_CREATED_STAMP, jodaDateTimeUtil.toSqlDate(importRuleTask.getCreatedDate()));
			
			RecordSet<ImportRuleTask> rSet = DAOUtils.getRecordSet(addImportRuleTaskStoredProcedure.execute(inputs));
			
			ImportRuleTask result = rSet.getList().size() > 0 ? rSet.getList().get(0) : null;
			
			return result;
		} catch (Exception e) {
			throw new DaoException("Failed during addImportRuleTask()", e);
		}
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
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_STORE_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TARGET_RULE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_IMPORT_TYPE, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_STATUS, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_START_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_END_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_CREATED_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.PARAM_START_ROW, Types.INTEGER));
			declareParameter(new SqlParameter(WorkflowConstants.PARAM_END_ROW, Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<ImportRuleTask>() {
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
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_CREATED_BY, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_CREATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_2, new RowMapper<ImportRuleTask>() {
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
					ruleEntity = RuleEntity.find(ruleTypeVal);
				} catch (Exception e) {
				}
			}
			
			return new ImportRuleTask(rs.getString(WorkflowConstants.COLUMN_TASK_ID), 
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
							jodaDateTimeUtil.toDateTime(rs.getTimestamp(WorkflowConstants.COLUMN_TASK_START_STAMP)), 
							jodaDateTimeUtil.toDateTime(rs.getTimestamp(WorkflowConstants.COLUMN_TASK_END_STAMP))));
		
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
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_START_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_TASK_END_STAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_LAST_UPDATED_BY, Types.VARCHAR));
			declareParameter(new SqlParameter(WorkflowConstants.COLUMN_LAST_UPDATED_STAMP, Types.TIMESTAMP));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			// TODO Auto-generated method stub
			
		}
	}
}
