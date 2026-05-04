package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.StoreServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.PendingStorePromoteRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.PendingStoreRejectRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreAliasRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreAliasResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.PendingStoreResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.StoreRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StoreControllerTest {
    @Test
    void shouldExposeAliasAndPendingAdminFlows() {
        StoreServiceImpl storeService = mock(StoreServiceImpl.class);
        StoreRestMapper mapper = mock(StoreRestMapper.class);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        Authentication auth = mock(Authentication.class);
        StoreController c = new StoreController();
        ReflectionTestUtils.setField(c, "storeServiceImpl", storeService);
        ReflectionTestUtils.setField(c, "storeMapper", mapper);
        ReflectionTestUtils.setField(c, "userServiceImpl", userService);

        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(new User("u-1"));

        StoreAlias alias = new StoreAlias();
        StoreAliasResponse aliasResponse = new StoreAliasResponse("a-1", "s-1", "LIDL SUPERMERCADOS");
        when(storeService.createAliasForUser("u-1", "s-1", "LIDL SUPERMERCADOS")).thenReturn(alias);
        when(mapper.toAliasResponse(alias)).thenReturn(aliasResponse);
        assertEquals(HttpStatus.CREATED, c.createAliasForStore(auth, "s-1", new StoreAliasRequest("LIDL SUPERMERCADOS")).getStatusCode());

        PendingStore pendingStore = new PendingStore();
        PendingStoreResponse pendingResponse = new PendingStoreResponse("p-1", "Unknown", 1, "OPEN", null, null);
        when(storeService.listPendingStores()).thenReturn(List.of(pendingStore));
        when(mapper.toPendingResponseList(any())).thenReturn(List.of(pendingResponse));
        assertEquals(HttpStatus.OK, c.listPendingStores().getStatusCode());

        when(storeService.promotePendingStore(eq("p-1"), eq("s-1"), eq(null))).thenReturn(pendingStore);
        when(mapper.toPendingResponse(pendingStore)).thenReturn(pendingResponse);
        assertEquals(HttpStatus.OK, c.promotePendingStore("p-1", new PendingStorePromoteRequest("s-1", null)).getStatusCode());

        when(storeService.rejectPendingStore("p-1", "bad scan")).thenReturn(pendingStore);
        assertEquals(HttpStatus.OK, c.rejectPendingStore("p-1", new PendingStoreRejectRequest("bad scan")).getStatusCode());
    }
}
