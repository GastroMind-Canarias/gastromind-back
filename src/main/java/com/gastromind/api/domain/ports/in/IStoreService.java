package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Store;

public interface IStoreService {
    List<Store> findAll();
    Store findById(String id);
    Store create(Store store);
    Store update(String id, Store store);
    void delete(String id);
}