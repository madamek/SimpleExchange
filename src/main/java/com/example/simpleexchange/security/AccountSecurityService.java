package com.example.simpleexchange.security;

import com.example.simpleexchange.account.persistence.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Component("accountSecurityService")
@RequiredArgsConstructor
public class AccountSecurityService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public boolean isAccountOwner(Authentication authentication, UUID accountId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();

        return accountRepository.findById(accountId)
                .map(account -> account.getUser().getUsername())
                .map(ownerUsername -> ownerUsername.equals(currentUsername))
                .orElse(false);
    }
}
