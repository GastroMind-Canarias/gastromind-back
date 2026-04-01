package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HouseHold {
    String id;
    String name;
    List<User> members = new ArrayList<>();
    private List<Appliance> appliances = new ArrayList<>();

    /**
     * Constructor con todos los parametros
     * 
     * @param id         id de la casa
     * @param name       nombere de la casa
     * @param members    miembros de la casa
     * @param appliances utensilios de la casa
     */
    public HouseHold(String id, String name, List<User> members, List<Appliance> appliances) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.appliances = appliances;
    }

    /**
     * Constructor con id
     * 
     * @param id id de la casa
     */
    public HouseHold(String id) {
        this.id = id;
    }

    /**
     * Constructor vacio
     */
    public HouseHold() {
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

    public int getMembersCount() {
        return members != null ? members.size() : 0;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public void setAppliances(List<Appliance> appliances) {
        this.appliances = appliances;
    }

    public void addAppliance(Appliance appliance) {
        if (this.appliances == null) this.appliances = new ArrayList<>();
        this.appliances.add(appliance);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HouseHold houseHold = (HouseHold) o;
        return Objects.equals(getId(), houseHold.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
