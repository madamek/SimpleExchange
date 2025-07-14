package com.example.simpleexchange.security;

import com.example.simpleexchange.user.persistence.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("should return UserDetails when user is found")
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // given
        String username = "test.user";
        User userEntity = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password("encoded-password")
                .enabled(true)
                .authorities(Set.of(Authority.builder().authority("ROLE_USER").build()))
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(userEntity));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo("encoded-password");
        assertThat(userDetails.getAuthorities())
                .hasSize(1)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException when user is not found")
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {
        // given
        String username = "nonexistent.user";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with username: " + username);
    }
}