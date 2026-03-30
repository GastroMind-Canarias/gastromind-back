package com.gastromind.api.infrastructure.security.auth.controllers;

import com.gastromind.api.application.usecases.RegisterUserUseCase;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.security.auth.dtos.LoginRequest;
import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;
import com.gastromind.api.infrastructure.security.auth.dtos.TokenResponse;
import com.gastromind.api.infrastructure.security.auth.services.IAuthService;
import com.gastromind.api.infrastructure.security.auth.services.IJwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
@Tag(name = "Autenticacion", description = "Servicios para el acceso de usuarios, registro y gestion de tokens JWT.")
public class AuthController {

    private final IAuthService authService;
    private final RegisterUserUseCase registerUserUseCase;
    private final IJwtService jwtService;

    public AuthController(IAuthService authService, RegisterUserUseCase registerUserUseCase, IJwtService jwtService) {
        this.authService = authService;
        this.registerUserUseCase = registerUserUseCase;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y devuelve un token JWT valido.")
    @ApiPostDoc
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        if (!authService.validateCredentials(req.username(), req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }
        try {
            String token = jwtService.generateToken(req.username());
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno al generar la sesion");
        }
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta de usuario en el sistema con las credenciales proporcionadas.")
    @ApiPostDoc
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.exec(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con exito");
    }
}