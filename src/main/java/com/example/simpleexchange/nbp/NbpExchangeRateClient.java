package com.example.simpleexchange.nbp;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.domain.*;
import com.example.simpleexchange.exception.*;
import com.example.simpleexchange.nbp.dto.*;
import com.example.simpleexchange.port.*;
import lombok.*;
import org.springframework.retry.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NbpExchangeRateClient implements ExchangeRateClient {

    private final NbpRestClient nbpRestClient;

    @Override
    @Retryable(
            retryFor = { ExchangeRateProviderException.class, feign.RetryableException.class },
            noRetryFor = { ResourceNotFoundException.class },
            backoff = @Backoff(delay = 100))
    public Rate getExchangeRate(Currency currency) {

        NbpExchangeRateResponse response = nbpRestClient.getRatesForCurrency(currency.name().toLowerCase());

        NbpRate nbpRate = Optional.ofNullable(response)
                .map(NbpExchangeRateResponse::rates)
                .filter(rates -> !rates.isEmpty())
                .map(List::getFirst)
                .orElseThrow(() -> new ResourceNotFoundException("Received empty response from NBP for currency: " + currency));

        return Rate.builder()
                .buyRate(nbpRate.ask())
                .sellRate(nbpRate.bid())
                .build();
    }
}