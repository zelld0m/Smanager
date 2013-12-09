package com.search.manager.workflow.model;

import org.joda.time.DateTime;

public class TaskExecutionResult {
	private TaskStatus taskStatus;
	
	private String taskErrorMessage;
	
	private DateTime taskStartDateTime;
	
	private DateTime taskEndDateTime;

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