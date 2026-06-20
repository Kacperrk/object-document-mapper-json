package org.example.odm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    private final String jsonText;
    private int index = 0;

    public JsonParser(String jsonText) {
        this.jsonText = jsonText;
    }

    public Map<String, JsonValue> parseObject() {
        Map<String, JsonValue> parsedObject = parseObjectValue();

        skipWhitespace();

        if (index != jsonText.length()) {
            throw new RuntimeException("Niepoprawny JSON.");
        }

        return parsedObject;
    }

    private Map<String, JsonValue> parseObjectValue() {
        skipWhitespace();
        expect('{');

        Map<String, JsonValue> objectFields = new LinkedHashMap<>();

        skipWhitespace();

        if (current() == '}') {
            index++;
            return objectFields;
        }

        while (true) {
            skipWhitespace();
            String key = parseString();

            skipWhitespace();
            expect(':');

            skipWhitespace();
            JsonValue value = parseValue();

            objectFields.put(key, value);

            skipWhitespace();

            if (current() == '}') {
                index++;
                break;
            }

            expect(',');
        }

        return objectFields;
    }

    private List<JsonValue> parseArrayValue() {
        skipWhitespace();
        expect('[');

        List<JsonValue> arrayElements = new ArrayList<>();

        skipWhitespace();

        if (current() == ']') {
            index++;
            return arrayElements;
        }

        while (true) {
            skipWhitespace();
            arrayElements.add(parseValue());

            skipWhitespace();

            if (current() == ']') {
                index++;
                break;
            }

            expect(',');
        }

        return arrayElements;
    }

    private JsonValue parseValue() {
        char current = current();

        if (current == '{') {
            return JsonValue.ofObject(parseObjectValue());
        }

        if (current == '[') {
            return JsonValue.ofArray(parseArrayValue());
        }

        if (current == '"') {
            return JsonValue.ofString(parseString());
        }

        if (current == 'n') {
            parseLiteral("null");
            return JsonValue.ofNull();
        }

        if (current == 't') {
            parseLiteral("true");
            return JsonValue.ofBoolean(true);
        }

        if (current == 'f') {
            parseLiteral("false");
            return JsonValue.ofBoolean(false);
        }

        if (current == '-' || Character.isDigit(current)) {
            return parseNumber();
        }

        throw new RuntimeException("Nieobsługiwana wartość JSON.");
    }

    private JsonValue parseNumber() {
        int start = index;

        if (current() == '-') {
            index++;
        }

        readDigits();

        boolean isDouble = false;

        if (index < jsonText.length() && jsonText.charAt(index) == '.') {
            isDouble = true;
            index++;
            readDigits();
        }

        if (index < jsonText.length() && (jsonText.charAt(index) == 'e' || jsonText.charAt(index) == 'E')) {
            isDouble = true;
            index++;

            if (index < jsonText.length() && (jsonText.charAt(index) == '+' || jsonText.charAt(index) == '-')) {
                index++;
            }

            readDigits();
        }

        String number = jsonText.substring(start, index);

        if (isDouble) {
            return JsonValue.ofDouble(Double.parseDouble(number));
        }

        return JsonValue.ofInteger(Integer.parseInt(number));
    }

    private void readDigits() {
        if (index >= jsonText.length() || !Character.isDigit(jsonText.charAt(index))) {
            throw new RuntimeException("Niepoprawna liczba JSON.");
        }

        while (index < jsonText.length() && Character.isDigit(jsonText.charAt(index))) {
            index++;
        }
    }

    private void parseLiteral(String literal) {
        if (!jsonText.startsWith(literal, index)) {
            throw new RuntimeException("Niepoprawna wartość JSON.");
        }

        index += literal.length();
    }

    private String parseString() {
        expect('"');

        StringBuilder parsedString = new StringBuilder();

        while (index < jsonText.length()) {
            char character = current();

            if (character == '"') {
                expect('"');
                return parsedString.toString();
            }

            if (character == '\\') {
                index++;

                if (index >= jsonText.length()) {
                    throw new RuntimeException("Niezamknięty escape w stringu JSON.");
                }

                parsedString.append(parseEscapedCharacter());
                continue;
            }

            parsedString.append(character);
            index++;
        }

        throw new RuntimeException("Niezamknięty string JSON.");
    }

    private char parseEscapedCharacter() {
        char escaped = current();
        index++;

        return switch (escaped) {
            case '"' -> '"';
            case '\\' -> '\\';
            case 'n' -> '\n';
            case 't' -> '\t';
            case 'r' -> '\r';
            default -> throw new RuntimeException("Nieobsługiwany znak escape: \\" + escaped);
        };
    }

    private void expect(char expected) {
        skipWhitespace();

        if (current() != expected) {
            throw new RuntimeException("Oczekiwano znaku: " + expected);
        }

        index++;
    }

    private char current() {
        if (index >= jsonText.length()) {
            throw new RuntimeException("Nieoczekiwany koniec JSON.");
        }

        return jsonText.charAt(index);
    }

    private void skipWhitespace() {
        while (index < jsonText.length() && Character.isWhitespace(jsonText.charAt(index))) {
            index++;
        }
    }
}
