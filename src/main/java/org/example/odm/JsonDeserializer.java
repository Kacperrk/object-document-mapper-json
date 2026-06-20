package org.example.odm;

import org.example.annotations.JsonDefaultValue;
import org.example.util.ReflectionUtils;

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

    public <T> T fromFile(Path path, Class<T> targetClass) {
        try {
            String jsonText = Files.readString(path);
            Map<String, JsonValue> root = new JsonParser(jsonText).parseObject();
            return mapToObject(root, targetClass);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się odczytać pliku: " + path, e);
        }
    }

    private <T> T mapToObject(Map<String, JsonValue> jsonObject, Class<T> targetClass) {
        try {
            Constructor<T> constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            for (Field field : ReflectionUtils.getSerializableFields(targetClass)) {
                field.setAccessible(true);

                String jsonKey = ReflectionUtils.getJsonFieldName(field);

                if (!jsonObject.containsKey(jsonKey)) {
                    if (field.isAnnotationPresent(JsonDefaultValue.class)) {
                        Object defaultValue = readDefaultValue(field);
                        field.set(instance, defaultValue);
                    }
                    continue;
                }

                JsonValue value = jsonObject.get(jsonKey);

                if (value.isNull()) {
                    if (field.getType().isPrimitive()) {
                        throw new RuntimeException("Nie można przypisać null do typu prostego: " + field.getName());
                    }

                    field.set(instance, null);
                    continue;
                }

                field.set(instance, readFieldValue(value, field));
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się utworzyć obiektu: " + targetClass.getName(), e);
        }
    }

    private Object readFieldValue(JsonValue value, Field field) {
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
            return mapToObject(value.asObject(), type);
        }

        throw new RuntimeException("Nieobsługiwany typ pola: " + fieldName);
    }

    private List<Object> readList(JsonValue value, ParameterizedType listType, String fieldName) {
        if (value.getType() != JsonValue.Type.ARRAY) {
            throw new RuntimeException("Oczekiwano listy dla pola: " + fieldName);
        }

        Type elementType = listType.getActualTypeArguments()[0];
        List<Object> listValues = new ArrayList<>();

        for (JsonValue element : value.asArray()) {
            if (element.isNull()) {
                listValues.add(null);
            } else {
                listValues.add(readValue(element, elementType, fieldName));
            }
        }

        return listValues;
    }

    private Object readDefaultValue(Field field) {
        String value = field.getAnnotation(JsonDefaultValue.class).value();
        Class<?> type = field.getType();

        if (type == String.class) {
            return value;
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }

        if (type == boolean.class || type == Boolean.class) {
            if (!value.equals("true") && !value.equals("false")) {
                throw new RuntimeException("Niepoprawna wartość boolean dla @JsonDefaultValue: " + field.getName());
            }

            return Boolean.parseBoolean(value);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }

        throw new RuntimeException("Nieobsługiwany typ dla @JsonDefaultValue: " + field.getName());
    }
}
