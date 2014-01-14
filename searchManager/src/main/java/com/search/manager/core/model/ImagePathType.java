package com.search.manager.core.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter = EnumConverter.class)
public enum ImagePathType {
	IMAGE_LINK("IMAGE_LINK"), UPLOAD_LINK("IMAGE_UPLOAD");

	private final String displayText;
	private static final Map<String, ImagePathType> lookup = new HashMap<String, ImagePathType>();

	ImagePathType(String displayText) {
		this.displayText = displayText;
	}

	static {
		for (ImagePathType iType : EnumSet.allOf(ImagePathType.class))
			lookup.put(StringUtils.lowerCase(iType.getDisplayText()), iType);
	}

	public static ImagePathType get(String displayText) {
		return lookup.get(StringUtils.lowerCase(displayText));
	}

	public String getDisplayText() {
		return displayText;
	}
}