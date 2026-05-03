package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Define el contrato de persistencia o integracion para user.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    void deleteById(String id);

    List<User> findByHouseholdId(String householdId);

    List<User> findAll();

}
