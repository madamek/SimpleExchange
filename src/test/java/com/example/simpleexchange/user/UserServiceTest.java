package com.example.simpleexchange.user;

import com.example.simpleexchange.exception.*;
import com.example.simpleexchange.user.dto.*;
import com.example.simpleexchange.user.persistence.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.security.crypto.password.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("should create user and account successfully when username is unique")
    void createUser_whenUsernameIsUnique_shouldCreateUser() {
        // given
        CreateUserRequest request = new CreateUserRequest("new.user", "password123", "New", "User", new BigDecimal("100.00"));
        given(userRepository.existsByUsername("new.user")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        given(userRepository.save(userCaptor.capture())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        UserCreatedResponse response = userService.createUser(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isNotNull();
        assertThat(response.accountId()).isNotNull();

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("new.user");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getAccounts()).hasSize(1);
        assertThat(savedUser.getAccounts().stream().findFirst().get().getBalancePln()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("should throw UserAlreadyExistsException when username is taken")
    void createUser_whenUsernameExists_shouldThrowException() {
        // given
        CreateUserRequest request = new CreateUserRequest("existing.user", "password123", "Existing", "User", BigDecimal.ZERO);
        given(userRepository.existsByUsername("existing.user")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'existing.user' already exists.");

        then(passwordEncoder).should(never()).encode(anyString());
        then(userRepository).should(never()).save(any(User.class));
    }
}