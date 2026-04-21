package org.example.odm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonOdmMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(Object object) {
        if (object == null) {
            throw new RuntimeException("Nie można serializować null.");
        }

        StringBuilder json = new StringBuilder();
        json.append("{");

        List<Field> fields = ReflectionUtils.getSerializableFields(object.getClass());

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            field.setAccessible(true);

            try {
                Object value = field.get(object);

                json.append("\"")
                        .append(field.getName())
                        .append("\":")
                        .append(formatValue(value, field.getType()));

                if (i < fields.size() - 1) {
                    json.append(",");
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Brak dostępu do pola: " + field.getName(), e);
            }
        }

        json.append("}");
        return json.toString();
    }

    public void writeToFile(Object object, Path path) {
        try {
            Files.writeString(path, toJson(object));
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się zapisać pliku: " + path, e);
        }
    }

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

                JsonNode valueNode = root.get(field.getName());
                if (valueNode == null || valueNode.isNull()) {
                    continue;
                }

                Object value = readValue(valueNode, field.getType());
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się utworzyć obiektu: " + clazz.getName(), e);
        }
    }

    private String formatValue(Object value, Class<?> type) {
        if (value == null) {
            return "null";
        }

        if (type == String.class) {
            return "\"" + ReflectionUtils.escapeJson((String) value) + "\"";
        }

        if (type == int.class || type == Integer.class) {
            return String.valueOf(value);
        }

        throw new RuntimeException("Pole nieobsługiwane.");
    }

    private Object readValue(JsonNode node, Class<?> type) {
        if (type == String.class) {
            return node.asText();
        }

        if (type == int.class || type == Integer.class) {
            return node.asInt();
        }

        throw new RuntimeException("Pole nieobsługiwane.");
    }
}
