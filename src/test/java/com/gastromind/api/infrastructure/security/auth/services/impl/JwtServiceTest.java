package com.gastromind.api.infrastructure.security.auth.services.impl;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "a".repeat(64);

    @Test
    void generateExtractValidateAndIsValid() {
        JwtService jwt = new JwtService(SECRET, 120);
        String token = jwt.generateToken("alice");
        assertEquals("alice", jwt.extractUsername(token));

        UserDetails ud = User.withUsername("alice").password("x").roles("USER").build();
        assertTrue(jwt.isTokenValid(token, ud));
        assertTrue(jwt.isValid(token));
    }

    @Test
    void isTokenValid_falseForWrongUser() {
        JwtService jwt = new JwtService(SECRET, 120);
        String token = jwt.generateToken("alice");
        UserDetails other = User.withUsername("bob").password("x").roles("USER").build();
        assertFalse(jwt.isTokenValid(token, other));
    }

    @Test
    void isTokenValid_falseForCorruptToken() {
        JwtService jwt = new JwtService(SECRET, 120);
        UserDetails ud = User.withUsername("alice").password("x").roles("USER").build();
        assertFalse(jwt.isTokenValid("not-a-jwt", ud));
    }

    @Test
    void isValid_falseForGarbage() {
        JwtService jwt = new JwtService(SECRET, 120);
        assertFalse(jwt.isValid("not-a-jwt"));
    }

    @Test
    void isValid_falseWhenExpired() throws Exception {
        JwtService jwt = new JwtService(SECRET, 0);
        String token = jwt.generateToken("alice");
        Thread.sleep(1500);
        assertFalse(jwt.isValid(token));
    }
}
