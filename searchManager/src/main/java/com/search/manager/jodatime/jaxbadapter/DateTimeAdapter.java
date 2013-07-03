package com.search.manager.jodatime.jaxbadapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class DateTimeAdapter extends XmlAdapter<String, DateTime> {
	public DateTime unmarshal(String v) throws Exception {
		if(StringUtils.isNotBlank(v)){
			return new DateTime(v);
		}
		
		return null;
	}

	public String marshal(DateTime v) throws Exception {
		return ObjectUtils.toString(v);
	}
}