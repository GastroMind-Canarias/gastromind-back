package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeItemEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.FridgeItemMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.FridgeItemJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
/**
 * Representa fridge item dentro del dominio de la aplicacion.
 */
public class FridgeItemAdapter implements FridgeItemRepository {

    @Autowired
    private FridgeItemJpaRepository fridgeItemJpaRepository;

    @Autowired
    private FridgeItemMapper fridgeItemMapper;
    /**
     * Registra un nuevo fridge item.
     * @param fridgeItem el producto de la nevera
     * @return resultado de la operacion solicitada.
     */

    @Override
    public FridgeItem save(FridgeItem fridgeItem) {
        FridgeItemEntity entity = fridgeItemMapper.toEntity(fridgeItem);
        return fridgeItemMapper.toDomain(fridgeItemJpaRepository.save(entity));
    }
    /**
     * Devuelve fridge item por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<FridgeItem> findById(String id) {
        return fridgeItemJpaRepository.findById(id).map(fridgeItemMapper::toDomain);
    }
    /**
     * Devuelve fridge item por fridge id.
     * @param fridgeId identificador de la nevera.
     * @return lista actual.
     */

    @Override
    public List<FridgeItem> findByFridgeId(String fridgeId) {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findByFridgeId(fridgeId);
        return fridgeItemMapper.toDomainList(entities);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        fridgeItemJpaRepository.deleteById(id);
    }
    /**
     * Realiza find expiring items.
     * @param fridgeId identificador de la nevera.
     * @param thresholdDate valor a utilizar.
     * @return lista actual.
     */

    @Override
    public List<FridgeItem> findExpiringItems(String fridgeId, java.time.LocalDate thresholdDate) {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findByFridgeIdAndExpirationDateBefore(fridgeId,
                thresholdDate);
        return fridgeItemMapper.toDomainList(entities);
    }
    /**
     * Devuelve fridge item por fridge id and category id.
     * @param fridgeId identificador de la nevera.
     * @param categoryId identificador de la categoria.
     * @return lista actual.
     */

    @Override
    public List<FridgeItem> findByFridgeIdAndCategoryId(String fridgeId, String categoryId) {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findByFridgeIdAndProductCategoryId(fridgeId,
                categoryId);
        return fridgeItemMapper.toDomainList(entities);
    }
    /**
     * Lista todos los fridge item.
     * @return lista actual.
     */

    @Override
    public List<FridgeItem> findAll() {
        List<FridgeItemEntity> entities = fridgeItemJpaRepository.findAll();
        return fridgeItemMapper.toDomainList(entities);
    }
}




