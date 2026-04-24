package com.gastromind.api.infrastructure.security.filter;

import com.gastromind.api.infrastructure.security.auth.services.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IJwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noBearer_continuesWithoutAuth() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void nonBearer_continuesWithoutAuth() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic x");
        filter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validBearer_setsAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtService.extractUsername("good")).thenReturn("alice");
        UserDetails ud = User.withUsername("alice").password("p").roles("USER").build();
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(ud);
        when(jwtService.isTokenValid("good", ud)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidToken_doesNotSetAuth() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad");
        when(jwtService.extractUsername("bad")).thenReturn("alice");
        UserDetails ud = User.withUsername("alice").password("p").roles("USER").build();
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(ud);
        when(jwtService.isTokenValid("bad", ud)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void extractThrows_continuesChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer x");
        when(jwtService.extractUsername("x")).thenThrow(new IllegalArgumentException("bad jwt"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void nullUsername_skipsJwtValidation() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer t");
        when(jwtService.extractUsername("t")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void existingAuthentication_skipsJwtValidation() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken existing =
                new UsernamePasswordAuthenticationToken("bob", "x", List.of());
        SecurityContextHolder.getContext().setAuthentication(existing);

        when(request.getHeader("Authorization")).thenReturn("Bearer t");
        when(jwtService.extractUsername("t")).thenReturn("alice");

        filter.doFilterInternal(request, response, filterChain);

        assertSame(existing, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
