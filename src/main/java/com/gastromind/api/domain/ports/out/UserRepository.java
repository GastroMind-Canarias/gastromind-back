package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByName(String name);

    void deleteById(String id);

    /** Lista todos los User de una unidad familiar */
    List<User> findByHouseholdId(String householdId);

    /** Lista todos los User */
    List<User> findAll();

    /** RegistrarNuevoAlergenoDeUsuario: añade un registro en user_allergens */
    void addAllergenToUser(String userId, String allergenId);

    /** EliminarAlergenoDeUsuario: elimina un registro en user_allergens */
    void removeAllergenFromUser(String userId, String allergenId);

    /** Lista los alérgenos de un usuario */
    List<Allergen> findAllergensByUserId(String userId);
}
