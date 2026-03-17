package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;

import com.gastromind.api.domain.ports.in.IFridgeService;
import com.gastromind.api.domain.ports.out.FridgeRepository;

@Service
public class FridgeServiceImpl implements IFridgeService {

    private final FridgeRepository repository;


    public FridgeServiceImpl(FridgeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Fridge> findAll() {
        return repository.findAll();
    }

    @Override
    public Fridge findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Nevera no encontrada"));
    }

    @Override
    public Fridge create(Fridge fridge) {
        return repository.save(fridge);
    }

    @Override
    public Fridge update(String id, Fridge fridge) {
        findById(id);
        fridge.setId(id);
        return repository.save(fridge);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}