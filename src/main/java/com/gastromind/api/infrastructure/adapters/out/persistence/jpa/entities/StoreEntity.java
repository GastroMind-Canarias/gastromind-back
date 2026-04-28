package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "store")
/**
 * Representa store dentro del dominio de la aplicacion.
 */
public class StoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @OneToMany(mappedBy = "store")
    private List<TicketEntity> tickets;
    /**
     * Constructor de store.
     */

    public StoreEntity() {
    }
    /**
     * Constructor de store.
     * @param id el identificador del recurso
     */

    public StoreEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de store.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param tickets valor a utilizar.
     */

    public StoreEntity(String id, String name, List<TicketEntity> tickets) {
        this.id = id;
        this.name = name;
        this.tickets = tickets;
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
     * Devuelve name.
     * @return valor actual.
     */

    public String getName() {
        return name;
    }
    /**
     * Define name.
     * @param name el nombre
     */

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Devuelve tickets.
     * @return lista actual.
     */

    public List<TicketEntity> getTickets() {
        return tickets;
    }
    /**
     * Define tickets.
     * @param tickets valor a utilizar.
     */

    public void setTickets(List<TicketEntity> tickets) {
        this.tickets = tickets;
    }
    /**
     * Realiza ensure id before insert.
     */

    @PrePersist
    public void ensureIdBeforeInsert() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
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
        StoreEntity other = (StoreEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




