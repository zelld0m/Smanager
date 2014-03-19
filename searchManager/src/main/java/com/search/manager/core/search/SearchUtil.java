package com.search.manager.core.search;

import java.util.Collection;

public class SearchUtil {

    public static String paramDisplayString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Collection) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getClass().getSimpleName());
            sb.append(" {");
            boolean first = true;

            for (Object o : (Collection<?>) value) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(paramDisplayString(o));
            }
            sb.append("}");

            return sb.toString();
        } else if (value instanceof Object[]) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getClass().getComponentType().getSimpleName());
            sb.append("[] {");
            boolean first = true;

            for (Object o : (Object[]) value) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(paramDisplayString(o));
            }

            sb.append("}");
            return sb.toString();
        } else {
            return value.toString();
        }
    }

}
