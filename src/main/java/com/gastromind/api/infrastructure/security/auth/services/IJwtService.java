package com.gastromind.api.infrastructure.security.auth.services;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Define el contrato de ijwt.
 */
public interface IJwtService {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isValid(String token);
}






