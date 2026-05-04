package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.enums.PendingStoreStatus;
import com.gastromind.api.domain.ports.out.PendingStoreCachePort;
import com.gastromind.api.domain.ports.out.PendingStoreRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PendingStoreServiceTest {
    @Test
    void createOrUpdateSighting_shouldPersistAndCache() {
        PendingStoreRepository repository = mock(PendingStoreRepository.class);
        PendingStoreCachePort cachePort = mock(PendingStoreCachePort.class);
        StoreNameNormalizer normalizer = new StoreNameNormalizer();
        PendingStoreService service = new PendingStoreService(repository, cachePort, normalizer);
        when(repository.findFirstByDetectedNameNormAndStatus("lidl", PendingStoreStatus.OPEN)).thenReturn(Optional.empty());
        when(repository.save(any(PendingStore.class))).thenAnswer(inv -> inv.getArgument(0));

        PendingStore out = service.createOrUpdateSighting("LIDL SUPERMERCADOS S.A.U.");
        assertEquals(1, out.getSightingsCount());
        verify(cachePort).rememberPendingSighting("lidl");
    }
}
