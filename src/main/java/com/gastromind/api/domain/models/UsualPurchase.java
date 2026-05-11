package com.gastromind.api.domain.models;

/**
 * Patrón de recompra por usuario: producto objetivo y cantidad habitual para sugerencias tras tickets.
 */
public class UsualPurchase {
    String id;
    User user_id;
    Product product_id;
    float target_quantity;

    public UsualPurchase(String id, User user_id, Product product_id, float target_quantity) {
        this.id = id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.target_quantity = target_quantity;
    }

    public UsualPurchase(String id) {
        this.id = id;
    }

    public UsualPurchase() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public Product getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }

    public float getTarget_quantity() {
        return target_quantity;
    }

    public void setTarget_quantity(float target_quantity) {
        this.target_quantity = target_quantity;
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
        UsualPurchase other = (UsualPurchase) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
