package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket")
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketItemEntity> items;

    public TicketEntity() {
    }

    public TicketEntity(String id) {
        this.id = id;
    }

    public TicketEntity(String id, BigDecimal totalAmount, LocalDateTime purchaseDate, UserEntity user,
            StoreEntity store, List<TicketItemEntity> items) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
        this.user = user;
        this.store = store;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    public List<TicketItemEntity> getItems() {
        return items;
    }

    public void setItems(List<TicketItemEntity> items) {
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
        TicketEntity other = (TicketEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
