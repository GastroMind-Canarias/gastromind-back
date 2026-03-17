package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.ports.out.HouseholdApplianceRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.HouseholdApplianceMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.HouseholdApplianceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HouseholdApplianceAdapter implements HouseholdApplianceRepository {

    @Autowired
    private HouseholdApplianceJpaRepository jpaRepository;

    @Autowired
    private HouseholdApplianceMapper mapper;

    @Override
    public HouseholdAppliance save(HouseholdAppliance appliance) {
        HouseholdApplianceEntity entity = mapper.toEntity(appliance);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<HouseholdAppliance> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<HouseholdAppliance> findByHouseholdId(String householdId) {
        return mapper.toDomainList(jpaRepository.findByHouseholdId(householdId));
    }
}
