package com.gastromind.api.domain.models;

/**
 * Modelo de dominio para una compra habitual detectada.
 */
public class UsualPurchase {
    String id;
    User user_id;
    Product product_id;
    float target_quantity;
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param user_id valor a utilizar.
     * @param product_id valor a utilizar.
     * @param target_quantity valor a utilizar.
     */

    public UsualPurchase(String id, User user_id, Product product_id, float target_quantity) {
        this.id = id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.target_quantity = target_quantity;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public UsualPurchase(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     */

    public UsualPurchase() {
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
     * Devuelve user id.
     * @return resultado de la operacion solicitada.
     */

    public User getUser_id() {
        return user_id;
    }
    /**
     * Define user id.
     * @param user_id valor a utilizar.
     */

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }
    /**
     * Devuelve product id.
     * @return resultado de la operacion solicitada.
     */

    public Product getProduct_id() {
        return product_id;
    }
    /**
     * Define product id.
     * @param product_id valor a utilizar.
     */

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }
    /**
     * Devuelve target quantity.
     * @return el hash calculado
     */

    public float getTarget_quantity() {
        return target_quantity;
    }
    /**
     * Define target quantity.
     * @param target_quantity valor a utilizar.
     */

    public void setTarget_quantity(float target_quantity) {
        this.target_quantity = target_quantity;
    }
    /**
     * Calcula el hash de esta instancia.
     * @return el hash calculado
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param obj objeto a comparar
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

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
