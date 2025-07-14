package com.example.simpleexchange.account.strategy;

import com.example.simpleexchange.account.persistence.Account;
import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.domain.Rate;
import com.example.simpleexchange.exception.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsdToPlnExchangeStrategyTest {

    private final UsdToPlnExchangeStrategy usdToPlnExchangeStrategy = new UsdToPlnExchangeStrategy();

    @Test
    @DisplayName("should update balances when there are sufficient funds")
    void exchange_whenSufficientFunds_shouldUpdateBalances() {
        // given
        Account account = Account.builder()
                .balancePln(BigDecimal.valueOf(0))
                .balanceUsd(BigDecimal.valueOf(210))
                .build();
        BigDecimal amount = BigDecimal.valueOf(200);
        Rate rate = new Rate(new BigDecimal("4.00"), new BigDecimal("4.10"));

        // when
        usdToPlnExchangeStrategy.exchange(account, amount, rate);

        // then
        BigDecimal expectedPln = new BigDecimal("800.0000");
        BigDecimal expectedUsd = new BigDecimal("10");
        assertThat(account.getBalancePln()).isEqualByComparingTo(expectedPln);
        assertThat(account.getBalanceUsd()).isEqualByComparingTo(expectedUsd);
    }

    @Test
    @DisplayName("should throw InsufficientFundsException when there are insufficient funds")
    void exchange_whenInsufficientFunds_shouldThrowInsufficientFundsException() {
        // given
        Account account = Account.builder()
                .balancePln(BigDecimal.valueOf(200))
                .balanceUsd(BigDecimal.valueOf(100))
                .build();
        BigDecimal amount = BigDecimal.valueOf(200);
        Rate rate = new Rate(new BigDecimal("4.00"), new BigDecimal("4.10"));

        // when & then
        assertThatThrownBy(() -> usdToPlnExchangeStrategy.exchange(account, amount, rate))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Insufficient USD balance.");
    }

    @Test
    @DisplayName("should return USD as from currency")
    void getFromCurrency_shouldReturnUSD() {
        assertThat(usdToPlnExchangeStrategy.getFromCurrency()).isEqualTo(Currency.USD);
    }

    @Test
    @DisplayName("should return PLN as to currency")
    void getToCurrency_shouldReturnPLN() {
        assertThat(usdToPlnExchangeStrategy.getToCurrency()).isEqualTo(Currency.PLN);
    }

    @Test
    @DisplayName("should return USD as rate currency")
    void getRateCurrency_shouldReturnUSD() {
        assertThat(usdToPlnExchangeStrategy.getRateCurrency()).isEqualTo(Currency.USD);
    }
}
