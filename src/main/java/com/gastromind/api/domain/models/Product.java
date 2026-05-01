package com.gastromind.api.domain.models;

/**
 * Modelo de dominio para un producto del catálogo.
 */
public class Product {
    String id;
    String name;
    boolean is_essential;
    boolean needsReview;
    String reviewNote;
    Allergen allergen;
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public Product(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param is_essential valor a utilizar.
     * @param allergen el alergeno
     */

    public Product(String id, String name, boolean is_essential, Allergen allergen) {
        this.id = id;
        this.name = name;
        this.is_essential = is_essential;
        this.allergen = allergen;
    }
    /**
     * Crea una nueva instancia.
     */

    public Product() {
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
     * Indica si is essential.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    public boolean isIs_essential() {
        return is_essential;
    }
    /**
     * Define is essential.
     * @param is_essential valor a utilizar.
     */

    public void setIs_essential(boolean is_essential) {
        this.is_essential = is_essential;
    }
    /**
     * Indica si needs review.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    public boolean isNeedsReview() {
        return needsReview;
    }
    /**
     * Define needs review.
     * @param needsReview valor a utilizar.
     */

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }
    /**
     * Devuelve review note.
     * @return el valor actual
     */

    public String getReviewNote() {
        return reviewNote;
    }
    /**
     * Define review note.
     * @param reviewNote valor a utilizar.
     */

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
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
        Product other = (Product) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    /**
     * Devuelve allergen.
     * @return resultado de la operacion solicitada.
     */

    public Allergen getAllergen() {
        return allergen;
    }
    /**
     * Define allergen.
     * @param allerge valor a utilizar.
     */

    public void setAllergen(Allergen allerge) {
        this.allergen = allerge;
    }

}
