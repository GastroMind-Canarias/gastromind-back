package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Store;

import java.util.List;

public interface IStoreService {
    List<Store> findAll();
    Store findById(String id);
    Store create(Store store);
    Store update(String id, Store store);
    void delete(String id);
}