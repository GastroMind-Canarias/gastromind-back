package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Store;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para store.
 */
public interface StoreRepository {
    Store save(Store store);

    Optional<Store> findById(String id);

    void deleteById(String id);

    List<Store> findAll();

    Optional<Store> findFirstByNameIgnoreCase(String name);

    Optional<Store> findFirstByNameNorm(String nameNorm);

    List<Store> findByNameNorm(String nameNorm);
}
