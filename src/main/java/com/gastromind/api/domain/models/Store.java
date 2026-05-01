package com.gastromind.api.domain.models;

/**
 * Modelo de dominio para una tienda o supermercado.
 */
public class Store {
    String id;
    String name;
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param name el nombre
     */

    public Store(String id, String name) {
        this.id = id;
        this.name = name;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public Store(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     */

    public Store() {
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
     * Devuelve name.
     * @return el valor actual
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
        Store other = (Store) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
