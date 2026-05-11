package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.TicketServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.ImportTicketFromImageResult;
import com.gastromind.api.application.usecases.ImportTicketFromImageUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.PendingStoreInfoResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.TicketRestMapper;
import com.gastromind.api.infrastructure.adapters.out.ai.TicketImageProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    @Test
    void crudAndMineFlow_shouldDelegateToServicesAndMapper() {
        TicketServiceImpl ticketService = mock(TicketServiceImpl.class);
        TicketRestMapper mapper = mock(TicketRestMapper.class);
        ImportTicketFromImageUseCase importUseCase = mock(ImportTicketFromImageUseCase.class);
        TicketImageProperties imageProps = new TicketImageProperties();
        UserServiceImpl userService = mock(UserServiceImpl.class);
        Authentication auth = mock(Authentication.class);
        TicketController c = buildController(ticketService, mapper, importUseCase, imageProps, userService);

        Ticket ticket = new Ticket();
        TicketResponse response = mock(TicketResponse.class);
        User user = new User();
        user.setId("u-1");
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(user);

        when(ticketService.findAll()).thenReturn(List.of(ticket));
        when(mapper.toResponseList(List.of(ticket))).thenReturn(List.of(response));
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());

        when(ticketService.findById("t-1")).thenReturn(ticket);
        when(mapper.toResponse(ticket)).thenReturn(response);
        assertEquals(HttpStatus.OK, c.getById("t-1").getStatusCode());

        when(ticketService.findAllVisibleForUserHousehold("u-1")).thenReturn(List.of(ticket));
        when(ticketService.findByIdForHouseholdMember("t-1", "u-1")).thenReturn(ticket);
        assertEquals(HttpStatus.OK, c.listMyTickets(auth).getStatusCode());
        assertEquals(HttpStatus.OK, c.getMyTicketById(auth, "t-1").getStatusCode());

        TicketRequest req = mock(TicketRequest.class);
        TicketMeRequest meReq = mock(TicketMeRequest.class);
        when(mapper.toDomain(req)).thenReturn(ticket);
        when(mapper.toDomainForMe(meReq, "u-1")).thenReturn(ticket);
        when(ticketService.create(ticket)).thenReturn(ticket);
        when(ticketService.update(eq("t-1"), any(Ticket.class))).thenReturn(ticket);
        when(ticketService.updateForUser(eq("t-1"), any(Ticket.class), eq("u-1"))).thenReturn(ticket);

        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.CREATED, c.createMyTicket(auth, meReq).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("t-1", req).getStatusCode());
        assertEquals(HttpStatus.OK, c.updateMyTicket(auth, "t-1", meReq).getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, c.delete("t-1").getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.deleteMyTicket(auth, "t-1").getStatusCode());
        verify(ticketService).deleteForUser("t-1", "u-1");
    }

    @Test
    void importFromImage_shouldValidateFileAndCreateTicket() throws Exception {
        TicketServiceImpl ticketService = mock(TicketServiceImpl.class);
        TicketRestMapper mapper = mock(TicketRestMapper.class);
        ImportTicketFromImageUseCase importUseCase = mock(ImportTicketFromImageUseCase.class);
        TicketImageProperties imageProps = new TicketImageProperties();
        imageProps.setMaxImageBytes(1024);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        Authentication auth = mock(Authentication.class);
        TicketController c = buildController(ticketService, mapper, importUseCase, imageProps, userService);

        User user = new User();
        user.setId("u-1");
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(user);

        MockMultipartFile file = new MockMultipartFile("file", "ticket.png", "image/png", new byte[]{1, 2, 3});
        Ticket saved = new Ticket();
        TicketResponse response = mock(TicketResponse.class);
        when(importUseCase.execute(any(byte[].class), eq("image/png"), eq("u-1"), eq("s-1")))
                .thenReturn(new ImportTicketFromImageResult(saved, null, "Lidl"));
        when(mapper.toResponse(saved)).thenReturn(response);

        var out = c.importFromImage(auth, file, "s-1");
        assertEquals(HttpStatus.CREATED, out.getStatusCode());
        verify(importUseCase).execute(any(byte[].class), eq("image/png"), eq("u-1"), eq("s-1"));
    }

    @Test
    void importFromImage_shouldReturn202WhenStoreUnresolved() {
        TicketServiceImpl ticketService = mock(TicketServiceImpl.class);
        TicketRestMapper mapper = mock(TicketRestMapper.class);
        ImportTicketFromImageUseCase importUseCase = mock(ImportTicketFromImageUseCase.class);
        TicketImageProperties imageProps = new TicketImageProperties();
        imageProps.setMaxImageBytes(1024);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        Authentication auth = mock(Authentication.class);
        TicketController c = buildController(ticketService, mapper, importUseCase, imageProps, userService);

        User user = new User();
        user.setId("u-1");
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(user);

        MockMultipartFile file = new MockMultipartFile("file", "ticket.png", "image/png", new byte[]{1, 2, 3});
        Ticket saved = new Ticket();
        PendingStore pendingStore = new PendingStore();
        pendingStore.setId("pending-1");
        TicketResponse response = mock(TicketResponse.class);
        when(importUseCase.execute(any(byte[].class), eq("image/png"), eq("u-1"), eq("s-1")))
                .thenReturn(new ImportTicketFromImageResult(saved, pendingStore, "Unknown Shop"));
        when(mapper.withPendingInfo(eq(saved), any(PendingStoreInfoResponse.class))).thenReturn(response);

        var out = c.importFromImage(auth, file, "s-1");
        assertEquals(HttpStatus.ACCEPTED, out.getStatusCode());
    }

    @Test
    void importFromImage_shouldFailOnInvalidInput() {
        TicketController c = buildController(
                mock(TicketServiceImpl.class),
                mock(TicketRestMapper.class),
                mock(ImportTicketFromImageUseCase.class),
                new TicketImageProperties(),
                mock(UserServiceImpl.class));

        Authentication auth = mock(Authentication.class);
        UserServiceImpl userService = (UserServiceImpl) ReflectionTestUtils.getField(c, "userServiceImpl");
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(new User("u-1"));

        MockMultipartFile empty = new MockMultipartFile("file", "x.png", "image/png", new byte[]{});
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> c.importFromImage(auth, empty, null));
        assertEquals("El archivo de imagen es obligatorio", ex1.getMessage());

        MockMultipartFile badType = new MockMultipartFile("file", "x.gif", "image/gif", new byte[]{1});
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> c.importFromImage(auth, badType, null));
        assertEquals("Tipo de imagen no permitido. Use JPEG, PNG o WebP.", ex2.getMessage());
    }

    @Test
    void mineEndpoints_shouldFailWhenUnauthenticated() {
        TicketController c = buildController(
                mock(TicketServiceImpl.class),
                mock(TicketRestMapper.class),
                mock(ImportTicketFromImageUseCase.class),
                new TicketImageProperties(),
                mock(UserServiceImpl.class));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> c.listMyTickets(null));
        assertEquals("Usuario no autenticado", ex.getMessage());
    }

    private static TicketController buildController(
            TicketServiceImpl service,
            TicketRestMapper mapper,
            ImportTicketFromImageUseCase importUseCase,
            TicketImageProperties props,
            UserServiceImpl userService) {
        TicketController c = new TicketController();
        ReflectionTestUtils.setField(c, "ticketServiceImpl", service);
        ReflectionTestUtils.setField(c, "ticketMapper", mapper);
        ReflectionTestUtils.setField(c, "importTicketFromImageUseCase", importUseCase);
        ReflectionTestUtils.setField(c, "ticketImageProperties", props);
        ReflectionTestUtils.setField(c, "userServiceImpl", userService);
        return c;
    }
}
