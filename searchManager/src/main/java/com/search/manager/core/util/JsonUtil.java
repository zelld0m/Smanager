package com.search.manager.core.util;

import org.codehaus.jackson.map.ext.JodaDeserializers.DateTimeDeserializer;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

	public static String toJson(Object object) {
		// TODO Joda time format
		// Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeDeserializer()).setDateFormat("MM/dd/yyyy hh:mm:ss").create();
		Gson gson = new Gson();

		return gson.toJson(object);
	}

}
