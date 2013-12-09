package com.search.manager.workflow.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.workflow.constant.WorkflowConstants;
import com.search.manager.workflow.dao.ImportRuleTaskDAO;


public class ImportRuleTaskDAOImpl 
	implements ImportRuleTaskDAO{

	private GetImportRuleTaskStoredProcedure getImportRuleTaskStoredProcedure;
	private AddImportRuleTaskStoredProcedure addImportRuleTaskStoredProcedure;
	private UpdateImportRuleTaskStoredProcedure updateImportRuleTaskStoredProcedure;
	
	// needed by spring AOP
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
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class AddImportRuleTaskStoredProcedure extends CUDStoredProcedure {
		public AddImportRuleTaskStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, WorkflowConstants.SP_ADD_IMPORT_RULE_TASK);
		}

		@Override
		protected void declareParameters() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class UpdateImportRuleTaskStoredProcedure extends CUDStoredProcedure {
		public UpdateImportRuleTaskStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, WorkflowConstants.SP_UPDATE_IMPORT_RULE_TASK);
		}

		@Override
		protected void declareParameters() {
			// TODO Auto-generated method stub
			
		}
	}
}
