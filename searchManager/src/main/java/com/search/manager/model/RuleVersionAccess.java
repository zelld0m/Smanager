package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RuleVersionAccess {
	private String currentOwner;
	private boolean locked;
	
	private RuleVersionAccess(String currentOwner, boolean locked) {
		super();
		this.currentOwner = currentOwner;
		this.locked = locked;
	}

	public String getCurrentOwner() {
		return currentOwner;
	}
	
	public void setCurrentOwner(String currentOwner) {
		this.currentOwner = currentOwner;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}