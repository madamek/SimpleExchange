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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CurrencyExchangeStrategyFactory strategyFactory;
    private final AccountMapper accountMapper;
    private final ExchangeRateClient exchangeRateClient;

    @Transactional(readOnly = true)
    public AccountResponse getAccountDetails(UUID accountId) {
        log.info("Fetching account details for accountId: {}", accountId);
        Account account = findAccountById(accountId);
        return accountMapper.toAccountResponse(account);
    }

    @Transactional
    public AccountResponse exchangeCurrency(UUID accountId, ExchangeRequest exchangeRequest) {
        log.info("Executing currency exchange for accountId: {}. Request: {} {} -> {}",
                accountId, exchangeRequest.amount(), exchangeRequest.fromCurrency(), exchangeRequest.toCurrency());

        Currency currencyFrom = exchangeRequest.fromCurrency();
        Currency currencyTo = exchangeRequest.toCurrency();

        if (currencyFrom == currencyTo) {
            throw new InvalidCurrencyException("Source currency and target currency must be different.");
        }

        Account account = findAccountById(accountId);

        CurrencyExchangeStrategy strategy = strategyFactory.getStrategy(currencyFrom, currencyTo);
        Rate rate = exchangeRateClient.getExchangeRate(strategy.getRateCurrency());
        log.info("Fetched {} rate for {} -> {} conversion: {}", strategy.getRateCurrency(), currencyFrom, currencyTo, rate);

        strategy.exchange(account, exchangeRequest.amount(), rate);

        log.info("Successfully finished exchange for accountId: {}. New balances: PLN={}, USD={}",
                accountId, account.getBalancePln(), account.getBalanceUsd());

        return accountMapper.toAccountResponse(account);
    }

    private Account findAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
    }
}
