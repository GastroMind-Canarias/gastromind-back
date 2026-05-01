package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.Role;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Modelo de dominio para un usuario de la aplicación.
 */
public class User {

    String id;
    String name;
    String email;
    String password;
    HouseHold houseHold_id;
    Role role;
    Set<Allergen> allergens = new HashSet<>();
    /**
     * Crea una nueva instancia.
     */

    public User() {
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     */

    public User(String id) {
        this.id = id;
    }
    /**
     * Crea una nueva instancia.
     * @param id el identificador del recurso
     * @param name el nombre
     * @param email el correo electronico
     * @param password la contrasena
     * @param houseHold_id valor a utilizar.
     * @param role el rol
     * @param allergens valor a utilizar.
     */

    public User(String id, String name, String email, String password, HouseHold houseHold_id, Role role, Set<Allergen> allergens) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.houseHold_id = houseHold_id;
        this.role = role;
        this.allergens = allergens;
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
     * Devuelve name.
     * @return el valor actual
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
     * Devuelve email.
     * @return el valor actual
     */

    public String getEmail() {
        return email;
    }
    /**
     * Define email.
     * @param email el correo electronico
     */

    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Devuelve password.
     * @return el valor actual
     */

    public String getPassword() {
        return password;
    }
    /**
     * Define password.
     * @param password la contrasena
     */

    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Devuelve house hold id.
     * @return resultado de la operacion solicitada.
     */

    public HouseHold getHouseHold_id() {
        return houseHold_id;
    }
    /**
     * Define house hold id.
     * @param houseHold_id valor a utilizar.
     */

    public void setHouseHold_id(HouseHold houseHold_id) {
        this.houseHold_id = houseHold_id;
    }
    /**
     * Devuelve role.
     * @return resultado de la operacion solicitada.
     */

    public Role getRole() {
        return role;
    }
    /**
     * Define role.
     * @param role el rol
     */

    public void setRole(Role role) {
        this.role = role;
    }
    /**
     * Realiza add allergen.
     * @param allergen el alergeno
     */

    public void addAllergen(Allergen allergen) {
        if (allergen != null) {
            this.allergens.add(allergen);
        }
    }
    /**
     * Realiza remove allergen.
     * @param allergenId valor a utilizar.
     */

    public void removeAllergen(String allergenId) {
        this.allergens.removeIf(a -> a.getId().equals(allergenId));
    }
    /**
     * Define allergens.
     * @param allergens valor a utilizar.
     */

    public void setAllergens(Set<Allergen> allergens) {
        this.allergens = allergens;
    }
    /**
     * Devuelve allergens.
     * @return resultado de la operacion solicitada.
     */

    public Set<Allergen> getAllergens() {
        return this.allergens;
    }
    /**
     * Compara esta instancia con otro objeto.
     * @param o valor a utilizar.
     * @return true si ambos objetos son equivalentes; false en caso contrario
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }
    /**
     * Calcula el hash de esta instancia.
     * @return el hash calculado
     */

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
