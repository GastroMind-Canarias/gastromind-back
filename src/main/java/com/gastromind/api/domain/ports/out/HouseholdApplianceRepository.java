package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.HouseholdAppliance;

import java.util.List;
import java.util.Optional;

public interface HouseholdApplianceRepository {
    HouseholdAppliance save(HouseholdAppliance appliance);

    void deleteById(String id);

    void deleteAllByHouseholdId(String householdId);

    Optional<HouseholdAppliance> findById(String id);

    List<HouseholdAppliance> findByHouseholdId(String householdId);
}
