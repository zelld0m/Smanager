package com.search.manager.jodatime;

import org.directwebremoting.ConversionException;
import org.directwebremoting.extend.Converter;
import org.directwebremoting.extend.ConverterManager;
import org.directwebremoting.extend.InboundVariable;
import org.directwebremoting.extend.NonNestedOutboundVariable;
import org.directwebremoting.extend.OutboundContext;
import org.directwebremoting.extend.OutboundVariable;
import org.directwebremoting.extend.ProtocolConstants;
import org.joda.time.DateTime;

public class DateTimeConverter implements Converter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.directwebremoting.extend.Converter#convertInbound(java.lang.Class,
	 *      org.directwebremoting.extend.InboundVariable)
	 */
	@Override
	public Object convertInbound(Class<?> paramType, InboundVariable data) throws ConversionException {

		DateTime ret = null;

		final String value = data.getValue();
		if (!value.trim().equals(ProtocolConstants.INBOUND_NULL)) {

			long millis = 0;
			if (value.length() > 0) {
				millis = Long.parseLong(value);
			}

			if (DateTime.class.equals(paramType)) {
				ret = new DateTime(millis);
			} else {
				throw new ConversionException(paramType);
			}
		}

		return ret;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.directwebremoting.extend.Converter#convertOutbound(java.lang.Object,
	 *      org.directwebremoting.extend.OutboundContext)
	 */
	@Override
	public OutboundVariable convertOutbound(Object data, OutboundContext outctx) throws ConversionException {

		final long millis;
		if (data instanceof DateTime) {
			millis = ((DateTime) data).getMillis();
		} else {
			throw new ConversionException(data.getClass());
		}

		return new NonNestedOutboundVariable(new StringBuilder("new Date(").append(millis).append(")").toString());
	}

	@Override
	public void setConverterManager(ConverterManager config) {
	
	}
}