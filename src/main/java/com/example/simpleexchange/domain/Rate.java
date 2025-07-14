package com.example.simpleexchange.domain;

import lombok.*;

import java.math.*;

@Builder
public record Rate(BigDecimal sellRate, BigDecimal buyRate) {
}
