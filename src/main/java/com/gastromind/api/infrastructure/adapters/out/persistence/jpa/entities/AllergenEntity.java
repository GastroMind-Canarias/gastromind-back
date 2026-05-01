package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "allergen")
/**
 * Representa allergen dentro del dominio de la aplicacion.
 */
public class AllergenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @ManyToMany(mappedBy = "allergens")
    private Set<UserEntity> users;

    @ManyToMany(mappedBy = "allergens")
    private Set<ProductEntity> products;
    /**
     * Constructor de allergen.
     */

    public AllergenEntity() {
    }
    /**
     * Constructor de allergen.
     * @param id el identificador del recurso
     */

    public AllergenEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de allergen.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param users valor a utilizar.
     * @param products los productos
     */

    public AllergenEntity(String id, String name, Set<UserEntity> users, Set<ProductEntity> products) {
        this.id = id;
        this.name = name;
        this.users = users;
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
     * Devuelve users.
     * @return resultado de la operacion solicitada.
     */

    public Set<UserEntity> getUsers() {
        return users;
    }
    /**
     * Define users.
     * @param users valor a utilizar.
     */

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
    /**
     * Devuelve products.
     * @return resultado de la operacion solicitada.
     */

    public Set<ProductEntity> getProducts() {
        return products;
    }
    /**
     * Define products.
     * @param products los productos
     */

    public void setProducts(Set<ProductEntity> products) {
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
        AllergenEntity other = (AllergenEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




