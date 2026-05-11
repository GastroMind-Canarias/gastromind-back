package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

/**
 * Vínculo persistido entre un hogar y un tipo de electrodoméstico (horno, batidora, etc.).
 */
public class HouseholdAppliance {
    private String id;
    private Appliance appliance;
    private String householdId;

    public HouseholdAppliance() {
    }

    public HouseholdAppliance(String id, Appliance appliance, String householdId) {
        this.id = id;
        this.appliance = appliance;
        this.householdId = householdId;
    }

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
