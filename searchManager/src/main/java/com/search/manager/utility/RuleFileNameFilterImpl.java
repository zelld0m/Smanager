package com.search.manager.utility;

import java.io.File;
import java.io.FilenameFilter;

public class RuleFileNameFilterImpl implements FilenameFilter {

	private String ruleName;
	
	public RuleFileNameFilterImpl(String ruleName) {
		super();
		this.ruleName = ruleName;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.equalsIgnoreCase(ruleName + ".xml");
	}
}