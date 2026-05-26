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

        if (value == null) {
            // TODO: Obecny parser nie obsługuje null.
            // return "null";

            throw new RuntimeException("Pierwsza wersja parsera nie obsługuje null: " + field.getName());
        }

        if (type == String.class) {
            return "\"" + ReflectionUtils.escapeJson((String) value) + "\"";
        }

        // TODO: Przywrócić po rozszerzeniu własnego parsera JSON.
        //
        // if (type == int.class || type == Integer.class ||
        //         type == boolean.class || type == Boolean.class ||
        //         type == double.class || type == Double.class) {
        //     return String.valueOf(value);
        // }
        //
        // if (ReflectionUtils.isList(field)) {
        //     return formatStringList((List<?>) value, field);
        // }
        //
        // return toJsonObject(value);

        throw new RuntimeException("Pierwsza wersja parsera obsługuje tylko String: " + field.getName());
    }

    // TODO: Przywrócić po rozszerzeniu własnego parsera JSON.
    //
    // private String formatStringList(List<?> list, Field field) {
    //     Class<?> elementType = ReflectionUtils.getListElementType(field);
    //
    //     if (elementType != String.class) {
    //         throw new RuntimeException("Obsługiwane są tylko listy typu List<String>: " + field.getName());
    //     }
    //
    //     StringBuilder json = new StringBuilder();
    //     json.append("[");
    //
    //     for (int i = 0; i < list.size(); i++) {
    //         Object element = list.get(i);
    //
    //         if (element == null) {
    //             json.append("null");
    //         } else if (element instanceof String text) {
    //             json.append("\"").append(ReflectionUtils.escapeJson(text)).append("\"");
    //         } else {
    //             throw new RuntimeException("Lista może zawierać tylko String: " + field.getName());
    //         }
    //
    //         if (i < list.size() - 1) {
    //             json.append(",");
    //         }
    //     }
    //
    //     json.append("]");
    //     return json.toString();
    // }
}
