package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "fridge")
public class FridgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private HouseholdEntity household;

    @OneToMany(mappedBy = "fridge", cascade = CascadeType.ALL)
    private List<FridgeItemEntity> items;

    public FridgeEntity() {
    }

    public FridgeEntity(String id) {
        this.id = id;
    }

    public FridgeEntity(String id, HouseholdEntity household, List<FridgeItemEntity> items) {
        this.id = id;
        this.household = household;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HouseholdEntity getHousehold() {
        return household;
    }

    public void setHousehold(HouseholdEntity household) {
        this.household = household;
    }

    public List<FridgeItemEntity> getItems() {
        return items;
    }

    public void setItems(List<FridgeItemEntity> items) {
        this.items = items;
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
        FridgeEntity other = (FridgeEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
