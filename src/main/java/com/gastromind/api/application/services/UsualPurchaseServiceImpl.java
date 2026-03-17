package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.in.IUsualPurchaseService;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;

@Service
public class UsualPurchaseServiceImpl implements IUsualPurchaseService {

    private final UsualPurchaseRepository repository;


    public UsualPurchaseServiceImpl(UsualPurchaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UsualPurchase> findAll() {
        return repository.findAll();
    }

    @Override
    public UsualPurchase findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Producto más comprado no encontrado"));
    }

    @Override
    public UsualPurchase create(UsualPurchase usualPurchase) {
        return repository.save(usualPurchase);
    }

    @Override
    public UsualPurchase update(String id, UsualPurchase usualPurchase) {
        findById(id);
        usualPurchase.setId(id);
        return repository.save(usualPurchase);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}