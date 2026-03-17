package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Unit;

public interface IUnitService {
    List<Unit> findAll();
    Unit findById(String id);
    Unit create(Unit unit);
    Unit update(String id, Unit unit);
    void delete(String id);
}