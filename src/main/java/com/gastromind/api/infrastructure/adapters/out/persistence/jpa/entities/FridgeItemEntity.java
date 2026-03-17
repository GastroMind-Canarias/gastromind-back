package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fridge_items")
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
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
    
    public FridgeItemEntity(String id, BigDecimal quantity, LocalDate expirationDate, ItemStatus status,
            FridgeEntity fridge, ProductEntity product) {
        this.id = id;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.status = status;
        this.fridge = fridge;
        this.product = product;
    }

    public FridgeItemEntity(String id) {
        this.id = id;
    }

    public FridgeItemEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public FridgeEntity getFridge() {
        return fridge;
    }

    public void setFridge(FridgeEntity fridge) {
        this.fridge = fridge;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
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
        FridgeItemEntity other = (FridgeItemEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
