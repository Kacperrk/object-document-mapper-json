package org.example.odm;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonParser {

    private final String json;
    private int index = 0;

    public JsonParser(String json) {
        this.json = json;
    }

    public Map<String, JsonValue> parseObject() {
        skipWhitespace();
        expect('{');

        Map<String, JsonValue> result = new LinkedHashMap<>();

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
            JsonValue value = parseValue();

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

    private JsonValue parseValue() {
        char current = current();

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

        if (index < json.length() && json.charAt(index) == '.') {
            isDouble = true;
            index++;
            readDigits();
        }

        if (index < json.length() && (json.charAt(index) == 'e' || json.charAt(index) == 'E')) {
            isDouble = true;
            index++;

            if (index < json.length() && (json.charAt(index) == '+' || json.charAt(index) == '-')) {
                index++;
            }

            readDigits();
        }

        String number = json.substring(start, index);

        if (isDouble) {
            return JsonValue.ofDouble(Double.parseDouble(number));
        }

        return JsonValue.ofInteger(Integer.parseInt(number));
    }

    private void readDigits() {
        if (index >= json.length() || !Character.isDigit(json.charAt(index))) {
            throw new RuntimeException("Niepoprawna liczba JSON.");
        }

        while (index < json.length() && Character.isDigit(json.charAt(index))) {
            index++;
        }
    }

    private void parseLiteral(String literal) {
        if (!json.startsWith(literal, index)) {
            throw new RuntimeException("Niepoprawna wartość JSON.");
        }

        index += literal.length();
    }

    private String parseString() {
        expect('"');

        StringBuilder result = new StringBuilder();

        while (index < json.length()) {
            char character = current();

            if (character == '"') {
                expect('"');
                return result.toString();
            }

            if (character == '\\') {
                index++;

                if (index >= json.length()) {
                    throw new RuntimeException("Niezamknięty escape w stringu JSON.");
                }

                result.append(parseEscapedCharacter());
                continue;
            }

            result.append(character);
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
