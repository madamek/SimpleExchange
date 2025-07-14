package com.example.simpleexchange.nbp.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NbpExchangeRateResponse(List<NbpRate> rates) {
}
