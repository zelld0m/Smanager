package com.search.manager.enums;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter=EnumConverter.class)
public enum MemberTypeEntity {
	PART_NUMBER,
	FACET
}
