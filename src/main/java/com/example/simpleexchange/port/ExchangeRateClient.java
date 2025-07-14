package com.example.simpleexchange.port;

import com.example.simpleexchange.domain.*;

public interface ExchangeRateClient {
    Rate getExchangeRate(Currency currency);
}
