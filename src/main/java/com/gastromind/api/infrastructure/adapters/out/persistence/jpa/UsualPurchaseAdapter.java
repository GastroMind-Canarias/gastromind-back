package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UsualPurchaseEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UsualPurchaseMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UsualPurchaseJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
/**
 * Representa usual purchase dentro del dominio de la aplicacion.
 */
public class UsualPurchaseAdapter implements UsualPurchaseRepository {

    @Autowired
    UsualPurchaseJpaRepository usualPurchaseJpaRepository;

    @Autowired
    UsualPurchaseMapper usualPurchaseMapper;
    /**
     * Registra un nuevo usual purchase.
     * @param usualPurchase valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public UsualPurchase save(UsualPurchase usualPurchase) {
        UsualPurchaseEntity entity = usualPurchaseMapper.toEntity(usualPurchase);
        return usualPurchaseMapper.toDomain(usualPurchaseJpaRepository.save(entity));
    }
    /**
     * Devuelve usual purchase por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<UsualPurchase> findById(String id) {
        return usualPurchaseJpaRepository.findById(id).map(usualPurchaseMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        usualPurchaseJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los usual purchase.
     * @return lista actual.
     */

    @Override
    public List<UsualPurchase> findAll() {
         List<UsualPurchaseEntity> usualPurchaseEntities = usualPurchaseJpaRepository.findAll();
        return usualPurchaseMapper.toDomainList(usualPurchaseEntities);
    }
    /**
     * Realiza find all by user id.
     * @param userId el identificador del usuario
     * @return lista actual.
     */

    @Override
    public List<UsualPurchase> findAllByUserId(String userId) {
        return usualPurchaseMapper.toDomainList(usualPurchaseJpaRepository.findByUser_Id(userId));
    }
    /**
     * Devuelve usual purchase por user id and product id.
     * @param userId el identificador del usuario
     * @param productId valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<UsualPurchase> findByUserIdAndProductId(String userId, String productId) {
        return usualPurchaseJpaRepository.findByUser_IdAndProduct_Id(userId, productId)
                .map(usualPurchaseMapper::toDomain);
    }
}




