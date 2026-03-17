package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
    private List<HouseholdApplianceEntity> aplliances = new ArrayList<>();

    public HouseholdEntity() {
    }

    public HouseholdEntity(String id) {
        this.id = id;
    }

    public HouseholdEntity(String id, String name, int members_count, List<UserEntity> users,
            List<FridgeEntity> fridges,
            List<HouseholdApplianceEntity> aplliances) {
        this.id = id;
        this.name = name;
        this.members_count = members_count;
        this.users = users;
        this.fridges = fridges;
        this.aplliances = aplliances;
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

    public List<HouseholdApplianceEntity> getAplliances() {
        return aplliances;
    }

    public void setAplliances(List<HouseholdApplianceEntity> aplliances) {
        this.aplliances = aplliances;
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
        HouseholdEntity other = (HouseholdEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
