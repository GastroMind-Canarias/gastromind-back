package com.gastromind.api.infrastructure.security.filter;

import com.gastromind.api.infrastructure.security.auth.services.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

        // DEBUG 1: AAaAAAasAAAAAaAAasAALlega el header?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            String username = jwtService.extractUsername(token);
            System.out.println("DEBUG: Username en Token -> " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // AquAAaAaAaaAAaAAasAA suele fallar: si el nombre no coincide exacto con la DB
                UserDetails user = userDetailsService.loadUserByUsername(username);
                System.out.println("DEBUG: Usuario encontrado en DB -> " + user.getUsername());
                System.out.println("DEBUG: Autoridades -> " + user.getAuthorities());

                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("DEBUG: AAaAAAasAAAAAaAAasAAAutenticaciAAaAaAaaAAaAAasAAn establecida con AAaAaAaaAAaAAasAAxito!");
                } else {
                    System.out.println("DEBUG: El token no es vAAaAaAaaAAaAAasAAlido para este usuario");
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG: ERROR EN FILTRO -> " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}




