package org.example.odm;

import org.example.util.ReflectionUtils;

// import org.example.annotations.JsonDefaultValue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonDeserializer {

    public <T> T fromFile(Path path, Class<T> clazz) {
        try {
            String json = Files.readString(path);
            Map<String, JsonValue> root = new JsonParser(json).parseObject();
            return fromMap(root, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się odczytać pliku: " + path, e);
        }
    }

    private <T> T fromMap(Map<String, JsonValue> root, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            for (Field field : ReflectionUtils.getSerializableFields(clazz)) {
                field.setAccessible(true);

                JsonValue value = root.get(ReflectionUtils.getJsonFieldName(field));

                if (!root.containsKey(ReflectionUtils.getJsonFieldName(field))) {
                    // TODO: Przywrócić po rozszerzeniu własnego parsera JSON.
                    //
                    // if (field.isAnnotationPresent(JsonDefaultValue.class)) {
                    //     Object defaultValue = readDefaultValue(field);
                    //     field.set(instance, defaultValue);
                    // }

                    continue;
                }

                if (value.isNull()) {
                    if (field.getType().isPrimitive()) {
                        throw new RuntimeException("Nie można przypisać null do typu prostego: " + field.getName());
                    }

                    field.set(instance, null);
                    continue;
                }

                field.set(instance, readSimpleValue(value, field));
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się utworzyć obiektu: " + clazz.getName(), e);
        }
    }

    private Object readSimpleValue(JsonValue value, Field field) {
        return readValue(value, field.getGenericType(), field.getName());
    }

    private Object readValue(JsonValue value, Type targetType, String fieldName) {
        if (targetType instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() instanceof Class<?> rawType
                && List.class.isAssignableFrom(rawType)) {
            return readList(value, parameterizedType, fieldName);
        }

        if (!(targetType instanceof Class<?> type)) {
            throw new RuntimeException("Nieobsługiwany typ pola: " + fieldName);
        }

        if (type == String.class) {
            if (value.getType() != JsonValue.Type.STRING) {
                throw new RuntimeException("Oczekiwano String dla pola: " + fieldName);
            }

            return value.asString();
        }

        if (type == int.class || type == Integer.class) {
            if (value.getType() != JsonValue.Type.INTEGER) {
                throw new RuntimeException("Oczekiwano int dla pola: " + fieldName);
            }

            return value.asInteger();
        }

        if (type == double.class || type == Double.class) {
            if (value.getType() != JsonValue.Type.INTEGER && value.getType() != JsonValue.Type.DOUBLE) {
                throw new RuntimeException("Oczekiwano double dla pola: " + fieldName);
            }

            return value.asDouble();
        }

        if (type == boolean.class || type == Boolean.class) {
            if (value.getType() != JsonValue.Type.BOOLEAN) {
                throw new RuntimeException("Oczekiwano boolean dla pola: " + fieldName);
            }

            return value.asBoolean();
        }

        if (value.getType() == JsonValue.Type.OBJECT) {
            return fromMap(value.asObject(), type);
        }

        throw new RuntimeException("Nieobsługiwany typ pola: " + fieldName);
    }

    private List<Object> readList(JsonValue value, ParameterizedType listType, String fieldName) {
        if (value.getType() != JsonValue.Type.ARRAY) {
            throw new RuntimeException("Oczekiwano listy dla pola: " + fieldName);
        }

        Type elementType = listType.getActualTypeArguments()[0];
        List<Object> result = new ArrayList<>();

        for (JsonValue element : value.asArray()) {
            if (element.isNull()) {
                result.add(null);
            } else {
                result.add(readValue(element, elementType, fieldName));
            }
        }

        return result;
    }

    // TODO: Przywrócić po rozszerzeniu własnego parsera JSON.
    //
    // private Object readDefaultValue(Field field) {
    //     String value = field.getAnnotation(JsonDefaultValue.class).value();
    //     Class<?> type = field.getType();
    //
    //     if (type == String.class) {
    //         return value;
    //     }
    //
    //     if (type == int.class || type == Integer.class) {
    //         return Integer.parseInt(value);
    //     }
    //
    //     if (type == boolean.class || type == Boolean.class) {
    //         return Boolean.parseBoolean(value);
    //     }
    //
    //     if (type == double.class || type == Double.class) {
    //         return Double.parseDouble(value);
    //     }
    //
    //     throw new RuntimeException("Nieobsługiwany typ dla @JsonDefaultValue: " + field.getName());
    // }
}
