package com.gastromind.api.domain.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo de dominio para un ticket de compra.
 */
public class Ticket {
    String id;
    User user_id;
    HouseHold houseHold_id;
    Store store_id;
    float total_amount;
    LocalDate purchaseDate;
    List<TicketItem> items = new ArrayList<>();
    /**
     * Crea una nueva instancia.
     */

    public Ticket() {
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public Ticket(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param user_id valor a utilizar.
     * @param store_id valor a utilizar.
     * @param total_amount valor a utilizar.
     * @param purchaseDate valor a utilizar.
     */

    public Ticket(String id, User user_id, Store store_id, float total_amount, LocalDate purchaseDate) {
        this.id = id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.total_amount = total_amount;
        this.purchaseDate = purchaseDate;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param user_id valor a utilizar.
     * @param store_id valor a utilizar.
     * @param total_amount valor a utilizar.
     * @param purchaseDate valor a utilizar.
     * @param items valor a utilizar.
     */

    public Ticket(String id, User user_id, Store store_id, float total_amount, LocalDate purchaseDate,
            List<TicketItem> items) {
        this.id = id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.total_amount = total_amount;
        this.purchaseDate = purchaseDate;
        this.items = items != null ? items : new ArrayList<>();
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
     * Devuelve house hold id.
     * @return resultado de la operacion solicitada.
     */

    public HouseHold getHouseHold_id() {
        return houseHold_id;
    }
    /**
     * Define house hold id.
     * @param houseHold_id valor a utilizar.
     */

    public void setHouseHold_id(HouseHold houseHold_id) {
        this.houseHold_id = houseHold_id;
    }
    /**
     * Devuelve store id.
     * @return resultado de la operacion solicitada.
     */

    public Store getStore_id() {
        return store_id;
    }
    /**
     * Define store id.
     * @param store_id valor a utilizar.
     */

    public void setStore_id(Store store_id) {
        this.store_id = store_id;
    }
    /**
     * Devuelve total amount.
     * @return el hash calculado
     */

    public float getTotal_amount() {
        return total_amount;
    }
    /**
     * Define total amount.
     * @param total_amount valor a utilizar.
     */

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }
    /**
     * Devuelve purchase date.
     * @return resultado de la operacion solicitada.
     */

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    /**
     * Define purchase date.
     * @param purchaseDate valor a utilizar.
     */

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    /**
     * Devuelve items.
     * @return lista actual.
     */

    public List<TicketItem> getItems() {
        return items;
    }
    /**
     * Define items.
     * @param items valor a utilizar.
     */

    public void setItems(List<TicketItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(getId(), ticket.getId());
    }
    /**
     * Calcula el hash de esta instancia.
     * @return el hash calculado
     */

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
