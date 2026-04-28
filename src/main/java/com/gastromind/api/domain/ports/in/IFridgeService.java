package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Fridge;

import java.util.List;

/**
 * Define las operaciones de negocio para neveras de hogar.
 */
public interface IFridgeService {
    List<Fridge> findAll();
    Fridge findById(String id);
    Fridge create(Fridge fridge);
    Fridge update(String id, Fridge fridge);
    void delete(String id);
}
