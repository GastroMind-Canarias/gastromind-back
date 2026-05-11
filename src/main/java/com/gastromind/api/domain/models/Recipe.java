package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Appliance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Receta persistida o generada: texto, tiempos, electrodoméstico necesario y lista de ingredientes con cantidades para la cocina del hogar.
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

    public Recipe(String id) {
        this.id = id;
    }

    public Recipe() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(int prep_time) {
        this.prep_time = prep_time;
    }

    public Appliance getAppliance_needed() {
        return appliance_needed;
    }

    public void setAppliance_needed(Appliance appliance_needed) {
        this.appliance_needed = appliance_needed;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public List<RecipeIngredientUsage> getIngredientsUsed() {
        return ingredientsUsed;
    }

    public void setIngredientsUsed(List<RecipeIngredientUsage> ingredientsUsed) {
        this.ingredientsUsed = ingredientsUsed != null ? ingredientsUsed : new ArrayList<>();
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
        Recipe other = (Recipe) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
