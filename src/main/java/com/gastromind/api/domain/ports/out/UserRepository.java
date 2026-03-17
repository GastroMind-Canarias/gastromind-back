package com.gastromind.api.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;

public interface UserRepository {

    /**
     * Guarda un usuario
     * 
     * @param user usuario a guardar
     * @return El usuario guardado
     */
    User save(User user);

    /**
     * Busca un User por id
     * 
     * @param id id del usuairo
     * @return El usuario o null
     */
    Optional<User> findById(String id);

    /**
     * Busca un User por name
     *
     * @param name nombre del usuairo
     * @return El usuario o null
     */
    Optional<User> findByName(String name);

    /**
     * Borra el usuario
     * 
     * @param id Id del usuario
     */
    void deleteById(String id);

    /**
     * Lista todos los User de una unidad familiar
     *
     * @param householdId Id de la unidad familiar
     * @return Lista con todos los usuarios de la unidad familiar
     */
    List<User> findByHouseholdId(String householdId);

    /**
     * Lista todos los User
     * 
     * @return Lista con todos los usuarios
     */
    List<User> findAll();

}
