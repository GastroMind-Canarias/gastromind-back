package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import com.gastromind.api.domain.models.enums.Appliance;

public class ApplianceResponse {
    private String id;
    private Appliance appliance;
    private String householdId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }
}
