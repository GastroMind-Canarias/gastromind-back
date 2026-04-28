package com.gastromind.api.infrastructure.security.auth.services;

/**
 * Define el contrato de iauth.
 */
public interface IAuthService {
    boolean validateCredentials(String username, String password);
}






