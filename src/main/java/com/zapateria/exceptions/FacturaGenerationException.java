package com.zapateria.exceptions;

public class FacturaGenerationException extends RuntimeException {
    
    public FacturaGenerationException(String message) {
        super(message);
    }
    
    public FacturaGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
