package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "household")
public class HouseholdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    /**
     * Sin cascade hacia User: borrar el hogar no debe eliminar cuentas de usuario.
     * La relación se gestiona desde {@link UserEntity#household} (FK).
     */
    @OneToMany(mappedBy = "household")
    private List<UserEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<FridgeEntity> fridges = new ArrayList<>();

    /** Sin cascade (se gestionan vía su repositorio); cascade ALL desincronizaba deletes con el padre cargado. */
    @OneToMany(mappedBy = "household")
    private List<HouseholdApplianceEntity> appliances = new ArrayList<>();

    public HouseholdEntity() {
    }

    public HouseholdEntity(String id) {
        this.id = id;
    }

    public HouseholdEntity(String id, String name, List<UserEntity> members,
            List<FridgeEntity> fridges,
            List<HouseholdApplianceEntity> appliances) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.fridges = fridges;
        this.appliances = appliances;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(List<UserEntity> members) {
        this.members = members;
        if (members != null) {
            members.forEach(m -> m.setHousehold(this));
        }
    }

    public List<FridgeEntity> getFridges() {
        return fridges;
    }

    public void setFridges(List<FridgeEntity> fridges) {
        this.fridges = fridges;
    }

    public List<HouseholdApplianceEntity> getAppliances() {
        return appliances;
    }

    public void setAppliances(List<HouseholdApplianceEntity> appliances) {
        this.appliances = appliances;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HouseholdEntity that = (HouseholdEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
