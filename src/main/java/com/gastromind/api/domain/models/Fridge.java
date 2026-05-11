package com.gastromind.api.domain.models;

/**
 * Inventario del hogar: una nevera concreta vinculada a un {@link HouseHold}.
 */
public class Fridge {
    String id;
    HouseHold houseHold_id;

    public Fridge(String id, HouseHold houseHold_id) {
        this.id = id;
        this.houseHold_id = houseHold_id;
    }

    public Fridge(String id) {
        this.id = id;
    }

    public Fridge() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HouseHold getHouseHold_id() {
        return houseHold_id;
    }

    public void setHouseHold_id(HouseHold houseHold_id) {
        this.houseHold_id = houseHold_id;
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
        Fridge other = (Fridge) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
