package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ticket")
/**
 * Representa ticket dentro del dominio de la aplicacion.
 */
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
    @JoinColumn(name = "household_id")
    private HouseholdEntity household;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketItemEntity> items;
    /**
     * Constructor de ticket.
     */

    public TicketEntity() {
    }
    /**
     * Constructor de ticket.
     * @param id el identificador del recurso
     */

    public TicketEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de ticket.
     * @param id el identificador del recurso
     * @param totalAmount valor a utilizar.
     * @param purchaseDate valor a utilizar.
     * @param user valor a utilizar.
     * @param store la tienda
     * @param items valor a utilizar.
     */

    public TicketEntity(String id, BigDecimal totalAmount, LocalDateTime purchaseDate, UserEntity user,
            StoreEntity store, List<TicketItemEntity> items) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
        this.user = user;
        this.store = store;
        this.items = items;
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
     * Devuelve total amount.
     * @return resultado de la operacion solicitada.
     */

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    /**
     * Define total amount.
     * @param totalAmount valor a utilizar.
     */

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    /**
     * Devuelve purchase date.
     * @return resultado de la operacion solicitada.
     */

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    /**
     * Define purchase date.
     * @param purchaseDate valor a utilizar.
     */

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
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
     * Devuelve household.
     * @return resultado de la operacion solicitada.
     */

    public HouseholdEntity getHousehold() {
        return household;
    }
    /**
     * Define household.
     * @param household valor a utilizar.
     */

    public void setHousehold(HouseholdEntity household) {
        this.household = household;
    }
    /**
     * Devuelve store.
     * @return resultado de la operacion solicitada.
     */

    public StoreEntity getStore() {
        return store;
    }
    /**
     * Define store.
     * @param store la tienda
     */

    public void setStore(StoreEntity store) {
        this.store = store;
    }
    /**
     * Devuelve items.
     * @return lista actual.
     */

    public List<TicketItemEntity> getItems() {
        return items;
    }
    /**
     * Define items.
     * @param items valor a utilizar.
     */

    public void setItems(List<TicketItemEntity> items) {
        this.items = items;
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
        TicketEntity other = (TicketEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




