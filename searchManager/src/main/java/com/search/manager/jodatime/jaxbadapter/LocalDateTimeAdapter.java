package com.search.manager.jodatime.jaxbadapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
	public LocalDateTime unmarshal(String v) throws Exception {
		if(StringUtils.isNotBlank(v)){
			return new LocalDateTime(v);
		}
		return null;
	}

	public String marshal(LocalDateTime v) throws Exception {
		return ObjectUtils.toString(v);
	}
}