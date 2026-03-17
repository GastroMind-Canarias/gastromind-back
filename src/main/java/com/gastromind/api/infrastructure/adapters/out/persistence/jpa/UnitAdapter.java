package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.out.UnitRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UnitJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UnitMapper;
@Component
public class UnitAdapter implements UnitRepository {

    @Autowired
    UnitJpaRepository unitJpaRepository;

    @Autowired
    UnitMapper unitMapper;

    @Override
    public Unit save(Unit unit) {
        UnitEntity entity = unitMapper.toEntity(unit);
        return unitMapper.toDomain(unitJpaRepository.save(entity));
    }

    @Override
    public Optional<Unit> findById(String id) {
        return unitJpaRepository.findById(id).map(unitMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        unitJpaRepository.deleteById(id);
    }

    @Override
    public List<Unit> findAll() {
        List<UnitEntity> unitEntities = unitJpaRepository.findAll();
        return unitMapper.toDomainList(unitEntities);
    }

    
}
