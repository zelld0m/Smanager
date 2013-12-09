package com.search.manager.workflow.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(
		converter=EnumConverter.class, 
		type="enum")
public enum TaskStatus {
	QUEUED("Queued", "Queued"),
	IN_PROCESS("In Process", "In Process"),
	FAILED("Failed", "Failed"),
	COMPLETED("Completed", "Completed"),
	CANCELED("Canceled", "Canceled");
	
	private final String name;
	private final String displayText;
	
	private static final Map<String,TaskStatus> lookup = new HashMap<String,TaskStatus>();

	TaskStatus(String name, String displayText){
		this.name = name;
		this.displayText = displayText;
	}

	static {
		for(TaskStatus s : EnumSet.allOf(TaskStatus.class))
			lookup.put(s.getDisplayText(), s);
	}

	public String getDisplayText() {
		return displayText;
	}

	public static TaskStatus getByDisplayText(String displayText) { 
		return lookup.get(displayText); 
	}
	
	public String getName() {
		return name;
	}

	public static TaskStatus get(Integer intValue) { 
        return TaskStatus.values()[intValue-1];
    }
	
	@Override
	public String toString() {
		return String.valueOf(ordinal()+1);
	}
}