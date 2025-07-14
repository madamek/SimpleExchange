package com.example.simpleexchange.exception;

import lombok.*;
import org.springframework.http.*;

@Getter
public sealed class RestException extends RuntimeException permits
    InsufficientFundsException, InvalidCurrencyException, ResourceNotFoundException {

    private final HttpStatus httpStatus;

    public RestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
