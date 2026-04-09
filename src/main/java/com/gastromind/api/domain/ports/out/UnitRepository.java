package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Unit;

import java.util.List;
import java.util.Optional;

public interface UnitRepository {
    Unit save(Unit unit);

    Optional<Unit> findById(String id);

    void deleteById(String id);

    List<Unit> findAll();

    Optional<Unit> findFirstByNameIgnoreCase(String name);
}
