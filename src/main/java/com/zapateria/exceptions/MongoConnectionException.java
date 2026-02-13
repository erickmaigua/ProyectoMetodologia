package com.zapateria.exceptions;

public class MongoConnectionException extends RuntimeException {
    public MongoConnectionException(String message) {
        super(message);
    }
    public MongoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
