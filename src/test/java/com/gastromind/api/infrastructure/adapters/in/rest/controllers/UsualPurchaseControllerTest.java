package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.services.UsualPurchaseServiceImpl;
import com.gastromind.api.application.usecases.ListUsualPurchaseSuggestionsUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UsualPurchaseRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsualPurchaseControllerTest {

    @Test
    void crudMineAndSuggestions_shouldDelegateAndMap() {
        UsualPurchaseServiceImpl service = mock(UsualPurchaseServiceImpl.class);
        UsualPurchaseRestMapper mapper = mock(UsualPurchaseRestMapper.class);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        ListUsualPurchaseSuggestionsUseCase suggestions = mock(ListUsualPurchaseSuggestionsUseCase.class);
        Authentication auth = mock(Authentication.class);
        UsualPurchaseController c = buildController(service, mapper, userService, suggestions);

        User user = new User();
        user.setId("u-1");
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(user);

        UsualPurchase purchase = new UsualPurchase();
        UsualPurchaseResponse response = mock(UsualPurchaseResponse.class);
        when(service.findAll()).thenReturn(List.of(purchase));
        when(service.findAllByUserId("u-1")).thenReturn(List.of(purchase));
        when(service.findById("p-1")).thenReturn(purchase);
        when(service.findByIdForUser("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "u-1")).thenReturn(purchase);
        when(mapper.toResponseList(List.of(purchase))).thenReturn(List.of(response));
        when(mapper.toResponse(purchase)).thenReturn(response);

        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.listMine(auth).getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("p-1").getStatusCode());
        assertEquals(HttpStatus.OK, c.getMineById(auth, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa").getStatusCode());

        UsualPurchaseRequest req = mock(UsualPurchaseRequest.class);
        UsualPurchaseMeRequest meReq = mock(UsualPurchaseMeRequest.class);
        when(mapper.toDomain(req)).thenReturn(purchase);
        when(mapper.toDomainForMe(meReq, "u-1")).thenReturn(purchase);
        when(service.create(purchase)).thenReturn(purchase);
        when(service.update(eq("p-1"), any(UsualPurchase.class))).thenReturn(purchase);
        when(service.updateForUser(eq("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), any(UsualPurchase.class), eq("u-1"))).thenReturn(purchase);

        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.CREATED, c.createMine(auth, meReq).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("p-1", req).getStatusCode());
        assertEquals(HttpStatus.OK, c.updateMine(auth, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", meReq).getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, c.delete("p-1").getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.deleteMine(auth, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa").getStatusCode());
        verify(service).deleteForUser("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "u-1");

        ListUsualPurchaseSuggestionsUseCase.UsualPurchaseSuggestion row =
                new ListUsualPurchaseSuggestionsUseCase.UsualPurchaseSuggestion(
                        "prod-1", "Leche", new BigDecimal("2.0000"), "ud",
                        new BigDecimal("0.5000"), 3.2, 4, LocalDateTime.now(), true);
        when(suggestions.execute("owner", true, 30)).thenReturn(List.of(row));
        var suggestionsResp = c.listSuggestions(auth, true, 30);
        assertEquals(HttpStatus.OK, suggestionsResp.getStatusCode());
        assertEquals(1, suggestionsResp.getBody().size());
    }

    @Test
    void mineEndpoints_shouldFailWhenNoAuthentication() {
        UsualPurchaseController c = buildController(
                mock(UsualPurchaseServiceImpl.class),
                mock(UsualPurchaseRestMapper.class),
                mock(UserServiceImpl.class),
                mock(ListUsualPurchaseSuggestionsUseCase.class));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> c.listMine(null));
        assertEquals("Usuario no autenticado", ex.getMessage());
    }

    private static UsualPurchaseController buildController(
            UsualPurchaseServiceImpl service,
            UsualPurchaseRestMapper mapper,
            UserServiceImpl userService,
            ListUsualPurchaseSuggestionsUseCase suggestions) {
        UsualPurchaseController c = new UsualPurchaseController();
        ReflectionTestUtils.setField(c, "usualPurchaseServiceImpl", service);
        ReflectionTestUtils.setField(c, "usualPurchaseMapper", mapper);
        ReflectionTestUtils.setField(c, "userServiceImpl", userService);
        ReflectionTestUtils.setField(c, "listUsualPurchaseSuggestionsUseCase", suggestions);
        return c;
    }
}
