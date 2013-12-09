package com.search.manager.workflow.constant;

public class WorkflowConstants {
	
	private WorkflowConstants() {}
	
	// IMPORT RULE TASK
    public static final String SP_ADD_IMPORT_RULE_TASK = "usp_Add_Import_Rule_Task";
    public static final String SP_UPDATE_IMPORT_RULE_TASK= "usp_Update_Import_Rule_Task";
    public static final String SP_GET_IMPORT_RULE_TASK = "usp_Get_Import_Rule_Task";
	
	public static final String COLUMN_TASK_ID = "TASK_ID";
	public static final String COLUMN_RULE_TYPE_ID = "RULE_TYPE_ID";
	public static final String COLUMN_SOURCE_STORE_ID = "SOURCE_STORE_ID";
	public static final String COLUMN_SOURCE_RULE_ID = "SOURCE_RULE_ID";
	public static final String COLUMN_SOURCE_RULE_NAME = "SOURCE_RULE_NAME";
	public static final String COLUMN_TARGET_STORE_ID = "TARGET_STORE_ID";
	public static final String COLUMN_TARGET_RULE_ID = "TARGET_RULE_ID";
	public static final String COLUMN_TARGET_RULE_NAME = "TARGET_RULE_NAME";
	public static final String COLUMN_IMPORT_TYPE = "IMPORT_TYPE";
	public static final String COLUMN_TASK_STATUS = "TASK_STATUS";
	public static final String COLUMN_TASK_ERROR_MESSAGE = "TASK_ERROR_MESSAGE";
	public static final String COLUMN_TASK_START_STAMP = "TASK_START_STAMP";
	public static final String COLUMN_TASK_END_STAMP = "TASK_END_STAMP";
	public static final String COLUMN_CREATED_BY = "CREATED_BY";
	public static final String COLUMN_CREATED_STAMP = "CREATED_STAMP";
	public static final String COLUMN_CREATED_TX_STAMP = "CREATED_TX_STAMP";
	public static final String COLUMN_LAST_UPDATED_BY = "LAST_UPDATED_BY";
	public static final String COLUMN_LAST_UPDATED_STAMP = "LAST_UPDATED_STAMP";
	public static final String COLUMN_LAST_UPDATED_TX_STAMP = "LAST_UPDATED_TX_STAMP";
}
