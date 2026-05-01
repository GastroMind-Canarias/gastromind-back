package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "fridge")
/**
 * Representa fridge dentro del dominio de la aplicacion.
 */
public class FridgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private HouseholdEntity household;

    @OneToMany(mappedBy = "fridge", cascade = CascadeType.ALL)
    private List<FridgeItemEntity> items;
    /**
     * Constructor de fridge.
     */

    public FridgeEntity() {
    }
    /**
     * Constructor de fridge.
     * @param id el identificador del recurso
     */

    public FridgeEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de fridge.
     * @param id el identificador del recurso
     * @param household valor a utilizar.
     * @param items valor a utilizar.
     */

    public FridgeEntity(String id, HouseholdEntity household, List<FridgeItemEntity> items) {
        this.id = id;
        this.household = household;
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
     * Devuelve items.
     * @return lista actual.
     */

    public List<FridgeItemEntity> getItems() {
        return items;
    }
    /**
     * Define items.
     * @param items valor a utilizar.
     */

    public void setItems(List<FridgeItemEntity> items) {
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
        FridgeEntity other = (FridgeEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




