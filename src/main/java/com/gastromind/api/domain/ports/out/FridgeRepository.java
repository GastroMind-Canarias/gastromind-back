package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Fridge;

public interface FridgeRepository {
    Fridge save(Fridge fridge);

    Optional<Fridge> findById(String id);

    void deleteById(String id);

    List<Fridge> findAll();
}
