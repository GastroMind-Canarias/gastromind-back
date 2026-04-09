package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Alérgenos de usuario: {@code user_allergens} es la relación Many-to-Many; quitar un alérgeno no borra filas de {@code allergen}.
 */
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final AllergenRepository allergenRepository;

    public UserServiceImpl(UserRepository userRepository, AllergenRepository allergenRepository) {
        this.userRepository = userRepository;
        this.allergenRepository = allergenRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByName(username).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(String id, User user) {
        findById(id);
        user.setId(id);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateProfile(String id, User userChanges) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        existingUser.setName(userChanges.getName());
        existingUser.setEmail(userChanges.getEmail());

        if (userChanges.getAllergens() != null) {
            existingUser.getAllergens().clear();

            userChanges.getAllergens().forEach(allergenChanges -> {
                Allergen realAllergen = allergenRepository.findById(allergenChanges.getId()).orElseThrow(() -> new NotFoundException("Alérgeno no encontrado: " + allergenChanges.getId()));
                existingUser.addAllergen(realAllergen);
            });
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void delete(String id) {
        findById(id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addAllergen(String userId, String allergenId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        Allergen allergen = allergenRepository.findById(allergenId).orElseThrow(() -> new NotFoundException("Alérgeno no encontrado"));
        user.addAllergen(allergen);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addAllergensBulk(String userId, List<String> allergenIds) {
        if (allergenIds == null || allergenIds.isEmpty()) {
            return;
        }
        for (String id : new LinkedHashSet<>(allergenIds)) {
            if (id == null || id.isBlank()) {
                continue;
            }
            addAllergen(userId, id);
        }
    }

    @Override
    @Transactional
    public void removeAllergen(String userId, String allergenId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.removeAllergen(allergenId);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeAllergensBulk(String userId, List<String> allergenIds) {
        if (allergenIds == null) {
            return;
        }
        for (String id : allergenIds) {
            if (id == null || id.isBlank()) {
                continue;
            }
            removeAllergen(userId, id);
        }
    }

    @Override
    @Transactional
    public void replaceAllergens(String userId, List<String> allergenIds) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.getAllergens().clear();
        userRepository.save(user);
        if (allergenIds == null || allergenIds.isEmpty()) {
            return;
        }
        addAllergensBulk(userId, allergenIds);
    }

    @Override
    public List<Allergen> listAllergens(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return List.copyOf(user.getAllergens());
    }

    @Override
    @Transactional
    public User updateUserRole(String id, Role newRole) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        user.setRole(newRole);

        return userRepository.save(user);
    }
}