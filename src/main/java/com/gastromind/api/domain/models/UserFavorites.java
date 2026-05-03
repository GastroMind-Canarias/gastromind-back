package com.gastromind.api.domain.models;
import java.util.Objects;

/**
 * Modelo de dominio para recetas favoritas del usuario.
 */
public class UserFavorites {
    String id;
    User user_id;
    Recipe recipe_id;
    /**
     * Crea una nueva instancia.
     */


    public UserFavorites() {
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */


    public UserFavorites(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param user_id valor a utilizar.
     * @param recipe_id valor a utilizar.
     */


    public UserFavorites(String id, User user_id, Recipe recipe_id) {
        this.id = id;
        this.user_id = user_id;
        this.recipe_id = recipe_id;
    }
    /**
     * Devuelve id.
     * @return el valor actual
     */

    public String getId() {
        return this.id;
    }
    /**
     * Define id.
     * @param id el identificador del recurso
     */

    public void setId(String id) {
        this.id = id;
    }
    /**
     * Devuelve user id.
     * @return resultado de la operacion solicitada.
     */

    public User getUser_id() {
        return this.user_id;
    }
    /**
     * Define user id.
     * @param user_id valor a utilizar.
     */

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }
    /**
     * Devuelve recipe id.
     * @return resultado de la operacion solicitada.
     */

    public Recipe getRecipe_id() {
        return this.recipe_id;
    }
    /**
     * Define recipe id.
     * @param recipe_id valor a utilizar.
     */

    public void setRecipe_id(Recipe recipe_id) {
        this.recipe_id = recipe_id;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

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
    /**
     * Calcula el hash de esta instancia.
     * @return el hash calculado
     */

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    

}
