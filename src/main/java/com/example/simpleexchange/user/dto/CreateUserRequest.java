package com.example.simpleexchange.user.dto;

import jakarta.validation.constraints.*;

import java.math.*;

public record CreateUserRequest(
        @NotBlank(message = "Username cannot be blank.")
        @Size(min = 3, message = "Username must be at least 3 characters long.")
        String username,

        @NotBlank(message = "Password cannot be blank.")
        @Size(min = 8, message = "Password must be at least 8 characters long.")
        String password,

        @NotBlank(message = "First name cannot be blank.")
        String firstName,

        @NotBlank(message = "Last name cannot be blank.")
        String lastName,

        @NotNull(message = "Initial balance cannot be null.")
        @PositiveOrZero(message = "Initial balance cannot be negative.")
        BigDecimal initialPlnBalance
) {}
