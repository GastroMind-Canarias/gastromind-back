package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_favorites")
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

    public UserFavoritesEntity() {
    }

    public UserFavoritesEntity(String id, UserEntity user, RecipeEntity recipe) {
        this.id = id;
        this.user = user;
        this.recipe = recipe;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RecipeEntity getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }
}