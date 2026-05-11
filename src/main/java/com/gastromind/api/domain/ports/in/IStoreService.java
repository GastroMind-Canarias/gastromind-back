package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.StoreAlias;

import java.util.List;

/**
 * Puerto de entrada para tiendas del catálogo, alias y flujo de tiendas pendientes de revisión.
 */
public interface IStoreService {
    List<Store> findAll();
    Store findById(String id);
    Store create(Store store);
    Store update(String id, Store store);
    void delete(String id);
    StoreAlias createAliasForUser(String userId, String storeId, String aliasName);
    List<StoreAlias> listAliases(String storeId);
    void deleteAlias(String aliasId);
    List<PendingStore> listPendingStores();
    PendingStore rejectPendingStore(String pendingId, String reason);
    PendingStore promotePendingStore(String pendingId, String existingStoreId, String newStoreName);
}
