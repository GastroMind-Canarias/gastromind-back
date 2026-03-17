package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.AllergenJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.AllergenMapper;
@Component
public class AllergenAdapter implements AllergenRepository {

    @Autowired
    AllergenJpaRepository allergenJpaRepository;

    @Autowired
    AllergenMapper allergenMapper;

    @Override
    public Allergen save(Allergen allergen) {
        AllergenEntity entity = allergenMapper.toEntity(allergen);
        return allergenMapper.toDomain(allergenJpaRepository.save(entity));

    }

    @Override
    public Optional<Allergen> findById(String id) {
        return allergenJpaRepository.findById(id).map(allergenMapper::toDomain);

    }

    @Override
    public void deleteById(String id) {
        allergenJpaRepository.deleteById(id);
    }

    @Override
    public List<Allergen> findAll() {
        List<AllergenEntity> allergenEntities = allergenJpaRepository.findAll();
        return allergenMapper.toDomainList(allergenEntities);
    }

}
