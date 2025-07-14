package com.example.simpleexchange.account.strategy;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.exception.InvalidCurrencyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CurrencyExchangeStrategyFactory {

    private final Map<String, CurrencyExchangeStrategy> strategies;

    public CurrencyExchangeStrategyFactory(List<CurrencyExchangeStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(this::getStrategyKey, Function.identity()));
    }

    public CurrencyExchangeStrategy getStrategy(Currency from, Currency to) {
        String key = getStrategyKey(from, to);
        CurrencyExchangeStrategy strategy = strategies.get(key);
        if (strategy == null) {
            throw new InvalidCurrencyException("Not supported exchange: " + from + " -> " + to);
        }
        return strategy;
    }

    private String getStrategyKey(CurrencyExchangeStrategy strategy) {
        return getStrategyKey(strategy.getFromCurrency(), strategy.getToCurrency());
    }

    private String getStrategyKey(Currency from, Currency to) {
        return from + "_" + to;
    }
}
