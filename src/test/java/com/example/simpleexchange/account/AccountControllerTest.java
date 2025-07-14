package com.example.simpleexchange.account;

import com.example.simpleexchange.account.dto.*;
import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.exception.*;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.http.*;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.bean.override.mockito.*;
import org.springframework.test.web.servlet.*;

import java.math.*;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Nested
    @DisplayName("GET /api/v1/accounts/{accountId}")
    class GetAccountByIdTests {

        @Test
        @WithMockUser
        @DisplayName("should return 200 OK and account data when account exists")
        void getAccountById_whenAccountExists_shouldReturn200AndAccountData() throws Exception {
            // given
            UUID accountId = UUID.randomUUID();
            AccountResponse expectedResponse = new AccountResponse(accountId, new BigDecimal("1000.00"), new BigDecimal("50.00"));
            given(accountService.getAccountDetails(accountId)).willReturn(expectedResponse);

            // when & then
            mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balancePln").value(1000.00));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 Not Found when account does not exist")
        void getAccountById_whenAccountDoesNotExist_shouldReturn404() throws Exception {
            // given
            UUID accountId = UUID.randomUUID();
            given(accountService.getAccountDetails(accountId)).willThrow(new ResourceNotFoundException("Account not found"));

            // when & then
            mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/accounts/{accountId}/exchange")
    class ExchangeCurrencyTests {

        @Test
        @WithMockUser
        @DisplayName("should return 200 OK and updated account when exchange is successful")
        void exchangeCurrency_whenRequestIsValid_shouldReturn200() throws Exception {
            // given
            UUID accountId = UUID.randomUUID();
            ExchangeRequest request = new ExchangeRequest(new BigDecimal("100.00"), Currency.PLN, Currency.USD);
            AccountResponse updatedAccount = new AccountResponse(accountId, new BigDecimal("900.00"), new BigDecimal("75.00"));

            given(accountService.exchangeCurrency(eq(accountId), any(ExchangeRequest.class))).willReturn(updatedAccount);

            // when & then
            mockMvc.perform(post("/api/v1/accounts/{accountId}/exchange", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balancePln").value(900.00));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 409 Conflict when funds are insufficient")
        void exchangeCurrency_whenInsufficientFunds_shouldReturn409() throws Exception {
            // given
            UUID accountId = UUID.randomUUID();
            ExchangeRequest request = new ExchangeRequest(new BigDecimal("1000.00"), Currency.PLN, Currency.USD);

            given(accountService.exchangeCurrency(eq(accountId), any(ExchangeRequest.class)))
                    .willThrow(new InsufficientFundsException("Insufficient funds"));

            // when & then
            mockMvc.perform(post("/api/v1/accounts/{accountId}/exchange", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpect(status().isConflict());
        }
    }
}