package com.example.simpleexchange.account.dto;

import com.example.simpleexchange.domain.*;
import jakarta.validation.constraints.*;

import java.math.*;

public record ExchangeRequest(
        @NotNull(message = "Amount cannot be empty")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Source currency cannot be empty")
        Currency fromCurrency,

        @NotNull(message = "Target currency cannot be empty")
        Currency toCurrency
) {
}
