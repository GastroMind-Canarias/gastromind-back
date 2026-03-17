package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.FridgeItemMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.FridgeItemJpaRepository;

@Component
public class FridgeItemAdapter implements FridgeItemRepository {

    @Autowired
    private FridgeItemJpaRepository fridgeItemJpaRepository;

    @Autowired
    private FridgeItemMapper fridgeItemMapper;

    @Override
    public FridgeItem save(FridgeItem fridgeItem) {
        FridgeItemEntity entity = fridgeItemMapper.toEntity(fridgeItem);
        return fridgeItemMapper.toDomain(fridgeItemJpaRepository.save(entity));
    }

    @Override
    public Optional<FridgeItem> findById(String id) {
        return fridgeItemJpaRepository.findById(id).map(fridgeItemMapper::toDomain);
    }

    @Override
    public List<FridgeItem> findByFridgeId(String fridgeId) {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findByFridgeId(fridgeId);
        return fridgeItemMapper.toDomainList(entities);
    }

    @Override
    public void deleteById(String id) {
        fridgeItemJpaRepository.deleteById(id);
    }

    @Override
    public List<FridgeItem> findExpiringItems(String fridgeId, java.time.LocalDate thresholdDate) {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findByFridgeIdAndExpirationDateBefore(fridgeId,
                thresholdDate);
        return fridgeItemMapper.toDomainList(entities);
    }

    @Override
    public List<FridgeItem> findByFridgeIdAndCategoryId(String fridgeId, String categoryId) {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findByFridgeIdAndProductCategoryId(fridgeId,
                categoryId);
        return fridgeItemMapper.toDomainList(entities);
    }

    @Override
    public List<FridgeItem> findAll() {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findAll();
        return fridgeItemMapper.toDomainList(entities);
    }
}