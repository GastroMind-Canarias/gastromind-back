package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Unit;

public interface UnitRepository {
    Unit save(Unit unit);

    Optional<Unit> findById(String id);

    void deleteById(String id);

    List<Unit> findAll();
}
