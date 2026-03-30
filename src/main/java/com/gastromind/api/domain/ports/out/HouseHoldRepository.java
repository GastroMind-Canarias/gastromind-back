package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.HouseHold;

import java.util.List;
import java.util.Optional;

public interface HouseHoldRepository {
    HouseHold save(HouseHold houseHold);

    Optional<HouseHold> findById(String id);

    void deleteById(String id);

    List<HouseHold> findAll();
}
