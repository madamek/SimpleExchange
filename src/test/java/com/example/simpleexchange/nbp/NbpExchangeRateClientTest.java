package com.example.simpleexchange.nbp;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.domain.Rate;
import com.example.simpleexchange.nbp.dto.NbpExchangeRateResponse;
import com.example.simpleexchange.nbp.dto.NbpRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NbpExchangeRateClientTest {

    @Mock
    private NbpRestClient nbpRestClient;

    @InjectMocks
    private NbpExchangeRateClient nbpExchangeRateClient;

    @Test
    void shouldReturnExchangeRate() {
        // given
        NbpExchangeRateResponse response = new NbpExchangeRateResponse(List.of(new NbpRate(BigDecimal.ONE, BigDecimal.TEN)));
        when(nbpRestClient.getRatesForCurrency("usd")).thenReturn(response);

        // when
        Rate rate = nbpExchangeRateClient.getExchangeRate(Currency.USD);

        // then
        assertThat(rate.buyRate()).isEqualTo(BigDecimal.TEN);
        assertThat(rate.sellRate()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void shouldThrowExceptionWhenRatesListIsEmpty() {
        // given
        NbpExchangeRateResponse response = new NbpExchangeRateResponse(Collections.emptyList());
        when(nbpRestClient.getRatesForCurrency("usd")).thenReturn(response);

        // when & then
        assertThrows(RuntimeException.class, () -> nbpExchangeRateClient.getExchangeRate(Currency.USD));
    }

    @Test
    void shouldThrowExceptionWhenResponseIsNull() {
        // given
        when(nbpRestClient.getRatesForCurrency("usd")).thenReturn(null);

        // when & then
        assertThrows(RuntimeException.class, () -> nbpExchangeRateClient.getExchangeRate(Currency.USD));
    }
}