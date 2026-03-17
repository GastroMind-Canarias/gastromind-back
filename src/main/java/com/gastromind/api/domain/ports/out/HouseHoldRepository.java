package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.HouseHold;

public interface HouseHoldRepository {
    HouseHold save(HouseHold houseHold);

    Optional<HouseHold> findById(String id);

    void deleteById(String id);

    List<HouseHold> findAll();
}
