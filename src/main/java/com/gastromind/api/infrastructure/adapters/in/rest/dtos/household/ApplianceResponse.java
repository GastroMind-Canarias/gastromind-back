package com.gastromind.api.infrastructure.adapters.in.rest.dtos.household;

import com.gastromind.api.domain.models.enums.Appliance;

/**
 * DTO de salida para exponer un electrodomestico asociado a un hogar.
 */
public class ApplianceResponse {
    private String id;
    private Appliance appliance;
    private String householdId;
    /** Identificador del vinculo hogar-electrodomestico. */

    public String getId() {
        return id;
    }
    /** Define el identificador del vinculo. */

    public void setId(String id) {
        this.id = id;
    }
    /** Tipo de electrodomestico registrado. */

    public Appliance getAppliance() {
        return appliance;
    }
    /** Define el tipo de electrodomestico. */

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }
    /** Identificador del hogar propietario. */

    public String getHouseholdId() {
        return householdId;
    }
    /** Define el hogar propietario del registro. */

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }
}




