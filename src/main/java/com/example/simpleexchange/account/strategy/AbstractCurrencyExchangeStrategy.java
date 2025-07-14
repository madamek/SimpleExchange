package com.example.simpleexchange.account.strategy;

import com.example.simpleexchange.account.persistence.Account;
import com.example.simpleexchange.domain.Rate;
import com.example.simpleexchange.exception.InsufficientFundsException;

import java.math.BigDecimal;

public abstract class AbstractCurrencyExchangeStrategy implements CurrencyExchangeStrategy {

    @Override
    public void exchange(Account account, BigDecimal amount, Rate rate) {
        if (account.getBalance(getFromCurrency()).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient " + getFromCurrency() + " balance.");
        }

        BigDecimal newFromBalance = account.getBalance(getFromCurrency()).subtract(amount);
        BigDecimal newToBalance = account.getBalance(getToCurrency()).add(calculateExchange(amount, rate));

        account.setBalance(getFromCurrency(), newFromBalance);
        account.setBalance(getToCurrency(), newToBalance);
    }

    protected abstract BigDecimal calculateExchange(BigDecimal amount, Rate rate);
}
