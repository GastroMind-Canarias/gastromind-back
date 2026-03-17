package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.ports.out.StoreRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.StoreJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.StoreMapper;
@Component
public class StoreAdapter implements StoreRepository {

    @Autowired
    StoreJpaRepository storeJpaRepository;

    @Autowired 
    StoreMapper storeMapper;

    @Override
    public com.gastromind.api.domain.models.Store save(com.gastromind.api.domain.models.Store store) {
        StoreEntity entity = storeMapper.toEntity(store);
        return storeMapper.toDomain(storeJpaRepository.save(entity));
    }

    @Override
    public Optional<com.gastromind.api.domain.models.Store> findById(String id) {
        return storeJpaRepository.findById(id).map(storeMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
       storeJpaRepository.deleteById(id);
    }

    @Override
    public List<com.gastromind.api.domain.models.Store> findAll() {
        List<StoreEntity> storeEntities = storeJpaRepository.findAll();
        return storeMapper.toDomainList(storeEntities);
    }

}
