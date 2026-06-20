package org.example.odm;

import org.example.model.Address;
import org.example.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonOdmMapperTest {

    @TempDir
    Path tempDir;

    private final JsonOdmMapper mapper = new JsonOdmMapper();

    private Person person;

    @BeforeEach
    void setUp() {
        Address address = new Address(
                "Warszawa",
                "Marszałkowska",
                10
        );

        person = new Person(
                "Jan",
                "Kowalski",
                25,
                true,
                3.5,
                address,
                List.of("Java", "SQL"),
                List.of(7, 14),
                List.of(true, false),
                List.of(4.0, 4.5),
                List.of(address),
                List.of(
                        List.of("Java", "Spring"),
                        List.of("Docker")
                ),
                Arrays.asList("A", null, "B"),
                List.of(),
                null,
                "Jan123",
                "haslo123",
                null,
                "Polska"
        );
    }

    private Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    @Test
    void shouldSerializePersonToJson() {
        String json = mapper.toJson(person);

        assertTrue(json.contains("\"name\":\"Jan\""));
        assertTrue(json.contains("\"age\":25"));
        assertTrue(json.contains("\"city\":\"Warszawa\""));
        // @JsonName pseudonim
        assertTrue(json.contains("\"pseudonim\":\"Jan123\""));
        // @JsonIgnore password
        assertFalse(json.contains("password"));
        // @JsonSkipNull email
        assertFalse(json.contains("email"));
    }

    @Test
    void shouldDeserializePersonFromJson() throws Exception {
        String json = mapper.toJson(person);

        Path jsonFile = tempDir.resolve("person.json");
        Files.writeString(jsonFile, json);

        Person result = mapper.fromFile(jsonFile, Person.class);

        assertEquals("Jan", getField(result, "name"));
        assertEquals(25, getField(result, "age"));
        assertTrue((Boolean) getField(result, "active"));

        Address resultAddress = (Address) getField(result, "address");
        assertEquals("Warszawa", getField(resultAddress, "city"));

        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) getField(result, "skills");
        assertEquals(Arrays.asList("Java", "SQL"), skills);
    }
}
