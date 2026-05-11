package com.gastromind.api.infrastructure.security.filter;

import com.gastromind.api.infrastructure.security.auth.services.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
/**
 * Representa jwt authentication filter dentro del dominio de la aplicacion.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;
    /**
     * Constructor de jwt authentication filter.
     * @param jwtService valor a utilizar.
     * @param userDetailsService valor a utilizar.
     */

    public JwtAuthenticationFilter(IJwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    /**
     * Realiza do filter internal.
     * @param request los datos de la solicitud
     * @param response la respuesta generada
     * @param filterChain valor a utilizar.
     * @throws ServletException si ocurre una condicion de error en la operacion
     * @throws IOException si ocurre una condicion de error en la operacion
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else if (log.isDebugEnabled()) {
                    log.debug("JWT invalido para usuario {}", username);
                }
            }
        } catch (UsernameNotFoundException e) {
            log.warn("JWT con usuario inexistente: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("Error validando JWT: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}




