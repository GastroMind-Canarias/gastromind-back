package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ApplianceType;
import jakarta.persistence.*;

@Entity
@Table(
        name = "household_appliances",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_household_appliance",
                columnNames = {"household_id", "appliance"}))
/**
 * Representa household appliance dentro del dominio de la aplicacion.
 */
public class HouseholdApplianceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private ApplianceType appliance;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private HouseholdEntity household;
    /**
     * Constructor de household appliance.
     */

    public HouseholdApplianceEntity() {
    }
    /**
     * Constructor de household appliance.
     * @param id el identificador del recurso
     */

    public HouseholdApplianceEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de household appliance.
     * @param id el identificador del recurso
     * @param appliance valor a utilizar.
     * @param household valor a utilizar.
     */

    public HouseholdApplianceEntity(String id, ApplianceType appliance, HouseholdEntity household) {
        this.id = id;
        this.appliance = appliance;
        this.household = household;
    }
    /**
     * Devuelve id.
     * @return valor actual.
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

    public ApplianceType getAppliance() {
        return appliance;
    }
    /**
     * Define appliance.
     * @param appliance valor a utilizar.
     */

    public void setAppliance(ApplianceType appliance) {
        this.appliance = appliance;
    }
    /**
     * Devuelve household.
     * @return resultado de la operacion solicitada.
     */

    public HouseholdEntity getHousehold() {
        return household;
    }
    /**
     * Define household.
     * @param household valor a utilizar.
     */

    public void setHousehold(HouseholdEntity household) {
        this.household = household;
    }
    /**
     * Calcula el hash de la instancia.
     * @return valor configurado.
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param obj valor a utilizar.
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HouseholdApplianceEntity other = (HouseholdApplianceEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




