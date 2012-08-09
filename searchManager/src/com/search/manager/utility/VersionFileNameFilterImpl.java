package com.search.manager.utility;

import java.io.File;
import java.io.FilenameFilter;

public class VersionFileNameFilterImpl implements FilenameFilter {

	private String prefix;
	
	public VersionFileNameFilterImpl(String prefix) {
		super();
		this.prefix = prefix;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.startsWith(prefix);
	}
}