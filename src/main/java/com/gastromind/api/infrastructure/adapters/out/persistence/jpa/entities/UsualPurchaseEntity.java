package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "usual_purchase",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usual_purchase_user_product",
                columnNames = {"user_id", "product_id"}))
/**
 * Representa usual purchase dentro del dominio de la aplicacion.
 */
public class UsualPurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "target_quantity")
    private BigDecimal targetQuantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    /**
     * Constructor de usual purchase.
     */

    public UsualPurchaseEntity() {
    }
    /**
     * Constructor de usual purchase.
     * @param id el identificador del recurso
     */

    public UsualPurchaseEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de usual purchase.
     * @param id el identificador del recurso
     * @param targetQuantity valor a utilizar.
     * @param user valor a utilizar.
     * @param product el producto
     */

    public UsualPurchaseEntity(String id, BigDecimal targetQuantity, UserEntity user, ProductEntity product) {
        this.id = id;
        this.targetQuantity = targetQuantity;
        this.user = user;
        this.product = product;
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
     * Devuelve target quantity.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getTargetQuantity() {
        return targetQuantity;
    }
    /**
     * Define target quantity.
     * @param targetQuantity valor a utilizar.
     */

    public void setTargetQuantity(BigDecimal targetQuantity) {
        this.targetQuantity = targetQuantity;
    }
    /**
     * Devuelve user.
     * @return resultado de la operacion solicitada.
     */

    public UserEntity getUser() {
        return user;
    }
    /**
     * Define user.
     * @param user valor a utilizar.
     */

    public void setUser(UserEntity user) {
        this.user = user;
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
        UsualPurchaseEntity other = (UsualPurchaseEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




