package com.example.simpleexchange.account.strategy;

import com.example.simpleexchange.account.persistence.Account;
import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.domain.Rate;

import java.math.BigDecimal;

public interface CurrencyExchangeStrategy {

    void exchange(Account account, BigDecimal amount, Rate rate);

    Currency getFromCurrency();

    Currency getToCurrency();

    Currency getRateCurrency();
}
