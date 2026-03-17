package com.gastromind.api.domain.ports.in;
import java.util.List;

import com.gastromind.api.domain.models.Fridge;

public interface IFridgeService {
    List<Fridge> findAll();
    Fridge findById(String id);
    Fridge create(Fridge fridge);
    Fridge update(String id, Fridge fridge);
    void delete(String id);
}
