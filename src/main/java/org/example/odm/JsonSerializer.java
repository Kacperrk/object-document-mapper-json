package org.example.odm;

import org.example.annotations.JsonSkipNull;
import org.example.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class JsonSerializer {

    public String toJson(Object object) {
        if (object == null) {
            throw new RuntimeException("Nie można serializować null.");
        }

        return toJsonObject(object);
    }

    private String toJsonObject(Object object) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        List<Field> fields = ReflectionUtils.getSerializableFields(object.getClass());
        boolean first = true;

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(object);

                if (value == null && field.isAnnotationPresent(JsonSkipNull.class)) {
                    continue;
                }

                if (!first) {
                    json.append(",");
                }

                json.append("\"")
                        .append(ReflectionUtils.getJsonFieldName(field))
                        .append("\":")
                        .append(formatValue(value, field));

                first = false;

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Brak dostępu do pola: " + field.getName(), e);
            }
        }

        json.append("}");
        return json.toString();
    }

    private String formatValue(Object value, Field field) {
        Class<?> type = field.getType();

        if (type.isArray()) {
            throw new RuntimeException("Tablice nie są obsługiwane: " + field.getName());
        }

        return formatValue(value);
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String text) {
            return "\"" + ReflectionUtils.escapeJson(text) + "\"";
        }

        if (value instanceof Integer || value instanceof Boolean || value instanceof Double) {
            return String.valueOf(value);
        }

        if (value instanceof List<?> list) {
            return formatList(list);
        }

        return toJsonObject(value);
    }

    private String formatList(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < list.size(); i++) {
            json.append(formatValue(list.get(i)));

            if (i < list.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }
}
