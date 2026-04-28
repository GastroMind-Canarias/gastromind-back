package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa house hold del negocio.
 */
public class HouseHold {
    String id;
    String name;
    List<User> members = new ArrayList<>();
    private List<Appliance> appliances = new ArrayList<>();
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param members valor a utilizar.
     * @param appliances valor a utilizar.
     */

    public HouseHold(String id, String name, List<User> members, List<Appliance> appliances) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.appliances = appliances;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public HouseHold(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     */

    public HouseHold() {
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
     * Devuelve name.
     * @return el valor actual
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
     * Devuelve members count.
     * @return el hash calculado
     */

    public int getMembersCount() {
        return members != null ? members.size() : 0;
    }
    /**
     * Devuelve members.
     * @return lista actual.
     */

    public List<User> getMembers() {
        return members;
    }
    /**
     * Define members.
     * @param members valor a utilizar.
     */

    public void setMembers(List<User> members) {
        this.members = members;
    }
    /**
     * Devuelve appliances.
     * @return lista actual.
     */

    public List<Appliance> getAppliances() {
        return appliances;
    }
    /**
     * Define appliances.
     * @param appliances valor a utilizar.
     */

    public void setAppliances(List<Appliance> appliances) {
        this.appliances = appliances;
    }
    /**
     * Realiza add appliance.
     * @param appliance valor a utilizar.
     */

    public void addAppliance(Appliance appliance) {
        if (this.appliances == null) this.appliances = new ArrayList<>();
        this.appliances.add(appliance);
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HouseHold houseHold = (HouseHold) o;
        return Objects.equals(getId(), houseHold.getId());
    }
    /**
     * Calcula el hash de esta instancia.
     * @return el hash calculado
     */

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
