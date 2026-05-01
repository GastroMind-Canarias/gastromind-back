package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_favorites",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_favorites_user_recipe",
                columnNames = {"user_id", "recipe_id"}))
/**
 * Representa user favorites dentro del dominio de la aplicacion.
 */
public class UserFavoritesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id ;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;
    /**
     * Constructor de user favorites.
     */

    public UserFavoritesEntity() {
    }
    /**
     * Constructor de user favorites.
     * @param id el identificador del recurso
     * @param user valor a utilizar.
     * @param recipe la receta
     */

    public UserFavoritesEntity(String id, UserEntity user, RecipeEntity recipe) {
        this.id = id;
        this.user = user;
        this.recipe = recipe;
    }

    // Getters y Setters
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
     * Devuelve recipe.
     * @return resultado de la operacion solicitada.
     */

    public RecipeEntity getRecipe() {
        return recipe;
    }
    /**
     * Define recipe.
     * @param recipe la receta
     */

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }
}




