package com.example.simpleexchange.user;

import com.example.simpleexchange.account.persistence.*;
import com.example.simpleexchange.exception.*;
import com.example.simpleexchange.user.dto.*;
import com.example.simpleexchange.user.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.math.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserCreatedResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("User with username '" + request.username() + "' already exists.");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .enabled(true)
                .build();

        Authority userRole = Authority.builder().authority("ROLE_USER").user(user).build();
        user.setAuthorities(Set.of(userRole));

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .user(user)
                .balancePln(request.initialPlnBalance())
                .balanceUsd(BigDecimal.ZERO)
                .build();
        user.setAccounts(Set.of(account));

        User savedUser = userRepository.save(user);
        UUID savedAccountId = savedUser.getAccounts().stream().findFirst().orElseThrow().getId();

        return new UserCreatedResponse(savedUser.getId(), savedAccountId);
    }
}
