package com.gastromind.api.domain.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ticket {
    String id;
    User user_id;
    Store store_id;
    float total_amount;
    LocalDate purchaseDate;
    List<TicketItem> items = new ArrayList<>();

    public Ticket() {
    }

    public Ticket(String id) {
        this.id = id;
    }

    public Ticket(String id, User user_id, Store store_id, float total_amount, LocalDate purchaseDate) {
        this.id = id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.total_amount = total_amount;
        this.purchaseDate = purchaseDate;
    }

    public Ticket(String id, User user_id, Store store_id, float total_amount, LocalDate purchaseDate,
            List<TicketItem> items) {
        this.id = id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.total_amount = total_amount;
        this.purchaseDate = purchaseDate;
        this.items = items != null ? items : new ArrayList<>();
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

    public Store getStore_id() {
        return store_id;
    }

    public void setStore_id(Store store_id) {
        this.store_id = store_id;
    }

    public float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<TicketItem> getItems() {
        return items;
    }

    public void setItems(List<TicketItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(getId(), ticket.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
