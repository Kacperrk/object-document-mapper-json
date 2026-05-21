package org.example.odm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.annotations.JsonDefaultValue;
import org.example.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonDeserializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T fromFile(Path path, Class<T> clazz) {
        try {
            JsonNode root = objectMapper.readTree(path.toFile());
            return fromJsonNode(root, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się odczytać pliku: " + path, e);
        }
    }

    private <T> T fromJsonNode(JsonNode root, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            List<Field> fields = ReflectionUtils.getSerializableFields(clazz);

            for (Field field : fields) {
                field.setAccessible(true);

                JsonNode valueNode = root.get(ReflectionUtils.getJsonFieldName(field));

                if (valueNode == null || valueNode.isNull()) {
                    if (field.isAnnotationPresent(JsonDefaultValue.class)) {
                        Object defaultValue = readDefaultValue(field);
                        field.set(instance, defaultValue);
                    }
                    continue;
                }

                Object value = readValue(valueNode, field);
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się utworzyć obiektu: " + clazz.getName(), e);
        }
    }

    private Object readValue(JsonNode node, Field field) {
        Class<?> type = field.getType();

        if (type.isArray()) {
            throw new RuntimeException("Tablice nie są obsługiwane: " + field.getName());
        }

        if (type == String.class) {
            return node.asText();
        }

        if (type == int.class || type == Integer.class) {
            return node.asInt();
        }

        if (type == boolean.class || type == Boolean.class) {
            return node.asBoolean();
        }

        if (type == double.class || type == Double.class) {
            return node.asDouble();
        }

        if (ReflectionUtils.isList(field)) {
            return readStringList(node, field);
        }

        return fromJsonNode(node, type);
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
            return Boolean.parseBoolean(value);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }

        throw new RuntimeException("Nieobsługiwany typ dla @JsonDefaultValue: " + field.getName());
    }

    private List<String> readStringList(JsonNode node, Field field) {
        if (!node.isArray()) {
            throw new RuntimeException("Oczekiwano listy dla pola: " + field.getName());
        }

        Class<?> elementType = ReflectionUtils.getListElementType(field);

        if (elementType != String.class) {
            throw new RuntimeException("Obsługiwane są tylko listy typu List<String>: " + field.getName());
        }

        List<String> result = new ArrayList<>();

        for (JsonNode elementNode : node) {
            if (elementNode.isNull()) {
                result.add(null);
            } else {
                result.add(elementNode.asText());
            }
        }

        return result;
    }
}
