package com.search.manager.workflow.model;

import org.joda.time.DateTime;

import com.search.manager.enums.ImportType;

public class TaskExecutionResult {
	private TaskStatus taskStatus;
	
	private String taskErrorMessage;
	
	private int runAttempt;
	
	private ImportType stateCompleted;
	
	private DateTime taskStartDateTime;
	
	private DateTime taskEndDateTime;

	public TaskExecutionResult(TaskStatus taskStatus, String taskErrorMessage, int runAttempt, ImportType stateCompleted, DateTime taskStartDateTime, DateTime taslEmdDateTime) {
		this.taskStatus = taskStatus;
		this.taskErrorMessage = taskErrorMessage;
		this.runAttempt = runAttempt;
		this.stateCompleted = stateCompleted;
		this.taskStartDateTime = taskStartDateTime;
		this.taskEndDateTime = taslEmdDateTime;
	}
	
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getTaskErrorMessage() {
		return taskErrorMessage;
	}

	public void setTaskErrorMessage(String taskErrorMessage) {
		this.taskErrorMessage = taskErrorMessage;
	}

	public ImportType getStateCompleted() {
		return stateCompleted;
	}

	public int getRunAttempt() {
		return runAttempt;
	}

	public void setRunAttempt(int runAttempt) {
		this.runAttempt = runAttempt;
	}

	public void setStateCompleted(ImportType stateCompleted) {
		this.stateCompleted = stateCompleted;
	}

	public DateTime getTaskStartDateTime() {
		return taskStartDateTime;
	}

	public void setTaskStartDateTime(DateTime taskStartDateTime) {
		this.taskStartDateTime = taskStartDateTime;
	}

	public DateTime getTaskEndDateTime() {
		return taskEndDateTime;
	}

	public void setTaskEndDateTime(DateTime taskEndDateTime) {
		this.taskEndDateTime = taskEndDateTime;
	}
}