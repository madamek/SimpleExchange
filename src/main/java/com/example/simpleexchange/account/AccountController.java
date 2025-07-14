package com.example.simpleexchange.account;

import com.example.simpleexchange.account.dto.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/{accountId}")
    @PreAuthorize("@accountSecurityService.isAccountOwner(authentication, #accountId)")
    ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID accountId) {
        AccountResponse response = accountService.getAccountDetails(accountId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/exchange")
    @PreAuthorize("@accountSecurityService.isAccountOwner(authentication, #accountId)")
    public ResponseEntity<AccountResponse> exchangeCurrency(
            @PathVariable UUID accountId,
            @Valid @RequestBody ExchangeRequest exchangeRequest) {
        AccountResponse response = accountService.exchangeCurrency(accountId, exchangeRequest);
        return ResponseEntity.ok(response);
    }
}
