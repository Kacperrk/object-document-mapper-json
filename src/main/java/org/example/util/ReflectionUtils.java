package org.example.util;

import org.example.annotations.JsonIgnore;
import org.example.annotations.JsonName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<Field> getSerializableFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
                continue;
            }

            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }

            fields.add(field);
        }

        return fields;
    }

    public static String getJsonFieldName(Field field) {
        JsonName annotation = field.getAnnotation(JsonName.class);

        if (annotation != null) {
            return annotation.value();
        }

        return field.getName();
    }

    public static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
