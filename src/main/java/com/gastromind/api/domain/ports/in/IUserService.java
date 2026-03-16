package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;

public interface IUserService {
    List<User> findAll();

    User findById(String id);

    User create(User user);

    User update(String id, User user);

    User updateProfile(String id, String name, String email);

    void delete(String id);

    void addAllergen(String userId, String allergenId);

    void removeAllergen(String userId, String allergenId);

    List<Allergen> listAllergens(String userId);
}