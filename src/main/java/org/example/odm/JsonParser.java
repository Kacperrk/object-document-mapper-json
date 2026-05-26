package org.example.odm;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonParser {

    private final String json;
    private int index = 0;

    public JsonParser(String json) {
        this.json = json;
    }

    public Map<String, String> parseObject() {
        skipWhitespace();
        expect('{');

        Map<String, String> result = new LinkedHashMap<>();

        skipWhitespace();

        if (current() == '}') {
            index++;
            return result;
        }

        while (true) {
            skipWhitespace();
            String key = parseString();

            skipWhitespace();
            expect(':');

            skipWhitespace();
            String value = parseString();

            result.put(key, value);

            skipWhitespace();

            if (current() == '}') {
                index++;
                break;
            }

            expect(',');
        }

        skipWhitespace();

        if (index != json.length()) {
            throw new RuntimeException("Niepoprawny JSON.");
        }

        return result;
    }

    private String parseString() {
        expect('"');

        StringBuilder result = new StringBuilder();

        while (current() != '"') {
            result.append(current());
            index++;
        }

        expect('"');

        return result.toString();
    }

    private void expect(char expected) {
        skipWhitespace();

        if (current() != expected) {
            throw new RuntimeException("Oczekiwano znaku: " + expected);
        }

        index++;
    }

    private char current() {
        if (index >= json.length()) {
            throw new RuntimeException("Nieoczekiwany koniec JSON.");
        }

        return json.charAt(index);
    }

    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }
}
