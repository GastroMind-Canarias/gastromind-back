package com.gastromind.api.infrastructure.security.auth.services.impl;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import com.gastromind.api.infrastructure.security.auth.services.IAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
/**
 * Representa auth dentro del dominio de la aplicacion.
 */
public class AuthService implements IAuthService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Constructor de auth.
     * @param userRepository valor a utilizar.
     * @param passwordEncoder valor a utilizar.
     */

    public AuthService(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Realiza validate credentials.
     * @param username valor a utilizar.
     * @param password la contrasena
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean validateCredentials(String username, String password) {
        return userRepository.findByName(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

}




