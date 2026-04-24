package com.gastromind.api.infrastructure.security.auth.controllers;

import com.gastromind.api.application.usecases.RegisterUserUseCase;
import com.gastromind.api.infrastructure.security.auth.dtos.LoginRequest;
import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;
import com.gastromind.api.infrastructure.security.auth.services.IAuthService;
import com.gastromind.api.infrastructure.security.auth.services.IJwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;
    @Mock
    private RegisterUserUseCase registerUserUseCase;
    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private AuthController controller;

    @Test
    void login_unauthorizedWhenBadCreds() {
        LoginRequest req = new LoginRequest("u", "secret12");
        when(authService.validateCredentials("u", "secret12")).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_okReturnsToken() {
        LoginRequest req = new LoginRequest("u", "secret12");
        when(authService.validateCredentials("u", "secret12")).thenReturn(true);
        when(jwtService.generateToken("u")).thenReturn("tok");
        var res = controller.login(req);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("tok", res.getBody().token());
    }

    @Test
    void register_delegates() {
        RegisterRequest req = org.mockito.Mockito.mock(RegisterRequest.class);
        var res = controller.register(req);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        verify(registerUserUseCase).exec(req);
    }
}
