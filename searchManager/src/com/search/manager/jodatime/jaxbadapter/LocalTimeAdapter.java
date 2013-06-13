package com.search.manager.jodatime.jaxbadapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
	public LocalTime unmarshal(String v) throws Exception {
		if(StringUtils.isNotBlank(v)){
			return new LocalTime(v);
		}
		return null;
	}

	public String marshal(LocalTime v) throws Exception {
		return ObjectUtils.toString(v);
	}
}