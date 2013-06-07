package com.search.manager.jodatime.jaxbadapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
	public LocalDate unmarshal(String v) throws Exception {
		if(StringUtils.isNotBlank(v)){
			return new LocalDate(v);
		}
		return null;
	}

	public String marshal(LocalDate v) throws Exception {
		return ObjectUtils.toString(v);
	}
}