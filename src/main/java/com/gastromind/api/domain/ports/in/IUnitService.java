package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Unit;

import java.util.List;

/**
 * Define las operaciones de negocio para unidades de medida.
 */
public interface IUnitService {
    List<Unit> findAll();
    Unit findById(String id);
    Unit create(Unit unit);
    Unit update(String id, Unit unit);
    void delete(String id);
}
