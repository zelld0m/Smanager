package com.search.manager.core.util;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

	public static String toJson(Object object) {
		Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class,
				new DateTimeTypeConverter()).create();

		return gson.toJson(object);
	}

}
