package org.example.odm;

public class JsonValue {

    public enum Type {
        NULL,
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN
    }

    private final Type type;
    private final Object value;

    private JsonValue(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static JsonValue ofNull() {
        return new JsonValue(Type.NULL, null);
    }

    public static JsonValue ofString(String value) {
        return new JsonValue(Type.STRING, value);
    }

    public static JsonValue ofInteger(Integer value) {
        return new JsonValue(Type.INTEGER, value);
    }

    public static JsonValue ofDouble(Double value) {
        return new JsonValue(Type.DOUBLE, value);
    }

    public static JsonValue ofBoolean(Boolean value) {
        return new JsonValue(Type.BOOLEAN, value);
    }

    public Type getType() {
        return type;
    }

    public boolean isNull() {
        return type == Type.NULL;
    }

    public String asString() {
        return (String) value;
    }

    public Integer asInteger() {
        return (Integer) value;
    }

    public Double asDouble() {
        if (type == Type.INTEGER) {
            return ((Integer) value).doubleValue();
        }

        return (Double) value;
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }
}
