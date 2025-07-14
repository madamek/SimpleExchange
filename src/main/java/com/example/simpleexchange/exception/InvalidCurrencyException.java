package com.example.simpleexchange.exception;

import org.springframework.http.*;

public final class InvalidCurrencyException extends RestException {
    public InvalidCurrencyException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
