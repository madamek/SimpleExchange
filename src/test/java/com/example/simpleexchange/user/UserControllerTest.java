package com.example.simpleexchange.user;

import com.example.simpleexchange.config.*;
import com.example.simpleexchange.exception.*;
import com.example.simpleexchange.user.dto.*;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.*;
import org.springframework.test.web.servlet.*;

import java.math.*;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("should return 201 Created when user is successfully registered")
    void registerUser_whenRequestIsValid_shouldReturn201Created() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("test.user", "password123", "Test", "User", BigDecimal.TEN);
        UserCreatedResponse response = new UserCreatedResponse(UUID.randomUUID(), UUID.randomUUID());

        given(userService.createUser(any(CreateUserRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                .andExpect(jsonPath("$.accountId").value(response.accountId().toString()));
    }

    @Test
    @DisplayName("should return 400 Bad Request when request body is invalid")
    void registerUser_whenRequestIsInvalid_shouldReturn400() throws Exception {
        // given
        CreateUserRequest invalidRequest = new CreateUserRequest("", "password123", "Test", "User", BigDecimal.TEN);

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 409 Conflict when user already exists")
    void registerUser_whenUserAlreadyExists_shouldReturn409() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("existing.user", "password123", "Test", "User", BigDecimal.TEN);
        given(userService.createUser(any(CreateUserRequest.class)))
                .willThrow(new UserAlreadyExistsException("User already exists"));

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}