package com.gastromind.api.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;

public class FridgeItem {
    private String id;
    private BigDecimal quantity;
    private LocalDate expirationDate;
    private ItemStatus status;
    private Product product;
    private String fridgeId;

    public FridgeItem() {
    }

    public FridgeItem(String id, BigDecimal quantity, LocalDate expirationDate, ItemStatus status, Product product,
            String fridgeId) {
        this.id = id;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.status = status;
        this.product = product;
        this.fridgeId = fridgeId;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getFridgeId() {
        return fridgeId;
    }

    public void setFridgeId(String fridgeId) {
        this.fridgeId = fridgeId;
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
        FridgeItem other = (FridgeItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}