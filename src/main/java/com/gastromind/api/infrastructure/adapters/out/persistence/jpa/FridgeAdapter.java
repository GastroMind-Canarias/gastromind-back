package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.FridgeMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.FridgeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class FridgeAdapter implements FridgeRepository {

    @Autowired
    FridgeJpaRepository fridgeJpaRepository;

    @Autowired
    FridgeMapper fridgeMapper;

    @Override
    public Fridge save(Fridge fridge) {
        FridgeEntity entity = fridgeMapper.toEntity(fridge);
        return fridgeMapper.toDomain(fridgeJpaRepository.save(entity));
    }

    @Override
    public Optional<Fridge> findById(String id) {
        return fridgeJpaRepository.findById(id).map(fridgeMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        fridgeJpaRepository.deleteById(id);
    }

    @Override
    public List<Fridge> findAll() {
        List<FridgeEntity> fridgeEntities = fridgeJpaRepository.findAll();
        return fridgeMapper.toDomainList(fridgeEntities);
    }

    @Override
    public List<Fridge> findByHouseholdId(String householdId) {
        return fridgeMapper.toDomainList(fridgeJpaRepository.findByHousehold_Id(householdId));
    }

}
