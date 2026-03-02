package com.gastromind.api.domain.models;

import java.time.LocalDate;

import com.gastromind.api.domain.models.enums.Appliance;

public class Recipe {
    String id;
    String title;
    String instructions;
    int servings;
    int prep_time;
    Appliance appliance_needed;
    String difficulty;
    String description;
    int calories;
    String image_url;
    LocalDate created_at;

    /**
     * 
     * @param id               id de la receta
     * @param title            titulo de la receta
     * @param instructions     instrucciones de la receta
     * @param servings         numero de personas
     * @param prep_time        tiempo de preparacion
     * @param appliance_needed utensilios necesarios
     * @param difficulty       dificultad
     * @param created_at       cuando fue generada
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
     * Constructor con id
     * 
     * @param id id de la receta
     */
    public Recipe(String id) {
        this.id = id;
    }

    /**
     * Constructor vacio
     */
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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
