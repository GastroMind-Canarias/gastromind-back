package com.gastromind.api.domain.models;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo de dominio para un producto almacenado en la nevera.
 */
public class FridgeItem {
    private String id;
    private BigDecimal quantity;
    private LocalDate expirationDate;
    private ItemStatus status;
    private Product product;
    private String productLabel;
    private String fridgeId;
    /**
     * Crea una nueva instancia.
     */

    public FridgeItem() {
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param quantity la cantidad
     * @param expirationDate valor a utilizar.
     * @param status valor a utilizar.
     * @param product el producto
     * @param fridgeId identificador de la nevera.
     */

    public FridgeItem(String id, BigDecimal quantity, LocalDate expirationDate, ItemStatus status, Product product,
            String fridgeId) {
        this.id = id;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.status = status;
        this.product = product;
        this.fridgeId = fridgeId;
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
     * Devuelve quantity.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getQuantity() {
        return quantity;
    }
    /**
     * Define quantity.
     * @param quantity la cantidad
     */

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    /**
     * Devuelve expiration date.
     * @return resultado de la operacion solicitada.
     */

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    /**
     * Define expiration date.
     * @param expirationDate valor a utilizar.
     */

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    /**
     * Devuelve status.
     * @return resultado de la operacion solicitada.
     */

    public ItemStatus getStatus() {
        return status;
    }
    /**
     * Define status.
     * @param status valor a utilizar.
     */

    public void setStatus(ItemStatus status) {
        this.status = status;
    }
    /**
     * Devuelve product.
     * @return resultado de la operacion solicitada.
     */

    public Product getProduct() {
        return product;
    }
    /**
     * Define product.
     * @param product el producto
     */

    public void setProduct(Product product) {
        this.product = product;
    }
    /**
     * Devuelve product label.
     * @return el valor actual
     */

    public String getProductLabel() {
        return productLabel;
    }
    /**
     * Define product label.
     * @param productLabel valor a utilizar.
     */

    public void setProductLabel(String productLabel) {
        this.productLabel = productLabel;
    }
    /**
     * Devuelve fridge id.
     * @return el valor actual
     */

    public String getFridgeId() {
        return fridgeId;
    }
    /**
     * Define fridge id.
     * @param fridgeId identificador de la nevera.
     */

    public void setFridgeId(String fridgeId) {
        this.fridgeId = fridgeId;
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
        FridgeItem other = (FridgeItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
