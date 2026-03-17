package com.gastromind.api.infrastructure.security.auth.services;

import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;

public interface IAuthService {
    boolean validateCredentials(String username, String password);
}
