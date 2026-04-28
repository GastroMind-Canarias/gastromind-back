package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "category")
/**
 * Representa category dentro del dominio de la aplicacion.
 */
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 160)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<ProductEntity> products;
    /**
     * Constructor de category.
     */

    public CategoryEntity() {
    }
    /**
     * Constructor de category.
     * @param id el identificador del recurso
     */

    public CategoryEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de category.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param products los productos
     */

    public CategoryEntity(String id, String name, List<ProductEntity> products) {
        this.id = id;
        this.name = name;
        this.products = products;
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
     * Devuelve products.
     * @return lista actual.
     */

    public List<ProductEntity> getProducts() {
        return products;
    }
    /**
     * Define products.
     * @param products los productos
     */

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
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
        CategoryEntity other = (CategoryEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




