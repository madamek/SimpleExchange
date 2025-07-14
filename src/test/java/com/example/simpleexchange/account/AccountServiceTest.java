package com.example.simpleexchange.account;

import com.example.simpleexchange.account.dto.AccountResponse;
import com.example.simpleexchange.account.dto.ExchangeRequest;
import com.example.simpleexchange.account.persistence.Account;
import com.example.simpleexchange.account.persistence.AccountRepository;
import com.example.simpleexchange.account.strategy.CurrencyExchangeStrategy;
import com.example.simpleexchange.account.strategy.CurrencyExchangeStrategyFactory;
import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.domain.Rate;
import com.example.simpleexchange.exception.InvalidCurrencyException;
import com.example.simpleexchange.exception.ResourceNotFoundException;
import com.example.simpleexchange.mapper.AccountMapper;
import com.example.simpleexchange.port.ExchangeRateClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private CurrencyExchangeStrategyFactory strategyFactory;
    @Mock
    private CurrencyExchangeStrategy exchangeStrategy;
    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private AccountService accountService;

    @Nested
    @DisplayName("Tests for getAccountDetails method")
    class GetAccountDetailsTests {

        @Test
        @DisplayName("should return account response when account exists")
        void getAccountDetails_whenAccountExists_shouldReturnAccountResponse() {
            // given
            UUID accountId = UUID.randomUUID();
            Account account = new Account();
            AccountResponse expectedResponse = new AccountResponse(accountId, BigDecimal.ZERO, BigDecimal.ZERO);

            given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
            given(accountMapper.toAccountResponse(account)).willReturn(expectedResponse);

            // when
            AccountResponse result = accountService.getAccountDetails(accountId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.accountId()).isEqualTo(accountId);
            verify(accountRepository).findById(accountId);
            verify(accountMapper).toAccountResponse(account);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when account does not exist")
        void getAccountDetails_whenAccountDoesNotExist_shouldThrowResourceNotFoundException() {
            // given
            UUID accountId = UUID.randomUUID();
            given(accountRepository.findById(accountId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> accountService.getAccountDetails(accountId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Account not found with id: " + accountId);

            verify(accountMapper, never()).toAccountResponse(any());
        }
    }

    @Nested
    @DisplayName("Tests for exchangeCurrency method")
    class ExchangeCurrencyTests {

        private final UUID accountId = UUID.randomUUID();

        @Test
        @DisplayName("should exchange currency successfully")
        void exchangeCurrency_whenSuccessful_shouldUpdateBalances() {
            // given
            ExchangeRequest request = new ExchangeRequest(new BigDecimal("200.00"), Currency.PLN, Currency.USD);
            Account account = Account.builder()
                    .balancePln(BigDecimal.valueOf(800))
                    .balanceUsd(BigDecimal.valueOf(0)).build();
            Rate rate = new Rate(BigDecimal.valueOf(4.0), BigDecimal.valueOf(4.1));

            given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
            given(strategyFactory.getStrategy(Currency.PLN, Currency.USD)).willReturn(exchangeStrategy);
            given(exchangeRateClient.getExchangeRate(any())).willReturn(rate);

            // when
            accountService.exchangeCurrency(accountId, request);

            // then
            verify(exchangeStrategy).exchange(account, request.amount(), rate);
            verify(accountMapper).toAccountResponse(account);
        }

        @Test
        @DisplayName("should throw InvalidCurrencyException when exchanging the same currency")
        void exchangeCurrency_whenSameCurrency_shouldThrowException() {
            // given
            ExchangeRequest request = new ExchangeRequest(new BigDecimal("200.00"), Currency.USD, Currency.USD);

            // when & then
            assertThatThrownBy(() -> accountService.exchangeCurrency(accountId, request))
                    .isInstanceOf(InvalidCurrencyException.class)
                    .hasMessage("Source currency and target currency must be different.");
        }
    }
}
