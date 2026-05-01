package com.gastromind.api.infrastructure.security.auth.services.impl;

import com.gastromind.api.infrastructure.security.auth.services.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
/**
 * Servicio de utilidades JWT para emisiAn y validaciAn de tokens.
 */
public class JwtService implements IJwtService {

    private final SecretKey key;
    private final long expirationMinutes;
    /**
     * Inicializa la firma criptogrAfica y la expiraciAn configurada para los tokens.
     */

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }
    /** Genera un token firmado para el usuario indicado. */

    @Override
    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }
    /** Extrae el nombre de usuario contenido en el token. */

    @Override
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }
    /** Valida que el token corresponda al usuario y no estA expirado. */

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            if (!claims.getSubject().equals(userDetails.getUsername())) {
                return false;
            }
            return !claims.getExpiration().before(new Date());
        } catch (RuntimeException e) {
            return false;
        }
    }
    /** Comprueba si el token es estructuralmente vAlido y vigente. */

    @Override
    public boolean isValid(String token) {
        try {
            Claims c = parseClaims(token);
            return c.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}




