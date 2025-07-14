package com.example.simpleexchange.account.dto;

import java.math.*;
import java.util.*;

public record AccountResponse(UUID accountId, BigDecimal balancePln, BigDecimal balanceUsd) {
}
