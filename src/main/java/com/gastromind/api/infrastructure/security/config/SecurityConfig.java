package com.gastromind.api.infrastructure.security.config;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import com.gastromind.api.infrastructure.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final SecurityPathsProperties paths;

    public SecurityConfig(SecurityPathsProperties paths) {
        this.paths = paths;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(paths.getPublicUrls()).permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserJpaRepository userRepository) {
        return username -> {
            UserEntity user = userRepository.findByName(username)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

            String roleName = user.getRole().name();

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getName())
                    .password(user.getPassword())
                    .authorities(roleName)
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
