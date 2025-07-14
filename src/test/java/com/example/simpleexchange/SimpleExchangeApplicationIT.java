package com.example.simpleexchange;

import com.example.simpleexchange.account.dto.*;
import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.user.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.*;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SimpleExchangeApplicationIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test-db")
            .withUsername("test-user")
            .withPassword("test-password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("should perform full user flow: register, exchange, and verify balance")
    void fullApiFlowIntegrationTest() {
        // === STEP 1 ===

        // given
        CreateUserRequest registrationRequest = new CreateUserRequest(
                "user123", "password123", "Api", "Test", new BigDecimal("1000.00")
        );

        // when
        ResponseEntity<UserCreatedResponse> registrationResponse = restTemplate.postForEntity(
                "/api/v1/users", registrationRequest, UserCreatedResponse.class
        );

        // then
        assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID accountId = registrationResponse.getBody().accountId();
        assertThat(accountId).isNotNull();


        // === STEP 2 ===

        // given
        ExchangeRequest exchangeRequest = new ExchangeRequest(new BigDecimal("400.00"), Currency.PLN, Currency.USD);

        // when
        ResponseEntity<AccountResponse> exchangeResponse = restTemplate
                .withBasicAuth("user123", "password123")
                .postForEntity("/api/v1/accounts/{accountId}/exchange", exchangeRequest, AccountResponse.class, accountId);

        // then
        assertThat(exchangeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchangeResponse.getBody().accountId()).isEqualTo(accountId);
        assertThat(exchangeResponse.getBody().balancePln()).isEqualByComparingTo("600.00");


        // === STEP 3 ===

        // when
        ResponseEntity<AccountResponse> finalStateResponse = restTemplate
                .withBasicAuth("user123", "password123")
                .getForEntity("/api/v1/accounts/{accountId}", AccountResponse.class, accountId);

        // then
        assertThat(finalStateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountResponse finalAccountState = finalStateResponse.getBody();

        assertThat(finalAccountState.balancePln()).isEqualByComparingTo("600.00");
    }
}

