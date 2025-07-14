package com.example.simpleexchange.exception;

import org.springframework.http.*;

public final class InsufficientFundsException extends RestException {
    public InsufficientFundsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
