package com.gastromind.api.infrastructure.security.auth.services.impl;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void validateCredentials_trueWhenMatch() {
        UserEntity u = new UserEntity();
        u.setPassword("{bcrypt}hash");
        when(userRepository.findByName("bob")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secret", u.getPassword())).thenReturn(true);
        assertTrue(authService.validateCredentials("bob", "secret"));
    }

    @Test
    void validateCredentials_falseWhenUserMissingOrBadPassword() {
        when(userRepository.findByName("x")).thenReturn(Optional.empty());
        assertFalse(authService.validateCredentials("x", "p"));

        UserEntity u = new UserEntity();
        u.setPassword("h");
        when(userRepository.findByName("y")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", u.getPassword())).thenReturn(false);
        assertFalse(authService.validateCredentials("y", "wrong"));
    }
}
