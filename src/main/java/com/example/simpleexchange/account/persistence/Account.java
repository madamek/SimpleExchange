package com.example.simpleexchange.account.persistence;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.user.persistence.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(name = "balance_pln", nullable = false)
    private BigDecimal balancePln;

    @Column(name = "balance_usd", nullable = false)
    private BigDecimal balanceUsd;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public BigDecimal getBalance(Currency currency) {
        return switch (currency) {
            case PLN -> balancePln;
            case USD -> balanceUsd;
        };
    }

    public void setBalance(Currency currency, BigDecimal balance) {
        switch (currency) {
            case PLN -> setBalancePln(balance);
            case USD -> setBalanceUsd(balance);
        }
    }
}
