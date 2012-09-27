package com.search.manager.enums;

import java.util.Arrays;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum SortType {
	ASC_ALPHABETICALLY,
	DESC_ALPHABETICALLY,
	ASC_COUNT,
	DESC_COUNT;
	
	public List<SortType> getSortOrder(){
		return Arrays.asList(SortType.values());
	}
}
