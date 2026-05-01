package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.Fridge;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para fridge.
 */
public interface FridgeRepository {
    Fridge save(Fridge fridge);

    Optional<Fridge> findById(String id);

    void deleteById(String id);

    List<Fridge> findAll();

    List<Fridge> findByHouseholdId(String householdId);

    Optional<Fridge> findFirstByHouseholdId(String householdId);
}
