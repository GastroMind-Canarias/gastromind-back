package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.TicketServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.ImportTicketFromImageResult;
import com.gastromind.api.application.usecases.ImportTicketFromImageUseCase;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.handler.GlobalExceptionHandler;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.TicketRestMapper;
import com.gastromind.api.infrastructure.adapters.out.ai.TicketImageProperties;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import com.gastromind.api.infrastructure.security.config.SecurityConfig;
import com.gastromind.api.infrastructure.security.config.SecurityPathsProperties;
import com.gastromind.api.infrastructure.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
@Import({SecurityConfig.class, SecurityPathsProperties.class, GlobalExceptionHandler.class})
class TicketControllerFromImageSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketServiceImpl ticketServiceImpl;
    @MockBean
    private TicketRestMapper ticketMapper;
    @MockBean
    private ImportTicketFromImageUseCase importTicketFromImageUseCase;
    @MockBean
    private TicketImageProperties ticketImageProperties;
    @MockBean
    private UserServiceImpl userServiceImpl;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUpFilterPassThrough() throws Exception {
        doAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void fromImage_requiresAuthentication() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "ticket.png", "image/png", new byte[]{1, 2, 3});
        mockMvc.perform(multipart("/api/v1/tickets/from-image").file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    void fromImage_allowsAuthenticatedUser() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "ticket.png", "image/png", new byte[]{1, 2, 3});
        when(ticketImageProperties.getMaxImageBytes()).thenReturn(1024L);
        User user = new User("user-1");
        when(userServiceImpl.findByUsername("owner")).thenReturn(user);
        when(importTicketFromImageUseCase.execute(any(byte[].class), eq(MediaType.IMAGE_PNG_VALUE), eq("user-1"), anyString()))
                .thenReturn(new ImportTicketFromImageResult(new Ticket(), null, "Store"));
        when(ticketMapper.toResponse(any(Ticket.class))).thenReturn(
                new com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketResponse(
                        null, null, null, null, null, 0f, null, java.util.List.of(), null));

        mockMvc.perform(multipart("/api/v1/tickets/from-image")
                        .file(file)
                        .param("store_id", "store-1")
                        .with(user("owner").authorities(new SimpleGrantedAuthority("ROLE_OWNER"))))
                .andExpect(status().isCreated());
    }
}
