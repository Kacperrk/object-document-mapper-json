package org.example.util;

import org.example.annotations.JsonIgnore;
import org.example.annotations.JsonName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    public static boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    public static Class<?> getListElementType(Field field) {
        if (!(field.getGenericType() instanceof ParameterizedType parameterizedType)) {
            throw new RuntimeException("Lista bez typu generycznego: " + field.getName());
        }

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length != 1) {
            throw new RuntimeException("Niepoprawny typ generyczny listy: " + field.getName());
        }

        Type elementType = actualTypeArguments[0];

        if (elementType instanceof Class<?> clazz) {
            return clazz;
        }

        if (elementType instanceof ParameterizedType nestedParameterizedType
                && nestedParameterizedType.getRawType() instanceof Class<?> rawType) {
            return rawType;
        }

        throw new RuntimeException("Nieobsługiwany typ elementu listy: " + field.getName());
    }
}
