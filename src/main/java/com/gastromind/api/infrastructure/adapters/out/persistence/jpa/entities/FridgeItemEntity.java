package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fridge_items")
/**
 * Representa fridge item dentro del dominio de la aplicacion.
 */
public class FridgeItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne
    @JoinColumn(name = "fridge_id", nullable = false)
    private FridgeEntity fridge;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "product_label", length = 200)
    private String productLabel;
    /**
     * Constructor de fridge item.
     * @param id el identificador del recurso
     * @param quantity la cantidad
     * @param expirationDate valor a utilizar.
     * @param status valor a utilizar.
     * @param fridge la nevera
     * @param product el producto
     */

    public FridgeItemEntity(String id, BigDecimal quantity, LocalDate expirationDate, ItemStatus status,
            FridgeEntity fridge, ProductEntity product) {
        this.id = id;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.status = status;
        this.fridge = fridge;
        this.product = product;
    }
    /**
     * Constructor de fridge item.
     * @param id el identificador del recurso
     */

    public FridgeItemEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de fridge item.
     */

    public FridgeItemEntity() {
    }
    /**
     * Devuelve id.
     * @return valor actual.
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
     * Devuelve fridge.
     * @return resultado de la operacion solicitada.
     */

    public FridgeEntity getFridge() {
        return fridge;
    }
    /**
     * Define fridge.
     * @param fridge la nevera
     */

    public void setFridge(FridgeEntity fridge) {
        this.fridge = fridge;
    }
    /**
     * Devuelve product.
     * @return resultado de la operacion solicitada.
     */

    public ProductEntity getProduct() {
        return product;
    }
    /**
     * Define product.
     * @param product el producto
     */

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
    /**
     * Devuelve product label.
     * @return valor actual.
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
     * Calcula el hash de la instancia.
     * @return valor configurado.
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
     * @param obj valor a utilizar.
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FridgeItemEntity other = (FridgeItemEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




