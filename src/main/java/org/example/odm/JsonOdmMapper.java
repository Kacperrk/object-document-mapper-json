package org.example.odm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonOdmMapper {

    private final JsonSerializer serializer = new JsonSerializer();
    private final JsonDeserializer deserializer = new JsonDeserializer();

    public String toJson(Object object) {
        return serializer.toJson(object);
    }

    public void writeToFile(Object object, Path path) {
        try {
            Files.writeString(path, toJson(object));
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się zapisać pliku: " + path, e);
        }
    }

    public <T> T fromFile(Path path, Class<T> targetClass) {
        return deserializer.fromFile(path, targetClass);
    }
}
