package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ApplianceType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "household_appliances")
public class HouseholdApplianceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private ApplianceType appliance;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private HouseholdEntity household;

    public HouseholdApplianceEntity() {
    }

    public HouseholdApplianceEntity(String id) {
        this.id = id;
    }

    public HouseholdApplianceEntity(String id, ApplianceType appliance, HouseholdEntity household) {
        this.id = id;
        this.appliance = appliance;
        this.household = household;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApplianceType getAppliance() {
        return appliance;
    }

    public void setAppliance(ApplianceType appliance) {
        this.appliance = appliance;
    }

    public HouseholdEntity getHousehold() {
        return household;
    }

    public void setHousehold(HouseholdEntity household) {
        this.household = household;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

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
