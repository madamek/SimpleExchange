package com.example.simpleexchange.nbp.dto;

import com.fasterxml.jackson.annotation.*;

import java.math.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NbpRate(BigDecimal bid, BigDecimal ask) {
}
