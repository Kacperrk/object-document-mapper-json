package org.example.exception;

public class OdmException extends RuntimeException {

    public OdmException(String message) {
        super(message);
    }

    public OdmException(String message, Throwable cause) {
        super(message, cause);
    }
}