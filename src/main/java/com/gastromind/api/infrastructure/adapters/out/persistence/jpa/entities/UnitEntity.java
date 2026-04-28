package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "unit")
/**
 * Representa unit dentro del dominio de la aplicacion.
 */
public class UnitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @OneToMany(mappedBy = "unit")
    private List<RecipeIngredientEntity> recipeIngredients;
    /**
     * Constructor de unit.
     */

    public UnitEntity() {
    }
    /**
     * Constructor de unit.
     * @param id el identificador del recurso
     */

    public UnitEntity(String id) {
        this.id = id;
    }
    /**
     * Constructor de unit.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param recipeIngredients valor a utilizar.
     */

    public UnitEntity(String id, String name, List<RecipeIngredientEntity> recipeIngredients) {
        this.id = id;
        this.name = name;
        this.recipeIngredients = recipeIngredients;
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
     * Devuelve recipe ingredients.
     * @return lista actual.
     */

    public List<RecipeIngredientEntity> getRecipeIngredients() {
        return recipeIngredients;
    }
    /**
     * Define recipe ingredients.
     * @param recipeIngredients valor a utilizar.
     */

    public void setRecipeIngredients(List<RecipeIngredientEntity> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
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
        UnitEntity other = (UnitEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}




