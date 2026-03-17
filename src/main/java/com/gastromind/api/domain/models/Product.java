package com.gastromind.api.domain.models;

public class Product {
    String id;
    String name;
    boolean is_essential;
    Allergen allergen;

    /**
     * Constructor con id
     * 
     * @param id id del producto
     */
    public Product(String id) {
        this.id = id;
    }

    /**
     * Constructor con todos los parametros
     * 
     * @param id           id del producto
     * @param name         nombre del producto
     * @param is_essential si el producto es esencial
     * @param allergen     Alergenos del producto
     */
    public Product(String id, String name, boolean is_essential, Allergen allergen) {
        this.id = id;
        this.name = name;
        this.is_essential = is_essential;
        this.allergen = allergen;
    }

    /**
     * Constructor vacio
     */
    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_essential() {
        return is_essential;
    }

    public void setIs_essential(boolean is_essential) {
        this.is_essential = is_essential;
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
        Product other = (Product) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Allergen getAllergen() {
        return allergen;
    }

    public void setAllergen(Allergen allerge) {
        this.allergen = allerge;
    }

}
