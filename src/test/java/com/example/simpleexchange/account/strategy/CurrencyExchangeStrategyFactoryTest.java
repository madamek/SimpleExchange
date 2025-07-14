package com.example.simpleexchange.account.strategy;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.exception.InvalidCurrencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeStrategyFactoryTest {

    @Mock
    private PlnToUsdExchangeStrategy plnToUsdExchangeStrategy;

    @Mock
    private UsdToPlnExchangeStrategy usdToPlnExchangeStrategy;

    private CurrencyExchangeStrategyFactory factory;

    @BeforeEach
    void setUp() {
        when(plnToUsdExchangeStrategy.getFromCurrency()).thenReturn(Currency.PLN);
        when(plnToUsdExchangeStrategy.getToCurrency()).thenReturn(Currency.USD);
        when(usdToPlnExchangeStrategy.getFromCurrency()).thenReturn(Currency.USD);
        when(usdToPlnExchangeStrategy.getToCurrency()).thenReturn(Currency.PLN);
        factory = new CurrencyExchangeStrategyFactory(List.of(plnToUsdExchangeStrategy, usdToPlnExchangeStrategy));
    }

    @Test
    void shouldReturnCorrectStrategy() {
        // when
        CurrencyExchangeStrategy strategy = factory.getStrategy(Currency.PLN, Currency.USD);

        // then
        assertThat(strategy).isEqualTo(plnToUsdExchangeStrategy);
    }

    @Test
    void shouldThrowExceptionWhenStrategyNotFound() {
        // when & then
        assertThrows(InvalidCurrencyException.class, () -> factory.getStrategy(Currency.PLN, Currency.PLN));
    }
}
