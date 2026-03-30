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
    private int members_count;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<FridgeEntity> fridges = new ArrayList<>();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    private List<HouseholdApplianceEntity> appliances = new ArrayList<>();

    public HouseholdEntity() {
    }

    public HouseholdEntity(String id) {
        this.id = id;
    }

    public HouseholdEntity(String id, String name, int members_count, List<UserEntity> users,
            List<FridgeEntity> fridges,
            List<HouseholdApplianceEntity> appliances) {
        this.id = id;
        this.name = name;
        this.members_count = members_count;
        this.users = users;
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

    public int getMembers_count() {
        return members_count;
    }

    public void setMembers_count(int members_count) {
        this.members_count = members_count;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
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
