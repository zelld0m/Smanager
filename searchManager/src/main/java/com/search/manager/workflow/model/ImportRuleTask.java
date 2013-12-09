package com.search.manager.workflow.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleType;
import com.search.manager.model.ModelBean;

@DataTransferObject(converter = BeanConverter.class)
public class ImportRuleTask extends ModelBean{
	
	private static final long serialVersionUID = 8380046988342666844L;

	private String taskId;
	
	private RuleType ruleType;
	
	private String sourceStoreId;
	
	private String sourceRuleId;
	
	private String sourceRuleName;
	
	private String targetStoreId;
	
	private String targetRuleId;
	
	private String targetRuleName;
	
	private ImportType importType;
	
	private TaskExecutionResult taskExecutionResult;
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}
	
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
	public String getSourceStoreId() {
		return sourceStoreId;
	}
	
	public void setSourceStoreId(String sourceStoreId) {
		this.sourceStoreId = sourceStoreId;
	}
	
	public String getSourceRuleId() {
		return sourceRuleId;
	}
	
	public void setSourceRuleId(String sourceRuleId) {
		this.sourceRuleId = sourceRuleId;
	}
	
	public String getSourceRuleName() {
		return sourceRuleName;
	}
	
	public void setSourceRuleName(String sourceRuleName) {
		this.sourceRuleName = sourceRuleName;
	}
	
	public String getTargetStoreId() {
		return targetStoreId;
	}
	
	public void setTargetStoreId(String targetStoreId) {
		this.targetStoreId = targetStoreId;
	}
	
	public String getTargetRuleId() {
		return targetRuleId;
	}
	
	public void setTargetRuleId(String targetRuleId) {
		this.targetRuleId = targetRuleId;
	}
	
	public String getTargetRuleName() {
		return targetRuleName;
	}
	
	public void setTargetRuleName(String targetRuleName) {
		this.targetRuleName = targetRuleName;
	}
	
	public ImportType getImportType() {
		return importType;
	}
	
	public void setImportType(ImportType importType) {
		this.importType = importType;
	}

	public TaskExecutionResult getTaskExecutionResult() {
		return taskExecutionResult;
	}

	public void setTaskExecutionResult(TaskExecutionResult taskExecutionResult) {
		this.taskExecutionResult = taskExecutionResult;
	}
}