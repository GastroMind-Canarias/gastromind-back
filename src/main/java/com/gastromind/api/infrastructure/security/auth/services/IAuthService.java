package com.gastromind.api.infrastructure.security.auth.services;

public interface IAuthService {
    boolean validateCredentials(String username, String password);
}
