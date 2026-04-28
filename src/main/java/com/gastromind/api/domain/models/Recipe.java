package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de dominio para una receta sugerida o guardada.
 */
public class Recipe {
    String id;
    String title;
    String instructions;
    int servings;
    int prep_time;
    Appliance appliance_needed;
    String difficulty;
    LocalDate created_at;

    private List<RecipeIngredientUsage> ingredientsUsed = new ArrayList<>();
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param title valor a utilizar.
     * @param instructions valor a utilizar.
     * @param servings valor a utilizar.
     * @param prep_time valor a utilizar.
     * @param appliance_needed valor a utilizar.
     * @param difficulty valor a utilizar.
     * @param created_at valor a utilizar.
     */

    public Recipe(String id, String title, String instructions, int servings, int prep_time, Appliance appliance_needed,
            String difficulty, LocalDate created_at) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.servings = servings;
        this.prep_time = prep_time;
        this.appliance_needed = appliance_needed;
        this.difficulty = difficulty;
        this.created_at = created_at;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public Recipe(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     */

    public Recipe() {
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
     * Devuelve title.
     * @return el valor actual
     */

    public String getTitle() {
        return title;
    }
    /**
     * Define title.
     * @param title valor a utilizar.
     */

    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Devuelve instructions.
     * @return el valor actual
     */

    public String getInstructions() {
        return instructions;
    }
    /**
     * Define instructions.
     * @param instructions valor a utilizar.
     */

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    /**
     * Devuelve servings.
     * @return el hash calculado
     */

    public int getServings() {
        return servings;
    }
    /**
     * Define servings.
     * @param servings valor a utilizar.
     */

    public void setServings(int servings) {
        this.servings = servings;
    }
    /**
     * Devuelve prep time.
     * @return el hash calculado
     */

    public int getPrep_time() {
        return prep_time;
    }
    /**
     * Define prep time.
     * @param prep_time valor a utilizar.
     */

    public void setPrep_time(int prep_time) {
        this.prep_time = prep_time;
    }
    /**
     * Devuelve appliance needed.
     * @return resultado de la operacion solicitada.
     */

    public Appliance getAppliance_needed() {
        return appliance_needed;
    }
    /**
     * Define appliance needed.
     * @param appliance_needed valor a utilizar.
     */

    public void setAppliance_needed(Appliance appliance_needed) {
        this.appliance_needed = appliance_needed;
    }
    /**
     * Devuelve difficulty.
     * @return el valor actual
     */

    public String getDifficulty() {
        return difficulty;
    }
    /**
     * Define difficulty.
     * @param difficulty valor a utilizar.
     */

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    /**
     * Devuelve created at.
     * @return resultado de la operacion solicitada.
     */

    public LocalDate getCreated_at() {
        return created_at;
    }
    /**
     * Define created at.
     * @param created_at valor a utilizar.
     */

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }
    /**
     * Devuelve ingredients used.
     * @return lista actual.
     */

    public List<RecipeIngredientUsage> getIngredientsUsed() {
        return ingredientsUsed;
    }
    /**
     * Define ingredients used.
     * @param ingredientsUsed valor a utilizar.
     */

    public void setIngredientsUsed(List<RecipeIngredientUsage> ingredientsUsed) {
        this.ingredientsUsed = ingredientsUsed != null ? ingredientsUsed : new ArrayList<>();
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
        Recipe other = (Recipe) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
