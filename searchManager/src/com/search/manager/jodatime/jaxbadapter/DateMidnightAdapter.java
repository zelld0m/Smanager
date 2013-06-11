package com.search.manager.jodatime.jaxbadapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;

public class DateMidnightAdapter extends XmlAdapter<String, DateMidnight> {
	public DateMidnight unmarshal(String v) throws Exception {
		if(StringUtils.isNotBlank(v)){
			return new DateMidnight(v);
		}
		
		return null;
	}

	public String marshal(DateMidnight v) throws Exception {
		return ObjectUtils.toString(v);
	}
}