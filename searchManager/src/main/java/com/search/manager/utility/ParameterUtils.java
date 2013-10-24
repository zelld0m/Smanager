package com.search.manager.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterUtils {

    private static final Logger logger = LoggerFactory.getLogger(ParameterUtils.class);

    private ParameterUtils() {
        // Utility class pattern requires constructor to be private.
    }

    public static String getValueFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
        List<NameValuePair> list = paramMap.get(paramterName);
        return list == null || list.size() == 0 ? "" : list.get(0).getValue();
    }

    protected static List<String> getValuesFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap,
            String paramterName) {
        List<NameValuePair> list = paramMap.get(paramterName);
        List<String> values = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (NameValuePair nvp : list) {
                values.add(nvp.getValue());
            }
        }
        return values;
    }

    public static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap,
            String paramterName) {
        List<NameValuePair> list = paramMap.get(paramterName);
        return list == null || list.size() == 0 ? null : list.get(0);
    }

    public static NameValuePair getNameValuePairFromList(List<NameValuePair> paramList, String parameterName) {
        for (NameValuePair param : paramList) {
            if (param.getName().equals(parameterName)) {
                return param;
            }
        }

        return null;
    }

    public static boolean resetNameValuePairFromMapAndList(HashMap<String, List<NameValuePair>> paramMap,
            List<NameValuePair> nameValuePairs, String paramterName, String value, String[] uniqueFields) {
        nameValuePairs.remove(getNameValuePairFromMap(paramMap, paramterName));
        paramMap.remove(paramterName);
        BasicNameValuePair nvp = new BasicNameValuePair(paramterName, value);

        if (addNameValuePairToMap(paramMap, paramterName, nvp, uniqueFields)) {
            return nameValuePairs.add(nvp);
        }

        return false;
    }

    public static void removeNameValuePairFromMapAndList(HashMap<String, List<NameValuePair>> paramMap,
            List<NameValuePair> nameValuePairs, String paramterName) {
        nameValuePairs.remove(getNameValuePairFromMap(paramMap, paramterName));
        paramMap.remove(paramterName);
    }

    public static boolean addNameValuePairToMap(Map<String, List<NameValuePair>> map, String paramName,
            NameValuePair pair, String[] uniqueFields) {
        boolean added = true;
        if (ArrayUtils.contains(uniqueFields, paramName) && map.containsKey(paramName)) {
            logger.warn(
                    "Request contained multiple declarations for parameter {}. Discarding subsequent declarations.",
                    paramName);
            added = false;
        } else {
            if (!map.containsKey(paramName)) {
                map.put(paramName, new ArrayList<NameValuePair>());
            }
            map.get(paramName).add(pair);
        }
        return added;
    }

}
