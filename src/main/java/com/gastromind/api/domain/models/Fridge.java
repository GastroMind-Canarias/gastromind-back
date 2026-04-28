package com.gastromind.api.domain.models;

/**
 * Modelo de dominio para una nevera de hogar.
 */
public class Fridge {
    String id;
    HouseHold houseHold_id;
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param houseHold_id valor a utilizar.
     */

    public Fridge(String id, HouseHold houseHold_id) {
        this.id = id;
        this.houseHold_id = houseHold_id;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public Fridge(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     */

    public Fridge() {
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
     * Calcula el hash de esta instancia.
     * @return el hash calculado
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
     * @param obj objeto a comparar
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Fridge other = (Fridge) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
