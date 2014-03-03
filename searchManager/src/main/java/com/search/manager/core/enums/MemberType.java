package com.search.manager.core.enums;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(
		converter=EnumConverter.class,
		type="enum")
public enum MemberType {

	MFR,
	PART_NUMBER;
}
