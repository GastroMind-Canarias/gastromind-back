package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.domain.ports.out.StoreAliasRepository;
import com.gastromind.api.domain.ports.out.StoreRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StoreResolutionServiceTest {
    @Test
    void resolve_shouldUseAliasAndFallbackPending() {
        StoreRepository storeRepository = mock(StoreRepository.class);
        StoreAliasRepository aliasRepository = mock(StoreAliasRepository.class);
        PendingStoreService pendingStoreService = mock(PendingStoreService.class);
        StoreNameNormalizer normalizer = new StoreNameNormalizer();
        StoreResolutionService service = new StoreResolutionService(storeRepository, aliasRepository, pendingStoreService, normalizer);

        StoreAlias alias = new StoreAlias();
        alias.setStoreId("s-1");
        Store store = new Store("s-1", "Lidl");
        PendingStore pending = new PendingStore();
        pending.setId("p-1");
        when(storeRepository.findFirstByNameNorm("lidl")).thenReturn(Optional.empty());
        when(aliasRepository.findFirstByAliasNorm("lidl")).thenReturn(Optional.of(alias));
        when(storeRepository.findById("s-1")).thenReturn(Optional.of(store));
        when(storeRepository.findByNameNorm("lidl")).thenReturn(List.of());
        assertEquals("s-1", service.resolve(null, "LIDL SUPERMERCADOS S.A.U.").store().getId());

        when(aliasRepository.findFirstByAliasNorm("unknown")).thenReturn(Optional.empty());
        when(storeRepository.findFirstByNameNorm("unknown")).thenReturn(Optional.empty());
        when(storeRepository.findByNameNorm("unknown")).thenReturn(List.of());
        when(pendingStoreService.createOrUpdateSighting("Unknown")).thenReturn(pending);
        StoreResolutionResult unresolved = service.resolve(null, "Unknown");
        assertNull(unresolved.store());
        assertEquals("p-1", unresolved.pendingStore().getId());
    }
}
