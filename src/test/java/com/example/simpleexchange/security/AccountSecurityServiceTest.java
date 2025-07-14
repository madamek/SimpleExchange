package com.example.simpleexchange.security;

import com.example.simpleexchange.account.persistence.*;
import com.example.simpleexchange.user.persistence.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.security.core.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AccountSecurityServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountSecurityService accountSecurityService;

    @Test
    @DisplayName("should return true when authenticated user is the account owner")
    void isAccountOwner_whenUserIsOwner_shouldReturnTrue() {
        // given
        String username = "test-user";
        UUID accountId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(true);
        given(authentication.getName()).willReturn(username);

        User owner = User.builder().username(username).build();
        Account account = Account.builder().user(owner).build();

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

        // when
        boolean result = accountSecurityService.isAccountOwner(authentication, accountId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when authenticated user is not the account owner")
    void isAccountOwner_whenUserIsNotOwner_shouldReturnFalse() {
        // given
        String currentUsername = "logged-in-user";
        String ownerUsername = "actual-owner";
        UUID accountId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(true);
        given(authentication.getName()).willReturn(currentUsername);

        User owner = User.builder().username(ownerUsername).build();
        Account account = Account.builder().user(owner).build();

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

        // when
        boolean result = accountSecurityService.isAccountOwner(authentication, accountId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when account is not found")
    void isAccountOwner_whenAccountNotFound_shouldReturnFalse() {
        // given
        UUID accountId = UUID.randomUUID();
        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(true);
        given(authentication.getName()).willReturn("test-user");

        given(accountRepository.findById(accountId)).willReturn(Optional.empty());

        // when
        boolean result = accountSecurityService.isAccountOwner(authentication, accountId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when authentication is null")
    void isAccountOwner_whenAuthenticationIsNull_shouldReturnFalse() {
        // given
        Authentication authentication = null;
        UUID accountId = UUID.randomUUID();

        // when
        boolean result = accountSecurityService.isAccountOwner(authentication, accountId);

        // then
        assertThat(result).isFalse();
        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("should return false when user is not authenticated")
    void isAccountOwner_whenUserIsNotAuthenticated_shouldReturnFalse() {
        // given
        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(false);
        UUID accountId = UUID.randomUUID();

        // when
        boolean result = accountSecurityService.isAccountOwner(authentication, accountId);

        // then
        assertThat(result).isFalse();
        verifyNoInteractions(accountRepository);
    }
}