package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;

import java.util.List;

/**
 * Puerto de entrada para perfil, preferencias alimentarias y cambios de datos del usuario.
 */
public interface IUserService {
    List<User> findAll();

    User findById(String id);

    User findByUsername(String username);

    User create(User user);

    User update(String id, User user);

    User updateProfile(String id, User user);

    void delete(String id);

    void addAllergen(String userId, String allergenId);

    void addAllergensBulk(String userId, List<String> allergenIds);

    void removeAllergen(String userId, String allergenId);

    void removeAllergensBulk(String userId, List<String> allergenIds);

    void replaceAllergens(String userId, List<String> allergenIds);

    List<Allergen> listAllergens(String userId);

    User updateUserRole(String id, Role newRole);
}
