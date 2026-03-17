package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.in.IUnitService;
import com.gastromind.api.domain.ports.out.UnitRepository;

@Service
public class UnitServiceImpl implements IUnitService {

    private final UnitRepository repository;


    public UnitServiceImpl(UnitRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Unit> findAll() {
        return repository.findAll();
    }

    @Override
    public Unit findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Unidad de Medida no encontrada"));
    }

    @Override
    public Unit create(Unit unit) {
        return repository.save(unit);
    }

    @Override
    public Unit update(String id, Unit unit) {
        findById(id);
        unit.setId(id);
        return repository.save(unit);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}