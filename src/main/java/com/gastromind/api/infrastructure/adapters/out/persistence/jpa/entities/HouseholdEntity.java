package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "household")
/**
 * Representa household dentro del dominio de la aplicacion.
 */
public class HouseholdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @OneToMany(mappedBy = "household")
    private List<UserEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<FridgeEntity> fridges = new ArrayList<>();

    @OneToMany(mappedBy = "household")
    private List<HouseholdApplianceEntity> appliances = new ArrayList<>();
    /**
     * Constructor de household.
     */

    public HouseholdEntity() {
    }
    /**
     * Constructor de household.
     * @param id el identificador del recurso
     */

    public HouseholdEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de household.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param members valor a utilizar.
     * @param fridges valor a utilizar.
     * @param appliances valor a utilizar.
     */

    public HouseholdEntity(String id, String name, List<UserEntity> members,
            List<FridgeEntity> fridges,
            List<HouseholdApplianceEntity> appliances) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.fridges = fridges;
        this.appliances = appliances;
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
     * Devuelve name.
     * @return valor actual.
     */

    public String getName() {
        return name;
    }
    /**
     * Define name.
     * @param name el nombre
     */

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Devuelve members.
     * @return lista actual.
     */

    public List<UserEntity> getMembers() {
        return members;
    }
    /**
     * Define members.
     * @param members valor a utilizar.
     */

    public void setMembers(List<UserEntity> members) {
        this.members = members;
        if (members != null) {
            members.forEach(m -> m.setHousehold(this));
        }
    }
    /**
     * Devuelve fridges.
     * @return lista actual.
     */

    public List<FridgeEntity> getFridges() {
        return fridges;
    }
    /**
     * Define fridges.
     * @param fridges valor a utilizar.
     */

    public void setFridges(List<FridgeEntity> fridges) {
        this.fridges = fridges;
    }
    /**
     * Devuelve appliances.
     * @return lista actual.
     */

    public List<HouseholdApplianceEntity> getAppliances() {
        return appliances;
    }
    /**
     * Define appliances.
     * @param appliances valor a utilizar.
     */

    public void setAppliances(List<HouseholdApplianceEntity> appliances) {
        this.appliances = appliances;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HouseholdEntity that = (HouseholdEntity) o;
        return Objects.equals(getId(), that.getId());
    }
    /**
     * Calcula el hash de la instancia.
     * @return valor configurado.
     */

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}




