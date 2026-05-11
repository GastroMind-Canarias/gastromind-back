package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.ports.out.StoreRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.StoreEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.StoreMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.StoreJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa store dentro del dominio de la aplicacion.
 */
public class StoreAdapter implements StoreRepository {

    @Autowired
    StoreJpaRepository storeJpaRepository;

    @Autowired 
    StoreMapper storeMapper;
    /**
     * Registra un nuevo store adapter.
     * @param store la tienda
     * @return resultado de la operacion solicitada.
     */

    @Override
    public com.gastromind.api.domain.models.Store save(com.gastromind.api.domain.models.Store store) {
        StoreEntity entity = storeMapper.toEntity(store);
        return storeMapper.toDomain(storeJpaRepository.save(entity));
    }
    /**
     * Devuelve store adapter por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<com.gastromind.api.domain.models.Store> findById(String id) {
        return storeJpaRepository.findById(id).map(storeMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
       storeJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los store adapter.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public List<com.gastromind.api.domain.models.Store> findAll() {
        List<StoreEntity> storeEntities = storeJpaRepository.findAll();
        return storeMapper.toDomainList(storeEntities);
    }
    /**
     * Realiza find first by name ignore case.
     * @param name el nombre
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<com.gastromind.api.domain.models.Store> findFirstByNameIgnoreCase(String name) {
        return storeJpaRepository.findFirstByNameIgnoreCase(name).map(storeMapper::toDomain);
    }

    @Override
    public Optional<com.gastromind.api.domain.models.Store> findFirstByNameNorm(String nameNorm) {
        return storeJpaRepository.findFirstByNameNorm(nameNorm).map(storeMapper::toDomain);
    }

    @Override
    public List<com.gastromind.api.domain.models.Store> findByNameNorm(String nameNorm) {
        return storeMapper.toDomainList(storeJpaRepository.findByNameNorm(nameNorm));
    }

}




