package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.HouseholdApplianceMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.HouseholdApplianceJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HouseholdApplianceAdapterTest {

    @Mock
    private HouseholdApplianceJpaRepository jpaRepository;
    @Mock
    private HouseholdApplianceMapper mapper;

    @InjectMocks
    private HouseholdApplianceAdapter adapter;

    @Test
    void allMethods_delegate() {
        HouseholdAppliance domain = new HouseholdAppliance("ha-1", Appliance.HORNO, "h-1");
        HouseholdApplianceEntity entity = new HouseholdApplianceEntity();
        HouseholdApplianceEntity saved = new HouseholdApplianceEntity();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(domain);
        assertEquals(domain, adapter.save(domain));

        when(jpaRepository.findById("x")).thenReturn(Optional.empty());
        assertTrue(adapter.findById("x").isEmpty());
        when(jpaRepository.findById("ha-1")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        assertEquals(Optional.of(domain), adapter.findById("ha-1"));

        when(jpaRepository.findByHousehold_Id("h-1")).thenReturn(List.of(entity));
        when(mapper.toDomainList(List.of(entity))).thenReturn(List.of(domain));
        assertEquals(List.of(domain), adapter.findByHouseholdId("h-1"));

        adapter.deleteById("ha-1");
        verify(jpaRepository).deleteById("ha-1");

        adapter.deleteAllByHouseholdId("h-1");
        verify(jpaRepository).deleteAllForHousehold("h-1");
        verify(jpaRepository).flush();
    }
}
