package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Role;

import java.util.Objects;

public class User {

    String id;
    String name;
    String email;
    String password;
    HouseHold houseHold_id;
    Role role;
    java.util.List<Allergen> allergens;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, String name, String email, String password, HouseHold houseHold_id, Role role,
            java.util.List<Allergen> allergens) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.houseHold_id = houseHold_id;
        this.role = role;
        this.allergens = allergens;
    }

    public java.util.List<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(java.util.List<Allergen> allergens) {
        this.allergens = allergens;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HouseHold getHouseHold_id() {
        return houseHold_id;
    }

    public void setHouseHold_id(HouseHold houseHold_id) {
        this.houseHold_id = houseHold_id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}