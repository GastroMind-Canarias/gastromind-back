package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.ports.in.IStoreService;
import com.gastromind.api.domain.ports.out.StoreRepository;

@Service
public class StoreServiceImpl implements IStoreService {

    private final StoreRepository repository;


    public StoreServiceImpl(StoreRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Store> findAll() {
        return repository.findAll();
    }

    @Override
    public Store findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Tienda no encontrada"));
    }

    @Override
    public Store create(Store store) {
        return repository.save(store);
    }

    @Override
    public Store update(String id, Store store) {
        findById(id);
        store.setId(id);
        return repository.save(store);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}