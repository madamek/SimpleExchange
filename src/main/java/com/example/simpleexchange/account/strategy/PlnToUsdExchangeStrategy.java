package com.example.simpleexchange.account.strategy;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.domain.Rate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PlnToUsdExchangeStrategy extends AbstractCurrencyExchangeStrategy {

    @Override
    public Currency getFromCurrency() {
        return Currency.PLN;
    }

    @Override
    public Currency getToCurrency() {
        return Currency.USD;
    }

    @Override
    public Currency getRateCurrency() {
        return Currency.USD;
    }

    @Override
    protected BigDecimal calculateExchange(BigDecimal amount, Rate rate) {
        return amount.divide(rate.buyRate(), 4, RoundingMode.HALF_UP);
    }
}
