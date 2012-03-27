package com.search.manager.schema.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter = EnumConverter.class)
public enum GenericType {
	DATE, STRING, NUMERIC
}
