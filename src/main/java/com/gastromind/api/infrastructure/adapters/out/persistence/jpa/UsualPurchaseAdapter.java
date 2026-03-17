package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UsualPurchaseJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UsualPurchaseMapper;

@Component
public class UsualPurchaseAdapter implements UsualPurchaseRepository {

    @Autowired
    UsualPurchaseJpaRepository usualPurchaseJpaRepository;

    @Autowired
    UsualPurchaseMapper usualPurchaseMapper;

    @Override
    public UsualPurchase save(UsualPurchase usualPurchase) {
        UsualPurchaseEntity entity = usualPurchaseMapper.toEntity(usualPurchase);
        return usualPurchaseMapper.toDomain(usualPurchaseJpaRepository.save(entity));
    }

    @Override
    public Optional<UsualPurchase> findById(String id) {
        return usualPurchaseJpaRepository.findById(id).map(usualPurchaseMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        usualPurchaseJpaRepository.deleteById(id);
    }

    @Override
    public List<UsualPurchase> findAll() {
         List<UsualPurchaseEntity> usualPurchaseEntities = usualPurchaseJpaRepository.findAll();
        return usualPurchaseMapper.toDomainList(usualPurchaseEntities);
    }

}
