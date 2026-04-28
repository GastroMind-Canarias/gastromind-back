package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

/**
 * Modelo de dominio para un electrodoméstico asociado al hogar.
 */
public class HouseholdAppliance {
    private String id;
    private Appliance appliance;
    private String householdId;
    /**
     * Crea una nueva instancia.
     */

    public HouseholdAppliance() {
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param appliance valor a utilizar.
     * @param householdId el identificador del hogar
     */

    public HouseholdAppliance(String id, Appliance appliance, String householdId) {
        this.id = id;
        this.appliance = appliance;
        this.householdId = householdId;
    }
    /**
     * Devuelve id.
     * @return el valor actual
     */

    public String getId() {
        return id;
    }
    /**
     * Define id.
     * @param id el identificador del recurso
     */

    public void setId(String id) {
        this.id = id;
    }
    /**
     * Devuelve appliance.
     * @return resultado de la operacion solicitada.
     */

    public Appliance getAppliance() {
        return appliance;
    }
    /**
     * Define appliance.
     * @param appliance valor a utilizar.
     */

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }
    /**
     * Devuelve household id.
     * @return el valor actual
     */

    public String getHouseholdId() {
        return householdId;
    }
    /**
     * Define household id.
     * @param householdId el identificador del hogar
     */

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }
}
