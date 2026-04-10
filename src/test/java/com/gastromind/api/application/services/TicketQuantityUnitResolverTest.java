package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.out.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketQuantityUnitResolverTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private TicketQuantityUnitResolver resolver;

    @Test
    void resolveFromAiCode_mapsKg() {
        Unit u = new Unit("u1", "Kilogramos");
        when(unitRepository.findFirstByNameIgnoreCase("Kilogramos")).thenReturn(Optional.of(u));
        assertEquals(u, resolver.resolveFromAiCode("kg"));
    }

    @Test
    void resolveFromAiCode_fallsBackToUnidades() {
        Unit u = new Unit("u2", "Unidades");
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(u));
        assertEquals(u, resolver.resolveFromAiCode("xyz"));
    }

    @Test
    void resolveFromAiCode_throwsWhenNoUnidadesSeed() {
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> resolver.resolveFromAiCode("ud"));
    }

    @Test
    void canonicalCode_mapsNames() {
        assertEquals("kg", TicketQuantityUnitResolver.canonicalCode(new Unit("1", "Kilogramos")));
        assertEquals("ud", TicketQuantityUnitResolver.canonicalCode(new Unit("1", "Unidades")));
        assertEquals("ud", TicketQuantityUnitResolver.canonicalCode(null));
    }
}
