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
        StringBuilder objectJson = new StringBuilder();
        objectJson.append("{");

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
                    objectJson.append(",");
                }

                objectJson.append("\"")
                        .append(ReflectionUtils.getJsonFieldName(field))
                        .append("\":")
                        .append(serializeFieldValue(value, field));

                first = false;

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Brak dostępu do pola: " + field.getName(), e);
            }
        }

        objectJson.append("}");
        return objectJson.toString();
    }

    private String serializeFieldValue(Object value, Field field) {
        Class<?> type = field.getType();

        if (type.isArray()) {
            throw new RuntimeException("Tablice nie są obsługiwane: " + field.getName());
        }

        return serializeValue(value);
    }

    private String serializeValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String text) {
            return "\"" + escapeJson(text) + "\"";
        }

        if (value instanceof Integer || value instanceof Boolean || value instanceof Double) {
            return String.valueOf(value);
        }

        if (value instanceof List<?> list) {
            return serializeList(list);
        }

        return toJsonObject(value);
    }

    private String serializeList(List<?> list) {
        StringBuilder arrayJson = new StringBuilder();
        arrayJson.append("[");

        for (int i = 0; i < list.size(); i++) {
            arrayJson.append(serializeValue(list.get(i)));

            if (i < list.size() - 1) {
                arrayJson.append(",");
            }
        }

        arrayJson.append("]");
        return arrayJson.toString();
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
