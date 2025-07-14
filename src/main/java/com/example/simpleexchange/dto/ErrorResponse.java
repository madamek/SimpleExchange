package com.example.simpleexchange.dto;

import lombok.*;

import java.time.*;

@Builder
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {

}
