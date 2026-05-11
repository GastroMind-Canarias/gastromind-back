package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.HouseHold;

import java.util.List;
import java.util.Optional;

/**
 * Hogares, miembros y datos compartidos entre usuarios de la misma cuenta familiar.
 */
public interface HouseHoldRepository {
    HouseHold save(HouseHold houseHold);

    boolean existsById(String id);

    Optional<HouseHold> findById(String id);

    void deleteById(String id);

    List<HouseHold> findAll();
}
