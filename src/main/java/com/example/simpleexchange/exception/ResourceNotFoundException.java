package com.example.simpleexchange.exception;

import org.springframework.http.*;

public final class ResourceNotFoundException extends RestException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
