package com.search.manager.model;

import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RecordSet<T> {
	
	private List<T> list;
	private int totalSize;
	
	public RecordSet(List<T> list, int totalSize) {
		this.list = list;
		this.totalSize = totalSize;
	}
	
	public List<T> getList() {
		return list;
	}

	public int getTotalSize() {
		return totalSize;
	}
}
