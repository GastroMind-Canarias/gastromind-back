package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.exceptions.ConflictException;
import com.gastromind.api.domain.exceptions.RateLimitExceededException;
import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.StoreAlias;
import com.gastromind.api.domain.ports.in.IStoreService;
import com.gastromind.api.domain.ports.out.AliasRateLimitPort;
import com.gastromind.api.domain.ports.out.StoreAliasRepository;
import com.gastromind.api.domain.ports.out.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar tiendas.
 */
public class StoreServiceImpl implements IStoreService {

    private final StoreRepository repository;
    private final StoreAliasRepository storeAliasRepository;
    private final AliasRateLimitPort aliasRateLimitPort;
    private final StoreNameNormalizer normalizer;
    private final PendingStoreService pendingStoreService;
    /**
     * Crea el servicio con el repositorio de tiendas.
     * @param repository repositorio de persistencia de tiendas
     */


    public StoreServiceImpl(
            StoreRepository repository,
            StoreAliasRepository storeAliasRepository,
            AliasRateLimitPort aliasRateLimitPort,
            StoreNameNormalizer normalizer,
            PendingStoreService pendingStoreService) {
        this.repository = repository;
        this.storeAliasRepository = storeAliasRepository;
        this.aliasRateLimitPort = aliasRateLimitPort;
        this.normalizer = normalizer;
        this.pendingStoreService = pendingStoreService;
    }
    /**
     * Devuelve todas las tiendas registradas.
     * @return listado completo de tiendas
     */

    @Override
    public List<Store> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una tienda por su identificador.
     * @param id identificador de la tienda
     * @return tienda encontrada
     * @throws NotFoundException si no existe una tienda con ese id
     */

    @Override
    public Store findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Tienda no encontrada"));
    }
    /**
     * Crea una nueva tienda.
     * @param store datos de la tienda a crear
     * @return tienda persistida
     */

    @Override
    public Store create(Store store) {
        store.setNameNorm(normalizer.normalize(store.getName()));
        return repository.save(store);
    }
    /**
     * Define una tienda existente.
     * @param id identificador de la tienda a actualizar
     * @param store nuevos datos de la tienda
     * @return tienda actualizada
     * @throws NotFoundException si no existe una tienda con ese id
     */

    @Override
    public Store update(String id, Store store) {
        findById(id);
        store.setId(id);
        store.setNameNorm(normalizer.normalize(store.getName()));
        return repository.save(store);
    }
    /**
     * Elimina una tienda por su identificador.
     * @param id identificador de la tienda a eliminar
     * @throws NotFoundException si no existe una tienda con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public StoreAlias createAliasForUser(String userId, String storeId, String aliasName) {
        if (!aliasRateLimitPort.allowAliasCreation(userId)) {
            throw new RateLimitExceededException("Has alcanzado el limite de creacion de aliases. Intenta mas tarde.");
        }
        Store store = findById(storeId);
        String aliasNorm = normalizer.normalize(aliasName);
        if (aliasNorm.isEmpty()) {
            throw new IllegalArgumentException("Alias de tienda invalido");
        }
        if (storeAliasRepository.existsByStoreIdAndAliasNorm(store.getId(), aliasNorm)) {
            throw new ConflictException("El alias ya existe para esta tienda");
        }
        StoreAlias alias = new StoreAlias();
        alias.setStoreId(store.getId());
        alias.setAlias(aliasName.trim());
        alias.setAliasNorm(aliasNorm);
        return storeAliasRepository.save(alias);
    }

    @Override
    public List<StoreAlias> listAliases(String storeId) {
        findById(storeId);
        return storeAliasRepository.findByStoreId(storeId);
    }

    @Override
    public void deleteAlias(String aliasId) {
        storeAliasRepository.findById(aliasId)
                .orElseThrow(() -> new NotFoundException("Alias no encontrado"));
        storeAliasRepository.deleteById(aliasId);
    }

    @Override
    public List<PendingStore> listPendingStores() {
        return pendingStoreService.listOpen();
    }

    @Override
    public PendingStore rejectPendingStore(String pendingId, String reason) {
        return pendingStoreService.reject(pendingId, reason);
    }

    @Override
    public PendingStore promotePendingStore(String pendingId, String existingStoreId, String newStoreName) {
        Store target;
        if (existingStoreId != null && !existingStoreId.isBlank()) {
            target = findById(existingStoreId);
        } else {
            if (newStoreName == null || newStoreName.isBlank()) {
                throw new IllegalArgumentException("Debe indicar store_id existente o store_name para crear");
            }
            Store store = new Store();
            store.setName(newStoreName.trim());
            store.setNameNorm(normalizer.normalize(store.getName()));
            target = repository.save(store);
        }
        return pendingStoreService.promote(pendingId, target);
    }
}




