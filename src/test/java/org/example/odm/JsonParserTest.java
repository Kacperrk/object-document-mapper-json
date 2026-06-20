package org.example.odm;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void shouldParseSimpleObject() {
        String json = "{\"name\":\"Anna\",\"age\":21}";

        Map<String, JsonValue> result = new JsonParser(json).parseObject();

        assertEquals("Anna", result.get("name").asString());
        assertEquals(21, result.get("age").asInteger());
    }

    @Test
    void shouldThrowExceptionForInvalidJson() {
        String invalidJson = "{\"name\":";

        assertThrows(
                RuntimeException.class,
                () -> new JsonParser(invalidJson).parseObject()
        );
    }
}
