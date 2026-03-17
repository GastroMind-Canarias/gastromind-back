package com.gastromind.api.domain.models;
import java.util.Objects;

public class UserFavorites {
    String id;
    User user_id;
    Recipe recipe_id;


    public UserFavorites() {
    }


    public UserFavorites(String id) {
        this.id = id;
    }


    public UserFavorites(String id, User user_id, Recipe recipe_id) {
        this.id = id;
        this.user_id = user_id;
        this.recipe_id = recipe_id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser_id() {
        return this.user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public Recipe getRecipe_id() {
        return this.recipe_id;
    }

    public void setRecipe_id(Recipe recipe_id) {
        this.recipe_id = recipe_id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserFavorites)) {
            return false;
        }
        UserFavorites userFavorites = (UserFavorites) o;
        return Objects.equals(id, userFavorites.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    

}
